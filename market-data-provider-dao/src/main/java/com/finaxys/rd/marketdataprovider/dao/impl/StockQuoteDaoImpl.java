/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.StockQuote;
import com.finaxys.rd.marketdataprovider.dao.StockQuoteDao;

// TODO: Auto-generated Javadoc
/**
 * The Class StockQuoteDaoImpl.
 */
public class StockQuoteDaoImpl extends AbstractBasicDao<StockQuote> implements StockQuoteDao {

	private static Logger logger = Logger.getLogger(StockQuoteDaoImpl.class);

	public StockQuoteDaoImpl() {
		super();
	}

	public StockQuoteDaoImpl(HConnection connection) {
		super(connection);
	}

}
