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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.dataextraction.domain.OptionChain;
import com.finaxys.rd.marketdataprovider.dao.OptionChainDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionChainDaoImpl.
 */
public class OptionChainDaoImpl implements OptionChainDao {

	

	/** The table name. */
	@Value("#{'${dao.optionChain.tableName:optionChain}'.bytes}")
	private byte[] TABLE_NAME;

	/** The info fam. */
	@Value("#{'${dao.optionChain.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;


	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructor
	/**
	 * Instantiates a new optionChain dao impl.
	 */
	public OptionChainDaoImpl() {
		super();
	}

	/**
	 * Instantiates a new optionChain dao impl.
	 * 
	 * @param connection
	 *            the connection
	 */
	public OptionChainDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers

	/**
	 * To optionChain summary.
	 * 
	 * @param result
	 *            the result
	 * @return the optionChain
	 */
	private OptionChain toOptionChain(Result result) {
		OptionChain optionChain = null;

		try {
			optionChain = new OptionChain();
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = optionChain.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(INFO_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(optionChain,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return optionChain;
	}

	/**
	 * Mk row key.
	 * 
	 * @param optionChain
	 *            the optionChain
	 * @return the byte[]
	 */
	private byte[] mkRowKey(OptionChain optionChain) {
		return mkRowKey(optionChain.getProvider(), optionChain.getSymbol(), optionChain.getExpiration().toDateTimeAtStartOfDay().getMillis(), optionChain.getInputDate().getMillis());
	}

	/**
	 * Mk row key.
	 * 
	 * @param provider
	 *            the provider
	 * @param exchSymb
	 *            the exch symb
	 * @param symbol
	 *            the symbol
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String symbol, Long expiration, Long inputDate) {
		byte provByte = (byte) provider;
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] expirationBytes = Bytes.toBytes(expiration);
		byte[] inputDateBytes= Bytes.toBytes(inputDate);
		byte[] rowkey = new byte[expirationBytes.length + inputDateBytes.length + DaoHelper.MD5_LENGTH + 1];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, provByte);
		offset = Bytes.putBytes(rowkey, offset, inputDateBytes, 0, inputDateBytes.length);
		offset = Bytes.putBytes(rowkey, offset, expirationBytes, 0, expirationBytes.length);
		 Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		

		return rowkey;
	}


	private Get mkGet(char provider, String symbol, LocalDate expiration, DateTime inputDate) {
		Get g = new Get(mkRowKey(provider, symbol, expiration.toDateTimeAtStartOfDay().getMillis(), inputDate.getMillis()));
		return g;
	}

	/**
	 * Mk put.
	 * 
	 * @param optionChain
	 *            the optionChain
	 * @return the put
	 */
	private Put mkPut(OptionChain optionChain) {
		Put p = null;
		try {
			p = new Put(mkRowKey(optionChain));
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = optionChain.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			Object value;
			for (Field attribute : attributes) {

				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(optionChain,
						attribute.getName())) != null)
					p.add(INFO_FAM, attribute.getName().getBytes(),
							DaoHelper.toBytes(attribute, value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}

	
	// CRUD

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.OptionChainDao#add(com.finaxys.
	 * rd.marketdataprovider.domain.OptionChain)
	 */
	public boolean add(OptionChain optionChain) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(optionChain);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionChainDao#get(char,
	 * java.lang.String, java.lang.String)
	 */
	public OptionChain get(char provider, String symbol, LocalDate expiration, DateTime inputDate) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, symbol, expiration, inputDate);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		OptionChain optionChain = toOptionChain(result);
		table.close();
		return optionChain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.OptionChainDao#list(java.lang.String
	 * )
	 */
	public List<OptionChain> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<OptionChain> ret = new ArrayList<OptionChain>();
		for (Result result : results) {
			ret.add(toOptionChain(result));
		}
		table.close();
		return ret;
	}
	
	
	 
	public List<OptionChain> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<OptionChain> ret = new ArrayList<OptionChain>();
		for (Result result : results) {
			ret.add(toOptionChain(result));
		}
		table.close();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionChainDao#listAll()
	 */
	public List<OptionChain> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<OptionChain> ret = new ArrayList<OptionChain>();
		for (Result result : results) {
			ret.add(toOptionChain(result));
		}
		table.close();
		return ret;
	}

	public List<OptionChain> list(char provider) throws IOException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}
	
}
