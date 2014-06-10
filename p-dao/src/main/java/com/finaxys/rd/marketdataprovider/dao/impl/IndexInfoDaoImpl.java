/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.finaxys.rd.marketdataprovider.dao.IndexInfoDao;
import com.finaxys.rd.marketdataprovider.domain.IndexInfo;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexInfoDaoImpl.
 */
public class IndexInfoDaoImpl implements IndexInfoDao {

	/** The symbol col. */
	@Value("#{'${dao.index.symbolCol:symbol}'.bytes}")
	private byte[] SYMBOL_COL;
	
	/** The name col. */
	@Value("#{'${dao.index.nameCol:name}'.bytes}")
	private byte[] NAME_COL;
	
	/** The exch symb col. */
	@Value("#{'${dao.index.exchSymbCol:exchSymb}'.bytes}")
	private byte[] EXCH_SYMB_COL;
	
	/** The provider col. */
	@Value("#{'${dao.index.providerCol:provider}'.bytes}")
	private byte[] PROVIDER_COL;

	/** The table name. */
	@Value("#{'${dao.index.tableName:index_info}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The info fam. */
	@Value("#{'${dao.index.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;

	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructors
	
	/**
	 * Instantiates a new index info dao impl.
	 */
	public IndexInfoDaoImpl() {
		super();
	}
	
	/**
	 * Instantiates a new index info dao impl.
	 *
	 * @param connection the connection
	 */
	public IndexInfoDaoImpl(HConnection connection) {
		super();
		this.connection = connection;
	}

	// Helpers

	/**
	 * To index info.
	 *
	 * @param r the r
	 * @return the index info
	 */
	private IndexInfo toIndexInfo(Result r) {
		return null;// ToDo
	}

	/**
	 * Mk row key.
	 *
	 * @param index the index
	 * @return the byte[]
	 */
	private byte[] mkRowKey(IndexInfo index) {
		return mkRowKey(index.getProvider(), index.getExchSymb(), index.getSymbol());
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
		byte provByte = (byte)provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] rowkey = new byte[1 + 2 * DaoHelper.MD5_LENGTH]; 

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
	 * @param indexInfo the index info
	 * @return the put
	 */
	private Put mkPut(IndexInfo indexInfo) {
		Put p = new Put(mkRowKey(indexInfo));
		p.add(INFO_FAM, SYMBOL_COL, Bytes.toBytes(indexInfo.getSymbol()));
		p.add(INFO_FAM, EXCH_SYMB_COL, Bytes.toBytes(indexInfo.getExchSymb()));
		p.add(INFO_FAM, PROVIDER_COL, Bytes.toBytes(indexInfo.getProvider()));
		p.add(INFO_FAM, NAME_COL, Bytes.toBytes(indexInfo.getName()));
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
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexInfoDao#add(com.finaxys.rd.marketdataprovider.domain.IndexInfo)
	 */
	public boolean add(IndexInfo indexInfo) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(indexInfo);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexInfoDao#get(char, java.lang.String, java.lang.String)
	 */
	public IndexInfo get(char provider, String exchSymb, String symbol) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, exchSymb, symbol);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		IndexInfo index = toIndexInfo(result);
		table.close();
		return index;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexInfoDao#list(java.lang.String)
	 */
	public List<IndexInfo> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan(prefix));
		List<IndexInfo> ret = new ArrayList<IndexInfo>();
		for (Result r : results) {
			ret.add(toIndexInfo(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexInfoDao#listAll()
	 */
	public List<IndexInfo> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<IndexInfo> ret = new ArrayList<IndexInfo>();
		for (Result r : results) {
			ret.add(toIndexInfo(r));
		}
		table.close();
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexInfoDao#listAllSymbols()
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
			if (i == 100) {
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
