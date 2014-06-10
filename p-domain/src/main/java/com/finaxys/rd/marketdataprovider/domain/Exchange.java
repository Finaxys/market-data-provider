/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.hadoop.hbase.util.Bytes;

// TODO: Auto-generated Javadoc
/**
 * The Class Exchange.
 */
@XmlRootElement(name = "exchange")
public class Exchange {

	/** The mic. */
	private String mic;
	
	/** The symbol. */
	private String symbol;
	
	/** The suffix. */
	private String suffix;
	
	/** The provider. */
	private char provider;
	
	/** The name. */
	private String name;
	
	/** The type. */
	private String type;
	
	/** The continent. */
	private String continent;
	
	/** The country. */
	private String country;
	
	/** The currency. */
	private String currency;
	
	/** The open time. */
	private long openTime;
	
	/** The close time. */
	private long closeTime;
	
	/** The status. */
	private boolean status;

	/**
	 * Instantiates a new exchange.
	 */
	public Exchange() {
		super();
	}

	/**
	 * Instantiates a new exchange.
	 *
	 * @param mic the mic
	 * @param symbol the symbol
	 * @param suffix the suffix
	 * @param provider the provider
	 * @param name the name
	 * @param type the type
	 * @param continent the continent
	 * @param country the country
	 * @param currency the currency
	 * @param openTime the open time
	 * @param closeTime the close time
	 * @param status the status
	 */
	public Exchange(String mic, String symbol, String suffix, char provider, String name, String type, String continent,
			String country, String currency, long openTime, long closeTime, boolean status) {
		super();
		this.mic = mic;
		this.symbol = symbol;
		this.suffix = suffix;
		this.name = name;
		this.type = type;
		this.continent = continent;
		this.country = country;
		this.currency = currency;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.status = status;
	}

	/**
	 * Instantiates a new exchange.
	 *
	 * @param mic the mic
	 * @param symbol the symbol
	 * @param suffix the suffix
	 * @param provider the provider
	 * @param name the name
	 * @param type the type
	 * @param continent the continent
	 * @param country the country
	 * @param currency the currency
	 * @param openTime the open time
	 * @param closeTime the close time
	 * @param status the status
	 */
	public Exchange(byte[] mic, byte[] symbol, byte[] suffix, byte[] provider, byte[] name, byte[] type, byte[] continent,
			byte[] country, byte[] currency, byte[] openTime, byte[] closeTime, byte[] status) {
		this(Bytes.toString(mic), Bytes.toString(symbol), Bytes.toString(suffix),(char)(provider[0]), Bytes.toString(name), Bytes
				.toString(type), Bytes.toString(continent), Bytes.toString(country), Bytes.toString(currency), Bytes
				.toLong(openTime), Bytes.toLong(closeTime), Bytes.toBoolean(status));
	}

	/**
	 * Gets the mic.
	 *
	 * @return the mic
	 */
	public String getMic() {
		return mic;
	}

	/**
	 * Sets the mic.
	 *
	 * @param mic the new mic
	 */
	public void setMic(String mic) {
		this.mic = mic;
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
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the continent.
	 *
	 * @return the continent
	 */
	public String getContinent() {
		return continent;
	}

	/**
	 * Sets the continent.
	 *
	 * @param continent the new continent
	 */
	public void setContinent(String continent) {
		this.continent = continent;
	}

	/**
	 * Gets the country.
	 *
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country.
	 *
	 * @param country the new country
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency.
	 *
	 * @param currency the new currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * Gets the open time.
	 *
	 * @return the open time
	 */
	public long getOpenTime() {
		return openTime;
	}

	/**
	 * Sets the open time.
	 *
	 * @param openTime the new open time
	 */
	public void setOpenTime(long openTime) {
		this.openTime = openTime;
	}

	/**
	 * Gets the close time.
	 *
	 * @return the close time
	 */
	public long getCloseTime() {
		return closeTime;
	}

	/**
	 * Sets the close time.
	 *
	 * @param closeTime the new close time
	 */
	public void setCloseTime(long closeTime) {
		this.closeTime = closeTime;
	}

	// public long getInputDate() {
	// return inputDate;
	// }
	// public void setInputDate(long inputDate) {
	// this.inputDate = inputDate;
	// }
	// public Map<String,String> getDataProviderSymbol() {
	// return dataProviderSymbol;
	// }
	// public void setDataProviderSymbol(Map<String,String> dataProviderSymbol)
	// {
	// this.dataProviderSymbol = dataProviderSymbol;
	// }

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * Gets the symbol.
	 *
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the symbol.
	 *
	 * @param symbol the new symbol
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
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
	 * @param provider the new provider
	 */
	public void setProvider(char provider) {
		this.provider = provider;
	}

	/**
	 * Gets the suffix.
	 *
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * Sets the suffix.
	 *
	 * @param suffix the new suffix
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	/**
	 * Equals.
	 *
	 * @param e the e
	 * @return true, if successful
	 */
	public boolean equals(Exchange e)
	{
		if (this.provider == e.getProvider() && this.mic == e.getMic() && this.suffix == e.getSuffix())
			return (true);
		return (false);
	}

}
