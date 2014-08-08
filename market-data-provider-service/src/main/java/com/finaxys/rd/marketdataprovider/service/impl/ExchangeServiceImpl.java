/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.marketdataprovider.dao.ExchangeDao;
import com.finaxys.rd.marketdataprovider.service.ExchangeService;

// TODO: Auto-generated Javadoc
/**
 * The Class ExchangeServiceImpl.
 */
public class ExchangeServiceImpl implements ExchangeService {


	@Autowired
	private ExchangeDao dao;

	@Override
	public boolean add(Exchange exchange) {
		return dao.add(exchange);
	}




	

}