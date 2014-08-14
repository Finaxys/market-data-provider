/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.marketdataprovider.dao.BasicDao;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;
import com.finaxys.rd.marketdataprovider.service.BasicService;
import com.finaxys.rd.marketdataprovider.service.exception.ServiceException;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyPairServiceImpl.
 */
public class BasicServiceImpl<T> implements BasicService<T> {

	@Autowired
	private BasicDao<T> dao;

	@Override
	public boolean add(T t) throws ServiceException {
		try {
			return dao.add(t);
		} catch (DataAccessException e) {
			throw new ServiceException(e);
		}
	}

}
