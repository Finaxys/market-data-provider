/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.dataextraction.msg.Document.DataType;
import com.finaxys.rd.marketdataprovider.domain.StockQuote;

// TODO: Auto-generated Javadoc
/**
 * The Interface StockQuoteDao.
 */
public interface StockQuoteDao {
	
	/**
	 * Adds the.
	 *
	 * @param index the index
	 * @return true, if successful
	 */
	public boolean add(StockQuote index);
	
	/**
	 * Gets the.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @param ts the ts
	 * @param dataType the data type
	 * @return the stock quote
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public StockQuote get(char provider, String exchSymb, String symbol, Long ts, DataType dataType) throws IOException;
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<StockQuote> list(String prefix) throws IOException;
	
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<StockQuote> listAll() throws IOException;
}
