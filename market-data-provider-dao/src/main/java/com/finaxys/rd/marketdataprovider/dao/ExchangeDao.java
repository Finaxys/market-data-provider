/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.dataextraction.domain.Exchange;

// TODO: Auto-generated Javadoc
/**
 * The Interface ExchangeDao.
 */
public interface ExchangeDao {
	
	/**
	 * Adds the.
	 *
	 * @param exchange the exchange
	 * @return true, if successful
	 */
	public boolean add(Exchange exchange);
	
	/**
	 * Gets the.
	 *
	 * @param provider the provider
	 * @param symbol the symbol
	 * @param mic the mic
	 * @return the exchange
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Exchange get(char provider, String symbol, String mic) throws IOException;
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Exchange> list(String prefix) throws IOException;
	
	
	public List<Exchange> list(byte[] prefix) throws IOException;
	
	
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Exchange> listAll() throws IOException;
	
	public List<Exchange> list(char provider) throws IOException;
}
