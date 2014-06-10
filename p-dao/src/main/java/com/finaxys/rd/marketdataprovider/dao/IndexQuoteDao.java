/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.marketdataprovider.domain.IndexQuote;

// TODO: Auto-generated Javadoc
/**
 * The Interface IndexQuoteDao.
 */
public interface IndexQuoteDao {
	
	/**
	 * Adds the.
	 *
	 * @param index the index
	 * @return true, if successful
	 */
	public boolean add(IndexQuote index);
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<IndexQuote> list(String prefix) throws IOException;
	
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<IndexQuote> listAll() throws IOException;
}
