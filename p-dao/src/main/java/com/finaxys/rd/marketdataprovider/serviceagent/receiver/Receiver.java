/*
 * 
 */
package com.finaxys.rd.marketdataprovider.serviceagent.receiver;

import com.finaxys.rd.dataextraction.msg.Message;


// TODO: Auto-generated Javadoc
/**
 * The Interface Receiver.
 */
public interface Receiver {
	
	/**
	 * Receive.
	 *
	 * @param message the message
	 * @return true, if successful
	 */
	public boolean receive(Message message);
}
