package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.InterbankRateData;

public interface InterbankRateDataDao {

	public boolean add(InterbankRateData interbankRateData);
	
	public List<InterbankRateData> list(String prefix) throws IOException;
	
	public List<InterbankRateData> listAll() throws IOException;
	
	
	public List<InterbankRateData> list(String symbol, String currency, DataType dataType,  DateTime start, DateTime end) throws IOException;

	public List<InterbankRateData> list(String symbol, String currency, DataType dataType) throws IOException;
	
	public List<InterbankRateData> list(String symbol, DataType dataType,  DateTime start, DateTime end) throws IOException;

	public List<InterbankRateData> list(String symbol, DataType dataType) throws IOException;
	
	public List<InterbankRateData> list( DataType dataType) throws IOException;
	
}
