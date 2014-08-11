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
import com.finaxys.rd.dataextraction.domain.Stock;
import com.finaxys.rd.marketdataprovider.dao.StockDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class StockDaoImpl.
 */
public class StockDaoImpl implements StockDao {

	
	
	/** The table name. */
	@Value("#{'${dao.stock.tableName:stock}'.bytes}")
	private byte[] TABLE_NAME;

	/** The info fam. */
	@Value("#{'${dao.stock.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;

	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructor
	/**
	 * Instantiates a new stock dao impl.
	 */
	public StockDaoImpl() {
		super();
	}

	/**
	 * Instantiates a new stock dao impl.
	 * 
	 * @param connection
	 *            the connection
	 */
	public StockDaoImpl(HConnection connection) {
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
	private Stock toStock(Result result) {
		Stock stock = null;


		try {
			stock = new Stock();
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = stock.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(INFO_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(stock,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stock;
	}

	/**
	 * Mk row key.
	 * 
	 * @param stock
	 *            the stock
	 * @return the byte[]
	 */
	private byte[] mkRowKey(Stock stock) {
		return mkRowKey(stock.getProvider(), stock.getSource(),stock.getExchSymb(), stock.getSymbol(), stock.getInputDate().getMillis());
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
	private byte[] mkRowKey(char provider, char source, String exchSymb, String symbol, Long inputDate) {
		byte provByte = (byte) provider;
		byte sourceByte = (byte) source;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] inputDateBytes =  Bytes.toBytes(inputDate);
		byte[] rowkey = new byte[2 * DaoHelper.MD5_LENGTH + inputDateBytes.length  + 2];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, provByte);
		offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putBytes(rowkey, offset,inputDateBytes, 0, inputDateBytes.length);
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putByte(rowkey, offset, sourceByte);

		return rowkey;
	}

	/**
	 * Mk get.
	 * 
	 * @param provider
	 *            the provider
	 * @param exchSymb
	 *            the exch symb
	 * @param symbol
	 *            the symbol
	 * @return the gets the
	 */
	private Get mkGet(char provider, char source, String exchSymb, String symbol, Long inputDate) {
		Get g = new Get(mkRowKey(provider, source, exchSymb, symbol,inputDate));
		return g;
	}

	/**
	 * Mk put.
	 * 
	 * @param stock
	 *            the stock
	 * @return the put
	 */
	private Put mkPut(Stock stock) {
		Put p = null;
		try {
			p = new Put(mkRowKey(stock));
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = stock.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			Object value;
			for (Field attribute : attributes) {

				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(stock,
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
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#add(com.finaxys.rd.
	 * dataextraction.domain.Stock)
	 */
	public boolean add(Stock stock) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(stock);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#get(char,
	 * java.lang.String, java.lang.String)
	 */
	public Stock get(char provider, char source, String exchSymb, String symbol, Long inputDate) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, source, exchSymb, symbol,inputDate);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		Stock stock = toStock(result);
		table.close();
		return stock;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.StockDao#list(java.lang.String)
	 */
	public List<Stock> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<Stock> ret = new ArrayList<Stock>();
		for (Result result : results) {
			ret.add(toStock(result));
		}
		table.close();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#listAll()
	 */
	public List<Stock> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<Stock> ret = new ArrayList<Stock>();
		for (Result result : results) {
			ret.add(toStock(result));
		}
		table.close();
		return ret;
	}

	
	public List<Stock> list(char provider, String exchSymb) throws IOException {
		byte provByte = (byte) provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] prefix = new byte[DaoHelper.MD5_LENGTH + 1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);
		Bytes.putBytes(prefix, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);

		return list(prefix);

	}
	
	public List<Stock> list(char provider) throws IOException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}
	
	
}
