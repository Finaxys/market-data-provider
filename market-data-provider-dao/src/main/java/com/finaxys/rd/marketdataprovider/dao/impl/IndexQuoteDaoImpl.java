/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.IndexQuote;
import com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuoteDaoImpl.
 */
public class IndexQuoteDaoImpl extends HBaseBasicDaoImpl<IndexQuote> implements IndexQuoteDao {

	static Logger logger = Logger.getLogger(IndexQuoteDaoImpl.class);

	public IndexQuoteDaoImpl() {
		super(IndexQuote.class);
	}

}
