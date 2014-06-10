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

import com.finaxys.rd.marketdataprovider.dao.IndexQuoteDao;
import com.finaxys.rd.marketdataprovider.domain.IndexQuote;
import com.finaxys.rd.marketdataprovider.domain.IndexQuotes;
import com.finaxys.rd.dataextraction.msg.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexQuoteReceiver.
 */
public class IndexQuoteReceiver  implements Receiver {

	/** The logger. */
	static Logger logger = Logger.getLogger(IndexQuoteReceiver.class);

	/** The index quote dao. */
	@Autowired
	private IndexQuoteDao indexQuoteDao;

	
	/**
	 * Instantiates a new index quote receiver.
	 */
	public IndexQuoteReceiver() {
		super();
	}

	/**
	 * Instantiates a new index quote receiver.
	 *
	 * @param indexQuoteDao the index quote dao
	 */
	public IndexQuoteReceiver(IndexQuoteDao indexQuoteDao) {
		super();
		this.indexQuoteDao = indexQuoteDao;
	}

	/**
	 * Gets the index quote dao.
	 *
	 * @return the index quote dao
	 */
	public IndexQuoteDao getIndexQuoteDao() {
		return indexQuoteDao;
	}

	/**
	 * Sets the index quote dao.
	 *
	 * @param indexQuoteDao the new index quote dao
	 */
	public void setIndexQuoteDao(IndexQuoteDao indexQuoteDao) {
		this.indexQuoteDao = indexQuoteDao;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.receiver.Receiver#receive(com.finaxys.rd.dataextraction.msg.Message)
	 */
	public boolean receive(Message msg) {
		try {
			InputStream is = new ByteArrayInputStream(msg.getBody().getContent());
			
			JAXBContext context = JAXBContext.newInstance(IndexQuotes.class);
			Unmarshaller um = context.createUnmarshaller();

			um.setEventHandler(new ValidationEventHandler() {
				public boolean handleEvent(ValidationEvent event) {
					throw new RuntimeException(event.getMessage(), event.getLinkedException());
				}
			});

			IndexQuotes indexQuotes = (IndexQuotes) um.unmarshal(is);
			List<IndexQuote> list = indexQuotes.getIndexQuotesList();
			boolean resp = true;
			if (list != null) {
				logger.info("index quotes size " + list.size());
				for (IndexQuote indexQuote : list) {
					resp = resp && indexQuoteDao.add(indexQuote);
				}
			} else
				logger.info("null index quotes");

			return resp;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}

}
