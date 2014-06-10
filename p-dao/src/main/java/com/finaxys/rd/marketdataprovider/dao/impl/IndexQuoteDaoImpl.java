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
import com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao;
import com.finaxys.rd.marketdataprovider.domain.IndexQuote;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuoteDaoImpl.
 */
public class IndexQuoteDaoImpl implements IndexQuoteDao {

	/** The symbol co. */
	@Value("#{'${dao.indexQuote.symbolCol:s}'.bytes}")
	private byte[] SYMBOL_CO;
	
	/** The provider col. */
	@Value("#{'${dao.indexQuote.providerCol:p}'.bytes}")
	private byte[] PROVIDER_COL;
	
	/** The exch symb col. */
	@Value("#{'${dao.indexQuote.exchSymbCol:exchSymb}'.bytes}")
	private byte[] EXCH_SYMB_COL;
	
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
	 * @param r the r
	 * @return the index quote
	 */
	private IndexQuote toIndexQuote(Result r) {
		return null;// ToDo
	}

	/**
	 * Mk row key.
	 *
	 * @param index the index
	 * @return the byte[]
	 */
	private byte[] mkRowKey(IndexQuote index) {
		return mkRowKey(index.getProvider(), index.getExchSymb(), index.getSymbol(), index.getTs(), index.getDataType());
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
		byte provByte = (byte)provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte typeByte = dataType.getTByte();
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 + 2 * DaoHelper.MD5_LENGTH + longLength];

		int offset = 0;
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putByte(rowkey, offset, provByte);
		Bytes.putBytes(rowkey, offset, timestamp, 0, longLength);

		return rowkey;
	}

	/**
	 * Mk put.
	 *
	 * @param index the index
	 * @return the put
	 */
	private Put mkPut(IndexQuote index) {
		Put p = new Put(mkRowKey(index));

		// Load all fields in the class (private included)
		Field[] ind_attributes = IndexQuote.class.getDeclaredFields();
		Field[] ind_cols = IndexQuoteDaoImpl.class.getDeclaredFields();
		int i = 0;
		Object value;
		for (Field field : ind_attributes) {

			try {
				if ((value = PropertyUtils.getSimpleProperty(index, field.getName())) != null) {
					if (field.getType().equals(String.class))
						p.add(VALUES_FAM, (byte[]) ind_cols[i].get(this), Bytes.toBytes((String) value));
					if (field.getType().equals(BigDecimal.class))
						p.add(VALUES_FAM, (byte[]) ind_cols[i].get(this), Bytes.toBytes((BigDecimal) value));
					if (field.getType().equals(Long.class))
						p.add(VALUES_FAM, (byte[]) ind_cols[i].get(this), Bytes.toBytes((Long) value));
					if (field.getType().equals(Integer.class))
						p.add(VALUES_FAM, (byte[]) ind_cols[i].get(this), Bytes.toBytes((Integer) value));
					if (field.getType().equals(char.class))
						p.add(VALUES_FAM, (byte[]) ind_cols[i].get(this), Bytes.toBytes((Character) value));
						
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
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao#add(com.finaxys.rd.marketdataprovider.domain.IndexQuote)
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
		ResultScanner results = table.getScanner(mkScan(prefix));
		List<IndexQuote> ret = new ArrayList<IndexQuote>();
		for (Result r : results) {
			ret.add(toIndexQuote(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao#listAll()
	 */
	public List<IndexQuote> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<IndexQuote> ret = new ArrayList<IndexQuote>();
		for (Result r : results) {
			ret.add(toIndexQuote(r));
		}
		table.close();
		return ret;
	}
}
