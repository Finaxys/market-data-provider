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

import com.finaxys.rd.marketdataprovider.domain.IndexInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class IndexInfos.
 */
@XmlRootElement(name="indexInfos")
public class IndexInfos implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -125329544452312068L;
	/** The index infos list. */
	private List<IndexInfo> indexInfosList;

	/**
	 * Gets the index infos list.
	 *
	 * @return the index infos list
	 */
	@XmlElementWrapper(name = "indexInfosList")
	@XmlElement(name = "indexInfo")
	public List<IndexInfo> getIndexInfosList() {
		if (indexInfosList == null)
			indexInfosList = new ArrayList<IndexInfo>();
		return indexInfosList;
	}

	/**
	 * Sets the ind infos list.
	 *
	 * @param indexInfosList the new ind infos list
	 */
	public void setIndInfosList(List<IndexInfo> indexInfosList) {
		this.indexInfosList = indexInfosList;
	}
}
