/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import com.finaxys.rd.marketdataprovider.domain.msg.Document.DataType;

// TODO: Auto-generated Javadoc
/**
 * The Class FXRate.
 */
@XmlRootElement(name = "rate")
@XmlType(propOrder = { "symbol", "provider", "ts", "dataType", "rate", "ask", "bid"})
public class FXRate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3345158994491126871L;

	/** The symbol. */
	private String symbol;
	
	/** The rate. */
	private BigDecimal rate;
	
	/** The ask. */
	private BigDecimal ask = new BigDecimal(0); // to delete after scaled risk model update
	
	/** The bid. */
	private BigDecimal bid = new BigDecimal(0); // to delete after scaled risk model update
	
	
	/** The provider. */
	private char provider;
	
	/** The ts. */
	private DateTime ts;
	
	/** The data type. */
	private DataType dataType;

	

	/**
	 * Instantiates a new FX rate.
	 */
	public FXRate() {
		super();
	}

	/**
	 * Instantiates a new FX rate.
	 *
	 * @param symbol the symbol
	 * @param rate the rate
	 * @param ask the ask
	 * @param bid the bid
	 * @param provider the provider
	 * @param ts the ts
	 * @param dataType the data type
	 */
	public FXRate(String symbol, BigDecimal rate, BigDecimal ask, BigDecimal bid, char provider, DateTime ts, DataType dataType) {
		super();
		this.symbol = symbol;
		this.rate = rate;
		this.ask = ask;
		this.bid = bid;
		this.provider = provider;
		this.ts = ts;
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
	 * Gets the rate.
	 *
	 * @return the rate
	 */
	@XmlElement(name = "Rate")
	public BigDecimal getRate() {
		return rate;
	}

	/**
	 * Sets the rate.
	 *
	 * @param rate the new rate
	 */
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	/**
	 * Gets the ask.
	 *
	 * @return the ask
	 */
	@XmlElement(name = "Ask")
	public BigDecimal getAsk() {
		return ask;
	}

	/**
	 * Sets the ask.
	 *
	 * @param ask the new ask
	 */
	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

	/**
	 * Gets the bid.
	 *
	 * @return the bid
	 */
	@XmlElement(name = "Bid")
	public BigDecimal getBid() {
		return bid;
	}

	/**
	 * Sets the bid.
	 *
	 * @param bid the new bid
	 */
	public void setBid(BigDecimal bid) {
		this.bid = bid;
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
	 * Gets the ts.
	 *
	 * @return the ts
	 */
	@XmlElement(name = "ts")
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
