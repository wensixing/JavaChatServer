package Chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sixing.wen on 10/27/16.
 */
public class ChatServer {
    private static final int LISTEN_PORT = 8933;

    public static void main(String[] args) {

        int port = LISTEN_PORT;
        ServerSocket serverSocket = null;
        Socket socket = null;

        try { // Start listenting
            serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();
                Connector connector = new Connector(socket);
                connector.start();
            }
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } finally {
            try {
                if(socket != null) {
                    socket.close();
                }
                if(serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }
        }
    }
}
