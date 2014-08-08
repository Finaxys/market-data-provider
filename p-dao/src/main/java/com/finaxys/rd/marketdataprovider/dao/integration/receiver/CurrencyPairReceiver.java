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
import com.finaxys.rd.marketdataprovider.dao.CurrencyPairDao;
import com.finaxys.rd.marketdataprovider.domain.CurrencyPair;
import com.finaxys.rd.marketdataprovider.domain.CurrencyPairs;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrencyPairReceiver.
 */
public class CurrencyPairReceiver implements Receiver {

	/** The logger. */
	static Logger logger = Logger.getLogger(CurrencyPairReceiver.class);

	/** The currency pair dao. */
	@Autowired
	private CurrencyPairDao currencyPairDao;

	/**
	 * Instantiates a new currency pair receiver.
	 */
	public CurrencyPairReceiver() {
		super();
	}

	/**
	 * Instantiates a new currency pair receiver.
	 *
	 * @param currencyPairDao the currency pair dao
	 */
	public CurrencyPairReceiver(CurrencyPairDao currencyPairDao) {
		super();
		this.currencyPairDao = currencyPairDao;
	}

	/**
	 * Gets the currency pair dao.
	 *
	 * @return the currency pair dao
	 */
	public CurrencyPairDao getCurrencyPairDao() {
		return currencyPairDao;
	}

	/**
	 * Sets the currency pair dao.
	 *
	 * @param currencyPairDao the new currency pair dao
	 */
	public void setCurrencyPairDao(CurrencyPairDao currencyPairDao) {
		this.currencyPairDao = currencyPairDao;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.receiver.Receiver#receive(com.finaxys.rd.dataextraction.msg.Message)
	 */
	public boolean receive(Message msg) {
		try {
			InputStream is = new ByteArrayInputStream(msg.getBody().getContent());

			JAXBContext context = JAXBContext.newInstance(CurrencyPairs.class);
			Unmarshaller um = context.createUnmarshaller();

			um.setEventHandler(new ValidationEventHandler() {
				public boolean handleEvent(ValidationEvent event) {
					throw new RuntimeException(event.getMessage(), event.getLinkedException());
				}
			});

			CurrencyPairs currencyPairs = (CurrencyPairs) um.unmarshal(is);
			List<CurrencyPair> list = currencyPairs.getCurrencyPairsList();
			boolean resp = true;
			if (list != null) {
				logger.info("currency pairs size " + list.size());
				for (CurrencyPair currencyPair : list) {
					resp = resp && currencyPairDao.add(currencyPair);
				}
			} else
				logger.info("null currency pairs");

			return resp;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}

}
