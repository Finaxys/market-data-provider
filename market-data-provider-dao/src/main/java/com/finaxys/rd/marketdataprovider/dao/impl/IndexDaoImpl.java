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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.Index;
import com.finaxys.rd.marketdataprovider.dao.IndexDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexDaoImpl.
 */
public class IndexDaoImpl implements IndexDao {

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
	
	/** The source col. */
	@Value("#{'${dao.index.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;
	
	/** The inputDate col. */
	@Value("#{'${dao.index.inputDateCol:inputDate}'.bytes}")
	private byte[] INPUTDATE_COL;

	@Value("#{'${dao.index.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;
	
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
	public IndexDaoImpl() {
		super();
	}
	
	/**
	 * Instantiates a new index info dao impl.
	 *
	 * @param connection the connection
	 */
	public IndexDaoImpl(HConnection connection) {
		super();
		this.connection = connection;
	}

	// Helpers

	/**
	 * To index info.
	 *
	 * @param result the result
	 * @return the index info
	 */
	private Index toIndex(Result result) {
		Index index = null;

		try {
			index = new Index();
			Field[] attributes = Index.class.getDeclaredFields();
			Field[] columns = IndexDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i< attributes.length; i++) {

				if ((value = result.getValue(INFO_FAM, (byte[]) columns[i-1].get(this))) != null)
					PropertyUtils.setSimpleProperty(index,  attributes[i].getName(), DaoHelper.getTypedValue( attributes[i], value));
					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return index;
	}

	/**
	 * Mk row key.
	 *
	 * @param index the index
	 * @return the byte[]
	 */
	private byte[] mkRowKey(Index index) {
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
	private Put mkPut(Index indexInfo) {
		Put p = null;
		try {
			p = new Put(mkRowKey(indexInfo));
			Field[] attributes = Index.class.getDeclaredFields();
			Field[] columns = IndexDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(indexInfo,
						attributes[i].getName())) != null)
					p.add(INFO_FAM, (byte[]) columns[i - 1].get(this),
							DaoHelper.toBytes(attributes[i], value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}


	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexDao#add(com.finaxys.rd.dataextraction.domain.Index)
	 */
	public boolean add(Index indexInfo) {
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
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexDao#get(char, java.lang.String, java.lang.String)
	 */
	public Index get(char provider, String exchSymb, String symbol) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, exchSymb, symbol);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		Index index = toIndex(result);
		table.close();
		return index;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexDao#list(java.lang.String)
	 */
	public List<Index> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<Index> ret = new ArrayList<Index>();
		for (Result result : results) {
			ret.add(toIndex(result));
		}
		table.close();
		return ret;
	}
	
	public List<Index> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<Index> ret = new ArrayList<Index>();
		for (Result result : results) {
			ret.add(toIndex(result));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexDao#listAll()
	 */
	public List<Index> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<Index> ret = new ArrayList<Index>();
		for (Result result : results) {
			ret.add(toIndex(result));
		}
		table.close();
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexDao#listAllSymbols()
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

			// i=1000  => yql no response  
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
	

	public List<Index> list(char provider, String exchSymb) throws IOException {
		byte provByte = (byte) provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] prefix = new byte[DaoHelper.MD5_LENGTH + 1];
	
		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);
		Bytes.putBytes(prefix, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
	
		return list(prefix);
	
	}
}
