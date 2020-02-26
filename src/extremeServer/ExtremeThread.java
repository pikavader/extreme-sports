package extremeServer;

public class ExtremeThread extends Thread {
	private volatile boolean keepRunning = true;
	protected DataBaseHandler database;
	
	protected ExtremeThread(DataBaseHandler database) {
		this.database = database;
	}
	
	public void StopRunning() {
		keepRunning = false;
	}
	
	protected boolean isOn() {
		return keepRunning;
	}
}
