import java.io.*;
import java.net.Socket;


public class ClientHandler implements Runnable {


   private final Socket socket;
   private final Board board;
   public ClientHandler(Socket socket, Board board) {
       this.socket = socket;
       this.board = board;
   }
   @Override
   public void run() {
       try (
           BufferedReader in = new BufferedReader(
               new InputStreamReader(socket.getInputStream()));
           PrintWriter out = new PrintWriter(
               socket.getOutputStream(), true)
       ) {
           // handshake which is 3 liones 
           out.print("BOARD " + board.getBoardWidth() + " " + board.getBoardHeight() + "\n");
           out.print("NOTE " + board.getNoteWidth() + " " + board.getNoteHeight() + "\n");
           out.print("COLORS " + String.join(" ", board.getValidColors()) + "\n");
           out.flush();


           String line;
           while ((line = in.readLine()) != null) {
               CommandResult result = CommandProcessor.process(line, board);


               //allow multi line responses
               out.print(result.response()); 
               out.flush();


               if (result.closeConnection()){
                   break;
               }
           }


       } catch (IOException e) {
           System.out.println("Client disconnected");
       } finally {
           try { socket.close();} catch (IOException ignored){}


       }
   }
}





