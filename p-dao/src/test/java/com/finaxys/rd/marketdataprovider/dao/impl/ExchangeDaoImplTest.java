package com.finaxys.rd.marketdataprovider.dao.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.finaxys.rd.marketdataprovider.domain.Exchange;

public class ExchangeDaoImplTest {
	private ExchangeDaoImpl exDao;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		HConnection hConnection = HConnectionManager.createConnection(HBaseConfiguration.create());
		exDao = new ExchangeDaoImpl(hConnection);
	}

	@Test
	public final void testNormalAdd() throws IOException {
		Exchange e1 = new Exchange("mic_add", "symbol_add", "suffixe_add", (char) 0, "Test Exchange add",
				"Interne Type add", "Europe", "France", "USD/EUR", 1, 10, false);

		System.out.println("Unit test for Add :");

		System.out
				.println("Add exchange with mic = 'mic_add'; suffix = 'suffixe_add'; provider = 0 --> exDao.add(exchange) == True ? ");
		assertTrue(exDao.add(e1));
	}

	@Test
	public final void testEmptyValuesAdd() throws IOException {
		Exchange e1 = new Exchange("", "", "", (char) 0, "", "", "", "", "", 0, 0, false);
		System.out
				.println("Add exchange with only provider = 0, closeTime = 0 and openTime = 0 and all strings parameters are empty strings --> exDao.add(exchange) == True ?");
		assertTrue(exDao.add(e1));
	}

	/*
	 * @Test public final void testExistingValueAdd() throws IOException {
	 * Exchange e1 = new Exchange("mic_add", "symbol_add", "suffixe_add", 0,
	 * "Test Exchange add", "Interne Type add", "Europe", "France", "USD/EUR",
	 * 1, 10, false);
	 * 
	 * System.out.println(
	 * "Try to add an exchange already in the table --> exDao.add(exchange) == True ?"
	 * ); assertTrue(exDao.add(e1));
	 * 
	 * System.out.println("				________________________________________"); }
	 */

	/*
	 * Add exchange e with mic = 'mic_add'; suffix = 'suffixe_add'; provider = 0
	 * and get result. Check result is not null and value is what we add -->
	 * result != null ? result == e ?
	 */
	@Test
	public final void testNormalGet() throws IOException {
		Exchange e = new Exchange("mic_get", "symbol_get", "suffixe_get", (char) 2, "Test Exchange get",
				"Interne Type get", "Europe", "France", "USD/EUR", 5, 6, true);
		boolean r = exDao.add(e);

		assertTrue(r);
		Exchange result = exDao.get((char) 20, "suffixe_get", "mic_get");
		assertNotNull(result);
		assertTrue(result.equals(e));
	}

	/*
	 * Try to get exchange with wrong provider value --> result == null ?
	 */
	@Test
	public final void testWrongProviderValueGet() throws IOException {
		Exchange result = exDao.get((char) 0, "suffixe_get", "mic_get");
		assertTrue(result == null);
	}

	/*
	 * Try to get exchange with wrong mic value --> result == null ?
	 */
	@Test
	public final void testWrongMicValueGet() throws IOException {
		Exchange result = exDao.get((char) 20, "suffixe_get", "micet");
		assertTrue(result == null);
	}

	/*
	 * Try to get exchange with wrong suffix value --> result == null ?
	 */
	@Test
	public final void testWrongSuffixValueGet() throws IOException {
		Exchange result = exDao.get((char) 0, "suffet", "mic_get");
		assertTrue(result == null);

		System.out.println("				________________________________________");
	}

	/*
	 * Add 6 values to table and check if list return at least 6 results.
	 */
	@Test
	public final void testList() throws IOException {
		Exchange e1 = new Exchange("mic_list1", "symbol_get", "suffixe_get", (char) 20, "Test Exchange list",
				"Interne Type list", "Europe", "France", "USD/EUR", 5, 6, true);
		exDao.add(e1);
		e1 = new Exchange("mic_list1", "symbol_get", "suffixe_get", (char) 17, "Test Exchange list",
				"Interne Type list", "Europe", "France", "USD/EUR", 3, 68, true);
		exDao.add(e1);
		e1 = new Exchange("mic_list1", "symbol_get", "suffixe_get", (char) 18, "Test Exchange list",
				"Interne Type list", "Europe", "France", "USD/EUR", 1, 0, true);
		exDao.add(e1);
		e1 = new Exchange("mic_list1", "symbol_get", "suffixe_get", (char) 18, "Test Exchange list",
				"Interne Type list", "Europe", "France", "USD/EUR", 55, 6, true);
		exDao.add(e1);
		e1 = new Exchange("mic_get", "symbol_get", "suffixe_get", (char) 20, "Test Exchange list", "Interne Type list",
				"Europe", "France", "USD/EUR", 9, 10, true);
		exDao.add(e1);
		e1 = new Exchange("mic_list1", "symbol_get", "suffixe_get", (char) 20, "Test Exchange list",
				"Interne Type list", "Europe", "France", "USD/EUR", 3, -5, true);
		exDao.add(e1);

		List<Exchange> result = exDao.list("");
		assertNotNull(result);
		assertTrue(result.size() > 6);
	}

}