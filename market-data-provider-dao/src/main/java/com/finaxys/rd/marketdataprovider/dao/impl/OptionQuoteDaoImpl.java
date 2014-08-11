/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.dataextraction.domain.OptionQuote;
import com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionQuoteDaoImpl.
 */
public class OptionQuoteDaoImpl implements OptionQuoteDao {


	/** The table name. */
	@Value("#{'${dao.optionQuote.tableName:option_quote}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The values fam. */
	@Value("#{'${dao.optionQuote.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;


	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructors
	
	/**
	 * Instantiates a new option quote dao impl.
	 */
	public OptionQuoteDaoImpl() {
		super();

	}

	/**
	 * Instantiates a new option quote dao impl.
	 *
	 * @param connection the connection
	 */
	public OptionQuoteDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers

	/**
	 * To option.
	 *
	 * @param result the result
	 * @return the option quote
	 */
	private OptionQuote toOption(Result result) {
		OptionQuote optionQuote = null;


		try {
			optionQuote = new OptionQuote();
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = optionQuote.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(VALUES_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(optionQuote,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return optionQuote;
	}

	/**
	 * Mk row key.
	 *
	 * @param optionQuote the option quote
	 * @return the byte[]
	 */
	private byte[] mkRowKey(OptionQuote optionQuote) {
		return mkRowKey(optionQuote.getSource(), optionQuote.getSymbol(), optionQuote.getQuoteDateTime().getMillis(),
				optionQuote.getDataType());
	}

	/**
	 * Mk row key.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @param ts the ts
	 * @param dataType the data type
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String symbol, Long ts, DataType dataType) {
		byte provByte = (byte) provider;
		byte typeByte = dataType.getTByte();
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 +  DaoHelper.MD5_LENGTH + timestamp.length];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, timestamp, 0, timestamp.length);
		offset = Bytes.putByte(rowkey, offset, provByte);

		return rowkey;
	}


	private Get mkGet(char provider, String symbol, Long ts, DataType dataType) {
		Get g = new Get(mkRowKey(provider, symbol, ts, dataType));
		return g;
	}

	/**
	 * Mk put.
	 *
	 * @param optionQuote the option quote
	 * @return the put
	 */
	private Put mkPut(OptionQuote optionQuote) {
		Put p = null;
		try {
			p = new Put(mkRowKey(optionQuote));
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = optionQuote.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			Object value;
			for (Field attribute : attributes) {

				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(optionQuote,
						attribute.getName())) != null)
					p.add(VALUES_FAM, attribute.getName().getBytes(),
							DaoHelper.toBytes(attribute, value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}


	
	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao#add(com.finaxys.rd.dataextraction.domain.OptionQuote)
	 */
	public boolean add(OptionQuote optionQuote) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(optionQuote);
			table.put(p);
			table.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			// logger.info(e.toString());
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			// logger.info(e.toString());
			return false;
		}

	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao#get(char, java.lang.String, java.lang.String, java.lang.Long, com.finaxys.rd.dataextraction.msg.Document.DataType)
	 */
	public OptionQuote get(char provider, String symbol, Long ts, DataType dataType) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, symbol, ts, dataType);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		OptionQuote optionQuote = toOption(result);
		table.close();
		return optionQuote;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao#list(java.lang.String)
	 */
	public List<OptionQuote> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<OptionQuote> ret = new ArrayList<OptionQuote>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}
	
	public List<OptionQuote> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<OptionQuote> ret = new ArrayList<OptionQuote>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao#listAll()
	 */
	public List<OptionQuote> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<OptionQuote> ret = new ArrayList<OptionQuote>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}

	@Override
	public List<OptionQuote> list(String symbol, String exchSymb,
			DataType dataType, DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OptionQuote> list(String symbol, String exchSymb,
			DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OptionQuote> list(String symbol, DataType dataType,
			DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OptionQuote> list(String symbol, DataType dataType)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OptionQuote> list(DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
