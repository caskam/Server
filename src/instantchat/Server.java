package instantchat;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;

    //constructor
    public Server() {
        super("SERVER");
        userText = new JTextField();
        //if you're not connected to anybody you can't type
        //we don't want to mess up our code from getting random things we don't need
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                sendMessage(event.getActionCommand());
                userText.setText("");
            }
        }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(700, 300); //change size later 
        setVisible(true);
    }

    //set up and run the server
    public void startRunning() {
        try {
            server = new ServerSocket(6789, 100); //testing, change later
            //this part of code we want to run forever.
            while (true) {
                try {
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                } catch (EOFException eofException) {
                    showMessage("\n Server ended the connection! ");
                } finally {
                    closeCrap();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //wait for connection, then display connection information
    private void waitForConnection() throws IOException {
        showMessage(" Waiting for someone to connect... \n");
        connection = server.accept();
        showMessage(" Now connected to " + connection.getInetAddress().getHostAddress());
    }

    //get stream to send and recieve data
    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage(" \n Stream are now setup! \n ");
    }

    //during the chat conversation
    private void whileChatting() throws IOException {
        String message = "  You are now connected! ";
        sendMessage(message);
        ableToType(true);
        do {
            //have conversation
            try {
                message = (String) input.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\n idk wtf that user send! "); //we don't want to see this
            }
        } while (!message.equals("CLIENT - END"));
    }

    //close streams and socents after you're done chatting
    private void closeCrap() {
        showMessage("\n Closing connections ... \n");
        ableToType(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    
    //send a message to client
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER - " + message);
            output.flush();
            showMessage("\n SERVER - " + message);
        }catch(IOException ioException){
            chatWindow.append("\n ERROR DUDE I CAN'T SEND THAT MESSAGE"); //we don't want to see this 
        }
    }
    
    //updates chatWindow
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        chatWindow.append(text);
                    }
                }
        );
    }
    
    //let the user type stuff into their box
    private void ableToType(final boolean tof){
      SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        userText.setEditable(tof);
                    }
                }
        );
    }
    
    
}
