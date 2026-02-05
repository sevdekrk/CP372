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
            out.println("OK CONNECTED");

            String line;
            while ((line = in.readLine()) != null) {
                String response = CommandProcessor.process(line, board);
                out.println(response);
            }

        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }
}

