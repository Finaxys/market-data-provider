/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.util.List;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.Option;
import com.finaxys.rd.marketdataprovider.dao.OptionDao;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionDaoImpl.
 */
public class OptionDaoImpl extends AbstractBasicDao<Option> implements OptionDao {

	private static Logger logger = Logger.getLogger(OptionDaoImpl.class);

	public OptionDaoImpl() {
		super();
	}

	public OptionDaoImpl(HConnection connection) {
		super(connection);
	}

	public List<Option> list(char provider, String exchSymb) throws DataAccessException {
		byte provByte = (byte) provider;
		byte[] exchSymbHash = DaoHelper.md5sum(exchSymb);
		byte[] prefix = new byte[exchSymbHash.length + 1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);
		Bytes.putBytes(prefix, offset, exchSymbHash, 0, exchSymbHash.length);

		return list(prefix);

	}

}
