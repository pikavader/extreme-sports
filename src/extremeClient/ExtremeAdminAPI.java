package extremeClient;

import java.util.Date;

import extremeServer.ExtremeServer;
import extremeServer.OperationTypeAdmin;

public class ExtremeAdminAPI extends ExtremeClientAPI {
	public ExtremeAdminAPI(int key) {
		super(key);
	}

	public boolean Init(String ServerIP, int port) {
		return Init(ServerIP, port, ADMIN);
	}
	
	public boolean addLocation(String city, String region, String country, String sport, int price, String start, String end) {
		String[] parameters = new String[OperationTypeAdmin.MAX_PARAMS];
		parameters[OperationTypeAdmin.OP_INDEX] = extremeServer.OperationTypeAdmin.ADD_STR;
		parameters[OperationTypeAdmin.CITY_INDEX] = city;
		parameters[OperationTypeAdmin.REGION_INDEX] = region;
		parameters[OperationTypeAdmin.COUNTRY_INDEX] = country;
		parameters[OperationTypeAdmin.SPORT_INDEX] = sport;
		parameters[OperationTypeAdmin.PRICE_INDEX] = Integer.toString(price);
		parameters[OperationTypeAdmin.DATE_START_INDEX] = start;
		parameters[OperationTypeAdmin.DATE_END_INDEX] = end;
		String toSend = "";
		for (int i = 0; i < parameters.length; ++i) {
			toSend += parameters[i];
			if (i != parameters.length - 1) {
				toSend += ExtremeServer.DELIM;
			}
		}
		toSend = OperationTypeAdmin.ADMIN + ExtremeServer.DELIM + toSend;
		return SendPackage(toSend);
	}
	
	public boolean removeLocation(String city, String region, String country, String sport) {
		String[] parameters = new String[OperationTypeAdmin.MAX_PARAMS];
		parameters[OperationTypeAdmin.OP_INDEX] = extremeServer.OperationTypeAdmin.REM_STR;
		parameters[OperationTypeAdmin.CITY_INDEX] = city;
		parameters[OperationTypeAdmin.REGION_INDEX] = region;
		parameters[OperationTypeAdmin.COUNTRY_INDEX] = country;
		parameters[OperationTypeAdmin.SPORT_INDEX] = sport;
		parameters[OperationTypeAdmin.PRICE_INDEX] = extremeServer.OperationTypeAdmin.NONE;
		parameters[OperationTypeAdmin.DATE_START_INDEX] = extremeServer.OperationTypeAdmin.NONE;
		parameters[OperationTypeAdmin.DATE_END_INDEX] = extremeServer.OperationTypeAdmin.NONE;
		String toSend = "";
		for (int i = 0; i < parameters.length; ++i) {
			toSend += parameters[i];
			if (i != parameters.length - 1) {
				toSend += ExtremeServer.DELIM;
			}
		}
		toSend = OperationTypeAdmin.ADMIN + ExtremeServer.DELIM + toSend;
		return SendPackage(toSend);
	}
	
	public boolean updateLocation(String city, String region, String country, String sport, int price, String start, String end) {
		String[] parameters = new String[OperationTypeAdmin.MAX_PARAMS];
		parameters[OperationTypeAdmin.OP_INDEX] = extremeServer.OperationTypeAdmin.UPD_STR;
		parameters[OperationTypeAdmin.CITY_INDEX] = city;
		parameters[OperationTypeAdmin.REGION_INDEX] = region;
		parameters[OperationTypeAdmin.COUNTRY_INDEX] = country;
		parameters[OperationTypeAdmin.SPORT_INDEX] = sport;
		parameters[OperationTypeAdmin.PRICE_INDEX] = Integer.toString(price);
		parameters[OperationTypeAdmin.DATE_START_INDEX] = start;
		parameters[OperationTypeAdmin.DATE_END_INDEX] = end;
		String toSend = "";
		for (int i = 0; i < parameters.length; ++i) {
			toSend += parameters[i];
			if (i != parameters.length - 1) {
				toSend += ExtremeServer.DELIM;
			}
		}
		toSend = OperationTypeAdmin.ADMIN + ExtremeServer.DELIM + toSend;
		return SendPackage(toSend);
	}
	
	public boolean getLocation(String city, String region, String country) {
		String[] parameters = new String[OperationTypeAdmin.MAX_PARAMS];
		parameters[OperationTypeAdmin.OP_INDEX] = extremeServer.OperationTypeAdmin.GET_STR;
		parameters[OperationTypeAdmin.CITY_INDEX] = city;
		parameters[OperationTypeAdmin.REGION_INDEX] = region;
		parameters[OperationTypeAdmin.COUNTRY_INDEX] = country;
		parameters[OperationTypeAdmin.SPORT_INDEX] = extremeServer.OperationTypeAdmin.NONE;
		parameters[OperationTypeAdmin.PRICE_INDEX] = extremeServer.OperationTypeAdmin.NONE;
		parameters[OperationTypeAdmin.DATE_START_INDEX] = extremeServer.OperationTypeAdmin.NONE;
		parameters[OperationTypeAdmin.DATE_END_INDEX] = extremeServer.OperationTypeAdmin.NONE;
		String toSend = "";
		for (int i = 0; i < parameters.length; ++i) {
			toSend += parameters[i];
			if (i != parameters.length - 1) {
				toSend += ExtremeServer.DELIM;
			}
		}
		toSend = OperationTypeAdmin.ADMIN + ExtremeServer.DELIM + toSend;
		return SendPackage(toSend);
	}
}
