package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.marketdataprovider.domain.Rate;

import domain.CurrencyPair;

public interface RatesDao {
	/**
	 * Adds the Rate.
	 *
	 * @param rate the rate
	 * @return true, if successful
	 */
	public boolean add(Rate rate);
	
	/**
	 * Gets the.
	 *
	 * @param name the name
	 * @param currency the currency
	 * @return the rate
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Rate get(String name, String currency) throws IOException;
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Rate> list(String prefix) throws IOException;
	
	/**
	 * List all symbols.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<Rate> listAll() throws IOException;
}
