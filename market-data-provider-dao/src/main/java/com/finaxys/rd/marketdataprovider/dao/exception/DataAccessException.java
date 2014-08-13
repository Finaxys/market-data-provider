package com.finaxys.rd.marketdataprovider.dao.exception;

public class DataAccessException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7562082707029723095L;

	public DataAccessException() {
		super();
	}

	public DataAccessException(String m) {
		super(m);
	}

	public DataAccessException(String m, Throwable throwable) {
		super(m, throwable);
	}

	public DataAccessException(Throwable throwable) {
		super(throwable);
	}
}
