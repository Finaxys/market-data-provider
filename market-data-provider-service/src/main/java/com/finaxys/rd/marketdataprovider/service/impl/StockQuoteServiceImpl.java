/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.StockQuote;
import com.finaxys.rd.marketdataprovider.dao.StockQuoteDao;
import com.finaxys.rd.marketdataprovider.service.StockQuoteService;

// TODO: Auto-generated Javadoc
/**
 * The Class StockQuoteServiceImpl.
 */
public class StockQuoteServiceImpl implements StockQuoteService {

	@Autowired
	private StockQuoteDao dao;

	@Override
	public boolean add(StockQuote stockQuote) {
	return dao.add(stockQuote);
	}


}