package instantchat;

import instantchat.Server;
import javax.swing.JFrame;

public class ServerTest {
    public static void main(String[] args){
        Server alise = new Server();
        alise.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        alise.startRunning();
        
    }
}
