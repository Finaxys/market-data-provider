/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.util.List;

import com.finaxys.rd.dataextraction.domain.Option;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Interface OptionDao.
 */
public interface OptionDao extends BasicDao<Option> {

	public List<Option> list(char provider, String exchSymb) throws DataAccessException;

}
