/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.marketdataprovider.domain.IndexInfo;

// TODO: Auto-generated Javadoc
/**
 * The Interface IndexInfoDao.
 */
public interface IndexInfoDao {
	
	/**
	 * Adds the.
	 *
	 * @param exchange the exchange
	 * @return true, if successful
	 */
	public boolean add(IndexInfo exchange);
	
	/**
	 * Gets the.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @return the index info
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public IndexInfo get(char provider, String exchSymb, String symbol) throws IOException;
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<IndexInfo> list(String prefix) throws IOException;
	
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<IndexInfo> listAll() throws IOException;
	
	/**
	 * List all symbols.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> listAllSymbols() throws IOException ;
}
