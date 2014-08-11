/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.FXRate;


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
	
	public List<FXRate> list(byte[] prefix) throws IOException;
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
	

	public List<FXRate> list(String symbol, DataType dataType,  DateTime start, DateTime end) throws IOException;

	public List<FXRate> list(String symbol, DataType dataType) throws IOException;
	

	public List<FXRate> list(DataType dataType) throws IOException;



}
