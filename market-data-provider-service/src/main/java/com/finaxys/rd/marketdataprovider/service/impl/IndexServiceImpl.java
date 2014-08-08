/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.dataextraction.domain.Index;
import com.finaxys.rd.marketdataprovider.dao.IndexDao;
import com.finaxys.rd.marketdataprovider.service.IndexService;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexInfoServiceImpl.
 */
public class IndexServiceImpl implements IndexService{


	@Autowired
	private IndexDao dao;

	@Override
	public boolean add(Index index) {
		return dao.add(index);
	}


}