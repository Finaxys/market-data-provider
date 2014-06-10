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
 * The Class Exchanges.
 */
@XmlRootElement(name = "exchanges")
public class Exchanges {

	/** The exchanges list. */
	private List<Exchange> exchangesList;

	/**
	 * Gets the exchanges list.
	 *
	 * @return the exchanges list
	 */
	@XmlElementWrapper(name = "exchangesList")
	@XmlElement(name = "exchange")
	public List<Exchange> getExchangesList() {
		if (exchangesList == null)
			exchangesList = new ArrayList<Exchange>();
		return exchangesList;
	}

	/**
	 * Sets the exchanges list.
	 *
	 * @param exchangesList the new exchanges list
	 */
	public void setExchangesList(List<Exchange> exchangesList) {
		this.exchangesList = exchangesList;
	}
}