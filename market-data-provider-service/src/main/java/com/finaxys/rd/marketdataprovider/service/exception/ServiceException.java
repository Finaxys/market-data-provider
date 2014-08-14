package com.finaxys.rd.marketdataprovider.service.exception;

public class ServiceException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7603283159015222620L;

	public ServiceException() {
		super();
	}

	public ServiceException(String m) {
		super(m);
	}

	public ServiceException(String m, Throwable throwable) {
		super(m, throwable);
	}

	public ServiceException(Throwable throwable) {
		super(throwable);
	}
}
