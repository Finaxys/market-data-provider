/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao;
import com.finaxys.rd.marketdataprovider.service.CurrencyPairService;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyPairServiceImpl.
 */
public class CurrencyPairServiceImpl implements CurrencyPairService {


	@Autowired
	private CurrencyPairDao dao;

	@Override
	public boolean add(CurrencyPair currencyPair) {
		return dao.add(currencyPair);
	}

}
