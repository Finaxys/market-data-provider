/*
 * 
 */
package com.finaxys.rd.marketdataprovider.dao.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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

import com.finaxys.rd.dataextraction.domain.hbase.HashRowKeyStrategy;
import com.finaxys.rd.dataextraction.domain.hbase.RowKey;
import com.finaxys.rd.dataextraction.domain.hbase.RowKeyField;
import com.finaxys.rd.marketdataprovider.dao.HBaseBasicDao;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessException;
import com.finaxys.rd.marketdataprovider.dao.exception.DataAccessFixtureException;
import com.finaxys.rd.marketdataprovider.dao.exception.HBaseIOException;
import com.finaxys.rd.marketdataprovider.dao.exception.HBaseRowKeyCreationException;
import com.finaxys.rd.marketdataprovider.helper.DaoHelper;
import com.finaxys.rd.marketdataprovider.util.HBaseUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class StockQuoteDaoImpl.
 */
public class HBaseBasicDaoImpl<T> implements HBaseBasicDao<T> {

	static Logger logger = Logger.getLogger(HBaseBasicDaoImpl.class);

	private Class<T> clazz;

	public static final byte[] DEFAULT_COLUMN_FAMILY = new byte[] { (byte) 'f' };
	@Autowired
	private HConnection connection;

	public HBaseBasicDaoImpl(Class<T> clazz) {
		super();
		this.clazz = clazz;

	}

	public HBaseBasicDaoImpl(Class<T> clazz, HConnection connection) {
		super();
		this.connection = connection;
		this.clazz = clazz;

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

	private RowKey mkRowKey(T bean) {
		Assert.notNull(bean, "Cannot persist null object");
		TreeSet<RowKeyField> rowKeyFields = null;
		try {
			rowKeyFields = HBaseUtil.getRowKeyFields(bean, clazz);
			if(rowKeyFields == null || rowKeyFields.isEmpty()) throw new HBaseRowKeyCreationException();
			RowKey rowkey = new RowKey(rowKeyFields, new HashRowKeyStrategy());
			rowkey.createRowKey();
			return rowkey;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new HBaseRowKeyCreationException(e);
		}
	}

	private Put mkPut(T bean) throws DataAccessFixtureException {
		Assert.notNull(bean, "Cannot persist null object");
		Put p = null;
		try {
			p = new Put(mkRowKey(bean).getKey());

			List<Field> attributes = DaoHelper.getFields(clazz);
			Object value;
			for (Field attribute : attributes)
				if (!attribute.getName().contains("serialVersionUID") && (value = PropertyUtils.getSimpleProperty(bean, attribute.getName())) != null)
					p.add(DEFAULT_COLUMN_FAMILY, attribute.getName().getBytes(), DaoHelper.toBytes(attribute, value));

			return p;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
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
			for (Result result : results) {
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
		Assert.notNull(bean, "Cannot persist null object");
		Put put = mkPut(bean);
		return executeAdd(clazz.getSimpleName().getBytes(), put);
	}

	public List<T> list(byte[] prefix) throws DataAccessException {
		return executeScan(clazz.getSimpleName().getBytes(), HBaseUtil.mkScan(prefix));
	}

	public List<T> listAll() throws DataAccessException {
		return executeScan(clazz.getSimpleName().getBytes(), HBaseUtil.mkScan());
	}

}
