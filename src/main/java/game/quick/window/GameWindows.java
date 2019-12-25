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
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

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

	private JTextArea area = new JTextArea();

	public GameWindows(GameHandler handler) {
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
		final GameWindows w = this;

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				render();
			}

		});

		new Thread() {
			public void run() {
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				while (true) {
					String line = null;
					try {
						while ((line = reader.readLine()) != null) {
							try {
								SwingUtilities.invokeAndWait(new CmdTask(w, line));
							} catch (InvocationTargetException | InterruptedException e) {
								e.printStackTrace();
							}
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
				Scanner in = new Scanner(System.in);
				while (true) {
					String line = in.nextLine();
					execute(line);
				}
			}
		}.start();

		return this;

	}

	public void execute(GameWindows win, String cmd) {
		handler.execute(win, cmd);
		render();
	}

	private void render() {
		String view = handler.view();
		this.area.setText(null);
		this.area.append(view);
	}

}
