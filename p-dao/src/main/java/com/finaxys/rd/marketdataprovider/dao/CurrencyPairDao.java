/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.marketdataprovider.domain.CurrencyPair;


// TODO: Auto-generated Javadoc
/**
 * The Interface CurrencyPairDao.
 */
public interface CurrencyPairDao {
	
	/**
	 * Adds the.
	 *
	 * @param currencyPair the currency pair
	 * @return true, if successful
	 */
	public boolean add(CurrencyPair currencyPair);
	
	/**
	 * Gets the.
	 *
	 * @param symbol the symbol
	 * @return the currency pair
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CurrencyPair get(String symbol) throws IOException;
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<CurrencyPair> list(String prefix) throws IOException;
	
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<CurrencyPair> listAll() throws IOException;
	
	/**
	 * List all symbols.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> listAllSymbols() throws IOException;
}
