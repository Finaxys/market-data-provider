/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.util.List;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyPairDaoImpl.
 */
public class CurrencyPairDaoImpl extends AbstractBasicDao<CurrencyPair> implements CurrencyPairDao {


	static Logger logger = Logger.getLogger(CurrencyPairDaoImpl.class);
	
	public CurrencyPairDaoImpl() {
		super();
	}
	
	public CurrencyPairDaoImpl(HConnection connection) {
		super(connection);
	}

	public List<CurrencyPair> list(char provider)  throws DataAccessException{
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}


}
