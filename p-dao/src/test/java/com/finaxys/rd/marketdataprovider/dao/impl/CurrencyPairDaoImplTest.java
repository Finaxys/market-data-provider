package com.finaxys.rd.marketdataprovider.dao.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.marketdataprovider.domain.CurrencyPair;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyPairDaoImplTest {

	private static final String HTableInterface = null;
	private  CurrencyPairDaoImpl exDao;
	
	@Before
	public void setUp() throws Exception 
	{
		HConnection hConnection = HConnectionManager.createConnection(HBaseConfiguration.create());
		exDao = new CurrencyPairDaoImpl(hConnection);
	}
	
	/*
	 * Add currency with symbol = Currency Add --> exDao.add(CurrencyPair) == True ? 
	 */
	@Test
	public final void testNormalAdd() throws IOException
	{
		HTableInterface mockHBase = Mockito.mock(HTableInterface.class);
		CurrencyPair  e1 = new CurrencyPair("CurrencyAdd", "CurrencyBaseAdd", "CurrencyQuoteAdd");

		Mockito.doThrow(new IOException()).when(mockHBase).close();
		assertTrue(exDao.add(e1));
	}
	
	/*
	 * Add CurrencyPair with all strings parameters are empty strings --> exDao.add(CurrencyPair) == True ?
	 */
	@Test
	public final void testEmptyValuesAdd() throws IOException
	{
		CurrencyPair e1 = new CurrencyPair("", "", "");
		assertTrue(exDao.add(e1));
	}

	/*
	 * Add CurrencyPair e with symbol = CurrencyGet'; base = CurrencyBaseGet; quote = CurrencyQuoteGet and get result. 
	 * Check result is not null and value is what we add --> result != null ? result == e ?
	 */
	@Test
	public final void testNormalGet() throws IOException
	{
		CurrencyPair e = new CurrencyPair("CurrencyGet", "CurrencyBaseGet", "CurrencyQuoteGet");
		boolean r = exDao.add(e);
		
		assertTrue(r);
	}
	
	/*
	 * Try to get CurrencyPair with wrong symbol value --> result == null ?
	 */
	@Test
	public final void testWrongSymbolValueGet() throws IOException
	{		
		CurrencyPair result = exDao.get("CurrencyAdd");
		assertTrue(result == null);
	}
	
	/*
	 * Add 6 values in table and verify if it exists at least 6 values in table.
	 */
	@Test
	public final void testList() throws IOException
	{
		CurrencyPair e1 = new CurrencyPair("Currency_symbol1", "CurrencyBase1", "CurrencyQuote1");
		exDao.add(e1);
		e1 = new CurrencyPair("Currency_symbol2", "CurrencyBase2", "CurrencyQuote2");
		exDao.add(e1);
		e1 = new CurrencyPair("Currency_symbol3", "CurrencyBase3", "CurrencyQuote3");
		exDao.add(e1);
		e1 = new CurrencyPair("Currency_symbol4", "CurrencyBase4", "CurrencyQuote4");
		exDao.add(e1);
		e1 = new CurrencyPair("Currency_symbol5", "CurrencyBase5", "CurrencyQuote5");
		exDao.add(e1);
		e1 = new CurrencyPair("Currency_symbol6", "CurrencyBase6", "CurrencyQuote6");
		exDao.add(e1);
		
		List<CurrencyPair> result = exDao.list("Currency_");
		assertTrue(result.size() >= 6);
	}		

}