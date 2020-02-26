package extremeServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class UserThread extends ExtremeThread {
	public UserThread(DataBaseHandler database) {
		super(database);
	}

	public void run() {
		DataBaseHandler.OperationClass op;
		while (isOn()) {
			op = database.GetOperationUser();
			if (op == null) {
				continue;
			}
			boolean correct = false;
			ByteBuffer response = null;
			String[] parameters = op.getParameters().split(ExtremeServer.DELIM);
			
			if (parameters.length != OperationTypeUser.MAX_PARAMS) {
				correct = false;
			} else {
				String[] sports = parameters[OperationTypeUser.SPORT_INDEX].split(ExtremeServer.DELIM_SERIAL);
				LocalDate start, end;
				try {
					start = LocalDate.parse(parameters[OperationTypeUser.DATE_START_INDEX]);
					end = LocalDate.parse(parameters[OperationTypeUser.DATE_END_INDEX]);
				} catch (DateTimeParseException e) {
					start = null;
					end = null;
				}
				if (start == null || end == null) {
					correct = false;
				} else {
					if (sports.length == 1 && sports[0].equals("")) {
						correct = false;
					} else {
						String responseStr = database.GetForUser(sports, start, end);
						if (responseStr != null) {
							correct = true;
							response = ByteBuffer.allocate(responseStr.length());
							response.put(responseStr.getBytes());
							response.flip();
						}
					}
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
				e.printStackTrace();
			}
		}
	}
}
