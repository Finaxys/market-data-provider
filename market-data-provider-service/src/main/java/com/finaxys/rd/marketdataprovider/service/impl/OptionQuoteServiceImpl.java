/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.OptionQuote;
import com.finaxys.rd.marketdataprovider.dao.OptionQuoteDao;
import com.finaxys.rd.marketdataprovider.service.OptionQuoteService;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionQuoteServiceImpl.
 */
public class OptionQuoteServiceImpl implements OptionQuoteService {

	@Autowired
	private OptionQuoteDao dao;

	@Override
	public boolean add(OptionQuote optionQuote) {
		return dao.add(optionQuote);
	}

	
}