package game.quick.window;

public interface GameHandler {
	void execute(GameWindows win, String cmd);

	String view();
}
