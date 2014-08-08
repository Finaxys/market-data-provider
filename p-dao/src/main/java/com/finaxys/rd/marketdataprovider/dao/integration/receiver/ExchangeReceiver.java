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
import com.finaxys.rd.marketdataprovider.dao.ExchangeDao;
import com.finaxys.rd.marketdataprovider.domain.Exchange;
import com.finaxys.rd.marketdataprovider.domain.Exchanges;

// TODO: Auto-generated Javadoc
/**
 * The Class ExchangeReceiver.
 */
public class ExchangeReceiver implements Receiver {

	/** The logger. */
	static Logger logger = Logger.getLogger(ExchangeReceiver.class);

	/** The exchange dao. */
	@Autowired
	private ExchangeDao exchangeDao;

	
	/**
	 * Instantiates a new exchange receiver.
	 */
	public ExchangeReceiver() {
		super();
	}

	/**
	 * Instantiates a new exchange receiver.
	 *
	 * @param exchangeDao the exchange dao
	 */
	public ExchangeReceiver(ExchangeDao exchangeDao) {
		super();
		this.exchangeDao = exchangeDao;
	}

	/**
	 * Gets the exchange dao.
	 *
	 * @return the exchange dao
	 */
	public ExchangeDao getExchangeDao() {
		return exchangeDao;
	}

	/**
	 * Sets the exchange dao.
	 *
	 * @param exchangeDao the new exchange dao
	 */
	public void setExchangeDao(ExchangeDao exchangeDao) {
		this.exchangeDao = exchangeDao;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.receiver.Receiver#receive(com.finaxys.rd.dataextraction.msg.Message)
	 */
	public boolean receive(Message msg) {
		try {
			InputStream is = new ByteArrayInputStream(msg.getBody().getContent());

			JAXBContext context = JAXBContext.newInstance(Exchanges.class);
			Unmarshaller um = context.createUnmarshaller();

			um.setEventHandler(new ValidationEventHandler() {
				public boolean handleEvent(ValidationEvent event) {
					throw new RuntimeException(event.getMessage(), event.getLinkedException());
				}
			});

			Exchanges exchanges = (Exchanges) um.unmarshal(is);
			List<Exchange> list = exchanges.getExchangesList();
			boolean resp = true;
			if (list != null) {
				logger.info("exchanges size " + list.size());
				for (Exchange exchange : list) {
					resp = resp && exchangeDao.add(exchange);
				}
			} else
				logger.info("null exchanges");

			return resp;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}

}
