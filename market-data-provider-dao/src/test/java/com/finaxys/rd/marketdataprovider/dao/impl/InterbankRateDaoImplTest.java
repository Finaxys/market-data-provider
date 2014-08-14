package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.InterbankRate;
import com.finaxys.rd.dataextraction.domain.Enum.DataType;

@RunWith(MockitoJUnitRunner.class)
public class InterbankRateDaoImplTest extends HBaseBasicDaoImplTest<InterbankRate> {

	@Override
	protected InterbankRate getFixtureBean() {
		return new InterbankRate('0', new DateTime(), "EONIA", DataType.REF, '1', "EUR");
	}

	@Override
	protected AbstractBasicDao<InterbankRate> getTarget(HConnection connection) {
		return new InterbankRateDaoImpl(connection);
	}

}