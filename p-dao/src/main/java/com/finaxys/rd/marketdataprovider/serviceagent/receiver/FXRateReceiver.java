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
import com.finaxys.rd.marketdataprovider.dao.FXRateDao;
import com.finaxys.rd.marketdataprovider.domain.FXRate;
import com.finaxys.rd.marketdataprovider.domain.FXRates;

// TODO: Auto-generated Javadoc
/**
 * The Class FXRateReceiver.
 */
public class FXRateReceiver implements Receiver {

	/** The logger. */
	static Logger logger = Logger.getLogger(FXRateReceiver.class);

	/** The fx rate dao. */
	@Autowired
	private FXRateDao fxRateDao;

	
	/**
	 * Instantiates a new FX rate receiver.
	 */
	public FXRateReceiver() {
		super();
	}

	/**
	 * Instantiates a new FX rate receiver.
	 *
	 * @param fxRateDao the fx rate dao
	 */
	public FXRateReceiver(FXRateDao fxRateDao) {
		super();
		this.fxRateDao = fxRateDao;
	}

	/**
	 * Gets the stock summary dao.
	 *
	 * @return the stock summary dao
	 */
	public FXRateDao getStockSummaryDao() {
		return fxRateDao;
	}

	/**
	 * Sets the stock summary dao.
	 *
	 * @param fxRateDao the new stock summary dao
	 */
	public void setStockSummaryDao(FXRateDao fxRateDao) {
		this.fxRateDao = fxRateDao;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.receiver.Receiver#receive(com.finaxys.rd.dataextraction.msg.Message)
	 */
	public boolean receive(Message msg) {
		try {
			InputStream is = new ByteArrayInputStream(msg.getBody().getContent());

			JAXBContext context = JAXBContext.newInstance(FXRates.class);
			Unmarshaller um = context.createUnmarshaller();

			um.setEventHandler(new ValidationEventHandler() {
				public boolean handleEvent(ValidationEvent event) {
					throw new RuntimeException(event.getMessage(), event.getLinkedException());
				}
			});

			FXRates fxRates = (FXRates) um.unmarshal(is);
			List<FXRate> list = fxRates.getRatesList();
			boolean resp = true;
			if (list != null) {
				logger.info("fx rates size " + list.size());
				for (FXRate stockQuote : list) {
					resp = resp && fxRateDao.add(stockQuote);
				}
			} else
				logger.info("null fx rates");

			return resp;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}

}
