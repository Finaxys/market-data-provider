/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.util.List;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.Stock;
import com.finaxys.rd.marketdataprovider.dao.StockDao;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class StockDaoImpl.
 */
public class StockDaoImpl extends AbstractBasicDao<Stock> implements StockDao {

	private static Logger logger = Logger.getLogger(StockDaoImpl.class);

	public StockDaoImpl() {
		super();
	}

	public StockDaoImpl(HConnection connection) {
		super(connection);
	}

	public List<Stock> list(char provider, String exchSymb) throws DataAccessException {
		byte provByte = (byte) provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] prefix = new byte[exchSymbHash.length + 1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);
		Bytes.putBytes(prefix, offset, exchSymbHash, 0, exchSymbHash.length);

		return list(prefix);

	}

	public List<Stock> list(char provider) throws DataAccessException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}

}
