package extremeServer;

import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/*
 * keeps track of operation queues 
 */
public class DataBaseHandler {
	public static class OperationClass {
		private SocketChannel connectionSocket;
		private String parameters;
		
		public OperationClass(SocketChannel connectionSocket, String parameters) {
			this.connectionSocket = connectionSocket;
			this.parameters = parameters;
		}

		public SocketChannel getConnectionSocket() {
			return connectionSocket;
		}

		public String getParameters() {
			return parameters;
		}
	}
	
	private Queue<OperationClass> AdminQueue;
	private int maxQueueLenAdmin;
	private Semaphore lockAdminProd;
	private Semaphore lockAdminCons;
	private Semaphore lockAdmin;
	
	private Queue<OperationClass> UserQueue;
	private int maxQueueLenUser;
	private Semaphore lockUserProd;
	private Semaphore lockUserCons;
	private Semaphore lockUser;
	
	private Hierarchy hierarchy;
	private Semaphore rwLock;
	private Semaphore countLock;
	private int readCount;
	
	public DataBaseHandler(int maxQLA, int maxQLU) {
		AdminQueue = new LinkedList<>();
		UserQueue = new LinkedList<>();
		
		maxQueueLenAdmin = maxQLA;
		maxQueueLenUser = maxQLU;
		
		lockAdminProd = new Semaphore(maxQueueLenAdmin);
		lockAdminCons = new Semaphore(0);
		lockAdmin = new Semaphore(1);
		
		lockUserProd = new Semaphore(maxQueueLenUser);
		lockUserCons = new Semaphore(0);
		lockUser = new Semaphore(1);

		hierarchy = new Hierarchy();
		
		rwLock = new Semaphore(1);
		countLock = new Semaphore(1);
		readCount = 0;
	}
	
	/*
	 * methods used for implementing thread-safe reading from and writing to the hierarchy
	 * uses readers - writers algorithm
	 */
	private void BeginHierarchyReading() {
		try {
			countLock.acquire();
		} catch (InterruptedException e) {
			// TODO
		}
		
		++readCount;
		if (readCount == 1) {
			try {
				rwLock.acquire();
			} catch (InterruptedException e) {
				// TODO
			}
		}
		countLock.release();
	}
	
	private void EndHierarchyReading() {
		try {
			countLock.acquire();
		} catch (InterruptedException e) {
			// TODO
		}
		--readCount;
		if (readCount == 0) {
			rwLock.release();
		}
		countLock.release();
	}
	
	private void BeginHierarchyWriting() {
		try {
			rwLock.acquire();
		} catch (InterruptedException e) {
			// TODO
		}
	}
	
	private void EndHierarchyWriting() {
		rwLock.release();
	}
	
