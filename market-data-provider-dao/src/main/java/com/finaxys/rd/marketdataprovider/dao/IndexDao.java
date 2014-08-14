/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.util.List;

import com.finaxys.rd.dataextraction.domain.Index;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Interface IndexInfoDao.
 */
public interface IndexDao extends BasicDao<Index> {
	
	 List<Index> list(char provider, String exchSymb) throws DataAccessException ;
}
