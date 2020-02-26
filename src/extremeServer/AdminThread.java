package extremeServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;

/*
 * performs administrator actions on the database:
 * adds a location with sport, price and date interval
 * gets a location information
 * removes a sport from a location
 * updates a sport from a location
 */
public class AdminThread extends ExtremeThread {
	public AdminThread(DataBaseHandler database) {
		super(database);
	}
	
	public void run() {
		DataBaseHandler.OperationClass op;
		while (isOn()) {
			op = database.GetOperationAdmin();
			if (op == null) {
				continue;
			}
			boolean correct = false;
			ByteBuffer response = null;
			String[] parameters = op.getParameters().split(ExtremeServer.DELIM);
			if (parameters.length != OperationTypeAdmin.MAX_PARAMS) {
				correct = false;
			} else if (parameters[OperationTypeAdmin.OP_INDEX].equals(OperationTypeAdmin.ADD_STR)) {
				boolean priceCorrectFormat = true;
				Integer price = 0;
				try {
					price = Integer.parseInt(parameters[OperationTypeAdmin.PRICE_INDEX]);
				} catch (NumberFormatException e) {
					priceCorrectFormat = false;
				}
				if (priceCorrectFormat) {
					LocalDate start, end;
					try {
						start = LocalDate.parse(parameters[OperationTypeAdmin.DATE_START_INDEX]);
						end = LocalDate.parse(parameters[OperationTypeAdmin.DATE_END_INDEX]);
					} catch (DateTimeParseException e) {
						start = null;
						end = null;
					}
					if (start != null && end != null) {
						int retValue = database.AddData(
								parameters[OperationTypeAdmin.CITY_INDEX],
								parameters[OperationTypeAdmin.REGION_INDEX],
								parameters[OperationTypeAdmin.COUNTRY_INDEX],
								parameters[OperationTypeAdmin.SPORT_INDEX],
								price, start, end);
						correct = true;
						String str = Integer.toString(retValue);
						response = ByteBuffer.allocate(str.length());
						response.put(str.getBytes());
						response.flip();
					}
				}
			} else if (parameters[OperationTypeAdmin.OP_INDEX].equals(OperationTypeAdmin.REM_STR)) {
				int retValue = database.RemData(
						parameters[OperationTypeAdmin.CITY_INDEX],
						parameters[OperationTypeAdmin.REGION_INDEX],
						parameters[OperationTypeAdmin.COUNTRY_INDEX],
						parameters[OperationTypeAdmin.SPORT_INDEX]);
				correct = true;
				String str = Integer.toString(retValue);
				response = ByteBuffer.allocate(str.length());
				response.put(str.getBytes());
				response.flip();
			} else if (parameters[OperationTypeAdmin.OP_INDEX].equals(OperationTypeAdmin.UPD_STR)) {
				Integer price = Integer.parseInt(parameters[OperationTypeAdmin.PRICE_INDEX]);
				int retValue = database.UpdateData(
						parameters[OperationTypeAdmin.CITY_INDEX],
						parameters[OperationTypeAdmin.REGION_INDEX],
						parameters[OperationTypeAdmin.COUNTRY_INDEX],
						parameters[OperationTypeAdmin.SPORT_INDEX], 
						price,
						parameters[OperationTypeAdmin.DATE_START_INDEX],
						parameters[OperationTypeAdmin.DATE_END_INDEX]);
				correct = true;
				String str = Integer.toString(retValue);
				response = ByteBuffer.allocate(str.length());
				response.put(str.getBytes());
				response.flip();
			} else if (parameters[OperationTypeAdmin.OP_INDEX].equals(OperationTypeAdmin.GET_STR)) {
				String responseStr = database.GetData(
						parameters[OperationTypeAdmin.CITY_INDEX],
						parameters[OperationTypeAdmin.REGION_INDEX],
						parameters[OperationTypeAdmin.COUNTRY_INDEX]);
				if (responseStr != null) {
					correct = true;
					response = ByteBuffer.allocate(responseStr.length());
					response.put(responseStr.getBytes());
					response.flip();
				}
			}

			if (!correct) {
				String str = Integer.toString(ResponseType.WRONG_FORMAT);
				response = ByteBuffer.allocate(str.length());
				response.put(str.getBytes());
				response.flip();
			}

			try {
				op.getConnectionSocket().write(response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
