package com.finaxys.rd.marketdataprovider.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;
import com.finaxys.rd.dataextraction.domain.OptionQuote;

@RunWith(MockitoJUnitRunner.class)
public class OptionQuoteDaoImplTest extends HBaseBasicDaoImplTest<OptionQuote> {

	@Override
	protected OptionQuote getFixtureBean() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MMM-yy").withLocale(Locale.US);
		
		return new OptionQuote( '1', new DateTime() ,  "A140816C00060000",
				DataType.INTRA ,  new DateTime("2014-07-24T02:54:40Z"),null, null,new  BigDecimal("0.73"), new BigDecimal("+0.14"), new BigDecimal ("0.79"), new BigDecimal ("1.03"),new BigDecimal ("0.65"), new BigDecimal ("0.70"), new BigDecimal ("60.00"),
				formatter.parseDateTime("16-Aug-14").toLocalDate(), new BigInteger ("48"), new Integer ("7330"));
		}

	@Override
	protected AbstractBasicDao<OptionQuote> getTarget(HConnection connection) {
		return new OptionQuoteDaoImpl(connection);
	}

	

}