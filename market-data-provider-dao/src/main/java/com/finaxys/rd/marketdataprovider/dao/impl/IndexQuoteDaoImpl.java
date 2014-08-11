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
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.dataextraction.domain.IndexQuote;
import com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuoteDaoImpl.
 */
public class IndexQuoteDaoImpl implements IndexQuoteDao {

	/** The table name. */
	@Value("#{'${dao.indexQuote.tableName:index_quote}'.bytes}")
	private byte[] TABLE_NAME;

	/** The values fam. */
	@Value("#{'${dao.indexQuote.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;

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
	 * @param connection
	 *            the connection
	 */
	public IndexQuoteDaoImpl(HConnection connection) {
		super();
		this.connection = connection;
	}

	// Helpers

	/**
	 * To index quote.
	 *
	 * @param result
	 *            the result
	 * @return the index quote
	 */
	private IndexQuote toIndexQuote(Result result) {
		IndexQuote indexQuote = null;

		try {
			indexQuote = new IndexQuote();
			List<Field> attributes = new ArrayList<Field>();
		    Class<?> tmpClass = indexQuote.getClass();
		    while (tmpClass != null) {
		    	attributes.addAll(Arrays.asList(tmpClass.getDeclaredFields()));
		        tmpClass = tmpClass .getSuperclass();
		    }

			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(VALUES_FAM, attribute.getName().getBytes())) != null)
					PropertyUtils.setSimpleProperty(indexQuote,  attribute.getName(), DaoHelper.getTypedValue( attribute, value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indexQuote;
	}

	/**
	 * Mk row key.
	 *
	 * @param index
	 *            the index
	 * @return the byte[]
	 */
	private byte[] mkRowKey(IndexQuote index) {
		return mkRowKey(index.getSource(), index.getSymbol(), index
				.getQuoteDateTime().getMillis(), index.getDataType());
	}

	/**
	 * Mk row key.
	 *
	 * @param provider
	 *            the provider // * @param exchSymb the exch symb
	 * @param symbol
	 *            the symbol
	 * @param ts
	 *            the ts
	 * @param dataType
	 *            the data type
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String symbol, Long ts,
			DataType dataType) {
		byte provByte = (byte) provider;
		// byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte typeByte = dataType.getTByte();
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 + DaoHelper.MD5_LENGTH + timestamp.length];

		int offset = 0;
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0,
				DaoHelper.MD5_LENGTH);
		offset = Bytes.putByte(rowkey, offset, typeByte);
		// offset = Bytes.putBytes(rowkey, offset, exchSymbHash, 0,
		// DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, timestamp, 0, timestamp.length);
		offset = Bytes.putByte(rowkey, offset, provByte);

		return rowkey;
	}

	/**
	 * Mk put.
	 *
	 * @param index
	 *            the index
	 * @return the put
	 */
	private Put mkPut(IndexQuote index) {
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
					p.add(VALUES_FAM, attribute.getName().getBytes(),
							DaoHelper.toBytes(attribute, value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao#add(com.finaxys.rd
	 * .dataextraction.domain.IndexQuote)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao#list(java.lang.String
	 * )
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

	/*
	 * (non-Javadoc)
	 * 
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
