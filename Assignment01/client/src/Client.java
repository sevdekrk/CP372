
import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {


   public static void main(String[] args) {
       try (
               Socket socket = new Socket("localhost", 6000);
               BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
               Scanner scanner = new Scanner(System.in)
       ) {
           // Read 3-line handshake
           System.out.println(in.readLine());
           System.out.println(in.readLine());
           System.out.println(in.readLine());


           while (true) {
               System.out.print("> ");
               String input = scanner.nextLine();
               out.println(input);


               // Read first response line
               String first = in.readLine();
               if (first == null) break;


               System.out.println(first);


               // If response is OK N, then read N more lines (NOTE... or PIN...)
               if (first.startsWith("OK ")) {
                   String[] parts = first.split(" ");
                   if (parts.length == 2) {
                       try {
                           int n = Integer.parseInt(parts[1]);
                           for (int i = 0; i < n; i++) {
                               System.out.println(in.readLine());
                           }
                       } catch (NumberFormatException ignored) {}
                   }
               }


               if (input.trim().equalsIgnoreCase("DISCONNECT")) {
                   break;
               }
           }


       } catch (IOException e) {
           e.printStackTrace();
       }
   }
}

