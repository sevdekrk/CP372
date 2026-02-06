public enum ErrorCode {
   INVALID_FORMAT("INVALID_FORMAT"),
   OUT_OF_BOUNDS("OUT_OF_BOUNDS"),
   COMPLETE_OVERLAP("COMPLETE_OVERLAP"),
   COLOR_NOT_SUPPORTED("COLOR_NOT_SUPPORTED"),
   NO_NOTE_AT_COORDINATE("NO_NOTE_AT_COORDINATE"),
   PIN_NOT_FOUND("PIN_NOT_FOUND"),
   UNKNOWN_COMMAND("UNKNOWN_COMMAND"),
   SERVER_ERROR("SERVER_ERROR");


   private final String code;

   ErrorCode(String code) {
       this.code = code;
   }
   public String msg(String detail) {
       if (detail == null || detail.isBlank()) {
           return "ERROR " + code;
       }
       return "ERROR " + code + " " + detail;
   }
}

