package extremeClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ExtremeClientAPI {
	protected static final int USER = 0;
	protected static final int ADMIN = 1;
	
	protected static final int IPSIZE = 4;
	
	protected static final int IPLENSTRMIN = 7;
	protected static final int IPLENSTRMAX = 15;

	protected boolean established = false;
	private String ServerIP;
	private int mode;
	private SocketChannel clientSocket;
	private int port;
	
	protected Integer key;
	
	public ExtremeClientAPI(int key) {
		this.key = key;
	}
	
	/*
	 * initialize socket channel for communication with server
	 * - called internally by ExtremeClientAPI.Init();
	 */
	private boolean InternalInit() {
		if (mode != USER && mode != ADMIN) {
			System.out.println("Invalid mode");
			established = false;
			return established;
		}
		try {
			clientSocket = SocketChannel.open(new InetSocketAddress(this.ServerIP, port));
		} catch (IOException e) {
			established = false;
			return established;
		}
		established = true;
		return established;
	}
	
	/*
	 * available to children for initializing
	 */
	protected boolean Init(String ServerIP, int port, int mode) {
		if (ServerIP == null) {
			System.out.println("Invalid address");
			established = false;
			return false;
		}
		this.ServerIP = new String(ServerIP);
		this.mode = mode;
		this.port = port;
		return InternalInit();
	}
	
	/*
	 * sends a package the server if a connection was previously established
	 */
	protected boolean SendPackage(String toSend) {
		if (!established) {
			return false;
		}
		toSend = key.toString() + extremeServer.ExtremeServer.DELIM + toSend;
		ByteBuffer buffSnd = ByteBuffer.allocate(toSend.length());
		buffSnd.put(toSend.getBytes());
		buffSnd.flip();
		try {
			clientSocket.write(buffSnd);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/*
	 * nonblocking check for received message
	 */
	public String CheckReceived() {
		if (!established) {
			return null;
		}
		ByteBuffer buffer = ByteBuffer.allocate(512);
		try {
			clientSocket.read(buffer);
		} catch (IOException e) {
			return null;
		}
		byte[] bytes;
		if (buffer.hasArray()) {
			bytes = buffer.array();
			String recv = new String(bytes);
			return recv;
		}
		return null;
	}
	
	/*
	 * closes the current established connection
	 */
	public boolean CloseConnection() {
		if (!established) {
			return false;
		}
		try {
			clientSocket.close();
		} catch (Exception e) {
			return false;
		}
		established = false;
		return true;
	}
}
