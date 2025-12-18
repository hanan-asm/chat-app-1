import java.net.*;
import java.io.*;
import java.util.*;

public class ClientHandler implements Runnable {

    private static Set<PrintWriter> clients = new HashSet<>();
    private Socket socket;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    private boolean isValidPassword(String pwd) {
        if (pwd.length() != 6) return false;

        boolean upper = false, lower = false, digit = false, special = false;

        for (char c : pwd.toCharArray()) {
            if (Character.isUpperCase(c)) upper = true;
            else if (Character.isLowerCase(c)) lower = true;
            else if (Character.isDigit(c)) digit = true;
            else if (c == '£' || c == '%') special = true;
        }

        return upper && lower && digit && special;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        ) {
            out = new PrintWriter(socket.getOutputStream(), true);

            username = in.readLine();
            String password = in.readLine();

            if (!isValidPassword(password)) {
                out.println("FAIL");
                socket.close();
                return;
            }

            out.println("SUCCESS");
            System.out.println(username + " logged in");

            synchronized (clients) {
                clients.add(out);
            }

            broadcast(username + " joined the chat");

            String message;
            while ((message = in.readLine()) != null) {

                System.out.println(username + ": " + message);

                if (message.equalsIgnoreCase("hi")) {
                    out.println("Server: Hi " + username );
                }

                broadcast(username + ": " + message);
            }

        } catch (Exception e) {
            System.out.println(username + " disconnected");
        } finally {
            synchronized (clients) {
                clients.remove(out);
            }
            broadcast(username + " left the chat");
        }
    }

    private void broadcast(String msg) {
        synchronized (clients) {
            for (PrintWriter writer : clients) {
                writer.println(msg);
            }
        }
    }
}








