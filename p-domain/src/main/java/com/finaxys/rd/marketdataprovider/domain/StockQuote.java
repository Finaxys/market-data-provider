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
 * The Class StockQuote.
 */
@XmlRootElement(name = "quote")
public class StockQuote {

	/** The symbol. */
	private String symbol;
	
	/** The exch symb. */
	private String exchSymb;
	
	/** The provider. */
	private char provider;
	
	/** The average daily volume. */
	private Integer averageDailyVolume;
	
	/** The change. */
	private BigDecimal change;
	
	/** The days low. */
	private BigDecimal daysLow;
	
	/** The days high. */
	private BigDecimal daysHigh;
	
	/** The year low. */
	private BigDecimal yearLow;
	
	/** The year high. */
	private BigDecimal yearHigh;
	
	/** The market capitalization. */
	private String marketCapitalization;
	
	/** The last trade price only. */
	private BigDecimal lastTradePriceOnly;
	
	/** The days range. */
	private String daysRange;
	
	/** The name. */
	private String name;
	
	/** The volume. */
	private Integer volume;
	
	/** The ts. */
	private Long ts;
	
	/** The data type. */
	private DataType dataType;

	
	/**
	 * Instantiates a new stock quote.
	 */
	public StockQuote() {
		super();
	}

	/**
	 * Instantiates a new stock quote.
	 *
	 * @param symbol the symbol
	 * @param exchSymb the exch symb
	 * @param provider the provider
	 * @param averageDailyVolume the average daily volume
	 * @param change the change
	 * @param daysLow the days low
	 * @param daysHigh the days high
	 * @param yearLow the year low
	 * @param yearHigh the year high
	 * @param marketCapitalization the market capitalization
	 * @param lastTradePriceOnly the last trade price only
	 * @param daysRange the days range
	 * @param name the name
	 * @param volume the volume
	 * @param ts the ts
	 * @param dataType the data type
	 */
	public StockQuote(String symbol, String exchSymb, char provider, Integer averageDailyVolume, BigDecimal change,
			BigDecimal daysLow, BigDecimal daysHigh, BigDecimal yearLow, BigDecimal yearHigh,
			String marketCapitalization, BigDecimal lastTradePriceOnly, String daysRange, String name, Integer volume,
			Long ts, DataType dataType) {
		super();
		this.symbol = symbol;
		this.exchSymb = exchSymb;
		this.provider = provider;
		this.averageDailyVolume = averageDailyVolume;
		this.change = change;
		this.daysLow = daysLow;
		this.daysHigh = daysHigh;
		this.yearLow = yearLow;
		this.yearHigh = yearHigh;
		this.marketCapitalization = marketCapitalization;
		this.lastTradePriceOnly = lastTradePriceOnly;
		this.daysRange = daysRange;
		this.name = name;
		this.volume = volume;
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
	 * Gets the average daily volume.
	 *
	 * @return the average daily volume
	 */
	@XmlElement(name = "AverageDailyVolume")
	public Integer getAverageDailyVolume() {
		return averageDailyVolume;
	}

	/**
	 * Sets the average daily volume.
	 *
	 * @param averageDailyVolume the new average daily volume
	 */
	public void setAverageDailyVolume(Integer averageDailyVolume) {
		this.averageDailyVolume = averageDailyVolume;
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
	 * Gets the year low.
	 *
	 * @return the year low
	 */
	@XmlElement(name = "YearLow")
	public BigDecimal getYearLow() {
		return yearLow;
	}

	/**
	 * Sets the year low.
	 *
	 * @param yearLow the new year low
	 */
	public void setYearLow(BigDecimal yearLow) {
		this.yearLow = yearLow;
	}

	/**
	 * Gets the year high.
	 *
	 * @return the year high
	 */
	@XmlElement(name = "YearHigh")
	public BigDecimal getYearHigh() {
		return yearHigh;
	}

	/**
	 * Sets the year high.
	 *
	 * @param yearHigh the new year high
	 */
	public void setYearHigh(BigDecimal yearHigh) {
		this.yearHigh = yearHigh;
	}

	/**
	 * Gets the market capitalization.
	 *
	 * @return the market capitalization
	 */
	@XmlElement(name = "MarketCapitalization")
	public String getMarketCapitalization() {
		return marketCapitalization;
	}

	/**
	 * Sets the market capitalization.
	 *
	 * @param marketCapitalization the new market capitalization
	 */
	public void setMarketCapitalization(String marketCapitalization) {
		this.marketCapitalization = marketCapitalization;
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
	 * Gets the days range.
	 *
	 * @return the days range
	 */
	@XmlElement(name = "DaysRange")
	public String getDaysRange() {
		return daysRange;
	}

	/**
	 * Sets the days range.
	 *
	 * @param daysRange the new days range
	 */
	public void setDaysRange(String daysRange) {
		this.daysRange = daysRange;
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
