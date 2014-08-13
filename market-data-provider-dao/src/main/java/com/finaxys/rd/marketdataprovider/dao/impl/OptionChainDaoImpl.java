/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.util.List;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.OptionChain;
import com.finaxys.rd.marketdataprovider.dao.OptionChainDao;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;

// TODO: Auto-generated Javadoc
/**
 * The Class OptionChainDaoImpl.
 */
public class OptionChainDaoImpl extends HBaseBasicDaoImpl<OptionChain> implements OptionChainDao {

	static Logger logger = Logger.getLogger(OptionChainDaoImpl.class);

	public OptionChainDaoImpl() {
		super(OptionChain.class);
	}

	public List<OptionChain> list(char provider) throws DataAccessException {
		byte provByte = (byte) provider;
		byte[] prefix = new byte[1];

		int offset = 0;
		offset = Bytes.putByte(prefix, offset, provByte);

		return list(prefix);

	}

}
