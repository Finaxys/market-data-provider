package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.InterbankRateData;
import com.finaxys.rd.marketdataprovider.dao.InterbankRateDataDao;
import com.finaxys.rd.marketdataprovider.service.InterbankRateDataService;

public class InterbankRateDataServiceImpl implements InterbankRateDataService{


	@Autowired
	private InterbankRateDataDao dao;

	@Override
	public boolean add( InterbankRateData interbankRateData) {
		return dao.add(interbankRateData);
	}

}
