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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyPairDaoImpl.
 */
public class CurrencyPairDaoImpl implements CurrencyPairDao {

	
	/** The table name. */
	@Value("#{'${dao.currencyPair.tableName:currency_pair}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The info fam. */
	@Value("#{'${dao.currencyPair.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;

	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructors

	/**
	 * Instantiates a new currency pair dao impl.
	 */
	public CurrencyPairDaoImpl() {
		super();
	}

	/**
	 * Instantiates a new currency pair dao impl.
	 *
	 * @param connection the connection
	 */
	public CurrencyPairDaoImpl(HConnection connection) {
		super();
		this.connection = connection;
	}

	// Helpers

	/**
	 * To currency pair.
	 *
	 * @param result the result
	 * @return the currency pair
	 */
	private CurrencyPair toCurrencyPair(Result result) {
		CurrencyPair currencyPair = null;

		try {
			currencyPair = new CurrencyPair();
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = currencyPair.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(INFO_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(currencyPair,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currencyPair;
	}

	/**
	 * Mk row key.
	 *
	 * @param currencyPair the currency pair
	 * @return the byte[]
	 */
	private byte[] mkRowKey(CurrencyPair currencyPair) {
		return mkRowKey(currencyPair.getSymbol(), currencyPair.getProvider(), currencyPair.getSource(), currencyPair.getInputDate().getMillis());
	}

	/**
	 * Mk row key.
	 *
	 * @param symbol the symbol
	 * @return the byte[]
	 */
	private byte[] mkRowKey(String symbol, char provider, char source, Long inputDate) {
		byte provByte = (byte) provider;
		byte sourceByte = (byte) source;
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] inputDateBytes =  Bytes.toBytes(inputDate);
		byte[] rowkey = new byte[ DaoHelper.MD5_LENGTH + inputDateBytes.length + 2];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, provByte);
		offset = Bytes.putBytes(rowkey, offset,inputDateBytes, 0, inputDateBytes.length);
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putByte(rowkey, offset, sourceByte);

		return rowkey;
	}

	/**
	 * Mk get.
	 *
	 * @param symbol the symbol
	 * @return the gets the
	 */
	private Get mkGet(String symbol, char provider, char source, Long inputDate) {
		Get g = new Get(mkRowKey(symbol,  provider,  source,  inputDate));
		return g;
	}

	/**
	 * Mk put.
	 *
	 * @param currencyPair the currency pair
	 * @return the put
	 */
	private Put mkPut(CurrencyPair currencyPair) {
		Put p = null;
		try {
			p = new Put(mkRowKey(currencyPair));
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = currencyPair.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			Object value;
			for (Field attribute : attributes) {

				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(currencyPair,
						attribute.getName())) != null)
					p.add(INFO_FAM, attribute.getName().getBytes(),
							DaoHelper.toBytes(attribute, value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}

	/**


	// CRUD

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao#add(com.finaxys.rd.dataextraction.domain.CurrencyPair)
	 */
	public boolean add(CurrencyPair currencyPair) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(currencyPair);
			table.put(p);
			table.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao#get(java.lang.String)
	 */
	public CurrencyPair get(String symbol, char provider, char source, Long inputDate) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(symbol,  provider,  source,  inputDate);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		CurrencyPair currencyPair = toCurrencyPair(result);
		table.close();
		return currencyPair;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao#list(java.lang.String)
	 */
	public List<CurrencyPair> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<CurrencyPair> ret = new ArrayList<CurrencyPair>();
		for (Result result : results) {
			ret.add(toCurrencyPair(result));
		}
		table.close();
		return ret;
	}
	
	public List<CurrencyPair> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<CurrencyPair> ret = new ArrayList<CurrencyPair>();
		for (Result result : results) {
			ret.add(toCurrencyPair(result));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao#listAll()
	 */
	public List<CurrencyPair> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<CurrencyPair> ret = new ArrayList<CurrencyPair>();
		for (Result result : results) {
			ret.add(toCurrencyPair(result));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao#listAllSymbols()
	 */
//	public List<String> listAllSymbols() throws IOException {
//		HTableInterface table = connection.getTable(TABLE_NAME);
//		ResultScanner results = table.getScanner(DaoHelper.mkScan());
//		List<String> sp = new ArrayList<String>();
//		StringBuilder sb = new StringBuilder();
//		int i = 0;
//		for (Result result : results) {
//			sb.append("\"" + Bytes.toString(result.getValue(INFO_FAM, SYMBOL_COL)) + "\",");
//			i++;
//
//			// i=1000 => no response
//			if (i == 500) {
//				sp.add(sb.toString().replaceAll(",$", ""));
//				sb.setLength(0);
//				i = 0;
//			}
//
//		}
//		sp.add(sb.toString().replaceAll(",$", ""));
//		table.close();
//		return sp;
//	}
//	
	
	public List<CurrencyPair> list(char provider) throws IOException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}


}
