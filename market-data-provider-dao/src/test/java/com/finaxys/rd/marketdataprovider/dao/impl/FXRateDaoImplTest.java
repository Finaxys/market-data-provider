package com.finaxys.rd.marketdataprovider.dao.impl;

import java.math.BigDecimal;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.FXRate;
import com.finaxys.rd.dataextraction.domain.Enum.DataType;

@RunWith(MockitoJUnitRunner.class)
public class FXRateDaoImplTest extends HBaseBasicDaoImplTest<FXRate> {

	@Override
	protected FXRate getFixtureBean() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy h:mmaa");
		return new FXRate('1', new DateTime(), "EURUSD",
				DataType.INTRA, formatter.parseDateTime("7/23/2014 10:35pm"),
				new BigDecimal("1.3463"), new BigDecimal("1.3463"),
				new BigDecimal("1.3462"));
		}

	@Override
	protected AbstractBasicDao<FXRate> getTarget(HConnection connection) {
		return new FXRateDaoImpl(connection);
	}

	

}