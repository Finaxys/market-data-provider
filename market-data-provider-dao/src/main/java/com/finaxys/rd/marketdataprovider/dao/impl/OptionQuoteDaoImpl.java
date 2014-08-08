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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.dataextraction.domain.OptionQuote;
import com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionQuoteDaoImpl.
 */
public class OptionQuoteDaoImpl implements OptionQuoteDao {


	/** The symbol col. */
	@Value("#{'${dao.optionQuote.symbolCol:symbol}'.bytes}")
	private byte[] SYMBOL_COL;
	
	/** The exch symb col. */
	@Value("#{'${dao.optionQuote.dataTypeCol:dt}'.bytes}")
	private byte[] dataType_COL;
	
	/** The source col. */
	@Value("#{'${dao.optionQuote.sourceCol:source}'.bytes}")
	private byte[] SOURCE_COL;

	/** The optionChain col. */
	@Value("#{'${dao.optionQuote.optionChainCol:optionChain}'.bytes}")
	private byte[] OPTIONCHAIN_COL;
	

	@Value("#{'${dao.optionQuote.tsCol:ts}'.bytes}")
	private byte[] TS_COL;
	
	/** The change col. */
	@Value("#{'${dao.optionQuote.optionTypeCol:ot}'.bytes}")
	private byte[] OPTION_TYPE_COL;
	
	/** The days low col. */
	@Value("#{'${dao.optionQuote.priceCol:price}'.bytes}")
	private byte[] PRICE_COL;
	
	/** The days high col. */
	@Value("#{'${dao.optionQuote.changeCol:chg}'.bytes}")
	private byte[] CHANGE_COL;
	
	/** The years low col. */
	@Value("#{'${dao.optionQuote.prevCloseCol:pc}'.bytes}")
	private byte[] PREVCLOSE_COL;
	
	/** The years high col. */
	@Value("#{'${dao.optionQuote.openCol:open}'.bytes}")
	private byte[] OPEN_COL;
	
	/** The mc high col. */
	@Value("#{'${dao.optionQuote.bidCol:bid}'.bytes}")
	private byte[] BID_COL;
	
	/** The ltpo col. */
	@Value("#{'${dao.optionQuote.askCol:ask}'.bytes}")
	private byte[] ASK_COL;
	
	/** The days rang col. */
	@Value("#{'${dao.optionQuote.strikeCol:strike}'.bytes}")
	private byte[] STRIKE_COL;
	
	/** The name col. */
	@Value("#{'${dao.optionQuote.expirationCol:exp}'.bytes}")
	private byte[] EXPIRATION_COL;
	
	/** The volume col. */
	@Value("#{'${dao.optionQuote.volumeCol:volume}'.bytes}")
	private byte[] VOLUME_COL;
	
	/** The volume col. */
	@Value("#{'${dao.optionQuote.openInterestCol:opInt}'.bytes}")
	private byte[] OPEN_INTEREST_COL;
	

	/** The type col. */
	@Value("#{'${dao.optionQuote.typeCol:t}'.bytes}")
	private byte[] TYPE_COL;

	/** The table name. */
	@Value("#{'${dao.optionQuote.tableName:option_quote}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The values fam. */
	@Value("#{'${dao.optionQuote.valuesFam:v}'.bytes}")
	private byte[] VALUES_FAM;
	
	/** The long length. */
	@Value("${longLength:8}")
	private  int longLength;

	/** The connection. */
	@Autowired
	private HConnection connection;

	// Constructors
	
	/**
	 * Instantiates a new option quote dao impl.
	 */
	public OptionQuoteDaoImpl() {
		super();

	}

	/**
	 * Instantiates a new option quote dao impl.
	 *
	 * @param connection the connection
	 */
	public OptionQuoteDaoImpl(HConnection connection) {
		super();
		this.connection = connection;

	}

	// Helpers

	/**
	 * To option.
	 *
	 * @param result the result
	 * @return the option quote
	 */
	private OptionQuote toOption(Result result) {
		OptionQuote optionQuote = null;

		try {
			optionQuote = new OptionQuote();
			Field[] attributes = OptionQuote.class.getDeclaredFields();
			Field[] columns = OptionQuoteDaoImpl.class.getDeclaredFields();

			byte[] value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = result.getValue(VALUES_FAM, (byte[]) columns[i- 1].get(this))) != null)
					PropertyUtils.setSimpleProperty(optionQuote, attributes[i].getName(), DaoHelper.getTypedValue(attributes[i], value));
					
			

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return optionQuote;
	}

	/**
	 * Mk row key.
	 *
	 * @param optionQuote the option quote
	 * @return the byte[]
	 */
	private byte[] mkRowKey(OptionQuote optionQuote) {
		return mkRowKey(optionQuote.getSource(), optionQuote.getSymbol(), optionQuote.getTs().getMillis(),
				optionQuote.getDataType());
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
	private byte[] mkRowKey(char provider, String symbol, Long ts, DataType dataType) {
		byte provByte = (byte) provider;
		byte typeByte = dataType.getTByte();
		byte[] symbHash = DaoHelper.md5sum(symbol);
		byte[] timestamp = Bytes.toBytes(ts);
		byte[] rowkey = new byte[2 +  DaoHelper.MD5_LENGTH + longLength];

		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, typeByte);
		offset = Bytes.putBytes(rowkey, offset, symbHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, timestamp, 0, longLength);
		offset = Bytes.putByte(rowkey, offset, provByte);

		return rowkey;
	}


	private Get mkGet(char provider, String symbol, Long ts, DataType dataType) {
		Get g = new Get(mkRowKey(provider, symbol, ts, dataType));
		return g;
	}

	/**
	 * Mk put.
	 *
	 * @param optionQuote the option quote
	 * @return the put
	 */
	private Put mkPut(OptionQuote optionQuote) {
		Put p = null;
		try {
			p = new Put(mkRowKey(optionQuote));
			Field[] attributes = OptionQuote.class.getDeclaredFields();
			Field[] columns = OptionQuoteDaoImpl.class.getDeclaredFields();

			Object value;
			for (int i = 1; i < attributes.length; i++) {

				if ((value = PropertyUtils.getSimpleProperty(optionQuote,
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
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao#add(com.finaxys.rd.dataextraction.domain.OptionQuote)
	 */
	public boolean add(OptionQuote optionQuote) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(optionQuote);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao#get(char, java.lang.String, java.lang.String, java.lang.Long, com.finaxys.rd.dataextraction.msg.Document.DataType)
	 */
	public OptionQuote get(char provider, String symbol, Long ts, DataType dataType) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, symbol, ts, dataType);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		OptionQuote optionQuote = toOption(result);
		table.close();
		return optionQuote;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao#list(java.lang.String)
	 */
	public List<OptionQuote> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<OptionQuote> ret = new ArrayList<OptionQuote>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}
	
	public List<OptionQuote> list(byte[] prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan(prefix));
		List<OptionQuote> ret = new ArrayList<OptionQuote>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao#listAll()
	 */
	public List<OptionQuote> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(DaoHelper.mkScan());
		List<OptionQuote> ret = new ArrayList<OptionQuote>();
		for (Result result : results) {
			ret.add(toOption(result));
		}
		table.close();
		return ret;
	}

	@Override
	public List<OptionQuote> list(String symbol, String exchSymb,
			DataType dataType, DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OptionQuote> list(String symbol, String exchSymb,
			DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OptionQuote> list(String symbol, DataType dataType,
			DateTime start, DateTime end) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OptionQuote> list(String symbol, DataType dataType)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OptionQuote> list(DataType dataType) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
