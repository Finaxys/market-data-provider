/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain.msg;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.finaxys.rd.marketdataprovider.domain.msg.Document;

// TODO: Auto-generated Javadoc
/**
 * The Class Message.
 */
public class Message implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5545589019698956395L;

	/** The id. */
	private String id;
	
	/** The body. */
	private Document body;
	
	/** The routing key. */
	private String routingKey;

	private boolean send;
	
	/**
	 * Instantiates a new message.
	 */
	public Message() {
		super();
	}

	/**
	 * Instantiates a new message.
	 *
	 * @param body the body
	 */
	public Message(Document body) {
		super();
		this.id = new Date().getTime() + body.hashCode()+ "";
		this.body = body;
		this.send = true;
	}

	/**
	 * Instantiates a new message.
	 *
	 * @param id the id
	 * @param body the body
	 */
	public Message(String id, Document body) {
		super();
		this.id = id;
		this.body = body;
		this.send = true;
	}

	/**
	 * Instantiates a new message.
	 *
	 * @param body the body
	 * @param routingKey the routing key
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Message(Document body, String routingKey) throws IOException {
		super();
		this.id = new Date().getTime() + body.getProvider() + body.getDataClass().name();
		this.body = body;
		this.routingKey = routingKey;
		this.send = true;
	}

	/**
	 * Instantiates a new message.
	 *
	 * @param id the id
	 * @param body the body
	 * @param routingKey the routing key
	 */
	public Message(String id, Document body, String routingKey) {
		super();
		this.id = id;
		this.body = body;
		this.routingKey = routingKey;
		this.send = true;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public Document getBody() {
		return body;
	}

	/**
	 * Sets the body.
	 *
	 * @param body the new body
	 */
	public void setBody(Document body) {
		this.body = body;
	}

	/**
	 * Gets the routing key.
	 *
	 * @return the routing key
	 */
	public String getRoutingKey() {
		return routingKey;
	}

	/**
	 * Sets the routing key.
	 *
	 * @param routingKey the new routing key
	 */
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public boolean isSend() {
		return send;
	}

	public void setSend(boolean send) {
		this.send = send;
	}

}
