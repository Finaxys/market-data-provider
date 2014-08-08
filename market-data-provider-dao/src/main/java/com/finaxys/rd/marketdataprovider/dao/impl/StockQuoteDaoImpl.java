/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.dataextraction.domain.StockQuote;
import com.finaxys.rd.marketdataprovider.dao.StockQuoteDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class StockQuoteDaoImpl.
 */
public class StockQuoteDaoImpl implements StockQuoteDao {

	/** The symbol col. */
	@Value("#{'${dao.stockQuote.symbolCol:symbol}'.bytes}")
	private byte[] SYMBOL_COL;

	/** The exch symb col. */
	@Value("#{'${dao.stockQuote.exchSymbCol:exchSymb}'.bytes}")
	private byte[] EXCH_SYMB_COL;

	/** The source col. */
	@Value("#{'${dao.stockQuote.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;

	/** The adv col. */
	@Value("#{'${dao.stockQuote.avdCol:avd}'.bytes}")
	private byte[] ADV_COL;

	/** The change col. */
	@Value("#{'${dao.stockQuote.changeCol:change}'.bytes}")
	private byte[] CHANGE_COL;

	/** The days low col. */
	@Value("#{'${dao.stockQuote.daysLowCol:dl}'.bytes}")
	private byte[] DAYS_LOW_COL;

	/** The days high col. */
	@Value("#{'${dao.stockQuote.daysHighCol:dh}'.bytes}")
	private byte[] DAYS_HIGH_COL;

	/** The years low col. */
	@Value("#{'${dao.stockQuote.yearsLowCol:yl}'.bytes}")
	private byte[] YEARS_LOW_COL;

	/** The years high col. */
	@Value("#{'${dao.stockQuote.yearsHighCol:yh}'.bytes}")
	private byte[] YEARS_HIGH_COL;

	/** The mc high col. */
	@Value("#{'${dao.stockQuote.mcHighCol:mch}'.bytes}")
	private byte[] MC_HIGH_COL;

	/** The ltpo col. */
	@Value("#{'${dao.stockQuote.ltpoCol:ltpo}'.bytes}")
	private byte[] LTPO_COL;

	/** The days rang col. */
	@Value("#{'${dao.stockQuote.daysRangCol:dr}'.bytes}")
	private byte[] DAYS_RANG_COL;

	/** The name col. */
	@Value("#{'${dao.stockQuote.nameCol:n}'.bytes}")
	private byte[] NAME_COL;

	/** The volume col. */
	@Value("#{'${dao.stockQuote.volumeCol:v}'.bytes}")
	private byte[] VOLUME_COL;

	/** The ts col. */
	@Value("#{'${dao.stockQuote.tsCol:ts}'.bytes}")
	private byte[] TS_COL;

	/** The type col. */
	@Value("#{'${dao.stockQuote.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;

	/** The open col. */
	@Value("#{'${dao.stockQuote.openCol:open}'.bytes}")
	private byte[] OPEN_COL;

	/** The close col. */
	@Value("#{'${dao.stockQuote.closeCol:close}'.bytes}")
	private byte[] CLOSE_COL;

	/** The adjClose col. */
	@Value("#{'${dao.stockQuote.adjCloseCol:adjClose}'.bytes}")
	private byte[] ADJCLOSE_COL;

	/** The table name. */
	@Value("#{'${dao.stockQuote.tableName:stock_quote}'.bytes}")
	private byte[] TABLE_NAME;

	/** The values fam. */
	@Value("#{'${dao.stockQuote.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;

	/** The long length. */
	@Value("${longLength:8}")
	private int longLength;

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
			Field[] attributes = StockQuote.class.getDeclaredFields();
			Field[] columns = StockQuoteDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = result.getValue(VALUES_FAM, (byte[]) columns[i- 1].get(this))) != null)
					PropertyUtils.setSimpleProperty(stockQuote, attributes[i].getName(), DaoHelper.getTypedValue(attributes[i], value));
					

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
		return mkRowKey(stockQuote.getSource(), stockQuote.getExchSymb(), stockQuote.getSymbol(), stockQuote.getTs().getMillis(), stockQuote.getDataType());
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
		byte[] rowkey = new byte[2 + 2 * DaoHelper.MD5_LENGTH + longLength];

		int offset = 0;
		offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, timestamp, 0, longLength);
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
			Field[] attributes = StockQuote.class.getDeclaredFields();
			Field[] columns = StockQuoteDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(stockQuote,
						attributes[i].getName())) != null)
					p.add(VALUES_FAM, (byte[]) columns[i - 1].get(this),
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
