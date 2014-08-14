package com.finaxys.rd.marketdataprovider.dao.impl;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.Option;
import com.finaxys.rd.dataextraction.domain.Enum.DataType;

@RunWith(MockitoJUnitRunner.class)
public class OptionDaoImplTest extends HBaseBasicDaoImplTest<Option> {

	@Override
	protected Option getFixtureBean() {
		DateTimeFormatter expirationFormatter = DateTimeFormat.forPattern(
				"dd-MMM-yyyy").withLocale(Locale.ENGLISH);
		return new Option('0', new DateTime(), "A140816C00050000",
				DataType.REF, '1', "NY", "A", "C", new BigDecimal("50.0"),
				expirationFormatter.parseDateTime("16-Aug-2014").toLocalDate());
		}

	@Override
	protected AbstractBasicDao<Option> getTarget(HConnection connection) {
		return new OptionDaoImpl(connection);
	}

	

}