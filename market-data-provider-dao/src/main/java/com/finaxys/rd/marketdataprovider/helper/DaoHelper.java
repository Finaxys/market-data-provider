/*
 * 
 */
package com.finaxys.rd.marketdataprovider.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.finaxys.rd.dataextraction.domain.Enum.DataType;

// TODO: Auto-generated Javadoc
/**
 * The Class DaoHelper.
 */
public class DaoHelper {

	static Logger logger = Logger.getLogger(DaoHelper.class);


	public static Object getTypedValue(Field field, byte[] value) {

		if (value != null) {
			if (field.getType().equals(String.class))
				return Bytes.toString(value);
			if (field.getType().equals(Long.class))
				return Bytes.toLong(value);
			if (field.getType().equals(Integer.class))
				return Bytes.toInt(value);
			if (field.getType().equals(LocalTime.class))
				return new DateTime(Bytes.toLong(value)).toLocalTime();
			if (field.getType().equals(LocalDate.class))
				return new DateTime(Bytes.toLong(value)).toLocalDate();
			if (field.getType().equals(char.class))
				return (char) value[0];
			if (field.getType().equals(DateTime.class))
				return new DateTime(Bytes.toLong(value));
			if (field.getType().equals(BigDecimal.class))
				return Bytes.toBigDecimal(value);
			if (field.getType().equals(BigInteger.class))
				return new BigInteger(value);
			if (field.getType().equals(DataType.class))
				return DataType.valueOf(Bytes.toString(value));

			return value;
		} else
			return null;

	}

	public static byte[] toBytes(Field field, Object value) throws IOException {

		if (value != null) {
			if (field.getType().equals(String.class))
				return Bytes.toBytes((String) value);
			if (field.getType().equals(Long.class))
				return Bytes.toBytes((Long) value);
			if (field.getType().equals(Integer.class))
				return Bytes.toBytes((Integer) value);
			if (field.getType().equals(LocalTime.class))
				return Bytes.toBytes(new LocalTime(value).toDateTimeToday().getMillis());
			if (field.getType().equals(LocalDate.class))
				return Bytes.toBytes(new LocalDate(value).toDateTimeAtStartOfDay().getMillis());
			if (field.getType().equals(char.class))
				return new byte[] { (byte) ((Character) value).charValue() };
			if (field.getType().equals(DateTime.class))
				return Bytes.toBytes(((DateTime) value).getMillis());
			if (field.getType().equals(BigDecimal.class))
				return Bytes.toBytes((BigDecimal) value);
			if (field.getType().equals(BigInteger.class))
				return ((BigInteger) value).toByteArray();
			if (field.getType().equals(DataType.class))
				return Bytes.toBytes(((DataType) value).getName());

			else {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				ObjectOutputStream o = new ObjectOutputStream(b);
				o.writeObject(value);
				return b.toByteArray();
			}
		} else
			return null;
	}

	/**
	 * Gets the path.
	 * 
	 * @param folder
	 *            the folder
	 * @param file
	 *            the file
	 * @param ext
	 *            the ext
	 * @return the path
	 */
	public static String getPath(String folder, String file, String ext) {
		return folder + "/" + file + "." + ext;
	}

	/**
	 * Gets the resource file.
	 * 
	 * @param path
	 *            the path
	 * @return the resource file
	 */
	public static File getResourceFile(String path) {
		File f = new File(DaoHelper.class.getResource(path).getPath());
		return f;

	}

	/**
	 * To bytes.
	 * 
	 * @param file
	 *            the file
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static byte[] toBytes(File file) throws IOException {
		return FileUtils.readFileToByteArray(file);
	}

	/**
	 * Md5sum.
	 * 
	 * @param s
	 *            the s
	 * @return the byte[]
	 */
	public static byte[] md5sum(String s) {
		MessageDigest d;
		try {
			d = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 algorithm not available!", e);
		}

		return d.digest(Bytes.toBytes(s));
	}

	public static List<Field> getFields(Class<?> clazz) {
		List<Field> attributes = new ArrayList<Field>();
		while (clazz != null) {
			attributes.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		return attributes;
	}

	
}
