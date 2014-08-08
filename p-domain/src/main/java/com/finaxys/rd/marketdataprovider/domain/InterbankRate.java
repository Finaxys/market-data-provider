package com.finaxys.rd.marketdataprovider.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rate")
public class Rate {
	
	/** Name */
	private String name;
	
	/** Currency name */
	private String currency;
	
	/** The provider. */
	private char provider;
	
	
	/**
	 * Instantiates a new rate.
	 */
	public Rate() {
	  super();
	}
	
	/**
	 * Instantiates a new rate.
	 *
	 * @param name the name of the rate
	 * @param currency the currency value
	 */
	public Rate(String name, String currency, char provider) {
		this.name = name;
		this.currency = currency;
		this.provider = provider;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@XmlElement(name = "Name")
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
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	@XmlElement(name = "Currency")
	public String getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency.
	 *
	 * @param name the new currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	/**
	 * Gets the provider.
	 *
	 * @return the provider
	 */
	@XmlElement(name = "Provider")
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

}
