/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.OptionQuote;
import com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionQuoteDaoImpl.
 */
public class OptionQuoteDaoImpl extends AbstractBasicDao<OptionQuote> implements OptionQuoteDao {

	private static Logger logger = Logger.getLogger(OptionQuoteDaoImpl.class);

	public OptionQuoteDaoImpl() {
		super();
	}

	public OptionQuoteDaoImpl(HConnection connection) {
		super(connection);
	}
	
}
