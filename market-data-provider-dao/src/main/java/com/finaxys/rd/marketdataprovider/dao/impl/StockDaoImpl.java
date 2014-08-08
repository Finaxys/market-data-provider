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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.dataextraction.domain.Stock;
import com.finaxys.rd.marketdataprovider.dao.StockDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class StockDaoImpl.
 */
public class StockDaoImpl implements StockDao {

	/** The symbol col. */
	@Value("#{'${dao.stock.symbolCol:symbol}'.bytes}")
	private byte[] SYMBOL_COL;

	/** The exch symb col. */
	@Value("#{'${dao.stock.exchSymbCol:exchSymb}'.bytes}")
	private byte[] EXCH_SYMB_COL;

	/** The provider col. */
	@Value("#{'${dao.stock.providerCol:provider}'.bytes}")
	private byte[] PROVIDER_COL;

	@Value("#{'${dao.stock.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;

	/** The cn col. */
	@Value("#{'${dao.stock.companyNameCol:cn}'.bytes}")
	private byte[] CN_COL;

	/** The start col. */
	@Value("#{'${dao.stock.startCol:start}'.bytes}")
	private byte[] START_COL;

	/** The end col. */
	@Value("#{'${dao.stock.endCol:end}'.bytes}")
	private byte[] END_COL;

	/** The sector col. */
	@Value("#{'${dao.stock.sectorCol:sector}'.bytes}")
	private byte[] SECTOR_COL;

	/** The industry col. */
	@Value("#{'${dao.stock.industryCol:industry}'.bytes}")
	private byte[] INDUSTRY_COL;

	/** The fte col. */
	@Value("#{'${dao.stock.fullTimeEmployeesCol:fte}'.bytes}")
	private byte[] FTE_COL;

	@Value("#{'${dao.stock.inputDateCol:inputDate}'.bytes}")
	private byte[] INPUTDATE_COL;

	@Value("#{'${dao.stock.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;
	
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
			Field[] attributes = Stock.class.getDeclaredFields();
			Field[] columns = StockDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = result.getValue(INFO_FAM,
						(byte[]) columns[i - 1].get(this))) != null)
					PropertyUtils.setSimpleProperty(stock,
							attributes[i].getName(),
							DaoHelper.getTypedValue(attributes[i], value));
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
		return mkRowKey(stock.getProvider(), stock.getSource(),stock.getExchSymb(), stock.getSymbol(), stock.getInputDate().toDateTimeAtStartOfDay().getMillis());
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
			Field[] attributes = Stock.class.getDeclaredFields();
			Field[] columns = StockDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(stock,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#listAllSymbols()
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
			if (i == 250) {
				sp.add(sb.toString().replaceAll(",$", ""));
				sb.setLength(0);
				i = 0;
			}

		}
		sp.add(sb.toString().replaceAll(",$", ""));
		table.close();
		return sp;
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
