import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Server {

    public static void main(String[] args) {
        // Check minimum args
        if (args.length < 6) {
            System.out.println("Usage: java Server <port> <board_width> <board_height> <note_width> <note_height> <color1> ... <colorN>");
            return;
        }

        try {
            // Parse command-line arguments
            int port = Integer.parseInt(args[0]);
            int boardWidth = Integer.parseInt(args[1]);
            int boardHeight = Integer.parseInt(args[2]);
            int noteWidth = Integer.parseInt(args[3]);
            int noteHeight = Integer.parseInt(args[4]);

            // All remaining args are colors
            List<String> colors = Arrays.asList(Arrays.copyOfRange(args, 5, args.length));

            // Create the Board with custom sizes and colors
            Board board = new Board(boardWidth, boardHeight, noteWidth, noteHeight, colors);

            System.out.println("Bulletin Board Server starting...");
            System.out.println("Listening on port " + port);
            System.out.println("Valid colors: " + String.join(", ", colors));

            // Start server socket
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(new ClientHandler(socket, board)).start();
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in arguments.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
