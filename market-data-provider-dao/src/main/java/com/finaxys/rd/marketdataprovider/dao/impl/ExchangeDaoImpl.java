/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.util.List;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.marketdataprovider.dao.ExchangeDao;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Class ExchangeDaoImpl.
 */
public class ExchangeDaoImpl extends AbstractBasicDao<Exchange> implements ExchangeDao {

	private static Logger logger = Logger.getLogger(ExchangeDaoImpl.class);

	public ExchangeDaoImpl() {
		super();
	}

	public ExchangeDaoImpl(HConnection connection) {
		super(connection);
	}

	public List<Exchange> list(char provider) throws DataAccessException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}

}
