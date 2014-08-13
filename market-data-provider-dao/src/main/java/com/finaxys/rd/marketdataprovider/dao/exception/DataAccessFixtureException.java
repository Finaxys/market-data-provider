package com.finaxys.rd.marketdataprovider.dao.exception;

public class DataAccessFixtureException extends DataAccessException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7562082707029723095L;


	public DataAccessFixtureException() {
		super("Exception when preparing data access.");
	}

	public DataAccessFixtureException( Throwable throwable) {
		super("Exception when preparing data access.", throwable);
	}

}
