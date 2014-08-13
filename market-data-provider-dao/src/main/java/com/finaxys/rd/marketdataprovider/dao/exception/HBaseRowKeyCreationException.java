package com.finaxys.rd.marketdataprovider.dao.exception;

public class HBaseRowKeyCreationException extends DataAccessException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7562082707029723095L;


	public HBaseRowKeyCreationException() {
		super("Hbase row key creation exception");
	}

	public HBaseRowKeyCreationException( Throwable throwable) {
		super("Hbase row key creation Exception", throwable);
	}

}
