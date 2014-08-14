package com.finaxys.rd.marketdataprovider.dao.impl;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.Assert;

import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;
import com.finaxys.rd.marketdataprovider.dao.exception.HBaseIOException;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;
import com.finaxys.rd.marketdataprovider.util.HBaseUtil;

@RunWith(MockitoJUnitRunner.class)
public abstract class HBaseBasicDaoImplTest<T> {

	@Mock
	private HTableInterface mockHTableInterface;
	@Mock
	private HConnection mockHConnection;
	@Captor
	private ArgumentCaptor<Put> putCaptor;

	private AbstractBasicDao<T> target;

	private Class<T> clazz;

	abstract protected T getFixtureBean();
	

	abstract protected AbstractBasicDao<T> getTarget(HConnection connection);

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.clazz = (Class<T>) genericSuperclass.getActualTypeArguments()[0];

	}

	
	private Result getFixtureResult() {
		try {
			Result result = Mockito.mock(Result.class);
			T bean = getFixtureBean();
			List<Field> attributes = DaoHelper.getFields(bean.getClass());
			for (Field attribute : attributes) {
				if (!attribute.getName().contains("serialVersionUID"))
					Mockito.doReturn(DaoHelper.toBytes(attribute, PropertyUtils.getSimpleProperty(bean, attribute.getName()))).when(result)
							.getValue(Matchers.eq(AbstractBasicDao.DEFAULT_COLUMN_FAMILY), Matchers.eq(attribute.getName().getBytes()));

			}
			return result;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
			return null;
		}
	}
	
	private AbstractBasicDao<T> getTargetWithNormalBehavior() throws IOException {
		Mockito.when(mockHConnection.getTable(clazz.getSimpleName().getBytes())).thenReturn(mockHTableInterface);
		return getTarget(mockHConnection);
	}

	private AbstractBasicDao<T> getTargetWithBadGetTableMethod() throws IOException {
		Mockito.when(mockHConnection.getTable(clazz.getSimpleName().getBytes())).thenThrow(new IOException());
		return getTarget(mockHConnection);
	}

	private AbstractBasicDao<T> getTargetWithBadPutMethod() throws IOException {
		Mockito.doThrow(new IOException()).when(mockHTableInterface).put(Matchers.any(Put.class));
		Mockito.when(mockHConnection.getTable(clazz.getSimpleName().getBytes())).thenReturn(mockHTableInterface);
		return getTarget(mockHConnection);
	}

	@Test
	public final void test_executeAdd() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		T fixtureBean = getFixtureBean();
		target = getTargetWithNormalBehavior();

		target.add(fixtureBean);

		Mockito.verify(mockHTableInterface).put(putCaptor.capture());
		Put put = putCaptor.getValue();
		
		Assert.notNull(put);
		// When trying to compare bytes arrays, don't use assertEquels() wich
		// compare objects references and use Arrays.equals() which compare
		// arrays contents.
		assertTrue(Arrays.equals(put.getRow(), HBaseUtil.mkRowKey(fixtureBean).getKey()));

		List<KeyValue> keyValue;
		List<Field> attributes = DaoHelper.getFields(clazz);
		for (Field attribute : attributes)
			if (!attribute.getName().contains("serialVersionUID") && (keyValue = put.get(target.DEFAULT_COLUMN_FAMILY, attribute.getName().getBytes())).size() > 0)
				assertTrue(Arrays.equals(keyValue.get(0).getValue(),
						DaoHelper.toBytes(attribute, PropertyUtils.getSimpleProperty(fixtureBean, attribute.getName()))));

	}

	@Test(expected = DataAccessException.class)
	public final void test_executeAdd_nullFixtureBean() throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		T fixtureBean = null;
		target = getTargetWithNormalBehavior();
		
		target.add(fixtureBean);
	}
	
	@Test(expected = HBaseIOException.class)
	public final void test_executeAdd_BadGetTableMethod_HBaseIoException() throws IOException {
		T fixtureBean = getFixtureBean();
		target = getTargetWithBadGetTableMethod();
		
		target.add(fixtureBean);
	}

	@Test(expected = HBaseIOException.class)
	public final void test_executeAdd__BadPutMethod_HBaseIoException() throws IOException {
		T fixtureBean = getFixtureBean();
		target = getTargetWithBadPutMethod();
		
		target.add(fixtureBean);
	}
	
	@Test
	public final void test_list() throws IOException {
		
		ResultScanner mockResultScanner = Mockito.mock(ResultScanner.class);
		
		
		Iterator<Result> iteratorFixture  = Arrays.asList(getFixtureResult()).iterator();
		
		Mockito.doReturn(iteratorFixture).when(mockResultScanner).iterator();
		
		Mockito.when(mockHTableInterface.getScanner(Matchers.any(Scan.class))).thenReturn(mockResultScanner);
		
		Mockito.when(mockHConnection.getTable(clazz.getSimpleName().getBytes())).thenReturn(mockHTableInterface);
		
		target = getTarget(mockHConnection);
		
		List<T> list = target.listAll();
		
		Assert.notNull(list);
		assertTrue(list.size() == 1);
		assertTrue(Arrays.equals(HBaseUtil.mkRowKey(list.get(0)).getKey(), HBaseUtil.mkRowKey(getFixtureBean()).getKey()));
	}
	

}