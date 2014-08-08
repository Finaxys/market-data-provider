/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao;

import java.io.IOException;
import java.util.List;

import org.joda.time.LocalDate;

import com.finaxys.rd.dataextraction.domain.Option;

// TODO: Auto-generated Javadoc
/**
 * The Interface OptionDao.
 */
public interface OptionDao {

	public boolean add(Option option);

	public Option get(char provider, String exchSymb, String symbol, String optionChain, LocalDate expiration, LocalDate inputDate) throws IOException;

	public List<Option> list(String prefix) throws IOException;
	
	public List<Option> list(byte[] prefix) throws IOException;

	public List<Option> listAll() throws IOException;

	public List<String> listAllSymbols() throws IOException;	
	
	public List<Option> list(char provider, String exchSymb) throws IOException;
	

}
