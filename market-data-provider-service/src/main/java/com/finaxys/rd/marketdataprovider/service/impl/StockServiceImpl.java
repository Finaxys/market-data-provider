/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.Stock;
import com.finaxys.rd.marketdataprovider.dao.StockDao;
import com.finaxys.rd.marketdataprovider.service.StockService;

// TODO: Auto-generated Javadoc
/**
 * The Class StockServiceImpl.
 */
public class StockServiceImpl implements StockService {

	@Autowired
	private StockDao dao;

	@Override
	public boolean add(Stock stock) {
		return dao.add(stock);
	}


}