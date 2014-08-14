/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.finaxys.rd.marketdataprovider.dao.BasicDao;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessFixtureException;
import com.finaxys.rd.marketdataprovider.dao.exception.HBaseIOException;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;
import com.finaxys.rd.marketdataprovider.util.HBaseUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class StockQuoteDaoImpl.
 */
public abstract class AbstractBasicDao<T> implements BasicDao<T> {

	private static Logger logger = Logger.getLogger(AbstractBasicDao.class);

	private Class<T> clazz;

	public static final byte[] DEFAULT_COLUMN_FAMILY = new byte[] { (byte) 'f' };
	@Autowired
	private HConnection connection;

	public AbstractBasicDao() {
		super();
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.clazz = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
	}

	public AbstractBasicDao(HConnection connection) {
		super();
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.clazz = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
		this.connection = connection;
	}

	public HConnection getConnection() {
		return connection;
	}

	public void setConnection(HConnection connection) {
		this.connection = connection;
	}

	private T resultToConcreteObject(Result result) {
		T bean = null;
		try {
			bean = clazz.newInstance();

			List<Field> attributes = DaoHelper.getFields(clazz);
			byte[] value;
			for (Field attribute : attributes) {

				if ((value = result.getValue(DEFAULT_COLUMN_FAMILY, attribute.getName().getBytes())) != null)

					PropertyUtils.setSimpleProperty(bean, attribute.getName(), DaoHelper.getTypedValue(attribute, value));

			}

			return bean;
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new DataAccessException(e);
		}
	}

	private Put mkPut(T bean) throws DataAccessFixtureException {
		Put p = null;
		try {
			Assert.notNull(bean, "Cannot persist null object");
			p = new Put(HBaseUtil.mkRowKey(bean).getKey());

			List<Field> attributes = DaoHelper.getFields(clazz);
			Object value;
			for (Field attribute : attributes)
				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(bean, attribute.getName())) != null)
					p.add(DEFAULT_COLUMN_FAMILY, attribute.getName().getBytes(), DaoHelper.toBytes(attribute, value));

			return p;
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
			throw new DataAccessFixtureException(e);
		}
	}

	private boolean executeAdd(byte[] tableNameBytes, Put put) throws DataAccessException {

		HTableInterface table = null;
		try {
			table = connection.getTable(tableNameBytes);
			table.put(put);
			return true;

		} catch (IOException e) {
			throw new HBaseIOException(e);
		} finally {
			if (table != null)
				try {
					table.close();
				} catch (IOException e) {
					logger.error("Exception when releasing hbase table access.");
				}
		}
	}

	private List<T> executeScan(byte[] tableNameBytes, Scan scan) throws DataAccessException {

		HTableInterface table = null;
		try {
			table = connection.getTable(tableNameBytes);
			ResultScanner results = table.getScanner(scan);
			List<T> list = new ArrayList<T>();
			T bean = null;
			Iterator<Result> iterator = results.iterator();
			Result result = null;
			while (iterator.hasNext()) {
				result = iterator.next();
				bean = resultToConcreteObject(result);
				if (bean != null)
					list.add(bean);
			}

			return list;
		} catch (IOException e) {
			throw new HBaseIOException(e);
		} finally {
			if (table != null)
				try {
					table.close();
				} catch (IOException e) {
					logger.error("Exception when releasing hbase table access.");
				}
		}
	}

	public boolean add(T bean) throws DataAccessException {
		try {
			Assert.notNull(bean, "Cannot persist null object");

			Put put = mkPut(bean);
			return executeAdd(clazz.getSimpleName().getBytes(), put);
		} catch (IllegalArgumentException e) {
			throw new DataAccessException(e);
		}
	}

	public List<T> list(byte[] prefix) throws DataAccessException {
		return executeScan(clazz.getSimpleName().getBytes(), HBaseUtil.mkScan(prefix));
	}

	public List<T> listAll() throws DataAccessException {
		return executeScan(clazz.getSimpleName().getBytes(), HBaseUtil.mkScan());
	}

}
