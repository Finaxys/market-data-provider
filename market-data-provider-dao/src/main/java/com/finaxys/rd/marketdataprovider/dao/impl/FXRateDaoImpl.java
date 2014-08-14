/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.FXRate;
import com.finaxys.rd.marketdataprovider.dao.FXRateDao;

// TODO: Auto-generated Javadoc
/**
 * The Class FXRateDaoImpl.
 */
public class FXRateDaoImpl  extends AbstractBasicDao<FXRate> implements FXRateDao {
	


	static Logger logger = Logger.getLogger(FXRateDaoImpl.class);

	public FXRateDaoImpl() {
		super();
	}

	public FXRateDaoImpl(HConnection connection) {
		super(connection);
	}

}
