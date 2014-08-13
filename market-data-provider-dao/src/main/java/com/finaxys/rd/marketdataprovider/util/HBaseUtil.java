/*
 * 
 */
package com.finaxys.rd.marketdataprovider.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.log4j.Logger;

import com.finaxys.rd.dataextraction.domain.hbase.HBaseRowKeyField;
import com.finaxys.rd.dataextraction.domain.hbase.HBaseRowKeysFields;
import com.finaxys.rd.dataextraction.domain.hbase.RowKeyField;

public class HBaseUtil {

	static Logger logger = Logger.getLogger(HBaseUtil.class);

	public static TreeSet<RowKeyField> getRowKeyFields(Object bean, Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> mainClass = clazz;
		TreeSet<RowKeyField> fields = new TreeSet<RowKeyField>();
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(HBaseRowKeyField.class)) {
					Annotation annotation = field.getAnnotation(HBaseRowKeyField.class);
					HBaseRowKeyField hbaseRowKey = (HBaseRowKeyField) annotation;
					fields.add(new RowKeyField(PropertyUtils.getSimpleProperty(bean, field.getName()), hbaseRowKey.order()));
				}
				if(field.isAnnotationPresent(HBaseRowKeysFields.class)){
					Annotation annotation = field.getAnnotation(HBaseRowKeysFields.class);
					HBaseRowKeysFields hbaseRowKeyDeclarations = (HBaseRowKeysFields) annotation;
					HBaseRowKeyField[] hbaseRowKeyAnnotations = hbaseRowKeyDeclarations.rowkeys();
					for(int i = 0; i <  hbaseRowKeyAnnotations.length; i++)
						if(hbaseRowKeyAnnotations[i].className().equals(mainClass.getSimpleName())){
							fields.add(new RowKeyField(PropertyUtils.getSimpleProperty(bean, field.getName()), hbaseRowKeyAnnotations[i].order()));
							
						}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	public static Scan mkScan() {
		Scan scan = new Scan();
		return scan;
	}

	public static Scan mkScan(byte[] start, byte[] end) {
		Scan scan = new Scan(start, end);
		return scan;
	}

	public static Scan mkScan(String prefix) {
		Scan scan = new Scan();
		org.apache.hadoop.hbase.filter.RegexStringComparator prefixFilter = new org.apache.hadoop.hbase.filter.RegexStringComparator("^" + prefix + "*");
		RowFilter rowFilter = new RowFilter(CompareOp.EQUAL, prefixFilter);
		scan.setFilter(rowFilter);

		return scan;
	}

	public static Scan mkScan(byte[] prefix) {
		Scan scan = new Scan();
		PrefixFilter prefixFilter = new org.apache.hadoop.hbase.filter.PrefixFilter(prefix);
		scan.setFilter(prefixFilter);

		return scan;
	}

	

}
