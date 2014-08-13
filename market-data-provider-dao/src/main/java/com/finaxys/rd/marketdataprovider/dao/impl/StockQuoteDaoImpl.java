/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.StockQuote;
import com.finaxys.rd.marketdataprovider.dao.StockQuoteDao;

// TODO: Auto-generated Javadoc
/**
 * The Class StockQuoteDaoImpl.
 */
public class StockQuoteDaoImpl extends HBaseBasicDaoImpl<StockQuote> implements StockQuoteDao {

	static Logger logger = Logger.getLogger(StockQuoteDaoImpl.class);

	public StockQuoteDaoImpl() {
		super(StockQuote.class);
	}

}
