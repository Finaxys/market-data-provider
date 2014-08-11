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
import com.finaxys.rd.dataextraction.domain.Index;
import com.finaxys.rd.marketdataprovider.dao.IndexDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexDaoImpl.
 */
public class IndexDaoImpl implements IndexDao {

	
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
	 * Instantiates a new index dao impl.
	 */
	public IndexDaoImpl() {
		super();
	}
	
	/**
	 * Instantiates a new index dao impl.
	 *
	 * @param connection the connection
	 */
	public IndexDaoImpl(HConnection connection) {
		super();
		this.connection = connection;
	}

	// Helpers

	/**
	 * To index.
	 *
	 * @param result the result
	 * @return the index
	 */
	private Index toIndex(Result result) {
		Index index = null;

		try {
			index = new Index();
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = index.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(INFO_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(index,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

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
	 * @param index the index
	 * @return the put
	 */
	private Put mkPut(Index index) {
		Put p = null;
		try {
			p = new Put(mkRowKey(index));
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = index.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			Object value;
			for (Field attribute : attributes) {

				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(index,
						attribute.getName())) != null)
					p.add(INFO_FAM, attribute.getName().getBytes(),
							DaoHelper.toBytes(attribute, value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}


	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexDao#add(com.finaxys.rd.dataextraction.domain.Index)
	 */
	public boolean add(Index index) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(index);
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
