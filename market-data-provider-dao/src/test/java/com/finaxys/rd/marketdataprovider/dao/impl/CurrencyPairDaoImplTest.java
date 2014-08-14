package com.finaxys.rd.marketdataprovider.dao.impl;

import org.apache.hadoop.hbase.client.HConnection;
import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.finaxys.rd.dataextraction.domain.CurrencyPair;
import com.finaxys.rd.dataextraction.domain.Enum.DataType;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyPairDaoImplTest extends HBaseBasicDaoImplTest<CurrencyPair> {

	@Override
	protected CurrencyPair getFixtureBean() {
		return new CurrencyPair('0', new DateTime(), "EURUSD", DataType.REF, '1', "EUR", "USD");
	}

	@Override
	protected AbstractBasicDao<CurrencyPair> getTarget(HConnection connection) {
		return new CurrencyPairDaoImpl(connection);
	}

	

}