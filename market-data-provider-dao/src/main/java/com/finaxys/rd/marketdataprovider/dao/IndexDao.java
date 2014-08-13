/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import com.finaxys.rd.dataextraction.domain.Index;

// TODO: Auto-generated Javadoc
/**
 * The Interface IndexInfoDao.
 */
public interface IndexDao extends HBaseBasicDao<Index> {
	
	public List<Index> list(char provider, String exchSymb) throws IOException ;
}
