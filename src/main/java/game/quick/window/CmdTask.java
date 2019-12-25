package game.quick.window;

public class CmdTask implements Runnable {

	private String cmd;
	private GameWindows win;

	public CmdTask(GameWindows win, String cmd) {
		this.cmd = cmd;
		this.win = win;
	}

	@Override
	public void run() {
		try {
			win.execute(win, cmd);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
