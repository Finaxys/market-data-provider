package com.finaxys.rd.marketdataprovider.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import com.finaxys.rd.marketdataprovider.domain.msg.Document.DataType;


@XmlRootElement(name = "rate")
@XmlType(propOrder = { "symbol", "currency", "bucket", "dataType", "provider", "ts", "value" })
public class InterbankRateData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5256072055806288276L;

	private String symbol;
	
	/** Currency name */
	private String currency;
	
	/** BucketName name */
	private String bucket;
	
	private DateTime ts;
	

	private BigDecimal value = new BigDecimal(0);
	
	/** The provider. */
	private char provider;
	
	private DataType dataType;
	
	/**
	 * Instantiates a new rate.
	 */
	public InterbankRateData() {
	  super();
	}


	public InterbankRateData(String symbol, String currency, String bucket, DateTime ts, BigDecimal value,
			char provider, DataType dataType) {
		super();
		this.symbol = symbol;
		this.currency = currency;
		this.bucket = bucket;
		this.ts = ts;
		this.value = value;
		this.provider = provider;
		this.dataType = dataType;
	}


	@XmlElement(name = "Symbol", required = true, nillable=false)
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@XmlElement(name = "Currency", required = true, nillable=false)
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@XmlElement(name = "Bucket", required = true, nillable=false)
	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	

	@XmlElement(name = "ts", required = true, nillable=false)
	 @XmlJavaTypeAdapter(com.finaxys.rd.marketdataprovider.domain.jaxb.DateTimeAdapter.class)	
	public DateTime getTs() {
		return ts;
	}

	public void setTs(DateTime ts) {
		this.ts = ts;
	}

	@XmlElement(name = "Value", required = true, nillable=false)
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@XmlElement(name = "Provider", required = true, nillable=false)
	public char getProvider() {
		return provider;
	}

	public void setProvider(char provider) {
		this.provider = provider;
	}



	@XmlElement(name = "DataType", required = true, nillable=false)
	public DataType getDataType() {
		return dataType;
	}


	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

}
