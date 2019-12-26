package game.quick.window;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class GameWindows extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1015160603201425197L;
	private PipedInputStream inputStream;
	private PipedOutputStream outputStream;
	private BufferedWriter bw;
	private GameHandler handler;
	private GameWindows win = null;

	private JTextArea area = new JTextArea();
	private IView viewer = null;

	private ExecutorService logicThread = Executors.newSingleThreadExecutor();
	private ScheduledExecutorService scheduledThread = Executors.newScheduledThreadPool(1);

	public void setView(IView view) {
		this.viewer = view;
	}

	public GameWindows() {
		this.win = this;
	}

	public void setHandler(GameHandler handler) {
		this.handler = handler;
	}

	public GameWindows start() {
		initComponent();
		try {
			initCommunication();
		} catch (IOException e) {
			e.printStackTrace();
		}
		cmdStart();

		return this;
	}

	private void initCommunication() throws IOException {
		outputStream = new PipedOutputStream();
		inputStream = new PipedInputStream();

		outputStream.connect(inputStream);

		bw = new BufferedWriter(new OutputStreamWriter(outputStream));
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public BufferedWriter getWriter() {
		return bw;
	}

	public void execute(String line) {
		try {
			bw.write(line);
			bw.newLine();
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initComponent() {
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(true);

		setLayout(new BorderLayout());

		area = new JTextArea();
		area.setLineWrap(true);
		area.setEditable(false);
		add(area, BorderLayout.CENTER);
	}

	private void cmdStart() {

		render();

		new Thread() {
			public void run() {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				while (true) {
					String line = null;
					try {
						while ((line = reader.readLine()) != null) {
							logicThread.execute(new CmdRunnable(line));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	public GameWindows startConsoleThread() {

		new Thread() {
			@Override
			public void run() {
				@SuppressWarnings("resource")
				Scanner in = new Scanner(System.in);
				while (true) {
					String line = in.nextLine();
					execute(line);
				}
			}
		}.start();

		return this;

	}

	public final void render() {
		if (viewer == null) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				area.setText("");
				area.append(viewer.render());
			}

		});

	}

	/**
	 * 计时器，时间到了之后会触发事件，但是事件是调用逻辑线程， 逻辑线程将该事件执行完毕之后会进行渲染
	 * 
	 * @param runnable
	 * @param delay
	 * @param unit
	 * @return
	 */
	public ScheduledFuture<?> schedule(UIRunnable runnable, long delay, TimeUnit unit) {
		return scheduledThread.schedule(new Runnable() {

			@Override
			public void run() {
				logicThread.execute(new Runnable() {
					@Override
					public void run() {
						runnable.execute();
						render();
						runnable.afterExecute();
					}
				});

			}
		}, delay, unit);
	}

	private class CmdRunnable implements Runnable {
		String cmd;

		public CmdRunnable(String cmd) {
			this.cmd = cmd;
		}

		@Override
		public void run() {
			handler.execute(win, cmd);
			render();
		}
	}

}
