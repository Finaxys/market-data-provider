/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.util.List;

import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Interface CurrencyPairDao.
 */
public interface CurrencyPairDao extends BasicDao<CurrencyPair> {

	 List<CurrencyPair> list(char provider) throws DataAccessException;
}
