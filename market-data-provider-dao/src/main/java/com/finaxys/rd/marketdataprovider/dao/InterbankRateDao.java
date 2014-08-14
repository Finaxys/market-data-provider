package com.finaxys.rd.marketdataprovider.dao;

import java.util.List;

import com.finaxys.rd.dataextraction.domain.InterbankRate;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;


public interface InterbankRateDao extends BasicDao<InterbankRate>{
	
	 List<InterbankRate> list(char provider) throws DataAccessException;
}
