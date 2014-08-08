/*
 * 
 */
package com.finaxys.rd.marketdataprovider.serviceagent.receiver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.finaxys.rd.marketdataprovider.dao.StockQuoteDao;
import com.finaxys.rd.marketdataprovider.domain.StockQuote;
import com.finaxys.rd.marketdataprovider.domain.StocksQuotes;
import com.finaxys.rd.dataextraction.msg.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class StockQuoteReceiver.
 */
public class StockQuoteReceiver implements Receiver{
	
	/** The logger. */
	static Logger logger = Logger.getLogger(StockQuoteReceiver.class);

	/** The stock quote dao. */
	@Autowired
	private StockQuoteDao stockQuoteDao;

	
	/**
	 * Instantiates a new stock quote receiver.
	 */
	public StockQuoteReceiver() {
		super();
	}

	/**
	 * Instantiates a new stock quote receiver.
	 *
	 * @param stockQuoteDao the stock quote dao
	 */
	public StockQuoteReceiver(StockQuoteDao stockQuoteDao) {
		super();
		this.stockQuoteDao = stockQuoteDao;
	}

	/**
	 * Gets the stock summary dao.
	 *
	 * @return the stock summary dao
	 */
	public StockQuoteDao getStockSummaryDao() {
		return stockQuoteDao;
	}

	/**
	 * Sets the stock summary dao.
	 *
	 * @param stockQuoteDao the new stock summary dao
	 */
	public void setStockSummaryDao(StockQuoteDao stockQuoteDao) {
		this.stockQuoteDao = stockQuoteDao;
	}
	
	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.receiver.Receiver#receive(com.finaxys.rd.dataextraction.msg.Message)
	 */
	public boolean receive(Message msg) {
		try {
			InputStream is = new ByteArrayInputStream(msg.getBody().getContent());
			
			JAXBContext context = JAXBContext.newInstance(StocksQuotes.class);
			Unmarshaller um = context.createUnmarshaller();

			um.setEventHandler(new ValidationEventHandler() {
				public boolean handleEvent(ValidationEvent event) {
					throw new RuntimeException(event.getMessage(), event.getLinkedException());
				}
			});

			StocksQuotes stocksQuotes = (StocksQuotes) um.unmarshal(is);
			List<StockQuote> list = stocksQuotes.getQuotesList();
			boolean resp = true;
			if (list != null) {
				logger.info("stocks quotes size " + list.size());
				for (StockQuote stockQuote : list) {
					resp = resp && stockQuoteDao.add(stockQuote);
				}
			} else
				logger.info("null stocks quotes");

			return resp;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}


}
