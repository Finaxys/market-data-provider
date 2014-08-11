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
import com.finaxys.rd.dataextraction.domain.StockQuote;
import com.finaxys.rd.marketdataprovider.dao.StockQuoteDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class StockQuoteDaoImpl.
 */
public class StockQuoteDaoImpl implements StockQuoteDao {

	
	/** The table name. */
	@Value("#{'${dao.stockQuote.tableName:stock_quote}'.bytes}")
	private byte[] TABLE_NAME;

	/** The values fam. */
	@Value("#{'${dao.stockQuote.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;



	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructors

	/**
	 * Instantiates a new stock quote dao impl.
	 */
	public StockQuoteDaoImpl() {
		super();

	}

	/**
	 * Instantiates a new stock quote dao impl.
	 * 
	 * @param connection
	 *            the connection
	 */
	public StockQuoteDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers

	/**
	 * To stock.
	 * 
	 * @param result
	 *            the result
	 * @return the stock quote
	 */
	private StockQuote toStock(Result result) {
		StockQuote stockQuote = null;

		try {
			stockQuote = new StockQuote();
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = stockQuote.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(VALUES_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(stockQuote,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stockQuote;
	}

	/**
	 * Mk row key.
	 * 
	 * @param stockQuote
	 *            the stock quote
	 * @return the byte[]
	 */
	private byte[] mkRowKey(StockQuote stockQuote) {
		return mkRowKey(stockQuote.getSource(), stockQuote.getExchSymb(), stockQuote.getSymbol(), stockQuote.getQuoteDateTime().getMillis(), stockQuote.getDataType());
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
	 * @param ts
	 *            the ts
	 * @param dataType
	 *            the data type
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char source, String exchSymb, String symbol, Long ts, DataType dataType) {
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte sourceByte = (byte) source;
		byte typeByte = dataType.getTByte();
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 + 2 * DaoHelper.MD5_LENGTH + timestamp.length];

		int offset = 0;
		offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, timestamp, 0, timestamp.length);
		offset = Bytes.putByte(rowkey, offset, sourceByte);

		return rowkey;
	}

	/**
	 * Mk get.
	 * 
	 * @param provider
	 *            the source
	 * @param exchSymb
	 *            the exch symb
	 * @param symbol
	 *            the symbol
	 * @param ts
	 *            the ts
	 * @param dataType
	 *            the data type
	 * @return the gets the
	 */
	private Get mkGet(char source, String exchSymb, String symbol, Long ts, DataType dataType) {
		Get g = new Get(mkRowKey(source, exchSymb, symbol, ts, dataType));
		return g;
	}

	/**
	 * Mk put.
	 * 
	 * @param stockQuote
	 *            the stock quote
	 * @return the put
	 */
	private Put mkPut(StockQuote stockQuote) {
		Put p = null;
		try {
			p = new Put(mkRowKey(stockQuote));
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = stockQuote.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			Object value;
			for (Field attribute : attributes) {

				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(stockQuote,
						attribute.getName())) != null)
					p.add(VALUES_FAM, attribute.getName().getBytes(),
							DaoHelper.toBytes(attribute, value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.StockQuoteDao#add(com.finaxys.rd
	 * .dataextraction.domain.StockQuote)
	 */
	public boolean add(StockQuote stockQuote) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(stockQuote);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.StockQuoteDao#get(char,
	 * java.lang.String, java.lang.String, java.lang.Long,
	 * com.finaxys.rd.dataextraction.msg.Document.DataType)
	 */
	public StockQuote get(char source, String exchSymb, String symbol, Long ts, DataType dataType) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(source, exchSymb, symbol, ts, dataType);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		StockQuote stockQuote = toStock(result);
		table.close();
		return stockQuote;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.StockQuoteDao#list(java.lang.String
	 * )
	 */
	public List<StockQuote> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<StockQuote> ret = new ArrayList<StockQuote>();
		for (Result result : results) {
			ret.add(toStock(result));
		}
		table.close();
		return ret;
	}

	public List<StockQuote> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<StockQuote> ret = new ArrayList<StockQuote>();
		for (Result result : results) {
			ret.add(toStock(result));
		}
		table.close();
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.StockQuoteDao#listAll()
	 */
	public List<StockQuote> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<StockQuote> ret = new ArrayList<StockQuote>();
		for (Result result : results) {
			ret.add(toStock(result));
		}
		table.close();
		return ret;
	}

	@Override
	public List<StockQuote> list(String symbol, String exchSymb,
			DataType dataType, DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StockQuote> list(String symbol, String exchSymb,
			DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StockQuote> list(String symbol, DataType dataType,
			DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StockQuote> list(String symbol, DataType dataType)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StockQuote> list(DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
