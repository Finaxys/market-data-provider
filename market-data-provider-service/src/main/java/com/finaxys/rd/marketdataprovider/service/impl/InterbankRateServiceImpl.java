package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.InterbankRate;
import com.finaxys.rd.marketdataprovider.dao.InterbankRateDao;
import com.finaxys.rd.marketdataprovider.service.InterbankRateService;

public class InterbankRateServiceImpl implements InterbankRateService {


	@Autowired
	private InterbankRateDao dao;

	@Override
	public boolean add(InterbankRate interbankRate) {
		
		return dao.add(interbankRate);
	}

}
