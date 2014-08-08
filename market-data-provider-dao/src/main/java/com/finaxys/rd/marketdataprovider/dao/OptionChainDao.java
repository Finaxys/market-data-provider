/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import org.joda.time.LocalDate;

import com.finaxys.rd.dataextraction.domain.OptionChain;

// TODO: Auto-generated Javadoc
/**
 * The Interface OptionChainDao.
 */
public interface OptionChainDao {

	public boolean add(OptionChain optionChain);

	public OptionChain get(char provider, String symbol, LocalDate expiration, LocalDate inputDate) throws IOException;

	public List<OptionChain> list(String prefix) throws IOException;

	public List<OptionChain> list(byte[] prefix) throws IOException;

	public List<OptionChain> listAll() throws IOException;

	public List<String> listAllSymbols() throws IOException;
	
	public List<OptionChain> list(char provider) throws IOException;
}
