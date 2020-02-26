package extremeServer;

public class ResponseType {
	public static final String EMPTY_STR = "";
	
	public static final int OP_SUC = 0;
	
	public static final int PERIOD_ERROR = 100;
	public static final int PERIOD_OVERLAP = 101;
	public static final int PERIOD_NONE = 102;
	public static final int PERIOD_OVERLAP_OK = 103;
	public static final int PERIOD_FORMAT_ERR = 104;
	
	public static final int CITY_ERROR = 200;
	public static final int CITY_INV = 200;
	public static final int CITY_TRUE = 200;
	
	public static final int REGION_ERROR = 300;
	public static final int REGION_INV = 301;
	public static final int REGION_TRUE = 302;
	
	public static final int COUNTRY_ERROR = 400;
	public static final int COUNTRY_INV = 401;
	
	public static final int SPORT_ERROR = 500;
	public static final int SPORT_INV = 501;
	
	public static final int LOCATION_ERROR = 600;
	public static final int LOCATION_INV = 601;
	
	public static final int PRICE_ERROR = 700;
	public static final int PRICE_INV = 701;
	
	public static final int UNKNOWN_ERROR = 800;
	public static final int INSUF_RIGHTS = 801;
	public static final int NOT_FOUND = 802;
	public static final int WRONG_FORMAT = 803;
	public static final int UNDER_LOAD = 804;
}
