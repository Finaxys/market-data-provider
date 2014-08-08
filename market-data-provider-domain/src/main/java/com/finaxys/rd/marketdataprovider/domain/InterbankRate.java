package com.finaxys.rd.marketdataprovider.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "rate")
@XmlType(propOrder = {"symbol", "currency", "provider"})
public class InterbankRate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5685797038348082263L;

	/** Symbol */
	private String symbol;
	
	/** Currency name */
	private String currency;
	
	/** The provider. */
	private char provider;
	
	
	/**
	 * Instantiates a new rate.
	 */
	public InterbankRate() {
	  super();
	}
	
	/**
	 * Instantiates a new rate.
	 *
	 * @param name the name of the rate
	 * @param currency the currency value
	 */
	public InterbankRate(String symbol, String currency, char provider) {
		this.symbol = symbol;
		this.currency = currency;
		this.provider = provider;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@XmlElement(name = "Symbol")
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
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
