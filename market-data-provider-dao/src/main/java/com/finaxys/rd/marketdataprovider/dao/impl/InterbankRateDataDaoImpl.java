package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.InterbankRateData;
import com.finaxys.rd.marketdataprovider.dao.InterbankRateDataDao;

public class InterbankRateDataDaoImpl extends AbstractBasicDao<InterbankRateData> implements InterbankRateDataDao {

	private static Logger logger = Logger.getLogger(InterbankRateDataDaoImpl.class);

	public InterbankRateDataDaoImpl() {
		super();
	}

	public InterbankRateDataDaoImpl(HConnection connection) {
		super(connection);
	}

}
