/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.IndexQuote;

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
	public boolean add(IndexQuote indexQuote);
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<IndexQuote> list(String prefix) throws IOException;

	public List<IndexQuote> list(byte[] prefix) throws IOException;
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */

	public List<IndexQuote> list(String symbol, String exchSymb, DataType dataType,  DateTime start, DateTime end) throws IOException;

	public List<IndexQuote> list(String symbol, String exchSymb, DataType dataType) throws IOException;
	
	public List<IndexQuote> list(String symbol, DataType dataType,  DateTime start, DateTime end) throws IOException;

	public List<IndexQuote> list(String symbol, DataType dataType) throws IOException;
	

	public List<IndexQuote> list( DataType dataType) throws IOException;

}
