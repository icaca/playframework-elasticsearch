package play.modules.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import play.Logger;
import play.db.Model;
import play.modules.elasticsearch.search.SearchResults;
import play.modules.elasticsearch.transformer.JPATransformer;
import play.modules.elasticsearch.transformer.MapperTransformer;
import play.modules.elasticsearch.transformer.SimpleTransformer;

/**
 * An elastic search query
 * 
 * @param <T> the generic model to search for
 */
public class Query<T extends Model> {

	private final Class<T> clazz;
	private final QueryBuilder builder;
	private final List<AggregationBuilder> aggrs;
	private final List<SortBuilder> sorts;
	private HighlightBuilder highLight = null;

	private int from = -1;
	private int size = -1;

	private boolean hydrate = false;
	private boolean useMapper = false;

	Query(Class<T> clazz, QueryBuilder builder) {
		Validate.notNull(clazz, "clazz cannot be null");
		Validate.notNull(builder, "builder cannot be null");
		this.clazz = clazz;
		this.builder = builder;
		this.aggrs = new ArrayList<AggregationBuilder>();
		this.sorts = new ArrayList<SortBuilder>();
	}

	/**
	 * Sets from
	 * 
	 * @param from record index to start from
	 * @return self
	 */
	public Query<T> from(int from) {
		this.from = from;

		return this;
	}

	/**
	 * Sets fetch size
	 * 
	 * @param size the fetch size
	 * @return self
	 */
	public Query<T> size(int size) {
		this.size = size;

		return this;
	}

	/**
	 * Controls entity hydration
	 * 
	 * @param hydrate hydrate entities
	 * @return self
	 */
	public Query<T> hydrate(boolean hydrate) {
		this.hydrate = hydrate;

		return this;
	}

	/**
	 * Controls the usage of mapper
	 * 
	 * @param useMapper use mapper during result processing
	 * @return self
	 */
	public Query<T> useMapper(boolean useMapper) {
		this.useMapper = useMapper;

		return this;
	}

	/**
	 * Adds a facet
	 * 
	 * @param aggr the facet
	 * @return self
	 */
	public Query<T> addAggr(AggregationBuilder aggr) {
		Validate.notNull(aggr, "facet cannot be null");
		aggrs.add(aggr);

		return this;
	}

	/**
	 * Sorts the result by a specific field
	 * 
	 * @param field the sort field
	 * @param order the sort order
	 * @return self
	 */
	public Query<T> addSort(String field, SortOrder order) {
		Validate.notEmpty(field, "field cannot be null");
		Validate.notNull(order, "order cannot be null");
		sorts.add(SortBuilders.fieldSort(field).order(order));

		return this;
	}

	/**
	 * Adds a generic {@link SortBuilder}
	 * 
	 * @param sort the sort builder
	 * @return self
	 */
	public Query<T> addSort(SortBuilder sort) {
		Validate.notNull(sort, "sort cannot be null");
		sorts.add(sort);

		return this;
	}

	public Query<T> setHighlight(HighlightBuilder highlight) {
		Validate.notNull(highlight, "highlight cannot be null");
		this.highLight = highlight;

		return this;
	}

	/**
	 * Runs the query
	 * 
	 * @return the search results
	 */
	public SearchResults<T> fetch() {
		// Build request
		SearchRequestBuilder request = ElasticSearch.builder(builder, clazz);

		// Facets
		for (AggregationBuilder aggr : aggrs) {
			request.addAggregation(aggr);
		}

		// Sorting
		for (SortBuilder sort : sorts) {
			request.addSort(sort);
		}

		// Paging
		if (from > -1) {
			request.setFrom(from);
		}
		if (size > -1) {
			request.setSize(size);
		}

		// Only load id field for hydrate
		if (hydrate) {
			request.addDocValueField("_id");
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("ES Query: %s", builder.toString());
		}
		if (highLight != null) {
			request.highlighter(highLight);
		}

		SearchResponse searchResponse = request.execute().actionGet();
		SearchResults<T> searchResults = null;
		if (hydrate) {
			searchResults = new JPATransformer<T>().toSearchResults(searchResponse, clazz);
		} else if (useMapper) {
			searchResults = new MapperTransformer<T>().toSearchResults(searchResponse, clazz);
		} else {
			searchResults = new SimpleTransformer<T>().toSearchResults(searchResponse, clazz);
		}
		return searchResults;
	}
}
