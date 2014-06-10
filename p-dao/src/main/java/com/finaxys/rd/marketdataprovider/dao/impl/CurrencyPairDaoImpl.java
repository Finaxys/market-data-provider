/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
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

import com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao;
import com.finaxys.rd.marketdataprovider.domain.CurrencyPair;

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
	 * @param r the r
	 * @return the currency pair
	 */
	private CurrencyPair toCurrencyPair(Result r) {
		return null;// ToDo
	}

	/**
	 * Mk row key.
	 *
	 * @param currencyPair the currency pair
	 * @return the byte[]
	 */
	private byte[] mkRowKey(CurrencyPair currencyPair) {
		return mkRowKey(currencyPair.getSymbol());
	}

	/**
	 * Mk row key.
	 *
	 * @param symbol the symbol
	 * @return the byte[]
	 */
	private byte[] mkRowKey(String symbol) {
		return symbol.getBytes();
	}

	/**
	 * Mk get.
	 *
	 * @param symbol the symbol
	 * @return the gets the
	 */
	private Get mkGet(String symbol) {
		Get g = new Get(mkRowKey(symbol));
		return g;
	}

	/**
	 * Mk put.
	 *
	 * @param currencyPair the currency pair
	 * @return the put
	 */
	private Put mkPut(CurrencyPair currencyPair) {
		Put p = new Put(mkRowKey(currencyPair));
		p.add(INFO_FAM, SYMBOL_COL, Bytes.toBytes(currencyPair.getSymbol()));
		p.add(INFO_FAM, BASE_COL, Bytes.toBytes(currencyPair.getBaseCurrency()));
		p.add(INFO_FAM, QUOTE_COL, Bytes.toBytes(currencyPair.getQuoteCurrency()));
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
	 * @see com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao#add(com.finaxys.rd.marketdataprovider.domain.CurrencyPair)
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
	public CurrencyPair get(String symbol) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(symbol);
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
		ResultScanner results = table.getScanner(mkScan(prefix));
		List<CurrencyPair> ret = new ArrayList<CurrencyPair>();
		for (Result r : results) {
			ret.add(toCurrencyPair(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao#listAll()
	 */
	public List<CurrencyPair> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<CurrencyPair> ret = new ArrayList<CurrencyPair>();
		for (Result r : results) {
			ret.add(toCurrencyPair(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao#listAllSymbols()
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

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		HConnection hConnection = HConnectionManager.createConnection(HBaseConfiguration.create());
		CurrencyPairDao d = new CurrencyPairDaoImpl(hConnection);
		System.out.println(d.listAllSymbols().size());
		System.out.println(d.listAllSymbols().get(0));
	}
}
