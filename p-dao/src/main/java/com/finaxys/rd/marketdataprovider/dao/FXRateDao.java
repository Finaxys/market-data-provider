/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.marketdataprovider.domain.FXRate;


// TODO: Auto-generated Javadoc
/**
 * The Interface FXRateDao.
 */
public interface FXRateDao {
	
	/**
	 * Adds the.
	 *
	 * @param stock the stock
	 * @return true, if successful
	 */
	public boolean add(FXRate stock);
	
	/**
	 * List.
	 *
	 * @param prefix the prefix
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<FXRate> list(String prefix) throws IOException;
	
	/**
	 * List.
	 *
	 * @param start the start
	 * @param end the end
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<FXRate> list(byte[] start, byte[] end) throws IOException;
	
	/**
	 * List all.
	 *
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<FXRate> listAll() throws IOException;
}
