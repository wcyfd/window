package game.quick.window;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

	public static String content = null;

	public static void main(String[] args) throws IOException {

		
		GameWindows g = new GameWindows(new GameHandler() {
			@Override
			public void execute(GameWindows win, String cmd) {
				content = cmd;
			}

			@Override
			public String view() {
				StringBuilder sb = new StringBuilder();
				sb.append("================菜单==================");
				sb.append("\n");
				sb.append(content).append("\n");
				sb.append("=====================================");
				return sb.toString();
			}
		});
		g.start();
		System.out.println("命令行启动");
		g.startConsoleThread();
	}
}
