package graphics;

import java.lang.*;
import java.net.UnknownHostException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.Client;
import server.EchoServer;

public class FindingPathsGUI {
	// Connect status constants
	final static int DISCONNECTED = 0;
	final static int BEGIN_CONNECT = 1;
	final static int CONNECTED = 2;
	
	private static Logger logger = LoggerFactory.getLogger(FindingPathsGUI.class);

	// Various GUI components and info
	public static JFrame mainFrame = null;
	public static JTextArea chatText = null;
	public static JTextField chatLine = null;
	public static JLabel statusBar = null;
	public static JTextField ipField = null;
	public static JTextField portField = null;
	public static JRadioButton hostOption = null;
	public static JRadioButton guestOption = null;
	public static JButton connectButton = null;
	public static JButton disconnectButton = null;
	public static JButton sendButton = null;

	// Connection info
	public static int connectionStatus = DISCONNECTED;
	public static boolean isHost = true;
	
	//ClientServer
	private Client client = null;
	private Thread server = null;

	public void initGUI() {
		// Set up the status bar
		statusBar = new JLabel();
		statusBar.setText("Offline");

		JPanel optionsPane = initOptionsPane();
		JPanel chatPane = initChatPane();

		// Set up the main pane
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);

		// Set up the main frame
		mainFrame = new JFrame("Simple TCP Chat");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private JPanel initChatPane() {
		// Create a chat pane
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		
		JPanel inputPane = new JPanel(new GridLayout(1,2));
		
		// Create chat line
		JScrollPane chatTextPane = new JScrollPane(chatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(true);
		
		// Create send button
		sendButton = new JButton("Send");
		sendButton.setEnabled(true);
		sendButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
		
		chatLine.setSize(new Dimension(150, 50));
		sendButton.setSize(new Dimension(50, 50));
		inputPane.add(chatLine);
		inputPane.add(sendButton);
		
		chatPane.add(inputPane, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(200, 200));
		
		return chatPane;
	}

	private JPanel initOptionsPane() {
		JPanel pane = null;

		// Create an options pane
		JPanel optionsPane = new JPanel(new GridLayout(4, 1));

		// IP address input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Host IP:"));
		ipField = new JTextField(10);
		ipField.setEditable(true);
		pane.add(ipField);
		optionsPane.add(pane);

		// Port input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Port:"));
		portField = new JTextField(10);
		portField.setEditable(true);
		pane.add(portField);
		optionsPane.add(pane);

		// Host/guest option
		ButtonGroup bg = new ButtonGroup();
		hostOption = new JRadioButton("Host", true);
		hostOption.setMnemonic(KeyEvent.VK_H);
		guestOption = new JRadioButton("Guest", false);
		guestOption.setMnemonic(KeyEvent.VK_G);
		bg.add(hostOption);
		bg.add(guestOption);
		pane = new JPanel(new GridLayout(1, 2));
		pane.add(hostOption);
		pane.add(guestOption);
		optionsPane.add(pane);

		// Connect/disconnect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		
		connectButton = new JButton("Connect");
		connectButton.setEnabled(true);
		connectButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});

		disconnectButton = new JButton("Disconnect");
		disconnectButton.setEnabled(false);
		disconnectButton.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				disconnect();
			}
		});

		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		optionsPane.add(buttonPane);

		return optionsPane;
	}

	private void connect() {
		if(!ipField.getText().isEmpty() && !portField.getText().isEmpty()) {
			if(hostOption.isSelected()) {
				EchoServer server = new EchoServer(
						this,
						Integer.parseInt(portField.getText()));
				this.server = new Thread(server);
				
				this.server.start();
				
				isHost = true;
			}
			try {
				client = new Client(
						ipField.getText(),
						Integer.parseInt(portField.getText()),
						this
						);
				
				connectButton.setEnabled(false);
				disconnectButton.setEnabled(true);
				connectionStatus = BEGIN_CONNECT;
				ipField.setEnabled(false);
				portField.setEnabled(false);
				hostOption.setEnabled(false);
				guestOption.setEnabled(false);
				chatLine.setEnabled(true);
				statusBar.setText("Online");
			} catch (UnknownHostException e) {
				addText("Invalid host.");
				logger.error("Invalid host.");
				e.printStackTrace();
			} catch (IOException e) {
				addText("Failed to connect to server.");
				logger.error("Failed to connect to server.");
				e.printStackTrace();
			}
		} else {
			if(ipField.getText().isEmpty())
				chatText.append("IP must be set.\n");
			if(portField.getText().isEmpty())
				chatText.append("Port must be set.\n");
		}
			
		
		mainFrame.repaint();
	}

	private void disconnect() {
		if(isHost)
			server.interrupt();
		
		client.shutdown();
		
		connectButton.setEnabled(true);
		disconnectButton.setEnabled(false);
		connectionStatus = DISCONNECTED;
		ipField.setEnabled(true);
		portField.setEnabled(true);
		hostOption.setEnabled(true);
		guestOption.setEnabled(true);
		chatLine.setText("");
		chatLine.setEnabled(false);
		statusBar.setText("Offline");
		mainFrame.repaint();
	}
	
	private void send() {
		if(!chatLine.getText().isEmpty())
			if(!client.send(chatLine.getText().getBytes()))
				chatText.append("Failed to send to server.\n");
	}
	
	public void addText(String text) {
		if(text == null || !text.isEmpty())
			chatText.append(text + "\n");
		else
			logger.info("Attempted to set empty string.");
	}
}

// Action adapter for easy event-listener coding
class ActionAdapter implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	}
}