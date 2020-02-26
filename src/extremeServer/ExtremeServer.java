package extremeServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class ExtremeServer {
	public static final String DateFormat = "yyyy-mm-dd";
	public static volatile boolean keepWorking = true;
	public static final String DELIM = "/";
	public static final String DELIM_SERIAL = ";";
	public static final String DELIM_LIST = "=";
	public static final int MAXREADCLIENT = 512;
	
	private static int QUEUESIZEMULT = 100;
	
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private ServerSocket serverSocket;
	private int port = -1;
	private LinkedList<SocketChannel> clients = new LinkedList<>();

	private int adminCount;
	private AdminThread[] adminThreads;
	private int userCount;
	private UserThread[] userThreads;
	private DataBaseHandler database;
	
	/*
	 * creates a database and threadpools for administrator and user threads
	 */
	public ExtremeServer(int port, int adminCount, int userCount) {
		this.port = port;
		this.adminCount = adminCount;
		if (this.adminCount < 1) {
			this.adminCount = 1;
		}
		this.userCount = userCount;
		if (this.userCount < 1) {
			this.userCount = 1;
		}
		database = new DataBaseHandler(adminCount * QUEUESIZEMULT, userCount * QUEUESIZEMULT);
		
		adminThreads = new AdminThread[adminCount];
		for (int i = 0; i < adminCount; ++i) {
			adminThreads[i] = new AdminThread(database);
		}
		
		userThreads = new UserThread[userCount];
		for (int i = 0; i < userCount; ++i) {
			userThreads[i] = new UserThread(database);
		}
	}
	
	/*
	 * initialize non-blocking server
	 */
	public boolean Start() {
		if (port == -1) {
			return false;
		}
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);
			int validOps = serverSocketChannel.validOps();
			serverSocketChannel.register(selector, validOps, null);
		} catch (IOException e) {
			return false;
		}
		for (AdminThread thread : adminThreads) {
			thread.start();
		}
		
		for (UserThread thread : userThreads) {
			thread.start();
		}
		return true;
	}
	
	/*
	 * checks if the key in the message is specific to an administrator
	 * currently useless, should be replaced with other security measures
	 */
	public boolean CheckKeyAdmin(String key) {
		// TODO
		return true;
	}
	
	/*
	 * checks if the key in the message is specific to an user
	 * currently useless, should be replaced with other security measures
	 */
	public boolean CheckKeyUser(String key) {
		// TODO
		return true;
	}
	
	/*
	 * checks if the operation is specific to an administrator operation
	 */
	public boolean IsAdmin(String mode) {
		return mode.equals(OperationTypeAdmin.ADMIN);
	}
	
	/*
	 * checks if the operation is specific to an user operation
	 */
	public boolean IsUser(String mode) {
		return mode.equals(OperationTypeUser.USER);
	}
	
	/*
	 * check for incoming messages and place them in operation queues for the admin and
	 * user threads to take care of
	 */
	public void Update() {
		try {
			selector.selectNow();
		} catch (IOException e) {
			return;
		}
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
        	SelectionKey key = iterator.next();
        	/*
        	 * check if a client is trying to connect
        	 * or check if a client is trying to send data
        	 */
        	if (key.isAcceptable()) {
        		SocketChannel client;
				try {
					client = serverSocketChannel.accept();
					if (client != null) {
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_READ);
						clients.add(client);
					}
				} catch (IOException e) {
					continue;
				}
        	} else if (key.isReadable()) {
        		SocketChannel client = (SocketChannel)key.channel();
        		ByteBuffer recvBuffer = ByteBuffer.allocate(MAXREADCLIENT);
        		try {
        			client.read(recvBuffer);
				} catch (IOException e) {
					continue;
				}
        		String recvStr = new String(recvBuffer.array()).trim();
        		if (recvStr.length() > 0) {
	        		
	        		if (!recvStr.contains(DELIM)) {
	        			return;
	        		}
	        		
	        		String[] splits = recvStr.split(DELIM, 3);
	        		if (IsAdmin(splits[1]) && CheckKeyAdmin(splits[0])) {
	        			int retVal = database.AddOperationAdmin(new DataBaseHandler.OperationClass(client, splits[2]));
	        			if (retVal != ResponseType.OP_SUC) {
	        				String str = Integer.toString(retVal);
	        				ByteBuffer response = ByteBuffer.allocate(str.length());
	        				response.put(str.getBytes());
	        				response.flip();
	        				try {
								client.write(response);
							} catch (IOException e) {
								// TODO
							}
	        			}
	        		} else if (IsUser(splits[1]) && CheckKeyUser(splits[0])) {
	        			int retVal = database.AddOperationUser(new DataBaseHandler.OperationClass(client, splits[2]));
	        			if (retVal != ResponseType.OP_SUC) {
	        				String str = Integer.toString(retVal);
	        				ByteBuffer response = ByteBuffer.allocate(str.length());
	        				response.put(str.getBytes());
	        				response.flip();
	        				try {
								client.write(response);
							} catch (IOException e) {
								// TODO
							}
	        			}
	        		}
        		}
        	}
    		iterator.remove();
        }
	}
	
	public void Stop() {
		for (SocketChannel client : clients) {
			if (client.isConnected()) {
				try {
					client.close();
				} catch (IOException e) {
					// TODO
				}
			}
		}
		for (ExtremeThread thread : adminThreads) {
			thread.StopRunning();
		}
		for (ExtremeThread thread : userThreads) {
			thread.StopRunning();
		}
		database.SaveAndClose();
		for (ExtremeThread thread : adminThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO
			}
		}
		for (ExtremeThread thread : userThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO
			}
		}
	}
}
