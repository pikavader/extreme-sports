package testClient;

import java.util.Scanner;
import extremeClient.ExtremeAdminAPI;
import extremeClient.ExtremeUserAPI;

/*
 * Test Class for Client - ADMIN
 */
public class MainAdminTest {
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		ExtremeAdminAPI admin = new ExtremeAdminAPI(0);
		admin.Init("127.0.0.1", 1254);
		String response;
		admin.removeLocation("Iasi", "Iasi", "Romania", "Trekking");
		while ((response = admin.CheckReceived()) == null) {
			;
		}
		System.out.println("REM Iasi ATV: " + response);
		s.nextLine();
		
			admin.addLocation("Iasi", "Iasi", "Romania", "ATV", 10, "2020-02-01", "2020-05-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Iasi ATV: " + response);
			s.nextLine();
			
			admin.addLocation("Pascani", "Iasi", "Romania", "ATV", 5, "2020-03-01", "2020-06-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Pascani ATV: " + response);
			s.nextLine();
			
			admin.addLocation("Pascani", "Iasi", "Romania", "ATV", 8, "2020-06-02", "2020-09-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Pascani ATV: " + response);
			s.nextLine();
			
			admin.updateLocation("Iasi", "Iasi", "Romania", "ATV", 20, "2020-06-02", "2020-09-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Iasi ATV: " + response);
			s.nextLine();

			admin.addLocation("Tibanesti", "Iasi", "Romania", "ATV", 2, "2020-01-02", "2020-12-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Tibanesti ATV: " + response);
			s.nextLine();

			admin.addLocation("Iasi", "Iasi", "Romania", "Running", 30, "2020-01-02", "2020-12-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Iasi Running: " + response);
			s.nextLine();
			
			admin.addLocation("Targu Frumos", "Iasi", "Romania", "Running", 20, "2020-03-02", "2020-09-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Targu Frumos ATV: " + response);
			s.nextLine();
			
			admin.addLocation("Iasi", "Iasi", "Romania", "Racing", 1000, "2020-05-02", "2020-07-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Iasi Racing: " + response);
			s.nextLine();

			admin.addLocation("Brasov", "Brasov", "Romania", "Racing", 300, "2020-02-02", "2020-10-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Brasov Racing: " + response);
			s.nextLine();
			
			admin.addLocation("Busteni", "Prahova", "Romania", "Racing", 100, "2020-02-02", "2020-10-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Busteni Racing: " + response);
			s.nextLine();
			
			admin.addLocation("Azuga", "Prahova", "Romania", "Racing", 2000, "2020-02-02", "2020-10-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Azuga Racing: " + response);
			s.nextLine();

			admin.addLocation("Predeal", "Brasov", "Romania", "Flying", 400, "2020-02-02", "2020-10-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Predeal Flying: " + response);
			s.nextLine();
			
			admin.addLocation("Predeal", "Brasov", "Romania", "Trekking", 10, "2020-12-15", "2021-01-15");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Predeal Trekking: " + response);
			s.nextLine();
			
			admin.updateLocation("Predeal", "Brasov", "Romania", "Trekking", 30, "2020-12-25", "2021-01-05");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("UPD Predeal Trekking: " + response);
			s.nextLine();
			
			admin.getLocation("Predeal", "Brasov", "Romania");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("GET Predeal: " + response);
			s.nextLine();
			
			admin.addLocation("Iasi", "Iasi", "Romania", "Biking", 10, "2020-02-01", "2020-05-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Iasi Biking: " + response);
			s.nextLine();
			
			admin.addLocation("Pascani", "Iasi", "Romania", "Biking", 5, "2020-03-01", "2020-06-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Pascani Biking: " + response);
			s.nextLine();
			
			admin.addLocation("Pascani", "Iasi", "Romania", "Biking", 8, "2020-06-02", "2020-09-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Pascani Biking: " + response);
			s.nextLine();
			
			admin.updateLocation("Iasi", "Iasi", "Romania", "Biking", 20, "2020-06-02", "2020-09-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("UPD Iasi ATV: " + response);
			s.nextLine();

			admin.addLocation("Tibanesti", "Iasi", "Romania", "Biking", 2, "2020-01-02", "2020-12-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Tibanesti ATV: " + response);
			s.nextLine();

			admin.addLocation("Iasi", "Iasi", "Romania", "ATV", 30, "2020-01-02", "2020-12-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Iasi ATV: " + response);
			s.nextLine();
			
			admin.addLocation("Targu Frumos", "Iasi", "Romania", "ATV", 20, "2020-03-02", "2020-09-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Targu Frumos ATV: " + response);
			s.nextLine();
			
			admin.addLocation("Iasi", "Iasi", "Romania", "Racing", 100, "2020-05-02", "2020-07-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Iasi Racing: " + response);
			s.nextLine();

			admin.addLocation("Brasov", "Brasov", "Romania", "Hiking", 3, "2020-02-02", "2020-10-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Brasov Hiking: " + response);
			s.nextLine();
			
			admin.addLocation("Busteni", "Prahova", "Romania", "Hiking", 1, "2020-02-02", "2020-10-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Busteni Hiking: " + response);
			s.nextLine();
			
			admin.addLocation("Azuga", "Prahova", "Romania", "Hiking", 2, "2020-02-02", "2020-10-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Azuga Hiking: " + response);
			s.nextLine();

			admin.addLocation("Predeal", "Brasov", "Romania", "Hiking", 4, "2020-02-02", "2020-10-01");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Predeal Hiking: " + response);
			s.nextLine();
			
			admin.addLocation("Predeal", "Brasov", "Romania", "Ski", 10, "2020-12-15", "2021-01-15");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("ADD Predeal Ski: " + response);
			s.nextLine();
			
			admin.updateLocation("Predeal", "Brasov", "Romania", "Ski", 30, "2020-12-25", "2021-01-05");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("UPD Predeal Ski: " + response);
			s.nextLine();
			
			admin.getLocation("Iasi", "Iasi", "Romania");
			while ((response = admin.CheckReceived()) == null) {
				;
			}
			System.out.println("GET Iasi: " + response);
			s.nextLine();
		
		admin.removeLocation("Iasi", "Iasi", "Romania", "Biking");
		
		admin.CloseConnection();
	}
}
