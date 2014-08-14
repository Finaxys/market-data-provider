package com.finaxys.rd.marketdataprovider.dao.impl;

import java.math.BigDecimal;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.InterbankRateData;
import com.finaxys.rd.dataextraction.domain.Enum.DataType;

@RunWith(MockitoJUnitRunner.class)
public class InterbankRateDataDaoImplTest extends HBaseBasicDaoImplTest<InterbankRateData> {

	@Override
	protected InterbankRateData getFixtureBean() {
		return new InterbankRateData('0',
				new DateTime(), "LIBOR", DataType.HIST, new DateTime(
						"2001-01-02"), "USD", "0D", new BigDecimal(
						"6.65125"));
		}

	@Override
	protected AbstractBasicDao<InterbankRateData> getTarget(HConnection connection) {
		return new InterbankRateDataDaoImpl(connection);
	}

	

}