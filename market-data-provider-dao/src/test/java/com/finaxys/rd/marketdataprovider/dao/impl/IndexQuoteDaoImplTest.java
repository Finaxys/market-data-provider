package com.finaxys.rd.marketdataprovider.dao.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.IndexQuote;
import com.finaxys.rd.dataextraction.domain.Enum.DataType;

@RunWith(MockitoJUnitRunner.class)
public class IndexQuoteDaoImplTest extends HBaseBasicDaoImplTest<IndexQuote> {

	@Override
	protected IndexQuote getFixtureBean() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy h:mmaa");
		return new IndexQuote('1', new DateTime(), "^GSPC",
				DataType.INTRA, formatter.parseDateTime("7/22/2014 4:30pm"),
				"US", new BigDecimal("1983.53"), new BigDecimal("+9.90"),
				new BigDecimal("1975.65"), new BigDecimal("1986.24"),
				new BigDecimal("1975.65"), new BigInteger("425726752"),
				new BigDecimal("0"), new BigDecimal("0"));
		}

	@Override
	protected AbstractBasicDao<IndexQuote> getTarget(HConnection connection) {
		return new IndexQuoteDaoImpl(connection);
	}

	

}