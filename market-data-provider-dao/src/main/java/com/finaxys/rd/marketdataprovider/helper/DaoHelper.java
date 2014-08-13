/*
 * 
 */
package com.finaxys.rd.marketdataprovider.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

	/** The Constant MD5_LENGTH. */
	public static final int MD5_LENGTH = Integer
			.valueOf(Configuration.MD5_LENGTH.get()); // bytes

	

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
			return value;
		} else
			return null;

	}

	public static byte[] toBytes(Field field, Object value) throws IOException {

		if (value != null) {
			if (field.getType().equals(String.class))
				return Bytes.toBytes((String) value);
			else if (field.getType().equals(Long.class))
				return Bytes.toBytes((Long) value);
			else if (field.getType().equals(Integer.class))
				return Bytes.toBytes((Integer) value);
			else if (field.getType().equals(LocalTime.class))
				return Bytes.toBytes(new LocalTime(value).toDateTimeToday()
						.getMillis());
			else if (field.getType().equals(LocalDate.class))
				return Bytes.toBytes(new LocalDate(value)
						.toDateTimeAtStartOfDay().getMillis());
			else if (field.getType().equals(char.class))
				return new byte[]{(byte)((Character) value).charValue()};
			else if (field.getType().equals(DateTime.class))
				return Bytes.toBytes(((DateTime) value).getMillis());
			else if (field.getType().equals(BigDecimal.class))
				return Bytes.toBytes((BigDecimal) value);
			else if (field.getType().equals(BigInteger.class))
				return ((BigInteger) value).toByteArray();
			else if (field.getType().equals(DataType.class))
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

	
	public static List<Field> getFields(Class<?> clazz){
		List<Field> attributes = new ArrayList<Field>();
	    while (clazz != null) {
	    	attributes.addAll(Arrays.asList(clazz.getDeclaredFields()));
	    	clazz = clazz.getSuperclass();
	    }
	    return attributes;
	}
	
	
	/**
	 * The Enum Configuration.
	 */
	public enum Configuration {

		/** The M d5_ length. */
		MD5_LENGTH("dao.helper.md5Length");

		/** The key. */
		private final String key;

		/**
		 * Instantiates a new configuration.
		 * 
		 * @param key
		 *            the key
		 */
		Configuration(String key) {
			this.key = key;
		}

		/** The Constant logger. */
		private final static Logger logger = Logger
				.getLogger(Configuration.class);
		// TODo Share resources (properties files) inter modules
		/** The Constant CONFIG_FILE. */
		private final static String CONFIG_FILE = "/dao.properties";

		/** The Constant configuration. */
		private final static Map<Configuration, String> configuration = new EnumMap<Configuration, String>(
				Configuration.class);

		static {
			readConfigurationFrom(CONFIG_FILE);
		}

		/**
		 * Read configuration from.
		 * 
		 * @param fileName
		 *            the file name
		 */
		private static void readConfigurationFrom(String fileName) {
			try {
				InputStream resource = Configuration.class
						.getResourceAsStream(fileName);
				Properties properties = new Properties();
				properties.load(resource); // throws a NPE if resource not
											// founds
				for (String key : properties.stringPropertyNames()) {
					Configuration c = getConfigurationKey(key);
					if (c != null)
						configuration.put(c, properties.getProperty(key));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Gets the configuration key.
		 * 
		 * @param key
		 *            the key
		 * @return the configuration key
		 */
		private static Configuration getConfigurationKey(String key) {
			for (Configuration c : values()) {
				if (c.key.equals(key)) {
					return c;
				}
			}
			return null;
		}

		/**
		 * Gets the.
		 * 
		 * @return the property corresponding to the key or null if not found
		 */
		public String get() {
			String c = configuration.get(this);
			if (c == null)
				throw new IllegalArgumentException();
			return c;
		}
	}
}
