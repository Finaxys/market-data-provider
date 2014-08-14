/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.util.List;

import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Interface ExchangeDao.
 */
public interface ExchangeDao extends BasicDao<Exchange>{
	
	 List<Exchange> list(char provider) throws DataAccessException;
}
