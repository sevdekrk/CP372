import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Board {

    private final int width, height, noteW, noteH;
    private final List<Note> notes = new ArrayList<>();
    private final List<Pin> pins = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    // Store valid colors passed from server
    private final Set<String> colorSet;

    // Updated constructor: now accepts custom colors
    public Board(int w, int h, int nw, int nh, List<String> validColors) {
        this.width = w;
        this.height = h;
        this.noteW = nw;
        this.noteH = nh;
        this.colorSet = new HashSet<>(validColors);
    }

    // Getters for handshake
    public int getBoardWidth() { return width; }
    public int getBoardHeight() { return height; }
    public int getNoteWidth() { return noteW; }
    public int getNoteHeight() { return noteH; }
    public List<String> getValidColors() { return new ArrayList<>(colorSet); }

    public String postNote(int x, int y, String color, String msg) {
        lock.lock();
        try {
            if (!colorSet.contains(color)) {
                return ErrorCode.COLOR_NOT_SUPPORTED.msg(color + " is not a valid color");
            }
            if (x < 0 || y < 0 || x + noteW > width || y + noteH > height) {
                return ErrorCode.OUT_OF_BOUNDS.msg("Note exceeds board boundaries");
            }

            Note newNote = new Note(x, y, noteW, noteH, color, msg);
            for (Note n : notes) {
                if (n.completelyOverlaps(newNote)) {
                    return ErrorCode.COMPLETE_OVERLAP.msg("Note overlaps an existing note entirely");
                }
            }

            notes.add(newNote);
            return "OK NOTE_POSTED";
        } finally {
            lock.unlock();
        }
    }

    public String pin(int x, int y) {
        lock.lock();
        try {
            boolean insideAny = false;
            for (Note n : notes) {
                if (n.contains(x, y)) {
                    insideAny = true;
                }
            }
            if (!insideAny) {
                return ErrorCode.NO_NOTE_AT_COORDINATE.msg("No note contains the given point");
            }

            pins.add(new Pin(x, y));
            return "OK PIN_ADDED";
        } finally {
            lock.unlock();
        }
    }

    public String unpin(int x, int y) {
        lock.lock();
        try {
            for (int i = 0; i < pins.size(); i++) {
                Pin p = pins.get(i);
                if (p.x() == x && p.y() == y) {
                    pins.remove(i);
                    return "OK PIN_REMOVED";
                }
            }
            return ErrorCode.PIN_NOT_FOUND.msg("No pin exists at the given coordinates");
        } finally {
            lock.unlock();
        }
    }

    public String shake() {
        lock.lock();
        try {
            notes.removeIf(n -> !isNotePinned(n));
            return "OK SHAKE_COMPLETE";
        } finally {
            lock.unlock();
        }
    }

    public String clear() {
        lock.lock();
        try {
            notes.clear();
            pins.clear();
            return "OK BOARD_CLEARED";
        } finally {
            lock.unlock();
        }
    }

    // GET PINS response
    public String getPinsResponse() {
        lock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("OK ").append(pins.size()).append("\n");
            for (Pin p : pins) {
                sb.append("PIN ").append(p.x()).append(" ").append(p.y()).append("\n");
            }
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }

    // GET notes with filters
    public String getNotesResponse(Map<String, String> kv) {
        lock.lock();
        try {
            String colorFilter = kv.getOrDefault("color", null);
            String refersTo = kv.getOrDefault("refersto", null);

            Integer cx = null, cy = null;
            if (kv.containsKey("contains")) {
                String[] xy = kv.get("contains").trim().split("\\s+");
                if (xy.length == 2) {
                    cx = Integer.parseInt(xy[0]);
                    cy = Integer.parseInt(xy[1]);
                }
            }

            List<Note> result = new ArrayList<>();
            for (Note n : notes) {
                if (colorFilter != null && !n.color().equals(colorFilter)) continue;
                if (cx != null && cy != null && !n.contains(cx, cy)) continue;
                if (refersTo != null && !n.message().contains(refersTo)) continue;
                result.add(n);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("OK ").append(result.size()).append("\n");
            for (Note n : result) {
                boolean pinned = isNotePinned(n);
                sb.append("NOTE ")
                        .append(n.x()).append(" ").append(n.y()).append(" ")
                        .append(n.color()).append(" ")
                        .append(n.message()).append(" ")
                        .append("PINNED=").append(pinned ? "true" : "false")
                        .append("\n");
            }
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }

    private boolean isNotePinned(Note n) {
        for (Pin p : pins) {
            if (n.contains(p.x(), p.y())) return true;
        }
        return false;
    }
}
