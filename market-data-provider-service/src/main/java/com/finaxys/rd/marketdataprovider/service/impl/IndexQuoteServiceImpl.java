/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.IndexQuote;
import com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao;
import com.finaxys.rd.marketdataprovider.service.IndexQuoteService;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuoteServiceImpl.
 */
public class IndexQuoteServiceImpl implements IndexQuoteService {


	@Autowired
	private IndexQuoteDao dao;

	@Override
	public boolean add(IndexQuote index) {
		return dao.add(index);
	}

}
