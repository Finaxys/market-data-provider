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

import com.finaxys.rd.marketdataprovider.domain.Stock;

// TODO: Auto-generated Javadoc
/**
 * The Class Stocks.
 */
@XmlRootElement(name="stocks")
public class Stocks implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8034646506825970724L;
	/** The stocks list. */
	private List<Stock> stocksList;

	/**
	 * Gets the stocks list.
	 *
	 * @return the stocks list
	 */
	@XmlElementWrapper(name = "stocksList")
	@XmlElement(name = "stock")
	public List<Stock> getStocksList() {
		if (stocksList == null)
			stocksList = new ArrayList<Stock>();
		return stocksList;
	}

	/**
	 * Sets the stocks list.
	 *
	 * @param stocksList the new stocks list
	 */
	public void setStocksList(List<Stock> stocksList) {
		this.stocksList = stocksList;
	}
}
