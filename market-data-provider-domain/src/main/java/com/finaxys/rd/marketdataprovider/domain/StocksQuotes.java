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

import com.finaxys.rd.marketdataprovider.domain.StockQuote;

// TODO: Auto-generated Javadoc
/**
 * The Class StocksQuotes.
 */
@XmlRootElement(name="quotes")
public class StocksQuotes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7497920947539676327L;
	/** The quotes list. */
	private List<StockQuote> quotesList;

	/**
	 * Gets the quotes list.
	 *
	 * @return the quotes list
	 */
	@XmlElementWrapper(name = "quotesList")
	@XmlElement(name = "quote")
	public List<StockQuote> getQuotesList() {
		if (quotesList == null)
			quotesList = new ArrayList<StockQuote>();
		return quotesList;
	}

	/**
	 * Sets the quotes list.
	 *
	 * @param quotesList the new quotes list
	 */
	public void setQuotesList(List<StockQuote> quotesList) {
		this.quotesList = quotesList;
	}
}
