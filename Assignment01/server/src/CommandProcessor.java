public class CommandProcessor {

    public static String process(String input, Board board) {
        if (input.trim().isEmpty()) {
            return ErrorCode.INVALID_COMMAND.message;
        }

        String[] parts = input.split(" ", 4);
        String cmd = parts[0].toUpperCase();

        try {
            switch (cmd) {
                case "POST":
                    return handlePost(parts, board);

                case "PIN":
                    return handlePin(parts, board);

                case "SHAKE":
                    return board.shake();

                case "GET":
                    return board.getNotes();

                default:
                    return ErrorCode.UNKNOWN_COMMAND.message;
            }
        } catch (Exception e) {
            return ErrorCode.SERVER_ERROR.message;
        }
    }

    private static String handlePost(String[] parts, Board board) {
        if (parts.length < 4) {
            return ErrorCode.INVALID_SYNTAX.message;
        }

        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        String[] rest = parts[3].split(" ", 2);

        if (rest.length < 2) {
            return ErrorCode.INVALID_SYNTAX.message;
        }

        String color = rest[0];
        String message = rest[1];

        return board.postNote(x, y, color, message);
    }

    private static String handlePin(String[] parts, Board board) {
        if (parts.length != 3) {
            return ErrorCode.INVALID_SYNTAX.message;
        }

        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);

        return board.addPin(x, y);
    }
}

