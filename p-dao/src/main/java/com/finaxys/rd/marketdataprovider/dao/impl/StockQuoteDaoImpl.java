/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.msg.Document.DataType;
import com.finaxys.rd.marketdataprovider.dao.StockQuoteDao;
import com.finaxys.rd.marketdataprovider.domain.StockQuote;
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
	
	/** The provider col. */
	@Value("#{'${dao.stockQuote.providerCol:provider}'.bytes}")
	private byte[] PROVIDER_COL;
	
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

	/** The table name. */
	@Value("#{'${dao.stockQuote.tableName:stock_quote}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The values fam. */
	@Value("#{'${dao.stockQuote.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;
	
	/** The long length. */
	@Value("${longLength:8}")
	private  int longLength;

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
	 * @param connection the connection
	 */
	public StockQuoteDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers

	/**
	 * To stock.
	 *
	 * @param r the r
	 * @return the stock quote
	 */
	private StockQuote toStock(Result r) {
		return null;// ToDo
	}

	/**
	 * Mk row key.
	 *
	 * @param stockQuote the stock quote
	 * @return the byte[]
	 */
	private byte[] mkRowKey(StockQuote stockQuote) {
		return mkRowKey(stockQuote.getProvider(), stockQuote.getExchSymb(), stockQuote.getSymbol(), stockQuote.getTs(),
				stockQuote.getDataType());
	}

	/**
	 * Mk row key.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @param ts the ts
	 * @param dataType the data type
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String exchSymb, String symbol, Long ts, DataType dataType) {
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte provByte = (byte) provider;
		byte typeByte = dataType.getTByte();
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 + 2 * DaoHelper.MD5_LENGTH + longLength];

		int offset = 0;
		offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putByte(rowkey, offset, provByte);
		Bytes.putBytes(rowkey, offset, timestamp, 0, longLength);

		return rowkey;
	}

	/**
	 * Mk get.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @param ts the ts
	 * @param dataType the data type
	 * @return the gets the
	 */
	private Get mkGet(char provider, String exchSymb, String symbol, Long ts, DataType dataType) {
		Get g = new Get(mkRowKey(provider, exchSymb, symbol, ts, dataType));
		return g;
	}

	/**
	 * Mk put.
	 *
	 * @param stockQuote the stock quote
	 * @return the put
	 */
	private Put mkPut(StockQuote stockQuote) {
		Put p = new Put(mkRowKey(stockQuote));

		// Load all fields in the class (private included)
		Field[] stockAttributes = StockQuote.class.getDeclaredFields();
		Field[] stockCols = StockQuoteDaoImpl.class.getDeclaredFields();
		int i = 0;
		Object value;
		for (Field field : stockAttributes) {

			try {
				if ((value = PropertyUtils.getSimpleProperty(stockQuote, field.getName())) != null) {
					if (field.getType().equals(String.class))
						p.add(VALUES_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes((String) value));
					else if (field.getType().equals(BigDecimal.class))
						p.add(VALUES_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes((BigDecimal) value));
					else if (field.getType().equals(Integer.class))
						p.add(VALUES_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes((Integer) value));
					else if (field.getType().equals(Long.class))
						p.add(VALUES_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes((Long) value));
					if (field.getType().equals(char.class))
						p.add(VALUES_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes((Character) value));
				}
				i++;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return p;
	}

	/**
	 * Mk scan.
	 *
	 * @return the scan
	 */
	private Scan mkScan() {
		Scan scan = new Scan();
		return scan;
	}

	/**
	 * Mk scan.
	 *
	 * @param prefix the prefix
	 * @return the scan
	 */
	private Scan mkScan(String prefix) {
		Scan scan = new Scan();
		org.apache.hadoop.hbase.filter.RegexStringComparator prefixFilter = new org.apache.hadoop.hbase.filter.RegexStringComparator(
				"^" + prefix + "*");
		RowFilter rowFilter = new RowFilter(CompareOp.EQUAL, prefixFilter);
		scan.setFilter(rowFilter);

		return scan;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockQuoteDao#add(com.finaxys.rd.marketdataprovider.domain.StockQuote)
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

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockQuoteDao#get(char, java.lang.String, java.lang.String, java.lang.Long, com.finaxys.rd.dataextraction.msg.Document.DataType)
	 */
	public StockQuote get(char provider, String exchSymb, String symbol, Long ts, DataType dataType) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, exchSymb, symbol, ts, dataType);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		StockQuote stockQuote = toStock(result);
		table.close();
		return stockQuote;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockQuoteDao#list(java.lang.String)
	 */
	public List<StockQuote> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan(prefix));
		List<StockQuote> ret = new ArrayList<StockQuote>();
		for (Result r : results) {
			ret.add(toStock(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockQuoteDao#listAll()
	 */
	public List<StockQuote> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<StockQuote> ret = new ArrayList<StockQuote>();
		for (Result r : results) {
			ret.add(toStock(r));
		}
		table.close();
		return ret;
	}
}
