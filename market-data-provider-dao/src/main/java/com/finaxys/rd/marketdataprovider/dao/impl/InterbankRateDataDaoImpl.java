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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.dataextraction.domain.InterbankRateData;
import com.finaxys.rd.marketdataprovider.dao.InterbankRateDataDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

public class InterbankRateDataDaoImpl implements InterbankRateDataDao{

	@Value("#{'${dao.interbankRateData.symbolCol:symbol}'.bytes}")
	private byte[] SYMBOL_COL;

	@Value("#{'${dao.interbankRateData.currencyCol:currency}'.bytes}")
	private byte[] currency_COL;

	@Value("#{'${dao.interbankRateData.bucketCol:bucket}'.bytes}")
	private byte[] bucket_COL;

	@Value("#{'${dao.interbankRateData.tsCol:ts}'.bytes}")
	private byte[] TS_COL;

	@Value("#{'${dao.interbankRateData.valueCol:value}'.bytes}")
	private byte[] value_COL;
	
	@Value("#{'${dao.interbankRateData.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;
	
	/** The type col. */
	@Value("#{'${dao.interbankRateData.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;

	@Value("#{'${dao.interbankRateData.tableName:interbank_rate_data}'.bytes}")
	private byte[] TABLE_NAME;

	@Value("#{'${dao.interbankRateData.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;

	@Value("${longLength:8}")
	private int longLength;

	@Autowired
	private HConnection connection;

	// Constructors

	public InterbankRateDataDaoImpl() {
		super();

	}


	public InterbankRateDataDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers


	private InterbankRateData toInterbankRateData(Result result) {
		InterbankRateData interbankRateData = null;

		try {
			interbankRateData = new InterbankRateData();
			Field[] attributes = InterbankRateData.class.getDeclaredFields();
			Field[] columns = InterbankRateDataDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i < attributes.length; i++) {


				if ((value = result.getValue(VALUES_FAM, (byte[]) columns[i- 1].get(this))) != null)
					PropertyUtils.setSimpleProperty(interbankRateData, attributes[i].getName(), DaoHelper.getTypedValue(attributes[i], value));
					

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return interbankRateData;
	}


	private byte[] mkRowKey(InterbankRateData interbankRateData) {
		return mkRowKey(interbankRateData.getSource(), interbankRateData.getSymbol(), interbankRateData.getCurrency(),interbankRateData.getBucket(), interbankRateData.getTs().getMillis(), interbankRateData.getDataType());
	}


	private byte[] mkRowKey(char source, String symbol, String currency,  String bucket,Long ts, DataType dataType) {
		
		byte sourceByte = (byte) source;
		byte typeByte = dataType.getTByte();
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] currencyHash = DaoHelper.md5sum(currency);
		byte[] bucketHash = DaoHelper.md5sum(bucket);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 + 3 * DaoHelper.MD5_LENGTH + longLength];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putBytes(rowkey, offset, currencyHash, 0, DaoHelper.MD5_LENGTH);
		offset = Bytes.putBytes(rowkey, offset, bucketHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, timestamp, 0, longLength);
		offset = Bytes.putByte(rowkey, offset, sourceByte);

		return rowkey;
	}

	private Get mkGet(char source, String symbol, String currency, String bucket, Long ts, DataType dataType) {
		Get g = new Get(mkRowKey(source, symbol, currency, bucket, ts, dataType));
		return g;
	}


	private Put mkPut(InterbankRateData interbankRateData) {
		Put p = null;
		try {
			p = new Put(mkRowKey(interbankRateData));
			Field[] attributes = InterbankRateData.class.getDeclaredFields();
			Field[] columns = InterbankRateDataDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(interbankRateData,
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
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.InterbankRateDataDao#add(com.finaxys.rd
	 * .dataextraction.domain.InterbankRateData)
	 */
	public boolean add(InterbankRateData interbankRateData) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(interbankRateData);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.InterbankRateDataDao#get(char,
	 * java.lang.String, java.lang.String, java.lang.Long,
	 * com.finaxys.rd.dataextraction.msg.Document.DataType)
	 */
	public InterbankRateData get(char source, String symbol, String currency, String bucket, Long ts, DataType dataType) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(source, symbol, currency, bucket, ts, dataType);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		InterbankRateData interbankRateData = toInterbankRateData(result);
		table.close();
		return interbankRateData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.finaxys.rd.marketdataprovider.dao.InterbankRateDataDao#list(java.lang.String
	 * )
	 */
	public List<InterbankRateData> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<InterbankRateData> ret = new ArrayList<InterbankRateData>();
		for (Result result : results) {
			ret.add(toInterbankRateData(result));
		}
		table.close();
		return ret;
	}

	public List<InterbankRateData> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<InterbankRateData> ret = new ArrayList<InterbankRateData>();
		for (Result result : results) {
			ret.add(toInterbankRateData(result));
		}
		table.close();
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.finaxys.rd.marketdataprovider.dao.InterbankRateDataDao#listAll()
	 */
	public List<InterbankRateData> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<InterbankRateData> ret = new ArrayList<InterbankRateData>();
		for (Result result : results) {
			ret.add(toInterbankRateData(result));
		}
		table.close();
		return ret;
	}


	@Override
	public List<InterbankRateData> list(String symbol, String currency,
			DataType dataType, DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<InterbankRateData> list(String symbol, String currency,
			DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<InterbankRateData> list(String symbol, DataType dataType,
			DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<InterbankRateData> list(String symbol, DataType dataType)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<InterbankRateData> list(DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
