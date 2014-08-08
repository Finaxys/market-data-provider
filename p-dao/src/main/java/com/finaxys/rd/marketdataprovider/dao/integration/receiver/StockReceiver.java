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

import com.finaxys.rd.dataextraction.msg.Message;
import com.finaxys.rd.marketdataprovider.dao.StockDao;
import com.finaxys.rd.marketdataprovider.domain.Stock;
import com.finaxys.rd.marketdataprovider.domain.Stocks;

// TODO: Auto-generated Javadoc
/**
 * The Class StockReceiver.
 */
public class StockReceiver implements Receiver {

	/** The logger. */
	static Logger logger = Logger.getLogger(StockReceiver.class);

	/** The stock dao. */
	@Autowired
	private StockDao stockDao;

	/**
	 * Instantiates a new stock receiver.
	 */
	public StockReceiver() {
		super();
	}

	/**
	 * Instantiates a new stock receiver.
	 *
	 * @param stockDao the stock dao
	 */
	public StockReceiver(StockDao stockDao) {
		super();
		this.stockDao = stockDao;
	}

	/**
	 * Gets the stock summary dao.
	 *
	 * @return the stock summary dao
	 */
	public StockDao getStockSummaryDao() {
		return stockDao;
	}

	/**
	 * Sets the stock summary dao.
	 *
	 * @param stockDao the new stock summary dao
	 */
	public void setStockSummaryDao(StockDao stockDao) {
		this.stockDao = stockDao;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.receiver.Receiver#receive(com.finaxys.rd.dataextraction.msg.Message)
	 */
	public boolean receive(Message msg) {
		try {
			InputStream is = new ByteArrayInputStream(msg.getBody().getContent());

			JAXBContext context = JAXBContext.newInstance(Stocks.class);
			Unmarshaller um = context.createUnmarshaller();

			um.setEventHandler(new ValidationEventHandler() {
				public boolean handleEvent(ValidationEvent event) {
					throw new RuntimeException(event.getMessage(), event.getLinkedException());
				}
			});

			Stocks stocks = (Stocks) um.unmarshal(is);
			List<Stock> list = stocks.getStocksList();
			boolean resp = true;
			if (list != null) {
				logger.info("stocks size " + list.size());
				for (Stock stock : list) {
					resp = resp && stockDao.add(stock);
				}
			} else
				logger.info("null stocks");

			return resp;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}

}
