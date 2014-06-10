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
 * The Class FXRates.
 */
@XmlRootElement(name="rates")
public class FXRates {

	/** The rates list. */
	private List<FXRate> ratesList;

	/**
	 * Gets the rates list.
	 *
	 * @return the rates list
	 */
	@XmlElementWrapper(name = "ratesList")
	@XmlElement(name = "rate")
	public List<FXRate> getRatesList() {
		if (ratesList == null)
			ratesList = new ArrayList<FXRate>();
		return ratesList;
	}

	/**
	 * Sets the ind infos list.
	 *
	 * @param ratesList the new ind infos list
	 */
	public void setIndInfosList(List<FXRate> ratesList) {
		this.ratesList = ratesList;
	}
}
