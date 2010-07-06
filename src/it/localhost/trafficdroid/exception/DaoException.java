package it.localhost.trafficdroid.exception;

public class DaoException extends Exception {
	public static final int SAXException = 0;
	public static final int IOException = 1;
	public static final int ParserConfigurationException = 2;
	public static final int FactoryConfigurationError = 3;
	public static final int MalformedURLException = 4;
	private static final long serialVersionUID = 1L;
	private int key;

	public DaoException(int key, String msg) {
		super(msg);
		this.key = key;
	}

	public int getKey() {
		return key;
	}
}
