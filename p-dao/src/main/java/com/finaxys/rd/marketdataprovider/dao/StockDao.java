/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.marketdataprovider.domain.Stock;

// TODO: Auto-generated Javadoc
/**
 * The Interface StockDao.
 */
public interface StockDao {
	
	/**
	 * Adds the.
	 *
	 * @param stock the stock
	 * @return true, if successful
	 */
	public boolean add(Stock stock);
	
	/**
	 * Gets the.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @return the stock
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Stock get(char provider, String exchSymb, String symbol) throws IOException;
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Stock> list(String prefix) throws IOException;
	
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Stock> listAll() throws IOException;
	
	/**
	 * List all symbols.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> listAllSymbols() throws IOException;
}
