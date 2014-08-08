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
import com.finaxys.rd.dataextraction.domain.IndexQuote;
import com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuoteDaoImpl.
 */
public class IndexQuoteDaoImpl implements IndexQuoteDao {

	/** The symbol co. */
	@Value("#{'${dao.indexQuote.symbolCol:s}'.bytes}")
	private byte[] SYMBOL_COL;

	@Value("#{'${dao.indexQuote.exchSymbCol:exchSymb}'.bytes}")
	private byte[] EXCHSYMB_COL;
	
	/** The provider col. */
	@Value("#{'${dao.indexQuote.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;
	
	
	/** The ltpo col. */
	@Value("#{'${dao.indexQuote.ltpoCol:ltpo}'.bytes}")
	private byte[] LTPO_COL;
	
	/** The ts col. */
	@Value("#{'${dao.indexQuote.tsCol:ts}'.bytes}")
	private byte[] TS_COL;
	
	/** The change col. */
	@Value("#{'${dao.indexQuote.changeCol:ch}'.bytes}")
	private byte[] CHANGE_COL;
	
	/** The open col. */
	@Value("#{'${dao.indexQuote.openCol:o}'.bytes}")
	private byte[] OPEN_COL;
	
	/** The days high col. */
	@Value("#{'${dao.indexQuote.daysHighCol:dh}'.bytes}")
	private byte[] DAYS_HIGH_COL;
	
	/** The days low col. */
	@Value("#{'${dao.indexQuote.daysLowCol:dl}'.bytes}")
	private byte[] DAYS_LOW_COL;
	
	/** The volume col. */
	@Value("#{'${dao.indexQuote.volumeCol:v}'.bytes}")
	private byte[] VOLUME_COL;
	
	/** The type col. */
	@Value("#{'${dao.indexQuote.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;

	/** The table name. */
	@Value("#{'${dao.indexQuote.tableName:index_quote}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The values fam. */
	@Value("#{'${dao.indexQuote.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;
	
	/** The long length. */
	@Value("${longLength:8}")
	private  int longLength;

	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructors
	
	/**
	 * Instantiates a new index quote dao impl.
	 */
	public IndexQuoteDaoImpl() {
		super();
	}
	
	/**
	 * Instantiates a new index quote dao impl.
	 *
	 * @param connection the connection
	 */
	public IndexQuoteDaoImpl(HConnection connection) {
		super();
		this.connection = connection;
	}

	// Helpers

	/**
	 * To index quote.
	 *
	 * @param result the result
	 * @return the index quote
	 */
	private IndexQuote toIndexQuote(Result result) {
		IndexQuote indexQuote = null;

		try {
			indexQuote = new IndexQuote();
			Field[] attributes = IndexQuote.class.getDeclaredFields();
			Field[] columns = IndexQuoteDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = result.getValue(VALUES_FAM, (byte[]) columns[i- 1].get(this))) != null)
					PropertyUtils.setSimpleProperty(indexQuote, attributes[i].getName(), DaoHelper.getTypedValue(attributes[i], value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indexQuote;
	}

	/**
	 * Mk row key.
	 *
	 * @param index the index
	 * @return the byte[]
	 */
	private byte[] mkRowKey(IndexQuote index) {
		return mkRowKey(index.getSource(), index.getSymbol(), index.getTs().getMillis(), index.getDataType());
	}

	/**
	 * Mk row key.
	 *
	 * @param provider the provider
//	 * @param exchSymb the exch symb
	 * @param symbol the symbol
	 * @param ts the ts
	 * @param dataType the data type
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String symbol, Long ts, DataType dataType) {
		byte provByte = (byte)provider;
//		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte typeByte = dataType.getTByte();
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 +   DaoHelper.MD5_LENGTH + longLength];

		int offset = 0;
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putByte(rowkey, offset, typeByte);
//		offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, timestamp, 0, longLength);
		offset = Bytes.putByte(rowkey, offset, provByte);

		return rowkey;
	}

	/**
	 * Mk put.
	 *
	 * @param index the index
	 * @return the put
	 */
	private Put mkPut(IndexQuote index) {
		Put p = null;
		try {
			p = new Put(mkRowKey(index));
			Field[] attributes = IndexQuote.class.getDeclaredFields();
			Field[] columns = IndexQuoteDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(index,
						attributes[i].getName())) != null)
					p.add(VALUES_FAM, (byte[]) columns[i - 1].get(this),
							DaoHelper.toBytes(attributes[i], value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}


	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao#add(com.finaxys.rd.dataextraction.domain.IndexQuote)
	 */
	public boolean add(IndexQuote index) {
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
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao#list(java.lang.String)
	 */
	public List<IndexQuote> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<IndexQuote> ret = new ArrayList<IndexQuote>();
		for (Result result : results) {
			ret.add(toIndexQuote(result));
		}
		table.close();
		return ret;
	}

	public List<IndexQuote> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<IndexQuote> ret = new ArrayList<IndexQuote>();
		for (Result result : results) {
			ret.add(toIndexQuote(result));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao#listAll()
	 */
	public List<IndexQuote> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<IndexQuote> ret = new ArrayList<IndexQuote>();
		for (Result result : results) {
			ret.add(toIndexQuote(result));
		}
		table.close();
		return ret;
	}

	@Override
	public List<IndexQuote> list(String symbol, String exchSymb,
			DataType dataType, DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IndexQuote> list(String symbol, String exchSymb,
			DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IndexQuote> list(String symbol, DataType dataType,
			DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IndexQuote> list(String symbol, DataType dataType)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IndexQuote> list(DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
