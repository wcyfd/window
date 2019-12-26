package game.quick.window;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

	public static String content = null;

	public static void main(String[] args) throws IOException {

		GameWindows g = new GameWindows();

		g.setHandler(new GameHandler() {
			@Override
			public void execute(GameWindows win, String cmd) {
				content = cmd;
			}

		});
		g.setView(new IView() {

			@Override
			public String render() {

				return content;
			}
		});
		g.start();
		System.out.println("命令行启动");
		g.startConsoleThread();
	}
}
