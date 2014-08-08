/*
 * 
 */
package com.finaxys.rd.marketdataprovider.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * The Class Rates.
 */
public class Rates {

	/** The stocks list. */
	private List<Rate> ratesList;

	/**
	 * Gets the stocks list.
	 *
	 * @return the stocks list
	 */
	@XmlElementWrapper(name = "ratesList")
	@XmlElement(name = "rate")
	public List<Rate> getRatesList() {
		if (ratesList == null)
			ratesList = new ArrayList<Rate>();
		return ratesList;
	}
	
	/**
	 * Sets the rates list.
	 *
	 * @param ratesList the new rates list
	 */
	public void setRatesList(List<Rate> ratesList) {
		this.ratesList = ratesList;
	}
}
