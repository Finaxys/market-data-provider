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
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.marketdataprovider.dao.ExchangeDao;
import com.finaxys.rd.marketdataprovider.domain.Exchange;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class ExchangeDaoImpl.
 */
public class ExchangeDaoImpl implements ExchangeDao {


	/** The mic col. */
	@Value("#{'${dao.exchange.micCol:mic}'.bytes}")
	private byte[] MIC_COL;
	
	/** The symbol col. */
	@Value("#{'${dao.exchange.symbolCol:symbol}'.bytes}")
	private byte[] SYMBOL_COL;
	
	/** The suffix col. */
	@Value("#{'${dao.exchange.suffixCol:suffix}'.bytes}")
	private byte[] SUFFIX_COL;
	
	/** The provider col. */
	@Value("#{'${dao.exchange.providerCol:provider}'.bytes}")
	private byte[] PROVIDER_COL;
	
	/** The name col. */
	@Value("#{'${dao.exchange.nameCol:name}'.bytes}")
	private byte[] NAME_COL;
	
	/** The type col. */
	@Value("#{'${dao.exchange.typeCol:type}'.bytes}")
	private byte[] TYPE_COL;
	
	/** The continent col. */
	@Value("#{'${dao.exchange.continentCol:continent}'.bytes}")
	private byte[] CONTINENT_COL;
	
	/** The country col. */
	@Value("#{'${dao.exchange.countryCol:country}'.bytes}")
	private byte[] COUNTRY_COL;
	
	/** The currency col. */
	@Value("#{'dao.exchange.currencyCol:currency}'.bytes}")
	private byte[] CURRENCY_COL;
	
	/** The open time col. */
	@Value("#{'${dao.exchange.openTimeCol:ot}'.bytes}")
	private byte[] OPEN_TIME_COL;
	
	/** The close time col. */
	@Value("#{'${dao.exchange.closeTimeCol:ct}'.bytes}")
	private byte[] CLOSE_TIME_COL;
	
	/** The status col. */
	@Value("#{'${dao.exchange.statusCol:status}'.bytes}")
	private byte[] STATUS_COL;

	/** The table name. */
	@Value("#{'${dao.exchange.tableName:exchange}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The info fam. */
	@Value("#{'${dao.exchange.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;


	/** The connection. */
	@Autowired
	private HConnection connection;
    
	// Constructors
	
	/**
	 * Instantiates a new exchange dao impl.
	 */
	public ExchangeDaoImpl() {
	}
	
	/**
	 * Instantiates a new exchange dao impl.
	 *
	 * @param connection the connection
	 */
	public ExchangeDaoImpl(HConnection connection) {
		this.connection = connection;
	}
   
	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public HConnection getConnection() {
		return connection;
	}

	/**
	 * Sets the connection.
	 *
	 * @param connection the new connection
	 */
	public void setConnection(HConnection connection) {
		this.connection = connection;
	}

	// Helpers
	
	/**
	 * To exchange.
	 *
	 * @param r the r
	 * @return the exchange
	 */
	private Exchange toExchange(Result r) {
		return new Exchange(r.getValue(INFO_FAM, MIC_COL), r.getValue(INFO_FAM, SYMBOL_COL), r.getValue(INFO_FAM, SUFFIX_COL), r.getValue(INFO_FAM,
				PROVIDER_COL), r.getValue(INFO_FAM, NAME_COL), r.getValue(INFO_FAM, TYPE_COL), r.getValue(INFO_FAM,
				CONTINENT_COL), r.getValue(INFO_FAM, COUNTRY_COL), r.getValue(INFO_FAM, CURRENCY_COL), r.getValue(
				INFO_FAM, OPEN_TIME_COL), r.getValue(INFO_FAM, CLOSE_TIME_COL), r.getValue(INFO_FAM, STATUS_COL));

	}

	/**
	 * Mk row key.
	 *
	 * @param exchange the exchange
	 * @return the byte[]
	 */
	private byte[] mkRowKey(Exchange exchange) {
		return mkRowKey(exchange.getProvider(), exchange.getSuffix(), exchange.getMic());
	}

	/**
	 * Mk row key.
	 *
	 * @param provider the provider
	 * @param suffix the suffix
	 * @param mic the mic
	 * @return the byte[]
	 */
	private byte[] mkRowKey(char provider, String suffix, String mic) {
		byte provBytes = (byte)provider;
		byte[] suffixHash = DaoHelper.md5sum(suffix);
		byte[] micb = Bytes.toBytes(mic);
		byte[] rowkey = new byte[1 + DaoHelper.MD5_LENGTH + micb.length]; 
		int offset = 0;
		offset = Bytes.putByte(rowkey, offset, provBytes);
		offset = Bytes.putBytes(rowkey, offset, suffixHash, 0, DaoHelper.MD5_LENGTH);
		Bytes.putBytes(rowkey, offset, micb, 0, micb.length);

		return rowkey;
	}

	/**
	 * Mk get.
	 *
	 * @param provider the provider
	 * @param suffix the suffix
	 * @param mic the mic
	 * @return the gets the
	 */
	private Get mkGet(char provider, String suffix, String mic) {
		Get g = new Get(mkRowKey(provider, suffix, mic));
		return g;
	}

	/**
	 * Mk put.
	 *
	 * @param exchange the exchange
	 * @return the put
	 */
	private Put mkPut(Exchange exchange) {
		Put p = new Put(mkRowKey(exchange));

		// Load all fields in the class (private included)
		Field[] exch_attributes = Exchange.class.getDeclaredFields();
		Field[] exch_cols = ExchangeDaoImpl.class.getDeclaredFields();
		int i = 0;
		Object value;
		for (Field field : exch_attributes) {

			try {
				if ((value = PropertyUtils.getSimpleProperty(exchange, field.getName())) != null) {
					if (field.getType().equals(String.class))
						p.add(INFO_FAM, (byte[]) exch_cols[i].get(this), Bytes.toBytes((String) value));
					if (field.getType().equals(Boolean.class))
						p.add(INFO_FAM, (byte[]) exch_cols[i].get(this), Bytes.toBytes((Boolean) value));
					if (field.getType().equals(Long.class))
						p.add(INFO_FAM, (byte[]) exch_cols[i].get(this), Bytes.toBytes((Long) value));
					if (field.getType().equals(Integer.class))
						p.add(INFO_FAM, (byte[]) exch_cols[i].get(this), Bytes.toBytes((Integer) value));
					if (field.getType().equals(char.class))
						p.add(INFO_FAM, (byte[]) exch_cols[i].get(this), Bytes.toBytes((Character) value));
			
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

	// CRUDmi	
	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.ExchangeDao#add(com.finaxys.rd.marketdataprovider.domain.Exchange)
	 */
	public boolean add(Exchange exchange) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(exchange);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.ExchangeDao#get(char, java.lang.String, java.lang.String)
	 */
	public Exchange get(char provider, String suffix, String mic) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(provider, suffix, mic);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		Exchange exchange = toExchange(result);
		table.close();
		return exchange;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.ExchangeDao#list(java.lang.String)
	 */
	public List<Exchange> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan(prefix));
		List<Exchange> ret = new ArrayList<Exchange>();
		for (Result r : results) {
			ret.add(toExchange(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.ExchangeDao#listAll()
	 */
	public List<Exchange> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<Exchange> ret = new ArrayList<Exchange>();
		for (Result r : results) {
			ret.add(toExchange(r));
		}
		table.close();
		return ret;
	}

}
