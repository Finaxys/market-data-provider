package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.dataextraction.domain.InterbankRate;


public interface InterbankRateDao {
	/**
	 * Adds the Rate.
	 *
	 * @param interbankRate the rate
	 * @return true, if successful
	 */
	public boolean add(InterbankRate interbankRate);
	
	/**
	 * Gets the.
	 *
	 * @param name the name
	 * @param currency the currency
	 * @return the rate
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public InterbankRate get(String name, String currency) throws IOException;
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<InterbankRate> list(String prefix) throws IOException;

	public List<InterbankRate> list(byte[] prefix) throws IOException;
	/**
	 * List all symbols.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<InterbankRate> listAll() throws IOException;
	
	public List<InterbankRate> list(char provider) throws IOException;
}
