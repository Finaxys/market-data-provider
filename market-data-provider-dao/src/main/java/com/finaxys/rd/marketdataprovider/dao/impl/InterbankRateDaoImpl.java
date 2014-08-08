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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.dataextraction.domain.InterbankRate;
import com.finaxys.rd.marketdataprovider.dao.InterbankRateDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

public class InterbankRateDaoImpl implements InterbankRateDao {

	/** The name col. */
	@Value("#{'${dao.interbank_rate.nameCol:n}'.bytes}")
	private byte[] NAME_COL;

	/** The currency col. */
	@Value("#{'${dao.interbank_rate.currencyCol:c}'.bytes}")
	private byte[] CURRENCY_COL;


	@Value("#{'${dao.interbank_rate.providerCol:provider}'.bytes}")
	private byte[] PROVIDER_COL;

	@Value("#{'${dao.interbank_rate.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;

	@Value("#{'${dao.interbank_rate.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;
	
	/** The table name. */
	@Value("#{'${dao.interbank_rate.tableName:interbank_rate}'.bytes}")
	private byte[] TABLE_NAME;

	/** The info fam. */
	@Value("#{'${dao.interbank_rate.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;

	@Autowired
	private HConnection connection;

	// Constructor
	/**
	 * Instantiates a new stock dao impl.
	 */
	public InterbankRateDaoImpl() {
		super();
	}

	/**
	 * Instantiates a new stock dao impl.
	 * 
	 * @param connection
	 *            the connection
	 */
	InterbankRateDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers
	/**
	 * To stock summary.
	 * 
	 * @param result
	 *            the result
	 * @return the stock
	 */
	private InterbankRate toInterbankRate(Result result) {
		InterbankRate interbankRate = null;

		try {
			interbankRate = new InterbankRate();
			Field[] attributes = InterbankRate.class.getDeclaredFields();
			Field[] columns = InterbankRateDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i< attributes.length; i++) {

				if ((value = result.getValue(INFO_FAM,
						(byte[]) columns[i-1].get(this))) != null)
					PropertyUtils.setSimpleProperty(interbankRate,
							 attributes[i].getName(),
							DaoHelper.getTypedValue( attributes[i], value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return interbankRate;
	}

	/**
	 * Mk row key.
	 * 
	 * @param stock
	 *            the stock
	 * @return the byte[]
	 */
	private byte[] mkRowKey(InterbankRate interbankRate) {
		return mkRowKey(interbankRate.getSymbol(), interbankRate.getCurrency());
	}

	private byte[] mkRowKey(String exchSymb, String symbol) {
		byte[] nameHash = DaoHelper.md5sum(exchSymb);
		byte[] currencyHash = DaoHelper.md5sum(symbol);
		byte[] rowkey = new byte[2 * DaoHelper.MD5_LENGTH];

		int offset = 0;
		offset = Bytes.putBytes(rowkey, offset, nameHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, currencyHash, 0, DaoHelper.MD5_LENGTH);

		return rowkey;
	}

	private Get mkGet(String exchSymb, String symbol) {
		Get g = new Get(mkRowKey(exchSymb, symbol));
		return g;
	}

	private Put mkPut(InterbankRate interbankRate) {
		Put p = null;
		try {
			p = new Put(mkRowKey(interbankRate));
			Field[] attributes = InterbankRate.class.getDeclaredFields();
			Field[] columns = InterbankRateDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(interbankRate,
						attributes[i].getName())) != null)
					p.add(INFO_FAM, (byte[]) columns[i - 1].get(this),
							DaoHelper.toBytes(attributes[i], value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.RateDao#add(com.finaxys.rd.
	 * marketdataprovider.domain.Stock)
	 */
	public boolean add(InterbankRate result) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(result);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.RateDao#get(char,
	 * java.lang.String, java.lang.String)
	 */
	public InterbankRate get(String name, String currency) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(name, currency);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		InterbankRate interbankRate = toInterbankRate(result);
		table.close();
		return interbankRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.RateDao#listAll()
	 */
	public List<InterbankRate> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<InterbankRate> ret = new ArrayList<InterbankRate>();
		for (Result result : results) {
			ret.add(toInterbankRate(result));
		}
		table.close();
		return ret;
	}

	public List<InterbankRate> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<InterbankRate> ret = new ArrayList<InterbankRate>();
		for (Result result : results) {
			ret.add(toInterbankRate(result));
		}
		table.close();
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.RateDao#listAllSymbols()
	 */
	public List<InterbankRate> listAll() throws IOException {
//		HTableInterface table = connection.getTable(TABLE_NAME);
//		ResultScanner results = table.getScanner(DaoHelper.mkScan());
//		List<String> sp = new ArrayList<String>();
//		StringBuilder sb = new StringBuilder();
//		int i = 0;
//		for (Result result : results) {
//			sb.append("\"" + Bytes.toString(result.getValue(INFO_FAM, NAME_COL)) + "\",");
//			i++;
//
//			// i = 1000 => no response
//			if (i == 500) {
//				sp.add(sb.toString().replaceAll(",$", ""));
//				sb.setLength(0);
//				i = 0;
//			}
//		}
		return null;
	}
	
	
	public List<InterbankRate> list(char provider) throws IOException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}
}
