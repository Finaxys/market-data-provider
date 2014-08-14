/*
 * 
 */
package com.finaxys.rd.marketdataprovider.service;

import com.finaxys.rd.marketdataprovider.service.exception.ServiceException;


// TODO: Auto-generated Javadoc
/**
 * The Interface CurrencyPairService.
 */
public interface BasicService<T> {
	 boolean add(T t) throws ServiceException;
}
