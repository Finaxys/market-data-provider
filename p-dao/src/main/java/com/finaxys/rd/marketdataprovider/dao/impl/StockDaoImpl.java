/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.stereotype.Repository;

import com.finaxys.rd.marketdataprovider.dao.StockDao;
import com.finaxys.rd.marketdataprovider.domain.Stock;
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
	 * @param connection the connection
	 */
	public StockDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers

	/**
	 * To stock summary.
	 *
	 * @param r the r
	 * @return the stock
	 */
	private Stock toStockSummary(Result r) {
		return null;// ToDo
	}

	/**
	 * Mk row key.
	 *
	 * @param stock the stock
	 * @return the byte[]
	 */
	private byte[] mkRowKey(Stock stock) {
		return mkRowKey(stock.getProvider(), stock.getExchSymb(), stock.getSymbol());
	}

	/**
	 * Mk row key.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String exchSymb, String symbol) {
		byte provByte = (byte) provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] rowkey = new byte[2 * DaoHelper.MD5_LENGTH + 1];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, provByte);
		offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);

		return rowkey;
	}

	/**
	 * Mk get.
	 *
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @return the gets the
	 */
	private Get mkGet(char provider, String exchSymb, String symbol) {
		Get g = new Get(mkRowKey(provider, exchSymb, symbol));
		return g;
	}

	/**
	 * Mk put.
	 *
	 * @param stock the stock
	 * @return the put
	 */
	private Put mkPut(Stock stock) {
		Put p = new Put(mkRowKey(stock));

		// Load all fields in the class (private included)
		Field[] stockAttributes = Stock.class.getDeclaredFields();
		Field[] stockCols = StockDaoImpl.class.getDeclaredFields();
		int i = 0;
		Object value;
		for (Field field : stockAttributes) {

			try {
				if ((value = PropertyUtils.getSimpleProperty(stock, field.getName())) != null) {
					if (field.getType().equals(String.class))
						p.add(INFO_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes((String) value));
					if (field.getType().equals(Date.class))
						p.add(INFO_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes(((Date) value).getTime()));
					if (field.getType().equals(Integer.class))
						p.add(INFO_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes((Integer) value));
					if (field.getType().equals(char.class))
						p.add(INFO_FAM, (byte[]) stockCols[i].get(this), Bytes.toBytes((Character) value));

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

	// CRUD

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#add(com.finaxys.rd.marketdataprovider.domain.Stock)
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

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#get(char, java.lang.String, java.lang.String)
	 */
	public Stock get(char provider, String exchSymb, String symbol) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, exchSymb, symbol);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		Stock stock = toStockSummary(result);
		table.close();
		return stock;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#list(java.lang.String)
	 */
	public List<Stock> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(mkScan(prefix));
		List<Stock> ret = new ArrayList<Stock>();
		for (Result r : results) {
			ret.add(toStockSummary(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#listAll()
	 */
	public List<Stock> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<Stock> ret = new ArrayList<Stock>();
		for (Result r : results) {
			ret.add(toStockSummary(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.StockDao#listAllSymbols()
	 */
	public List<String> listAllSymbols() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<String> sp = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Result r : results) {
			sb.append("\"" + Bytes.toString(r.getValue(INFO_FAM, SYMBOL_COL)) + "\",");
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

}
