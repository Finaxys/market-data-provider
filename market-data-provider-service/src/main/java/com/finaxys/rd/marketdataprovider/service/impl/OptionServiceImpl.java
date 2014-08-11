/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.Option;
import com.finaxys.rd.marketdataprovider.dao.OptionDao;
import com.finaxys.rd.marketdataprovider.service.OptionService;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionServiceImpl.
 */
public class OptionServiceImpl implements OptionService {

	@Autowired
	private OptionDao dao;

	@Override
	public boolean add(Option option) {
		return dao.add(option);
	}

}