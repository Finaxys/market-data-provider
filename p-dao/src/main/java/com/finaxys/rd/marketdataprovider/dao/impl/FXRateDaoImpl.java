/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.finaxys.rd.marketdataprovider.dao.FXRateDao;
import com.finaxys.rd.marketdataprovider.domain.FXRate;

// TODO: Auto-generated Javadoc
/**
 * The Class FXRateDaoImpl.
 */
public class FXRateDaoImpl implements FXRateDao{

	/** The symbol col. */
	@Value("#{'${dao.fxRate.symbolCol:s}'.bytes}")
	private byte[] SYMBOL_COL;
	
	/** The rate col. */
	@Value("#{'${dao.fxRate.rateCol:r}'.bytes}")
	private byte[] RATE_COL;
	
	/** The ask col. */
	@Value("#{'${dao.fxRate.askCol:a}'.bytes}")
	private byte[] ASK_COL;
	
	/** The bid col. */
	@Value("#{'${dao.fxRate.bidCol:b}'.bytes}")
	private byte[] BID_COL;
	
	/** The provider col. */
	@Value("#{'${dao.fxRate.providerCol:p}'.bytes}")
	private byte[] PROVIDER_COL;
	
	/** The ts col. */
	@Value("#{'${dao.fxRate.tsCol:ts}'.bytes}")
	private byte[] TS_COL;
	
	/** The type col. */
	@Value("#{'${dao.fxRate.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;

	/** The table name. */
	@Value("#{'${dao.fxRate.tableNme:fx_rate}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The values fam. */
	@Value("#{'${dao.fxRate.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;

	/** The long length. */
	@Value("${longLength:8}")
	private  int longLength;

	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructors
	
	/**
	 * Instantiates a new FX rate dao impl.
	 */
	public FXRateDaoImpl() {
		super();
	}
	
	/**
	 * Instantiates a new FX rate dao impl.
	 *
	 * @param connection the connection
	 */
	public FXRateDaoImpl(HConnection connection) {
		super();
		this.connection = connection;
	}

	// Helpers

	/**
	 * To currency pair.
	 *
	 * @param r the r
	 * @return the FX rate
	 */
	private FXRate toCurrencyPair(Result r) {
		return null;// ToDo
	}

	/**
	 * Mk row key.
	 *
	 * @param fxRate the fx rate
	 * @return the byte[]
	 */
	private byte[] mkRowKey(FXRate fxRate) {
		return mkRowKey(fxRate.getSymbol(), fxRate.getTs(), fxRate.getDataType());
	}

	/**
	 * Mk row key.
	 *
	 * @param symbol the symbol
	 * @param ts the ts
	 * @param dataType the data type
	 * @return the byte[]
	 */
	private byte[] mkRowKey(String symbol, Long ts, DataType dataType) {

		byte typeByte= dataType.getTByte();
		byte[] symbBytes = Bytes.toBytes(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[1 + symbBytes.length + longLength]; 

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, symbBytes, 0, symbBytes.length);
		Bytes.putBytes(rowkey, offset, timestamp, 0, longLength);
		
		return rowkey;
	}



	/**
	 * Mk put.
	 *
	 * @param fxRate the fx rate
	 * @return the put
	 */
	private Put mkPut(FXRate fxRate) {
		Put p = new Put(mkRowKey(fxRate));
		p.add(VALUES_FAM, SYMBOL_COL, Bytes.toBytes(fxRate.getSymbol()));
		p.add(VALUES_FAM, RATE_COL, Bytes.toBytes(fxRate.getRate()));
		p.add(VALUES_FAM, ASK_COL, Bytes.toBytes(fxRate.getAsk()));
		p.add(VALUES_FAM, BID_COL, Bytes.toBytes(fxRate.getBid()));
		p.add(VALUES_FAM, PROVIDER_COL, Bytes.toBytes(fxRate.getProvider()));
		p.add(VALUES_FAM, TS_COL, Bytes.toBytes(fxRate.getTs()));
		p.add(VALUES_FAM, TYPE_COL, Bytes.toBytes(fxRate.getDataType().getName()));
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
	
	/**
	 * Mk scan.
	 *
	 * @param start the start
	 * @param end the end
	 * @return the scan
	 */
	private Scan mkScan(byte[] start, byte[] end) {
		Scan scan = new Scan(start, end);
		return scan;
	}

	
	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.FXRateDao#add(com.finaxys.rd.marketdataprovider.domain.FXRate)
	 */
	public boolean add(FXRate stock) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(stock);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.FXRateDao#list(java.lang.String)
	 */
	public List<FXRate> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan(prefix));
		List<FXRate> ret = new ArrayList<FXRate>();
		for (Result r : results) {
			ret.add(toCurrencyPair(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.FXRateDao#listAll()
	 */
	public List<FXRate> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<FXRate> ret = new ArrayList<FXRate>();
		for (Result r : results) {
			ret.add(toCurrencyPair(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.FXRateDao#list(byte[], byte[])
	 */
	public List<FXRate> list(byte[] start, byte[] end) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan(start, end));
		List<FXRate> ret = new ArrayList<FXRate>();
		for (Result r : results) {
			ret.add(toCurrencyPair(r));
		}
		table.close();
		return ret;
	}


}
