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

import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.marketdataprovider.dao.ExchangeDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class ExchangeDaoImpl.
 */
public class ExchangeDaoImpl implements ExchangeDao {

	/** The mic col. */
	@Value("#{'${dao.exchange.micCol:mic}'.bytes}")
	private byte[] MIC_COL;

	/** The symbol col. */
	@Value("#{'${dao.exchange.symbolCol:symbol}'.bytes}")
	private byte[] SYMBOL_COL;

	/** The provider col. */
	@Value("#{'${dao.exchange.providerCol:provider}'.bytes}")
	private byte[] PROVIDER_COL;

	/** The symbol col. */
	@Value("#{'${dao.exchange.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;

	/** The name col. */
	@Value("#{'${dao.exchange.nameCol:name}'.bytes}")
	private byte[] NAME_COL;

	/** The type col. */
	@Value("#{'${dao.exchange.exchTypeCol:exchType}'.bytes}")
	private byte[] EXCH_TYPE_COL;

	/** The continent col. */
	@Value("#{'${dao.exchange.continentCol:continent}'.bytes}")
	private byte[] CONTINENT_COL;

	/** The country col. */
	@Value("#{'${dao.exchange.countryCol:country}'.bytes}")
	private byte[] COUNTRY_COL;

	/** The currency col. */
	@Value("#{'${dao.exchange.currencyCol:currency}'.bytes}")
	private byte[] CURRENCY_COL;

	/** The open time col. */
	@Value("#{'${dao.exchange.openTimeCol:ot}'.bytes}")
	private byte[] OPEN_TIME_COL;

	/** The close time col. */
	@Value("#{'${dao.exchange.closeTimeCol:ct}'.bytes}")
	private byte[] CLOSE_TIME_COL;

	/** The delay col. */
	@Value("#{'${dao.exchange.delayCol:delay}'.bytes}")
	private byte[] DELAY_COL;

	/** The input date col. */
	@Value("#{'${dao.exchange.inputDateCol:inputDate}'.bytes}")
	private byte[] INPUT_DATE_COL;

	@Value("#{'${dao.exchange.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;
	
	/** The table name. */
	@Value("#{'${dao.exchange.tableName:exchange}'.bytes}")
	private byte[] TABLE_NAME;

	/** The info fam. */
	@Value("#{'${dao.exchange.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;

	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructors

	/**
	 * Instantiates a new exchange dao impl.
	 */
	public ExchangeDaoImpl() {
	}

	/**
	 * Instantiates a new exchange dao impl.
	 * 
	 * @param connection
	 *            the connection
	 */
	public ExchangeDaoImpl(HConnection connection) {
		this.connection = connection;
	}

	/**
	 * Gets the connection.
	 * 
	 * @return the connection
	 */
	public HConnection getConnection() {
		return connection;
	}

	/**
	 * Sets the connection.
	 * 
	 * @param connection
	 *            the new connection
	 */
	public void setConnection(HConnection connection) {
		this.connection = connection;
	}

	// Helpers

	/**
	 * To exchange.
	 * 
	 * @param result
	 *            the result
	 * @return the exchange
	 */

	private Exchange toExchange(Result result) {

		Exchange exchange = null;

		try {
			exchange = new Exchange();
			Field[] attributes = Exchange.class.getDeclaredFields();
			Field[] columns = ExchangeDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = result.getValue(INFO_FAM,
						(byte[]) columns[i - 1].get(this))) != null)
					PropertyUtils.setSimpleProperty(exchange,
							attributes[i].getName(),
							DaoHelper.getTypedValue(attributes[i], value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exchange;
	}

	/**
	 * Mk row key.
	 * 
	 * @param exchange
	 *            the exchange
	 * @return the byte[]
	 */
	private byte[] mkRowKey(Exchange exchange) {
		return mkRowKey(exchange.getProvider(), exchange.getSymbol(),
				exchange.getMic());
	}

	/**
	 * Mk row key.
	 * 
	 * @param provider
	 *            the provider
	 * @param symbol
	 *            the symbol
	 * @param mic
	 *            the mic
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String symbol, String mic) {
		byte provBytes = (byte) provider;
		byte[] symbolHash = DaoHelper.md5sum(symbol);
		byte[] micb = Bytes.toBytes(mic);
		byte[] rowkey = new byte[1 + DaoHelper.MD5_LENGTH + micb.length];
		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, provBytes);
		offset = Bytes.putBytes(rowkey, offset, symbolHash, 0,
				DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, micb, 0, micb.length);

		return rowkey;
	}

	/**
	 * Mk get.
	 * 
	 * @param provider
	 *            the provider
	 * @param symbol
	 *            the symbol
	 * @param mic
	 *            the mic
	 * @return the gets the
	 */
	private Get mkGet(char provider, String symbol, String mic) {
		Get g = new Get(mkRowKey(provider, symbol, mic));
		return g;
	}

	/**
	 * Mk put.
	 * 
	 * @param exchange
	 *            the exchange
	 * @return the put
	 */
	private Put mkPut(Exchange exchange) {
		Put p = null;
		try {
			p = new Put(mkRowKey(exchange));
			Field[] attributes = Exchange.class.getDeclaredFields();
			Field[] columns = ExchangeDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(exchange,
						attributes[i].getName())) != null)
					p.add(INFO_FAM, (byte[]) columns[i - 1].get(this),
							DaoHelper.toBytes(attributes[i], value));

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
	 * com.finaxys.rd.marketdataprovider.dao.ExchangeDao#add(com.finaxys.rd.
	 * dataextraction.domain.Exchange)
	 */
	public boolean add(Exchange exchange) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(exchange);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.ExchangeDao#get(char,
	 * java.lang.String, java.lang.String)
	 */
	public Exchange get(char provider, String symbol, String mic)
			throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, symbol, mic);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		Exchange exchange = toExchange(result);
		table.close();
		return exchange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.ExchangeDao#list(java.lang.String)
	 */
	public List<Exchange> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<Exchange> ret = new ArrayList<Exchange>();
		for (Result result : results) {
			ret.add(toExchange(result));
		}
		table.close();
		return ret;
	}

	public List<Exchange> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<Exchange> ret = new ArrayList<Exchange>();
		for (Result result : results) {
			ret.add(toExchange(result));
		}
		table.close();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.ExchangeDao#listAll()
	 */
	public List<Exchange> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<Exchange> ret = new ArrayList<Exchange>();
		for (Result result : results) {
			ret.add(toExchange(result));
		}
		table.close();
		return ret;
	}

	public List<Exchange> list(char provider) throws IOException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}

}
