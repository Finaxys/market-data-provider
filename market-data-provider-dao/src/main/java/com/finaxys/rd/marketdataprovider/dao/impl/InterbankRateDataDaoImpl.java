package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.InterbankRateData;
import com.finaxys.rd.marketdataprovider.dao.InterbankRateDataDao;

public class InterbankRateDataDaoImpl extends HBaseBasicDaoImpl<InterbankRateData> implements InterbankRateDataDao {

	static Logger logger = Logger.getLogger(InterbankRateDataDaoImpl.class);

	public InterbankRateDataDaoImpl() {
		super(InterbankRateData.class);
	}

}
