package extremeClient;

import extremeServer.ExtremeServer;
import extremeServer.OperationTypeUser;

public class ExtremeUserAPI extends ExtremeClientAPI {
	public ExtremeUserAPI(int key) {
		super(key);
	}
	
	public boolean Init(String ServerIP, int port) {
		return Init(ServerIP, port, USER);
	}
	
	public boolean GetData(String[] sports, String start, String end) {
		String[] parameters = new String[OperationTypeUser.MAX_PARAMS];
		parameters[OperationTypeUser.SPORT_INDEX] = "";
		if (sports != null) {
			for (int i = 0; i < sports.length; ++i) {
				parameters[OperationTypeUser.SPORT_INDEX] += sports[i];
				if (i != sports.length - 1) {
					parameters[OperationTypeUser.SPORT_INDEX] += ExtremeServer.DELIM_SERIAL;
				}
			}
		}
		parameters[OperationTypeUser.DATE_START_INDEX] = start;
		parameters[OperationTypeUser.DATE_END_INDEX] = end;

		String toSend = "";
		for (int i = 0; i < parameters.length; ++i) {
			toSend += parameters[i];
			if (i != parameters.length - 1) {
				toSend += ExtremeServer.DELIM;
			}
		}
		toSend = OperationTypeUser.USER + ExtremeServer.DELIM + toSend;
		return SendPackage(toSend);
	}
}
