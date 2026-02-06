public record  Note(int x, int y, int w, int h, String color, String message) {

   public boolean contains(int px, int py) {
       return px >= x && px < x + w && py >= y && py < y + h;
   }

   public boolean completelyOverlaps(Note other) {
       return other.x() >= this.x()
               && other.y() >= this.y()
               && other.x() + other.w() <= this.x() + this.w()
               && other.y() + other.h() <= this.y() + this.h();
   }
}



