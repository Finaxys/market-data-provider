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
	 * The Class CurrencyPairs.
	 */
	@XmlRootElement(name = "currencyPairs")
	public class CurrencyPairs {

		/** The currency pairs list. */
		private List<CurrencyPair> currencyPairsList;

		/**
		 * Gets the currency pairs list.
		 *
		 * @return the currency pairs list
		 */
		@XmlElementWrapper(name ="currencyPairsList")
		@XmlElement(name = "currencyPair")
		public List<CurrencyPair> getCurrencyPairsList() {
			if (currencyPairsList == null)
				currencyPairsList = new ArrayList<CurrencyPair>();
			return currencyPairsList;
		}

		/**
		 * Sets the currency pairs list.
		 *
		 * @param currencyPairsList the new currency pairs list
		 */
		public void setCurrencyPairsList(List<CurrencyPair> currencyPairsList) {
			this.currencyPairsList = currencyPairsList;
		}
	}