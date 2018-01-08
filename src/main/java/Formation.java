public class Formation {

    private Position.Point[] points;

    private Formation(Position.Point[] points){
        this.points = points;
    }

    public Position.Point[] getPoints(){
        return this.points;
    }

    public static final Formation SINGLE = new Formation(new Position.Point[]{new Position.Point(0,0)});

    public static final Formation HEYI = new Formation(new Position.Point[]{new Position.Point(4,4), new Position.Point(3,3), new Position.Point(3,5),new Position.Point(2,2), new Position.Point(2,6),new Position.Point(1,1), new Position.Point(1,7),new Position.Point(0,0)});

    public static final Formation YANXING = new Formation(new Position.Point[]{new Position.Point(7,0),new Position.Point(6,1),new Position.Point(5,2),new Position.Point(4,3),new Position.Point(3,4),new Position.Point(2,5),new Position.Point(1,6),new Position.Point(0,7)});

    public static final Formation CHONGE = new Formation(new Position.Point[]{new Position.Point(7,0),new Position.Point(6,1),new Position.Point(5,0),new Position.Point(4,1),new Position.Point(3,0),new Position.Point(2,1),new Position.Point(1,0),new Position.Point(0,1)});

    public static final Formation CHANGSHE = new Formation(new Position.Point[]{new Position.Point(6,0), new Position.Point(5,0),new Position.Point(4,0),new Position.Point(3,0),new Position.Point(2,0),new Position.Point(1,0),new Position.Point(0,0)});

    public static final Formation YULING = new Formation(new Position.Point[]{new Position.Point(0,2),new Position.Point(1,3),new Position.Point(2,1),new Position.Point(2,3),new Position.Point(3,0),new Position.Point(3,2),new Position.Point(3,4),new Position.Point(4,2)});

    public static final Formation FANGYUAN = new Formation(new Position.Point[]{new Position.Point(0,2),new Position.Point(1,1),new Position.Point(1,3),new Position.Point(2,0),new Position.Point(2,4),new Position.Point(3,1),new Position.Point(3,3),new Position.Point(4,2)});

    public static final Formation[] choices = new Formation[]{HEYI, YANXING, CHONGE, YULING, FANGYUAN};
}
