package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.OptionChain;
import com.finaxys.rd.marketdataprovider.dao.OptionChainDao;
import com.finaxys.rd.marketdataprovider.service.OptionChainService;

public class OptionChainServiceImpl implements OptionChainService{

	


	@Autowired
	private OptionChainDao dao;

	@Override
	public boolean add(OptionChain optionChain) {
		return dao.add(optionChain);
	}

	
}