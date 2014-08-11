/*
 * 
 */


package com.finaxys.rd.marketdataprovider.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import com.finaxys.rd.marketdataprovider.domain.msg.Document.DataType;


// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuote.
 */
@XmlRootElement(name = "quote")
@XmlType(propOrder = { "symbol", "ts", "provider", "dataType", "lastTradePriceOnly", "change", "daysHigh", "daysLow", "volume", "open", "close", "adjClose" })
public class IndexQuote implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1394458807070131035L;

	/** The symbol. */
	private String symbol;
	
	/** The provider. */
	private char provider;
	
//	/** The exch symb. */
//	private String exchSymb;
	
	/** The last trade price only. */
	private BigDecimal lastTradePriceOnly;
	
	/** The ts. */
	private DateTime ts;
	
	/** The change. */
	private BigDecimal change = new BigDecimal(0);
	
	/** The open. */
	private BigDecimal open = new BigDecimal(0);
	
	/** The days high. */
	private BigDecimal daysHigh = new BigDecimal(0);
	
	/** The days low. */
	private BigDecimal daysLow = new BigDecimal(0);
	
	/** The volume. */
	private BigInteger volume = new BigInteger("0");
	
	/** The days low. */
	private BigDecimal close = new BigDecimal(0);

	/** The days low. */
	private BigDecimal adjClose = new BigDecimal(0);
	
	/** The data type. */
	private DataType dataType;
	

	/**
	 * Instantiates a new index quote.
	 */
	public IndexQuote() {
		super();
	}



	public IndexQuote(String symbol, char provider, BigDecimal lastTradePriceOnly, DateTime ts, BigDecimal change,
			BigDecimal open, BigDecimal daysHigh, BigDecimal daysLow, BigInteger volume, BigDecimal close,
			BigDecimal adjClose, DataType dataType) {
		super();
		this.symbol = symbol;
		this.provider = provider;
		this.lastTradePriceOnly = lastTradePriceOnly;
		this.ts = ts;
		this.change = change;
		this.open = open;
		this.daysHigh = daysHigh;
		this.daysLow = daysLow;
		this.volume = volume;
		this.close = close;
		this.adjClose = adjClose;
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
	@XmlElement(name = "ts", required = true, nillable=false)
	 @XmlJavaTypeAdapter(com.finaxys.rd.marketdataprovider.domain.jaxb.DateTimeAdapter.class)
	public DateTime getTs() {
		return ts;
	}

	/**
	 * Sets the ts.
	 *
	 * @param ts the new ts
	 */
	public void setTs(DateTime ts) {
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
	public BigInteger getVolume() {
		return volume;
	}

	/**
	 * Sets the volume.
	 *
	 * @param volume the new volume
	 */
	public void setVolume(BigInteger volume) {
		this.volume = volume;
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
	
	@XmlElement(name = "Close", defaultValue="0")
	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	@XmlElement(name = "AdjClose", defaultValue="0")
	public BigDecimal getAdjClose() {
		return adjClose;
	}

	public void setAdjClose(BigDecimal adjClose) {
		this.adjClose = adjClose;
	}


}
