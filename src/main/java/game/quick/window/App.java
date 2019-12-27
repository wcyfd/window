package game.quick.window;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

	public static String content = null;

	public static void main(String[] args) throws IOException {

		GameWindows.Builder.create().setHandler(new GameHandler() {
			@Override
			public void execute(GameWindows win, String cmd) {
				content = cmd;
			}

		}).setView(new IView() {

			@Override
			public String render() {
				return content;
			}
		}).build().startConsoleThread();
		System.out.println("命令行启动");

	}
}
