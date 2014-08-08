/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyPairDaoImpl.
 */
public class CurrencyPairDaoImpl implements CurrencyPairDao {

	/** The symbol col. */
	@Value("#{'${dao.currencyPair.symbolCol:symbol}'.bytes}")
	private byte[] SYMBOL_COL;
	
	/** The base col. */
	@Value("#{'${dao.currencyPair.baseCol:base}'.bytes}")
	private byte[] BASE_COL;
	
	/** The quote col. */
	@Value("#{'${dao.currencyPair.quoteCol:quote}'.bytes}")
	private byte[] QUOTE_COL;
	
	@Value("#{'${dao.currencyPair.providerCol:provider}'.bytes}")
	private byte[] PROVIDER_COL;
	
	@Value("#{'${dao.currencyPair.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;
	
	@Value("#{'${dao.currencyPair.inputDateCol:inputDprovider_COLate}'.bytes}")
	private byte[] INPUTDATE_COL;

	@Value("#{'${dao.currencyPair.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;
	
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
			Field[] attributes = CurrencyPair.class.getDeclaredFields();
			Field[] columns = CurrencyPairDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i< attributes.length; i++) {

				if ((value = result.getValue(INFO_FAM, (byte[]) columns[i-1].get(this))) != null)
					PropertyUtils.setSimpleProperty(currencyPair,  attributes[i].getName(), DaoHelper.getTypedValue( attributes[i], value));
					

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
		return mkRowKey(currencyPair.getSymbol(), currencyPair.getProvider(), currencyPair.getSource(), currencyPair.getInputDate().toDateTimeAtStartOfDay().getMillis());
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
			Field[] attributes = CurrencyPair.class.getDeclaredFields();
			Field[] columns = CurrencyPairDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(currencyPair,
						attributes[i].getName())) != null)
					p.add(INFO_FAM, (byte[]) columns[i - 1].get(this),
							DaoHelper.toBytes(attributes[i], value));

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
	public List<String> listAllSymbols() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<String> sp = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Result result : results) {
			sb.append("\"" + Bytes.toString(result.getValue(INFO_FAM, SYMBOL_COL)) + "\",");
			i++;

			// i=1000 => no response
			if (i == 500) {
				sp.add(sb.toString().replaceAll(",$", ""));
				sb.setLength(0);
				i = 0;
			}

		}
		sp.add(sb.toString().replaceAll(",$", ""));
		table.close();
		return sp;
	}
	
	
	public List<CurrencyPair> list(char provider) throws IOException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}
}
