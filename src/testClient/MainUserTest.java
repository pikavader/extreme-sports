package testClient;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import extremeClient.ExtremeUserAPI;

public class MainUserTest {
	public static void main(String[] args) {
		ExtremeUserAPI user = new ExtremeUserAPI(0);
		user.Init("127.0.0.1", 1254);
		int op = 0;
		String response;
		Scanner s = new Scanner(System.in);
		s.nextLine();
		while (true) {
			user.GetData(new String[] {"Hiking"}, "2020-01-02", "2020-04-02");
			while ((response = user.CheckReceived()) == null) {
				;
			}
			System.out.println(++op + ": " + response);
			user.GetData(new String[] {"Hiking", "Running"}, "2020-06-02", "2021-04-02");
			while ((response = user.CheckReceived()) == null) {
				;
			}
			System.out.println(++op + ": " + response);
			
			user.GetData(new String[] {"Hiking", "Running", "ATV"}, "2020-01-02", "2020-12-02");
			while ((response = user.CheckReceived()) == null) {
				;
			}
			System.out.println(++op + ": " + response);
			
			user.GetData(new String[] {"Racing",  "Running"}, "2020-02-02", "2020-02-05");
			while ((response = user.CheckReceived()) == null) {
				;
			}
			System.out.println(++op + ": " + response);
			
			user.GetData(new String[] {"Racing",  "Running", "Ski"}, "2020-02-02", "2021-02-05");
			while ((response = user.CheckReceived()) == null) {
				;
			}
			System.out.println(++op + ": " + response);
			
			user.GetData(new String[] {"Racing",  "Running", "Trekking"}, "2020-02-02", "2021-02-05");
			while ((response = user.CheckReceived()) == null) {
				;
			}
			System.out.println(++op + ": " + response);
//			try {
//				System.out.println("dozing off");
//				TimeUnit.SECONDS.sleep(3);
//			} catch (InterruptedException e) {
//				System.out.println("restless");
//			}
		}
	}
}
