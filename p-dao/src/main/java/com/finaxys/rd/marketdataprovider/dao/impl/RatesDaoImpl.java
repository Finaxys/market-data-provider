package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Value;

import com.finaxys.rd.marketdataprovider.dao.RatesDao;
import com.finaxys.rd.marketdataprovider.domain.Rate;
import com.finaxys.rd.marketdataprovider.domain.Stock;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

public class RatesDaoImpl implements RatesDao {
	
	/** The name col. */
	@Value("#{'${dao.rate.nameCol:n}'.bytes}")
	private byte[] NAME_COL;
	
	/** The currency col. */
	@Value("#{'${dao.rate.currencyCol:c}'.bytes}")
	private byte[] CURRENCY_COL;
	
	/** The table name. */
	@Value("#{'${dao.rate.tableName:rate}'.bytes}")
	private byte[] TABLE_NAME;
	
	/** The info fam. */
	@Value("#{'${dao.rate.infoFam:i}'.bytes}")
	private byte[] INFO_FAM;
	
	
	private HConnection connection;
	
	// Constructor
		/**
		 * Instantiates a new stock dao impl.
		 */
		public RatesDaoImpl() {
			super();
		}
	
		/**
		 * Instantiates a new stock dao impl.
		 *
		 * @param connection the connection
		 */
	RatesDaoImpl(HConnection connection)
	{
		super();
		this.connection = connection;
		
	}
	
	// Helpers
	/**
	 * To stock summary.
	 *
	 * @param r the r
	 * @return the stock
	 */
		private Rate toRate(Result r) {
			
			return null;// ToDo
		}

		
		/**
		 * Mk row key.
		 *
		 * @param stock the stock
		 * @return the byte[]
		 */
		private byte[] mkRowKey(Rate rate) {
			return mkRowKey(rate.getName(), rate.getCurrency());
		}

		private byte[] mkRowKey(String exchSymb, String symbol) {
			byte[] nameHash = DaoHelper.md5sum(exchSymb);
			byte[] currencyHash = DaoHelper.md5sum(symbol);
			byte[] rowkey = new byte[2 * DaoHelper.MD5_LENGTH + 1]; 

			int offset = 0;
			offset = Bytes.putBytes(rowkey, offset, nameHash, 0, DaoHelper.MD5_LENGTH);
			Bytes.putBytes(rowkey, offset, currencyHash, 0, DaoHelper.MD5_LENGTH);

			return rowkey;
		}

		private Get mkGet(String exchSymb, String symbol) {
			Get g = new Get(mkRowKey(exchSymb, symbol));
			return g;
		}

		private Put mkPut(Rate stock) {
			Put p = new Put(mkRowKey(stock));

			// Load all fields in the class (private included)
			Field[] rateAttributes = Rate.class.getDeclaredFields();
			Field[] rateCols = RatesDaoImpl.class.getDeclaredFields();
			int i = 0;
			Object value;
			for (Field field : rateAttributes) {

				try {
					if ((value = PropertyUtils.getSimpleProperty(stock, field.getName())) != null) {
						if (field.getType().equals(String.class))
							p.add(INFO_FAM, (byte[]) rateCols[i].get(null), Bytes.toBytes((String) value));
						if (field.getType().equals(Date.class))
							p.add(INFO_FAM, (byte[]) rateCols[i].get(null), Bytes.toBytes(((String) value)));
						if (field.getType().equals(char.class))
							p.add(INFO_FAM, (byte[]) rateCols[i].get(null), Bytes.toBytes((Character) value));
				
					}
					i++;

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return p;
		}

		private Scan mkScan() {
			Scan scan = new Scan();
			return scan;
		}

		private Scan mkScan(String prefix) {
			Scan scan = new Scan();
			org.apache.hadoop.hbase.filter.RegexStringComparator prefixFilter = new org.apache.hadoop.hbase.filter.RegexStringComparator(
					"^" + prefix + "*");
			RowFilter rowFilter = new RowFilter(CompareOp.EQUAL, prefixFilter);
			scan.setFilter(rowFilter);

			return scan;
		}
		
		/* (non-Javadoc)
		 * @see com.finaxys.rd.marketdataprovider.dao.RateDao#add(com.finaxys.rd.marketdataprovider.domain.Stock)
		 */
	public boolean add(Rate r) {
		try {
			HTableInterface table = connection.getTable(TABLE_NAME);
			Put p = mkPut(r);
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
	 * @see com.finaxys.rd.marketdataprovider.dao.RateDao#get(char, java.lang.String, java.lang.String)
	 */
	public Rate get(String name, String currency) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		Get g = mkGet(name, currency);
		Result result = table.get(g);
		if (result.isEmpty())
			return null;
		Rate r = toRate(result);
		table.close();
		return r;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.RateDao#listAll()
	 */
	public List<Rate> list(String prefix) throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);

		ResultScanner results = table.getScanner(mkScan(prefix));
		List<Rate> ret = new ArrayList<Rate>();
		for (Result r : results) {
			ret.add(toRate(r));
		}
		table.close();
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.dao.RateDao#listAllSymbols()
	 */
	public List<Rate> listAll() throws IOException {
		HTableInterface table = connection.getTable(TABLE_NAME);
		ResultScanner results = table.getScanner(mkScan());
		List<String> sp = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Result r : results) {
			sb.append("\"" + Bytes.toString(r.getValue(INFO_FAM, NAME_COL)) + "\",");
			i++;

			// i = 1000 => no response
			if (i == 500) {
				sp.add(sb.toString().replaceAll(",$", ""));
				sb.setLength(0);
				i = 0;
			}
	}
}
