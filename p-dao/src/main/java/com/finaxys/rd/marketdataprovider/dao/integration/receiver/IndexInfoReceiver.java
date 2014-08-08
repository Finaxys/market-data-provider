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
import com.finaxys.rd.marketdataprovider.dao.IndexInfoDao;
import com.finaxys.rd.marketdataprovider.domain.IndexInfo;
import com.finaxys.rd.marketdataprovider.domain.IndexInfos;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexInfoReceiver.
 */
public class IndexInfoReceiver implements Receiver {

	/** The logger. */
	static Logger logger = Logger.getLogger(IndexInfoReceiver.class);

	/** The index info dao. */
	@Autowired
	private IndexInfoDao indexInfoDao;

	
	/**
	 * Instantiates a new index info receiver.
	 */
	public IndexInfoReceiver() {
		super();
	}

	/**
	 * Instantiates a new index info receiver.
	 *
	 * @param indexInfoDao the index info dao
	 */
	public IndexInfoReceiver(IndexInfoDao indexInfoDao) {
		super();
		this.indexInfoDao = indexInfoDao;
	}

	/**
	 * Gets the index info dao.
	 *
	 * @return the index info dao
	 */
	public IndexInfoDao getIndexInfoDao() {
		return indexInfoDao;
	}

	/**
	 * Sets the index info dao.
	 *
	 * @param indexInfoDao the new index info dao
	 */
	public void setIndexInfoDao(IndexInfoDao indexInfoDao) {
		this.indexInfoDao = indexInfoDao;
	}

	/* (non-Javadoc)
	 * @see com.finaxys.rd.marketdataprovider.serviceagent.receiver.Receiver#receive(com.finaxys.rd.dataextraction.msg.Message)
	 */
	public boolean receive(Message msg) {
		try {
			InputStream is = new ByteArrayInputStream(msg.getBody().getContent());

			JAXBContext context = JAXBContext.newInstance(IndexInfos.class);
			Unmarshaller um = context.createUnmarshaller();

			um.setEventHandler(new ValidationEventHandler() {
				public boolean handleEvent(ValidationEvent event) {
					throw new RuntimeException(event.getMessage(), event.getLinkedException());
				}
			});

			IndexInfos indexInfos = (IndexInfos) um.unmarshal(is);
			List<IndexInfo> list = indexInfos.getIndexInfosList();
			boolean resp = true;
			if (list != null) {
				logger.info("index infos size " + list.size());
				for (IndexInfo indexInfo : list) {
					resp = resp && indexInfoDao.add(indexInfo);
				}
			} else
				logger.info("null index infos");

			return resp;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}

}
