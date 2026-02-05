import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Board {

    private final int width, height, noteW, noteH;
    private final List<Note> notes = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    private static final Set<String> COLORS =
            Set.of("white", "red", "blue", "yellow", "green");

    public Board(int w, int h, int nw, int nh) {
        this.width = w;
        this.height = h;
        this.noteW = nw;
        this.noteH = nh;
    }

    public String postNote(int x, int y, String color, String msg) {
        lock.lock();
        try {
            if (!COLORS.contains(color)) {
                return ErrorCode.INVALID_COLOR.message;
            }

            if (x < 0 || y < 0 || x + noteW > width || y + noteH > height) {
                return ErrorCode.OUT_OF_BOUNDS.message;
            }

            Note newNote = new Note(x, y, color, msg, noteW, noteH);

            for (Note n : notes) {
                if (n.completelyOverlaps(newNote)) {
                    return ErrorCode.COMPLETE_OVERLAP.message;
                }
            }

            notes.add(newNote);
            return "OK NOTE_POSTED";

        } finally {
            lock.unlock();
        }
    }

    public String addPin(int x, int y) {
        lock.lock();
        try {
            boolean pinned = false;
            for (Note n : notes) {
                if (n.contains(x, y)) {
                    n.addPin(x, y);
                    pinned = true;
                }
            }
            return pinned ? "OK PIN_ADDED" : ErrorCode.NO_NOTE_AT_LOCATION.message;
        } finally {
            lock.unlock();
        }
    }

    public String shake() {
        lock.lock();
        try {
            notes.removeIf(n -> !n.isPinned());
            return "OK SHAKE_COMPLETE";
        } finally {
            lock.unlock();
        }
    }

    public String getNotes() {
        lock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("OK ").append(notes.size());
            for (Note n : notes) {
                sb.append("\n").append(n);
            }
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }
}
