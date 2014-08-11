/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain.msg;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class Document.
 */
public class Document implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4579790937340382141L;

	/** The content type. */
	private ContentType contentType;

	/** The data type. */
	private DataType dataType;

	/** The data class. */
	private DataClass dataClass;

	/** The provider. */
	private char provider;

	/** The content. */
	private byte[] content;

	/**
	 * Instantiates a new document.
	 * 
	 * @param contentType
	 *            the content type
	 * @param dataType
	 *            the data type
	 * @param dataClass
	 *            the data class
	 * @param provider
	 *            the provider
	 * @param content
	 *            the content
	 */
	
	public Document(ContentType contentType, DataType dataType, DataClass dataClass, char provider, byte[] content) {
		super();
		this.contentType = contentType;
		this.dataType = dataType;
		this.dataClass = dataClass;
		this.provider = provider;
		this.content = content;
	}

	public Document(byte[] content) {
		super();
		this.content = content;
	}

	public Document( byte[] content, DataType dataType) {
		super();
		this.dataType = dataType;
		this.content = content;
	}

	public Document() {
		super();
	}

	/**
	 * Gets the content type.
	 * 
	 * @return the content type
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type.
	 * 
	 * @param contentType
	 *            the new content type
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets the data type.
	 * 
	 * @return the data type
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Sets the data type.
	 * 
	 * @param dataType
	 *            the new data type
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * Gets the data class.
	 * 
	 * @return the data class
	 */
	public DataClass getDataClass() {
		return dataClass;
	}

	/**
	 * Sets the data class.
	 * 
	 * @param dataClass
	 *            the new data class
	 */
	public void setDataClass(DataClass dataClass) {
		this.dataClass = dataClass;
	}

	/**
	 * Gets the provider.
	 * 
	 * @return the provider
	 */
	public char getProvider() {
		return provider;
	}

	/**
	 * Sets the provider.
	 * 
	 * @param provider
	 *            the new provider
	 */
	public void setProvider(char provider) {
		this.provider = provider;
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 * 
	 * @param content
	 *            the new content
	 */
	public void setContent(byte[] content) {
		this.content = content;
	}

	/**
	 * The Enum ContentType.
	 */
	public enum ContentType {

		/** The xml. */
		XML("xml"),
		/** The json. */
		JSON("json"),
		/** The xls. */
		XLS("xls");

		/** The name. */
		private final String name;

		/**
		 * Instantiates a new content type.
		 * 
		 * @param name
		 *            the name
		 */
		private ContentType(String name) {
			this.name = name;
		}

		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}

	/**
	 * The Enum DataClass.
	 */
	public enum DataClass {

		/** The Currency pair. */
		CurrencyPair,
		/** The Exchange. */
		Exchange,
		/** The FX rate. */
		FXRate,
		/** The Index info. */
		IndexInfo,
		/** The Index quote. */
		IndexQuote,
		/** The Stock quote. */
		StockQuote,
		/** The Stock. */
		Stock,
		/** The InterbankRate. */
		InterbankRate,
		/** The InterbankRatesData. */
		InterbankRatesData
	}

	/**
	 * The Enum DataType.
	 */
	public enum DataType {

		/** The eod. */
		EOD("EOD", 'e'),
		/** The intra. */
		INTRA("INTRA", 'i'),
		/** The hist. */
		HIST("HIST",'h'),
		/** The Ref. */
		Ref("REF", 'r');

		/** The name. */
		private final String name;

		/** The t byte. */
		private final byte tByte;

		/**
		 * Instantiates a new data type.
		 * 
		 * @param name
		 *            the name
		 * @param id
		 *            the id
		 */
		private DataType(String name, char id) {
			this.name = name;
			this.tByte = (byte) id;
		}

		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the t byte.
		 * 
		 * @return the t byte
		 */
		public byte getTByte() {
			return tByte;
		}
	}
}
