/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyPair.
 */
@XmlRootElement(name = "currency")
@XmlType(propOrder = {"symbol", "baseCurrency", "quoteCurrency"})
public class CurrencyPair implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5442610927173591118L;

	/** The symbol. */
	private String symbol;
	
	/** The base currency. */
	private String baseCurrency;
	
	/** The quote currency. */
	private String quoteCurrency;
	
	/**
	 * Instantiates a new currency pair.
	 */
	public CurrencyPair() {
		super();
	}

	/**
	 * Instantiates a new currency pair.
	 *
	 * @param symbol the symbol
	 * @param baseCurrency the base currency
	 * @param quoteCurrency the quote currency
	 */
	public CurrencyPair(String symbol, String baseCurrency, String quoteCurrency) {
		super();
		this.symbol = symbol;
		this.baseCurrency = baseCurrency;
		this.quoteCurrency = quoteCurrency;
	}
	
	/**
	 * Gets the symbol.
	 *
	 * @return the symbol
	 */
	@XmlElement(name = "symbol")
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
	 * Gets the base currency.
	 *
	 * @return the base currency
	 */
	@XmlElement(name = "base")
	public String getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * Sets the base currency.
	 *
	 * @param baseCurrency the new base currency
	 */
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}
	
	/**
	 * Gets the quote currency.
	 *
	 * @return the quote currency
	 */
	@XmlElement(name = "quote")
	public String getQuoteCurrency() {
		return quoteCurrency;
	}

	/**
	 * Sets the quote currency.
	 *
	 * @param quoteCurrency the new quote currency
	 */
	public void setQuoteCurrency(String quoteCurrency) {
		this.quoteCurrency = quoteCurrency;
	}	
	
}
