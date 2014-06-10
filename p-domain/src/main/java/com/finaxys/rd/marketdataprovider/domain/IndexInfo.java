/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain;

import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexInfo.
 */
@XmlRootElement(name = "indexInfo")
public class IndexInfo {

	/** The symbol. */
	private String symbol;
	
	/** The name. */
	private String name;
	
	/** The exch symb. */
	private String exchSymb;
	
	/** The provider. */
	private char provider;
		
	/**
	 * Instantiates a new index info.
	 */
	public IndexInfo() {
		super();
	}

	/**
	 * Instantiates a new index info.
	 *
	 * @param symbol the symbol
	 * @param name the name
	 * @param exchSymb the exch symb
	 * @param provider the provider
	 */
	public IndexInfo(String symbol, String name, String exchSymb, char provider) {
		super();
		this.symbol = symbol;
		this.exchSymb = exchSymb;
		this.name = name;
		this.provider = provider;
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
	 * Gets the exch symb.
	 *
	 * @return the exch symb
	 */
	public String getExchSymb() {
		return exchSymb;
	}
	
	/**
	 * Sets the exch symb.
	 *
	 * @param exchSymb the new exch symb
	 */
	public void setExchSymb(String exchSymb) {
		this.exchSymb = exchSymb;
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
	
	
}
