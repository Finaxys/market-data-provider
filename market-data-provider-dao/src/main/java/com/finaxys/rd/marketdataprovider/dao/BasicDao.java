/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.util.List;

import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;


// TODO: Auto-generated Javadoc
/**
 * The Interface CurrencyPairDao.
 */
public interface BasicDao<T> {

	 boolean add(T bean) throws DataAccessException;

	 List<T> list(byte[] prefix) throws DataAccessException;

	 List<T> listAll() throws DataAccessException;

}
