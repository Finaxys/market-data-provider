/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.finaxys.rd.marketdataprovider.domain.InterbankRate;

// TODO: Auto-generated Javadoc
/**
 * The Class Rates.
 */
@XmlRootElement(name = "rates")
public class InterbankRates implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1688269905374225854L;
	/** The stocks list. */
	private List<InterbankRate> ratesList;

	/**
	 * Gets the stocks list.
	 *
	 * @return the stocks list
	 */
	@XmlElementWrapper(name = "ratesList")
	@XmlElement(name = "rate")
	public List<InterbankRate> getRatesList() {
		if (ratesList == null)
			ratesList = new ArrayList<InterbankRate>();
		return ratesList;
	}
	
	/**
	 * Sets the rates list.
	 *
	 * @param ratesList the new rates list
	 */
	public void setRatesList(List<InterbankRate> ratesList) {
		this.ratesList = ratesList;
	}
}
