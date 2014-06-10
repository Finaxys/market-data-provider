/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import com.finaxys.rd.dataextraction.msg.Document.DataType;


// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuote.
 */
@XmlRootElement(name = "quote")
public class IndexQuote {
	
	/** The symbol. */
	private String symbol;
	
	/** The provider. */
	private char provider;
	
	/** The exch symb. */
	private String exchSymb;
	
	/** The last trade price only. */
	private BigDecimal lastTradePriceOnly;
	
	/** The ts. */
	private Long ts;
	
	/** The change. */
	private BigDecimal change;
	
	/** The open. */
	private BigDecimal open;
	
	/** The days high. */
	private BigDecimal daysHigh;
	
	/** The days low. */
	private BigDecimal daysLow;
	
	/** The volume. */
	private Integer volume;
	
	/** The data type. */
	private DataType dataType;
	

	/**
	 * Instantiates a new index quote.
	 */
	public IndexQuote() {
		super();
	}

	/**
	 * Instantiates a new index quote.
	 *
	 * @param symbol the symbol
	 * @param provider the provider
	 * @param exchSymb the exch symb
	 * @param lastTradePriceOnly the last trade price only
	 * @param ts the ts
	 * @param change the change
	 * @param open the open
	 * @param daysHigh the days high
	 * @param daysLow the days low
	 * @param volume the volume
	 * @param dataType the data type
	 */
	public IndexQuote(String symbol, char provider, String exchSymb, BigDecimal lastTradePriceOnly, Long ts,
			BigDecimal change, BigDecimal open, BigDecimal daysHigh, BigDecimal daysLow, Integer volume,
			DataType dataType) {
		super();
		this.symbol = symbol;
		this.provider = provider;
		this.exchSymb = exchSymb;
		this.lastTradePriceOnly = lastTradePriceOnly;
		this.ts = ts;
		this.change = change;
		this.open = open;
		this.daysHigh = daysHigh;
		this.daysLow = daysLow;
		this.volume = volume;
		this.dataType = dataType;
	}

	/**
	 * Gets the symbol.
	 *
	 * @return the symbol
	 */
	@XmlElement(name = "Symbol")
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

	/**
	 * Gets the last trade price only.
	 *
	 * @return the last trade price only
	 */
	@XmlElement(name = "LastTradePriceOnly")
	public BigDecimal getLastTradePriceOnly() {
		return lastTradePriceOnly;
	}

	/**
	 * Sets the last trade price only.
	 *
	 * @param lastTradePriceOnly the new last trade price only
	 */
	public void setLastTradePriceOnly(BigDecimal lastTradePriceOnly) {
		this.lastTradePriceOnly = lastTradePriceOnly;
	}

	/**
	 * Gets the ts.
	 *
	 * @return the ts
	 */
	@XmlElement(name = "ts")
	public Long getTs() {
		return ts;
	}

	/**
	 * Sets the ts.
	 *
	 * @param ts the new ts
	 */
	public void setTs(Long ts) {
		this.ts = ts;
	}

	/**
	 * Gets the change.
	 *
	 * @return the change
	 */
	@XmlElement(name = "Change")
	public BigDecimal getChange() {
		return change;
	}

	/**
	 * Sets the change.
	 *
	 * @param change the new change
	 */
	public void setChange(BigDecimal change) {
		this.change = change;
	}

	/**
	 * Gets the open.
	 *
	 * @return the open
	 */
	@XmlElement(name = "Open")
	public BigDecimal getOpen() {
		return open;
	}

	/**
	 * Sets the open.
	 *
	 * @param open the new open
	 */
	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	/**
	 * Gets the days high.
	 *
	 * @return the days high
	 */
	@XmlElement(name = "DaysHigh")
	public BigDecimal getDaysHigh() {
		return daysHigh;
	}

	/**
	 * Sets the days high.
	 *
	 * @param daysHigh the new days high
	 */
	public void setDaysHigh(BigDecimal daysHigh) {
		this.daysHigh = daysHigh;
	}

	/**
	 * Gets the days low.
	 *
	 * @return the days low
	 */
	@XmlElement(name = "DaysLow")
	public BigDecimal getDaysLow() {
		return daysLow;
	}

	/**
	 * Sets the days low.
	 *
	 * @param daysLow the new days low
	 */
	public void setDaysLow(BigDecimal daysLow) {
		this.daysLow = daysLow;
	}

	/**
	 * Gets the volume.
	 *
	 * @return the volume
	 */
	@XmlElement(name = "Volume")
	public Integer getVolume() {
		return volume;
	}

	/**
	 * Sets the volume.
	 *
	 * @param volume the new volume
	 */
	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	/**
	 * Gets the exch symb.
	 *
	 * @return the exch symb
	 */
	@XmlElement(name = "ExchSymb")
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
	 * Gets the data type.
	 *
	 * @return the data type
	 */
	@XmlElement(name = "DataType")
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Sets the data type.
	 *
	 * @param dataType the new data type
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

}
