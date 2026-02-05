import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try (
            Socket socket = new Socket("localhost", 6000);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println(in.readLine());

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                out.println(input);
                System.out.println(in.readLine().replace("|", "\n"));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
