import java.net.*;

public class ChatServer {

    public static void main(String[] args) {
        System.out.println("Chat Server started...");

        try (ServerSocket serverSocket = new ServerSocket(1234)) {

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New person connected");

                new Thread(new ClientHandler(socket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}









