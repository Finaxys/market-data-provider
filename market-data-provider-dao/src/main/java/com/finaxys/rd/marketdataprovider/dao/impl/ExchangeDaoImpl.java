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
import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.marketdataprovider.dao.ExchangeDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class ExchangeDaoImpl.
 */
public class ExchangeDaoImpl implements ExchangeDao {

	
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
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = exchange.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(INFO_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(exchange,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

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
		return mkRowKey(exchange.getProvider(), exchange.getSourceSymbol(),
				exchange.getSymbol());
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
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = exchange.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			Object value;
			for (Field attribute : attributes) {

				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(exchange,
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