	/*
	 * Queues operations - producer-consumer
	 */
	public int AddOperationAdmin(OperationClass operation) {
		if (AdminQueue.size() == this.maxQueueLenAdmin) {
			return ResponseType.UNDER_LOAD;
		}
		try {
			lockAdminProd.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AdminQueue.offer(operation);
		lockAdminCons.release();
		return ResponseType.OP_SUC;
	}
	
	public int AddOperationUser(OperationClass operation) {
		if (AdminQueue.size() == this.maxQueueLenUser) {
			return ResponseType.UNDER_LOAD;
		}
		try {
			lockUserProd.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserQueue.offer(operation);
		lockUserCons.release();
		return ResponseType.OP_SUC;
	}
	
	public OperationClass GetOperationAdmin() {
		if (!Thread.currentThread().getStackTrace()[2].getClassName().equals(AdminThread.class.getName())) {
			return null;
		}
		OperationClass operation = null;
		try {
			lockAdminCons.acquire();
		} catch (InterruptedException e) {
			// TODO
		}
		if (AdminQueue.size() != 0) {
			try {
				lockAdmin.acquire();
			} catch (InterruptedException e) {
				// TODO
			}
			operation = AdminQueue.poll();
			lockAdmin.release();
		}
		lockAdminProd.release();
		return operation;
	}
	
	public OperationClass GetOperationUser() {
		if (!Thread.currentThread().getStackTrace()[2].getClassName().equals(UserThread.class.getName())) {
			return null;
		}
		OperationClass operation = null;
		try {
			lockUserCons.acquire();
		} catch (InterruptedException e) {
			// TODO 
			
		}
		if (UserQueue.size() != 0) {
			try {
				lockUser.acquire();
			} catch (InterruptedException e) {
				// TODO
			}
			operation = UserQueue.poll();
			lockUser.release();
		}
		lockUserProd.release();
		return operation;
	}
	
	/*
	 * write
	 */
	public int AddData(String city, String region, String country, String sport, int price, LocalDate start, LocalDate end) {
		if (!Thread.currentThread().getStackTrace()[2].getClassName().equals(AdminThread.class.getName())) {
			return ResponseType.INSUF_RIGHTS;
		}
		int retValue = 0;
		BeginHierarchyWriting();
		retValue =  hierarchy.Add(country, region, city, sport, start, end, price);
		EndHierarchyWriting();
		return retValue;
	}
	
	/*
	 * read
	 */
	public String GetData(String city, String region, String country) {
		if (!Thread.currentThread().getStackTrace()[2].getClassName().equals(AdminThread.class.getName())) {
			return null;
		}
		String response = ""; 
		BeginHierarchyReading();
		response = hierarchy.Get(country, region, city);
		EndHierarchyReading();
		return response;
	}
	
	/*
	 * write
	 */
	public int RemData(String city, String region, String country, String sport) {
		if (!Thread.currentThread().getStackTrace()[2].getClassName().equals(AdminThread.class.getName())) {
			return ResponseType.INSUF_RIGHTS;
		}
		int retValue = 0;
		BeginHierarchyWriting();
		retValue = hierarchy.Remove(country, region, city, sport);
		EndHierarchyWriting();
		return retValue;
	}
	
	/*
	 * write
	 * takes only string parameters so the price, start or end can be the same
	 */
	public int UpdateData(String city, String region, String country, String sport, int price, String dateStart, String dateEnd) {
		if (!Thread.currentThread().getStackTrace()[2].getClassName().equals(AdminThread.class.getName())) {
			return ResponseType.INSUF_RIGHTS;
		}
		int retValue = 0;
		BeginHierarchyWriting();
		retValue = hierarchy.Update(country, region, city, sport, price, dateStart, dateEnd);
		EndHierarchyWriting();
		return retValue;
	}
	
	/*
	 * read
	 * sports[] - list of sports the user wants to know more about
	 * start-end - period in which the user is searching for a sport
	 */
	public String GetForUser(String[] sports, LocalDate start, LocalDate end) {
		if (!Thread.currentThread().getStackTrace()[2].getClassName().equals(UserThread.class.getName())) {
			return null;
		}
		String response = "";
		boolean changed = false;
		String list = "";
		for (int i = 0; i < sports.length - 1; ++i) {
			BeginHierarchyReading();
			list = hierarchy.GetAndSort(sports[i], start, end);
			EndHierarchyReading();
			if (!list.equals(ResponseType.EMPTY_STR)) {
				response += list + ExtremeServer.DELIM_LIST;
				changed = true;
			}
		}
		BeginHierarchyReading();
		list = hierarchy.GetAndSort(sports[sports.length - 1], start, end);
		EndHierarchyReading();
		if (!list.equals(ResponseType.EMPTY_STR)) {
			response += list + ExtremeServer.DELIM_LIST;
			changed = true;
		}
		
		if (changed) {
			return response;
		}
		return Integer.toString(ResponseType.NOT_FOUND);
	}
	
	public void StoreData() {
		// TODO
	}
	
	public void SaveAndClose() {
		for (int i = 0; i < maxQueueLenAdmin; ++i) {
			lockAdminCons.release();
		}
		for (int i = 0; i < maxQueueLenUser; ++i) {
			lockUserCons.release();
		}
		StoreData();
	}
}
