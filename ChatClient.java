import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class ChatClient {

    private static String passwordCheck(String pwd) {
        if (pwd.length() != 6) return "Must be exactly 6 characters";

        boolean u=false,l=false,d=false,s=false;
        for (char c : pwd.toCharArray()) {
            if (Character.isUpperCase(c)) u=true;
            else if (Character.isLowerCase(c)) l=true;
            else if (Character.isDigit(c)) d=true;
            else if (c=='£' || c=='%') s=true;
        }

        if (!u) return "Missing uppercase letter";
        if (!l) return "Missing lowercase letter";
        if (!d) return "Missing digit";
        if (!s) return "Missing £ or %";

        return "VALID";
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Username: ");
        String username = sc.nextLine();

        String password;
        while (true) {
            System.out.print("Password: ");
            password = sc.nextLine();
            String result = passwordCheck(password);
            if (result.equals("VALID")) break;
            System.out.println("Invalid password: " + result);
        }

        try {
            Socket socket = new Socket("localhost", 1234);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out.println(username);
            out.println(password);

            String response = in.readLine();
            if (!"SUCCESS".equals(response)) {
                System.out.println("Login failed");
                socket.close();
                return;
            }

            System.out.println("Login successful! Welcome " + username);

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (Exception e) {}
            }).start();

            while (true) {
                String msg = sc.nextLine();
                out.println(msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}













