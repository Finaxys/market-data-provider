/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.IndexQuote;
import com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuoteDaoImpl.
 */
public class IndexQuoteDaoImpl extends AbstractBasicDao<IndexQuote> implements IndexQuoteDao {

	private static Logger logger = Logger.getLogger(IndexQuoteDaoImpl.class);

	public IndexQuoteDaoImpl() {
		super();
	}

	public IndexQuoteDaoImpl(HConnection connection) {
		super(connection);
		// TODO Auto-generated constructor stub
	}

}
