import java.util.concurrent.locks.ReentrantLock;

public class Position {

    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;

    private Point point;
    private Creature holder;
    private ReentrantLock lock;

    public Position(Point point){
        this.point = point;
        this.holder = null;
        this.lock = new ReentrantLock();
    }

    public void lock(){
        this.lock.lock();
    }

    public void unlock(){
        this.lock.unlock();
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Creature getHolder() {
        return holder;
    }

    public void setHolder(Creature holder) {
        this.holder = holder;
    }

    public double distance(Position other){
        int dx = point.x - other.point.x;
        int dy = point.y - other.point.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    static class Point{
        private int x;
        private int y;

        public Point(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
}
