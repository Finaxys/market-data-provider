/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.dataextraction.domain.Index;

// TODO: Auto-generated Javadoc
/**
 * The Interface IndexInfoDao.
 */
public interface IndexDao {
	
	/**
	 * Adds the.
	 *
	 * @param exchange the exchange
	 * @return true, if successful
	 */
	public boolean add(Index index);
	
	/**
	 * Gets the.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @return the index info
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Index get(char provider, String exchSymb, String symbol) throws IOException;
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Index> list(String prefix) throws IOException;
	

	public List<Index> list(byte[] prefix) throws IOException;
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Index> listAll() throws IOException;
	
	/**
	 * List all symbols.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> listAllSymbols() throws IOException ;
	
	public List<Index> list(char provider, String exchSymb) throws IOException ;
}
