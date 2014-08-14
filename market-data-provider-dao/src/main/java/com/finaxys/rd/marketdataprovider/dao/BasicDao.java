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

	public boolean add(T bean) throws DataAccessException;

	public List<T> list(byte[] prefix) throws DataAccessException;

	public List<T> listAll() throws DataAccessException;

}
