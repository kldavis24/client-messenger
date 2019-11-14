import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	// Constructor
	public Client(String host) {
		super("Client Window");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);		
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(500, 350);
		setVisible(true);
	}
	
	//------------------------------------------------------------------------------------------------------------------------------------
	
	// Connect to Server
	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		}
		catch (EOFException eofException) {
			showMessage("\n Client terminated the connection");
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
		finally {
			closeWindow();
		}
	}
	
	// Method to connect to the server
	private void connectToServer() throws IOException {
		showMessage("Attempting connection...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to " + connection.getInetAddress().getHostName());
	}
	
	// Setup Streams to send and receive messages
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Your streams are now setup! \n");
	}
	
	// While chatting with the server
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}
			catch (ClassNotFoundException classNotFoundException) {
				showMessage("\n I don't know that object type");
			}
		}
		while (!message.equals("SERVER - END"));
	}
	
	// Close the Streams and Sockets
	private void closeWindow() {
		showMessage("\n Closing all connections...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	// Send messages to the server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\n CLIENT - " + message);
		}
		catch (IOException ioException) {
			chatWindow.append("\n An error occured while sending message!");
		}
	}
	
	// Update the GUI to display new messages
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(message);
				}
			}
		);
	}
	
	// Gives the user permission to type a message into the text box
	private void ableToType(final boolean tOrF) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(tOrF);
					}
				}
		);
	}

}


