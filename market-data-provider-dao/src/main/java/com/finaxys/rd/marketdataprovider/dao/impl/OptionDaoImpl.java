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
import com.finaxys.rd.dataextraction.domain.Option;
import com.finaxys.rd.marketdataprovider.dao.OptionDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionDaoImpl.
 */
public class OptionDaoImpl implements OptionDao {


		/** The table name. */
	@Value("#{'${dao.option.tableName:option}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The info fam. */
	@Value("#{'${dao.option.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;
	
	
	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructor
	/**
	 * Instantiates a new option dao impl.
	 */
	public OptionDaoImpl() {
		super();
	}

	/**
	 * Instantiates a new option dao impl.
	 *
	 * @param connection the connection
	 */
	public OptionDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers

	/**
	 * To option summary.
	 *
	 * @param result the result
	 * @return the option
	 */
	private Option toOption(Result result) {
		Option option = null;


		try {
			option = new Option();
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = option.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(INFO_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(option,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return option;
	}

	/**
	 * Mk row key.
	 *
	 * @param option the option
	 * @return the byte[]
	 */
	private byte[] mkRowKey(Option option) {
		return mkRowKey(option.getProvider(), option.getExchSymb(), option.getSymbol(), option.getOptionChain(), option.getExpiration(), option.getInputDate());
	}

	/**
	 * Mk row key.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String exchSymb, String symbol, String optionChain, LocalDate expiration, DateTime inputDate) {
		byte provByte = (byte) provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] optionChainHash = DaoHelper.md5sum(optionChain);
		byte[] expirationBytes = Bytes.toBytes(new DateTime(expiration.toString()).getMillis());
		byte[] inputDateBytes = Bytes.toBytes(new DateTime(inputDate.toString()).getMillis());
	
		byte[] rowkey = new byte[ inputDateBytes.length + expirationBytes.length + 3 * DaoHelper.MD5_LENGTH + 1];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, provByte);
		offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putBytes(rowkey, offset, inputDateBytes, 0, inputDateBytes.length);
		offset = Bytes.putBytes(rowkey, offset, expirationBytes, 0, expirationBytes.length);
		offset = Bytes.putBytes(rowkey, offset, optionChainHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);

		return rowkey;
	}


	private Get mkGet(char provider, String exchSymb, String symbol, String optionChain, LocalDate expiration, DateTime inputDate) {
		Get g = new Get(mkRowKey(provider, exchSymb, symbol, optionChain, expiration, inputDate));
		return g;
	}

	/**
	 * Mk put.
	 * 
	 * @param option
	 *            the option
	 * @return the put
	 */
	private Put mkPut(Option option) {
		Put p = null;
		try {
			p = new Put(mkRowKey(option));
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = option.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			Object value;
			for (Field attribute : attributes) {

				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(option,
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
	 * com.finaxys.rd.marketdataprovider.dao.OptionDao#add(com.finaxys.
	 * rd.marketdataprovider.domain.Option)
	 */
	public boolean add(Option option) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(option);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionDao#get(char,
	 * java.lang.String, java.lang.String)
	 */
	public Option get(char provider, String exchSymb, String symbol, String optionChain, LocalDate expiration, DateTime inputDate) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, exchSymb, symbol, optionChain, expiration, inputDate);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		Option option = toOption(result);
		table.close();
		return option;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.OptionDao#list(java.lang.String
	 * )
	 */
	public List<Option> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<Option> ret = new ArrayList<Option>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}
	
	public List<Option> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<Option> ret = new ArrayList<Option>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionDao#listAll()
	 */
	public List<Option> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<Option> ret = new ArrayList<Option>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}



	public List<Option> list(char provider, String exchSymb) throws IOException {
		byte provByte = (byte) provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] prefix = new byte[DaoHelper.MD5_LENGTH + 1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);
		Bytes.putBytes(prefix, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);

		return list(prefix);

	}


}
