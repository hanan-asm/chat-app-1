import java.net.*;
import java.io.*;
import java.util.*;

public class ClientHandler implements Runnable, PasswordValidator {

    private static Set<PrintWriter> clients = new HashSet<>();

    private Socket socket;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public boolean isValidPassword(String pwd) {
        if (pwd.length() != 6) return false;

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char ch : pwd.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpper = true;
            else if (Character.isLowerCase(ch)) hasLower = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if (ch == '%' || ch == '#') hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
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

            out.println(" Welcome to the chat, " + username + "!");

            broadcastExcept( username + " joined the chat", out);

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

    private void broadcastExcept(String msg, PrintWriter exclude) {
        synchronized (clients) {
            for (PrintWriter writer : clients) {
                if (writer != exclude) {
                    writer.println(msg);
                }
            }
        }
    }
}











