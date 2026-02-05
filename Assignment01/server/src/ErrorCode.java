public enum ErrorCode {

    INVALID_COMMAND("ERROR INVALID_COMMAND"),
    INVALID_SYNTAX("ERROR INVALID_SYNTAX"),
    INVALID_COLOR("ERROR INVALID_COLOR Unsupported color"),
    OUT_OF_BOUNDS("ERROR OUT_OF_BOUNDS Note exceeds board boundaries"),
    COMPLETE_OVERLAP("ERROR COMPLETE_OVERLAP Note overlaps an existing note entirely"),
    NO_NOTE_AT_LOCATION("ERROR NO_NOTE_AT_LOCATION"),
    UNKNOWN_COMMAND("ERROR UNKNOWN_COMMAND"),
    SERVER_ERROR("ERROR SERVER_ERROR");

    public final String message;

    ErrorCode(String msg) {
        this.message = msg;
    }
}

