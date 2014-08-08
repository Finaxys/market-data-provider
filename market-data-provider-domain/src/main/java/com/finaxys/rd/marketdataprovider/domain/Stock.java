/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

// TODO: Auto-generated Javadoc
/**
 * The Class Stock.
 */
@XmlRootElement(name = "stock")
@XmlType(propOrder = {"symbol", "exchSymb", "provider","companyName","start","end","sector","industry","fullTimeEmployees"})
public class Stock implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8475180797199129533L;

	/** The symbol. */
	private String symbol;
	
	/** The exch symb. */
	private String exchSymb;
	
	/** The provider. */
	private char provider;
	
	/** The company name. */
	private String companyName;
	
	/** The start. */
	private Date start;
	
	/** The end. */
	private Date end;
	
	/** The sector. */
	private String sector;
	
	/** The industry. */
	private String industry;
	
	/** The full time employees. */
	private Integer fullTimeEmployees;

	/**
	 * Instantiates a new stock.
	 */
	public Stock() {
		super();
	}

	/**
	 * Instantiates a new stock.
	 *
	 * @param symbol the symbol
	 * @param exchSymb the exch symb
	 * @param provider the provider
	 * @param companyName the company name
	 * @param start the start
	 * @param end the end
	 * @param sector the sector
	 * @param industry the industry
	 * @param fullTimeEmployees the full time employees
	 */
	public Stock(String symbol, String exchSymb, char provider, String companyName, Date start, Date end, String sector, String industry,
			Integer fullTimeEmployees) {
		super();
		this.exchSymb = exchSymb;
		this.provider = provider;
		this.symbol = symbol;
		this.companyName = companyName;
		this.start = start;
		this.end = end;
		this.sector = sector;
		this.industry = industry;
		this.fullTimeEmployees = fullTimeEmployees;
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
	 * Gets the company name.
	 *
	 * @return the company name
	 */
	@XmlElement(name = "CompanyName")
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Sets the company name.
	 *
	 * @param companyName the new company name
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	@XmlElement(name = "start")
	public Date getStart() {
		return start;
	}

	/**
	 * Sets the start.
	 *
	 * @param start the new start
	 */
	public void setStart(Date start) {
		this.start = start;
	}
	
	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	@XmlElement(name = "end")
	public Date getEnd() {
		return end;
	}

	/**
	 * Sets the end.
	 *
	 * @param end the new end
	 */
	public void setEnd(Date end) {
		this.end = end;
	}
	
	/**
	 * Gets the sector.
	 *
	 * @return the sector
	 */
	@XmlElement(name = "Sector")
	public String getSector() {
		return sector;
	}

	/**
	 * Sets the sector.
	 *
	 * @param sector the new sector
	 */
	public void setSector(String sector) {
		sector = sector;
	}
	
	/**
	 * Gets the industry.
	 *
	 * @return the industry
	 */
	@XmlElement(name = "Industry")
	public String getIndustry() {
		return industry;
	}

	/**
	 * Sets the industry.
	 *
	 * @param industry the new industry
	 */
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	
	/**
	 * Gets the full time employees.
	 *
	 * @return the full time employees
	 */
	@XmlElement(name = "FullTimeEmployees")
	public Integer getFullTimeEmployees() {
		return fullTimeEmployees;
	}

	/**
	 * Sets the full time employees.
	 *
	 * @param fullTimeEmployees the new full time employees
	 */
	public void setFullTimeEmployees(Integer fullTimeEmployees) {
		this.fullTimeEmployees = fullTimeEmployees;
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

}
