import java.util.HashMap;
import java.util.Map;


public class CommandProcessor {


   public static CommandResult process(String input, Board board) {
       if (input == null || input.trim().isEmpty()) {
           return new CommandResult(ErrorCode.INVALID_FORMAT.msg("Empty Command") + "\n",false);
       }


       String trimmed = input.trim();


       //To handl simple commands first
       if (trimmed.equalsIgnoreCase("SHAKE")) {
           return new CommandResult(board.shake() + "\n", false);
       }
       if (trimmed.equalsIgnoreCase("CLEAR")) {
           return new CommandResult(board.clear() + "\n", false);
       }
       if (trimmed.equalsIgnoreCase("DISCONNECT")) {
           return new CommandResult("OK DISCONNECTED\n", true);
       }


       String[] parts = trimmed.split("\\s+");
       String cmd = parts[0].toUpperCase();


       try {
           switch (cmd) {
               case "POST":
                   // based on the structure POST <x> <y> <color> <message>
                   if (parts.length < 5) {
                       return new CommandResult(ErrorCode.INVALID_FORMAT.msg("POST requires x y color message") + "\n", false);
                   }
                   int x = Integer.parseInt(parts[1]);
                   int y = Integer.parseInt(parts[2]);
                   String color = parts[3];
                   String message = joinFrom(parts, 4);
                   return new CommandResult(board.postNote(x, y, color, message) + "\n", false);


               case "PIN":
                   if (parts.length != 3) {
                       return new CommandResult(ErrorCode.INVALID_FORMAT.msg("PIN requires x y") + "\n", false);
                   }
                   return new CommandResult(board.pin(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])) + "\n", false);


               case "UNPIN":
                   if (parts.length != 3) {
                       return new CommandResult(ErrorCode.INVALID_FORMAT.msg("UNPIN requires x y") + "\n", false);
                   }
                   return new CommandResult(board.unpin(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])) + "\n", false);


               case "GET":
                   // either: GET PINS or GET color=.. contains=x y refersTo=..
                   if (parts.length == 2 && parts[1].equalsIgnoreCase("PINS")) {
                       return new CommandResult(board.getPinsResponse(), false);
                   }
                   Map<String, String> kv = parseKeyValues(trimmed.substring(3).trim());
                   return new CommandResult(board.getNotesResponse(kv), false);


               default:
                   return new CommandResult(ErrorCode.UNKNOWN_COMMAND.msg(cmd) + "\n", false);
           }
       } catch (NumberFormatException nfe) {
           return new CommandResult(ErrorCode.INVALID_FORMAT.msg("Expected integer coordinate") + "\n", false);
       } catch (Exception e) {
           return new CommandResult(ErrorCode.SERVER_ERROR.msg("Unhandled server error") + "\n", false);
       }
   }


   private static String joinFrom(String[] parts, int idx) {
       StringBuilder sb = new StringBuilder();
       for (int i = idx; i < parts.length; i++) {
           if (i > idx) sb.append(" ");
           sb.append(parts[i]);
       }
       return sb.toString();
   }


   // parse things colour=white
   private static Map<String, String> parseKeyValues(String s) {
       Map<String, String> map = new HashMap<>();
       if (s.isEmpty()) return map;


       // crude but effective: split by spaces, keep tokens with '='
       // Special case: contains=15 12 (two numbers)
       String[] tokens = s.split(" ");
       for (int i = 0; i < tokens.length; i++) {
           if (!tokens[i].contains("=")) continue;


           String[] kv = tokens[i].split("=", 2);
           String key = kv[0];
           String val = kv.length == 2 ? kv[1] : "";


           if (key.equalsIgnoreCase("contains")) {
               // expects 2 numbers; might be in same token or next token
               if (val.isEmpty() && i + 2 < tokens.length) {
                   val = tokens[i + 1] + " " + tokens[i + 2];
                   i += 2;
               } else if (i + 1 < tokens.length && !tokens[i + 1].contains("=")) {
                   // contains=15 then 12 next
                   val = val + " " + tokens[i + 1];
                   i += 1;
               }
           }


           map.put(key.toLowerCase(), val);
       }
       return map;
   }
}



