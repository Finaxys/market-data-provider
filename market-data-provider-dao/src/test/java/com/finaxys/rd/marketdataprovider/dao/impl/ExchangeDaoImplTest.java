package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.Exchange;
import com.finaxys.rd.dataextraction.domain.Enum.DataType;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeDaoImplTest extends HBaseBasicDaoImplTest<Exchange> {

	@Override
	protected Exchange getFixtureBean() {
		return new Exchange('0', new DateTime(), "XLON",
				DataType.REF, "L", '1', "LONDON STOCK EXCHANGE", "type",
				"Europe", "UNITED KINGDOM", "GBP", new LocalTime("11:00:00"),
				new LocalTime("15:00:00"), new Integer("0"));
		}

	@Override
	protected AbstractBasicDao<Exchange> getTarget(HConnection connection) {
		return new ExchangeDaoImpl(connection);
	}

	

}