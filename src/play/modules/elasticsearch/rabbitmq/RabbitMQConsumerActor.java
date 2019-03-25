package play.modules.elasticsearch.rabbitmq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import play.Logger;
import play.modules.elasticsearch.ElasticSearchIndexAction;
import play.modules.elasticsearch.ElasticSearchIndexEvent;

/**
 * The Class RabbitMQConsumerActor.
 */
public class RabbitMQConsumerActor implements Runnable, Consumer {

	@Override
	public void handleConsumeOk(String consumerTag) {

	}

	@Override
	public void handleCancelOk(String consumerTag) {

	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {

	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope env, BasicProperties props, byte[] body)
			throws IOException {
		try {

			Object object = deserialize(body);

			if (object instanceof ElasticSearchIndexEvent) {
				// Get Index Event
				ElasticSearchIndexEvent indexEvent = (ElasticSearchIndexEvent) object;
				Logger.info("event %s", indexEvent);

				// Fire Index Action
				ElasticSearchIndexAction indexAction = new ElasticSearchIndexAction();
				indexAction.invoke(indexEvent);

			} else {

				throw new RuntimeException("Unknown Message: " + body);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

		// Log Debug
//				Logger.debug("RabbitMQ Consumer Actor: %s", body);
//
//				// Check Message Type
//				if (body instanceof ElasticSearchIndexEvent) {
//					// Get Index Event
//					ElasticSearchIndexEvent indexEvent = (ElasticSearchIndexEvent) body;
//
//					// Fire Index Action
//					ElasticSearchIndexAction indexAction = new ElasticSearchIndexAction();
//					indexAction.invoke(indexEvent);
//
//				} else {
//					// Log Debug
//					throw new RuntimeException("Unknown Message: " +body);
//				}
//		
	}

	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {

	}

	@Override
	public void handleRecoverOk(String consumerTag) {

	}

	@Override
	public void run() {

	}

}
