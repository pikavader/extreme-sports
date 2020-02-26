package testServer;

import java.util.Scanner;

import extremeServer.ExtremeServer;

/*
 * Test Class for SERVER
 */

public class MainServerTest {
	public static volatile boolean keepRunning = true;
	public static final String StopSentence = "STOP";
	
	public static void main(String[] args) {
		System.out.println("Server is starting, type " + StopSentence + " to stop");
		/*
		 * Listener thread in case the server should be shut down
		 */
		Thread stopper = new Thread() {
			public void run() {
				Scanner scanner = new Scanner(System.in);
				while (true) {
					String input = scanner.nextLine();
					if (input.equals(StopSentence)) {
						System.out.println("Command recognized");
						MainServerTest.keepRunning = false;
						break;
					} else {
						System.out.println("Unrecognized command");
					}
				}
				scanner.close();
			}
		};
		stopper.start();
		
		/*
		 * create server on port 1254, with 2 admin threads and 6 user threads
		 */
		ExtremeServer server = new ExtremeServer(1254, 3, 30);
		if (!server.Start()) {
			System.out.println("Could not start the server, type " + StopSentence + " to exit");
		} else {
			while (keepRunning) {
				server.Update();
			}
			server.Stop();
			System.out.println("Server has stopped updating");
		}
		
		try {
			stopper.join();
		} catch (InterruptedException e) {
			// TODO
		}
	}
}
