package com.finaxys.rd.marketdataprovider.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.StockQuote;
import com.finaxys.rd.dataextraction.domain.Enum.DataType;

@RunWith(MockitoJUnitRunner.class)
public class StockQuoteDaoImplTest extends HBaseBasicDaoImplTest<StockQuote> {

	@Override
	protected StockQuote getFixtureBean() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy h:mmaa");
		return new StockQuote('1', new DateTime(), "YHOO", DataType.INTRA, new DateTime("2014-07-23T13:56:28Z"), "US", new BigInteger("19698700"), new BigDecimal(
				"+0.14"), new BigDecimal("33.71"), new BigDecimal("33.80"), new BigDecimal("26.75"), new BigDecimal("41.72"), "34.236B", new BigDecimal("33.74"), "33.71 - 33.80",
				"Yahoo! Inc.", new BigInteger("743201"), new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"));
}

	@Override
	protected AbstractBasicDao<StockQuote> getTarget(HConnection connection) {
		return new StockQuoteDaoImpl(connection);
	}

	

}