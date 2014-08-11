/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.FXRate;
import com.finaxys.rd.marketdataprovider.dao.FXRateDao;
import com.finaxys.rd.marketdataprovider.service.FXRateService;

// TODO: Auto-generated Javadoc
/**
 * The Class FXRateServiceImpl.
 */
public class FXRateServiceImpl implements FXRateService {


	@Autowired
	private FXRateDao dao;

	@Override
	public boolean add(FXRate fxRate) {
		return dao.add(fxRate);
	}


}
