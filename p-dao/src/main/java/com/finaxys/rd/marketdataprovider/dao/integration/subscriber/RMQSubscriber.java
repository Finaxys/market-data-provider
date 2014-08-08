/*
 * 
 */
package com.finaxys.rd.marketdataprovider.serviceagent.subscriber;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finaxys.rd.dataextraction.msg.Message;
import com.finaxys.rd.marketdataprovider.serviceagent.receiver.Receiver;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
// TODO: Auto-generated Javadoc
//proxy pattern ??
/**
 * The Class RabbitMqSubscriber.
 */
public class RabbitMqSubscriber implements Subscriber {
	
	/** The logger. */
	static Logger logger = Logger.getLogger(RabbitMqSubscriber.class);

	/** The receiver. */
	private Receiver receiver;
	
	/** The channel. */
	private Channel channel;
	
	/** The queue. */
	private String queue;
	
	/** The exchange. */
	private String exchange;
	
	/** The binding key. */
	private String bindingKey;
	
	/**
	 * Instantiates a new rabbit mq subscriber.
	 */
	public RabbitMqSubscriber() {
		super();
	}


	
	/**
	 * Instantiates a new rabbit mq subscriber.
	 *
	 * @param receiver the receiver
	 * @param channel the channel
	 * @param bindingKey the binding key
	 * @param queue the queue
	 * @param exchange the exchange
	 */
	public RabbitMqSubscriber(Receiver receiver, Channel channel, String bindingKey , String queue, String exchange) {
		super();
		this.receiver = receiver;
		this.channel = channel;
		this.queue = queue;
		this.exchange = exchange;
		this.bindingKey = bindingKey;
	}
	


	/**
	 * Gets the receiver.
	 *
	 * @return the receiver
	 */
	public Receiver getReceiver() {
		return receiver;
	}

	/**
	 * Sets the receiver.
	 *
	 * @param receiver the new receiver
	 */
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * Gets the binding key.
	 *
	 * @return the binding key
	 */
	public String getBindingKey() {
		return bindingKey;
	}

	/**
	 * Sets the binding key.
	 *
	 * @param bindingKey the new binding key
	 */
	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	
	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Sets the channel.
	 *
	 * @param channel the new channel
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	/**
	 * Gets the queue.
	 *
	 * @return the queue
	 */
	public String getQueue() {
		return queue;
	}

	/**
	 * Sets the queue.
	 *
	 * @param queue the new queue
	 */
	public void setQueue(String queue) {
		this.queue = queue;
	}

	/**
	 * Gets the exchange.
	 *
	 * @return the exchange
	 */
	public String getExchange() {
		return exchange;
	}

	/**
	 * Sets the exchange.
	 *
	 * @param exchange the new exchange
	 */
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.subscriber.Subscriber#subscribe()
	 */
	public void subscribe() throws Exception {

		channel.exchangeDeclare(exchange, queue);

		String queueName = bindingKey; 
	    channel.queueDeclare(queueName, true, false, false, null);
	    
		channel.queueBind(queueName, exchange, bindingKey);

		logger.info("waiting msgs...");
		channel.basicConsume(queueName, true, new QueueingConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				Message message = deserializeMessage(body);
				logger.info("msg received");
				receiver.receive(message);
			}
		});

	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.subscriber.Subscriber#unSubscribe()
	 */
	public void unSubscribe() throws Exception {
		channel.close();
	}

	/**
	 * Deserialize message.
	 *
	 * @param body the body
	 * @return the message
	 */
	private Message deserializeMessage(byte[] body) {
		ByteArrayInputStream bis = new ByteArrayInputStream(body);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return (Message) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			try {
				bis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}