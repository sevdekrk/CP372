
import java.util.ArrayList;
import java.util.List;

public class Note {

    private final int x, y, w, h;
    private final String color, message;
    private final List<Pin> pins = new ArrayList<>();

    public Note(int x, int y, String c, String m, int w, int h) {
        this.x = x;
        this.y = y;
        this.color = c;
        this.message = m;
        this.w = w;
        this.h = h;
    }

    public boolean contains(int px, int py) {
        return px >= x && px < x + w && py >= y && py < y + h;
    }

    public boolean completelyOverlaps(Note other) {
        return this.x == other.x &&
               this.y == other.y &&
               this.w == other.w &&
               this.h == other.h;
    }

    public void addPin(int px, int py) {
        pins.add(new Pin(px, py));
    }

    public boolean isPinned() {
        return !pins.isEmpty();
    }

    @Override
    public String toString() {
        return String.format(
            "NOTE %d %d %s %s PINNED=%s",
            x, y, color, message, isPinned()
        );
    }
}
