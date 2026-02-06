import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {


  public static final int PORT = 6000;


   public static final int BOARD_WIDTH = 200;
   public static final int BOARD_HEIGHT = 100;
   public static final int NOTE_WIDTH = 20;
   public static final int NOTE_HEIGHT = 10;


   public static void main(String[] args) {
       Board board = new Board(
               BOARD_WIDTH,
               BOARD_HEIGHT,
               NOTE_WIDTH,
               NOTE_HEIGHT
       );


       System.out.println("Bulletin Board Server starting...");


       try (ServerSocket serverSocket = new ServerSocket(PORT)) {
           System.out.println("Listening on port " + PORT);


           while (true) {
               Socket socket = serverSocket.accept();
               new Thread(new ClientHandler(socket, board)).start();
           }


       } catch (IOException e) {
           e.printStackTrace();
       }
   }
}
