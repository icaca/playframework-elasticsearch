package play.modules.elasticsearch.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * The Class RabbitMQConsumerActor.
 */
public class RabbitMQConsumerActor implements Runnable, Consumer {

	@Override
	public void handleConsumeOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCancelOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDelivery(String consumerTag, Envelope env, BasicProperties props, byte[] body)
			throws IOException {
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
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
