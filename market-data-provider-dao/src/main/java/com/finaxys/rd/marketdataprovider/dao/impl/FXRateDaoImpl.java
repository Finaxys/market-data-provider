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
import com.finaxys.rd.dataextraction.domain.FXRate;
import com.finaxys.rd.marketdataprovider.dao.FXRateDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class FXRateDaoImpl.
 */
public class FXRateDaoImpl implements FXRateDao {

	/** The symbol col. */
	@Value("#{'${dao.fxRate.symbolCol:s}'.bytes}")
	private byte[] SYMBOL_COL;

	/** The rate col. */
	@Value("#{'${dao.fxRate.rateCol:result}'.bytes}")
	private byte[] RATE_COL;

	/** The ask col. */
	@Value("#{'${dao.fxRate.askCol:a}'.bytes}")
	private byte[] ASK_COL;

	/** The bid col. */
	@Value("#{'${dao.fxRate.bidCol:b}'.bytes}")
	private byte[] BID_COL;

	/** The provider col. */
	@Value("#{'${dao.fxRate.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;

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
	private int longLength;

	@Value("${symbLength:48}")
	private int symbLength;

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
	 * @param connection
	 *            the connection
	 */
	public FXRateDaoImpl(HConnection connection) {
		super();
		this.connection = connection;
	}

	// Helpers

	/**
	 * To currency pair.
	 *
	 * @param result
	 *            the result
	 * @return the FX rate
	 */
	private FXRate toFXRate(Result result) {
		FXRate fxRate = null;

		try {
			fxRate = new FXRate();
			Field[] attributes = FXRate.class.getDeclaredFields();
			Field[] columns = FXRateDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = result.getValue(VALUES_FAM,
						(byte[]) columns[i - 1].get(this))) != null)
					PropertyUtils.setSimpleProperty(fxRate,
							attributes[i].getName(),
							DaoHelper.getTypedValue(attributes[i], value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fxRate;
	}

	/**
	 * Mk row key.
	 *
	 * @param fxRate
	 *            the fx rate
	 * @return the byte[]
	 */
	private byte[] mkRowKey(FXRate fxRate) {
		return mkRowKey(fxRate.getSymbol(), fxRate.getTs().getMillis(),
				fxRate.getDataType(), fxRate.getSource());
	}

	/**
	 * Mk row key.
	 *
	 * @param symbol
	 *            the symbol
	 * @param ts
	 *            the ts
	 * @param dataType
	 *            the data type
	 * @return the byte[]
	 */
	private byte[] mkRowKey(String symbol, Long ts, DataType dataType,
			char provider) {

		byte typeByte = dataType.getTByte();
		byte provByte = (byte) provider;
		byte[] symbBytes = Bytes.toBytes(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 + symbLength + longLength];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, symbBytes, 0, symbLength);
		Bytes.putBytes(rowkey, offset, timestamp, 0, longLength);
		offset = Bytes.putByte(rowkey, offset, provByte);

		return rowkey;
	}

	/**
	 * Mk put.
	 *
	 * @param fxRate
	 *            the fx rate
	 * @return the put
	 */
	private Put mkPut(FXRate fxRate) {
		Put p = null;
		try {
			p = new Put(mkRowKey(fxRate));
			Field[] attributes = FXRate.class.getDeclaredFields();
			Field[] columns = FXRateDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(fxRate,
						attributes[i].getName())) != null)
					p.add(VALUES_FAM, (byte[]) columns[i - 1].get(this),
							DaoHelper.toBytes(attributes[i], value));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return p;
	}



	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.FXRateDao#add(com.finaxys.rd.
	 * dataextraction.domain.FXRate)
	 */
	public boolean add(FXRate fxRate) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(fxRate);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.FXRateDao#list(java.lang.String)
	 */
	public List<FXRate> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<FXRate> ret = new ArrayList<FXRate>();
		for (Result result : results) {
			ret.add(toFXRate(result));
		}
		table.close();
		return ret;
	}

	public List<FXRate> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<FXRate> ret = new ArrayList<FXRate>();
		for (Result result : results) {
			ret.add(toFXRate(result));
		}
		table.close();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.FXRateDao#listAll()
	 */
	public List<FXRate> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<FXRate> ret = new ArrayList<FXRate>();
		for (Result result : results) {
			ret.add(toFXRate(result));
		}
		table.close();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.FXRateDao#list(byte[], byte[])
	 */
	public List<FXRate> list(byte[] start, byte[] end) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(start, end));
		List<FXRate> ret = new ArrayList<FXRate>();
		for (Result result : results) {
			ret.add(toFXRate(result));
		}
		table.close();
		return ret;
	}

	@Override
	public List<FXRate> list(String symbol, DataType dataType, DateTime startDate,
			DateTime endDate) throws IOException {
		byte typeByte = dataType.getTByte();
		byte[] symbBytes = Bytes.toBytes(symbol);
		
		final byte[] start = new byte[]{};
		byte[] end  = new byte[]{};
		
		int offset = 0;
		offset = Bytes.putByte(start, offset, typeByte);
		offset = Bytes.putBytes(start, offset, symbBytes, 0, symbLength);
		Bytes.putBytes(start, offset, Bytes.toBytes(endDate.getMillis()), 0, longLength);
		

		offset = 0;
		offset = Bytes.putByte(start, offset, typeByte);
		offset = Bytes.putBytes(start, offset, symbBytes, 0, symbLength);
		Bytes.putBytes(start, offset, Bytes.toBytes(startDate.getMillis()), 0, longLength);
		
		return list(start, end);
	}

	@Override
	public List<FXRate> list(String symbol, DataType dataType)
			throws IOException {
		byte typeByte = dataType.getTByte();
		byte[] symbBytes = Bytes.toBytes(symbol);
		
		final byte[] start = new byte[]{};
		byte[] end  = new byte[]{};
		int offset = 0;
		
		offset = Bytes.putByte(start, offset, typeByte);
		Bytes.putBytes(start, offset, symbBytes, 0, symbLength);
		
		end = Arrays.copyOf(start, start.length);
		end[start.length - 1]++;
		return list(start, end);
	}

	@Override
	public List<FXRate> list(DataType dataType) throws IOException {
		byte typeByte = dataType.getTByte();

		final byte[] start;
		final byte[] end;

		start = new byte[] { typeByte };
		end = Arrays.copyOf(start, start.length);
		end[start.length - 1]++;
		return list(start, end);

	}

}
