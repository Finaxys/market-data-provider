/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.OptionQuote;

// TODO: Auto-generated Javadoc
/**
 * The Interface OptionQuoteDao.
 */
public interface OptionQuoteDao {
		
	public boolean add(OptionQuote optionQuote);

	public OptionQuote get(char provider, String symbol, Long ts, DataType dataType) throws IOException;
	
	public List<OptionQuote> list(String prefix) throws IOException;
	
	public List<OptionQuote> list(byte[] prefix) throws IOException;

	public List<OptionQuote> listAll() throws IOException;
	
	public List<OptionQuote> list(String symbol, String exchSymb, DataType dataType,  DateTime start, DateTime end) throws IOException;

	public List<OptionQuote> list(String symbol, String exchSymb, DataType dataType) throws IOException;
	
	public List<OptionQuote> list(String symbol, DataType dataType,  DateTime start, DateTime end) throws IOException;

	public List<OptionQuote> list(String symbol, DataType dataType) throws IOException;
	

	public List<OptionQuote> list( DataType dataType) throws IOException;

	

}
