/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.util.List;

import com.finaxys.rd.dataextraction.domain.Stock;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Interface StockDao.
 */
public interface StockDao extends BasicDao<Stock> {
	
	 List<Stock> list(char provider, String exchSymb) throws DataAccessException;
	
	 List<Stock> list(char provider) throws DataAccessException;
}
