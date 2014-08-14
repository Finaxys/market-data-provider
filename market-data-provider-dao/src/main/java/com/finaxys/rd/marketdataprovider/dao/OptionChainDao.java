/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.util.List;

import com.finaxys.rd.dataextraction.domain.OptionChain;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Interface OptionChainDao.
 */
public interface OptionChainDao extends BasicDao<OptionChain>{
	
	public List<OptionChain> list(char provider) throws DataAccessException;
}
