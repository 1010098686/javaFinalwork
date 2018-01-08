import java.awt.*;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class Creature extends Observable implements Runnable{

    public static final Long SLEEP_TIME = 1000L;

    public abstract int getPower();
    public abstract Image getImage();
    protected abstract boolean sameKind(Creature c);

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    private boolean dead;
    private boolean running;
    private Position position;
    private SpacePanel space;

    public Creature(){
        position = null;
        dead = false;
        running = false;
        space = null;
    }

    public Creature(SpacePanel space){
        this.position = null;
        dead = false;
        running = false;
        this.space = space;
        addObserver(space);
    }

    public void setDead(boolean dead){
        this.dead = dead;
    }

    public boolean isDead(){
        return dead;
    }

    public void kill(){
        if(!dead){
            dead = true;
            running = false;
            setChanged();
            notifyObservers();
        }
    }

    public void stop(){
        running = false;
    }

    private void fight(Creature c){
        if(sameKind(c)) return;
        if(getPower() > c.getPower()){
            c.kill();
        }else if(getPower() < c.getPower()){
            kill();
        }else{
            double r = Math.random();
            if(r >= 0.5){
                c.kill();
            }else{
                kill();
            }
        }
    }

    public void move(Position pos){
        pos.setHolder(this);
        if(this.position != null) {
            this.position.setHolder(null);
        }
        this.position = pos;
    }

    private int sign(int x){
        if(x>0) return 1;
        else if(x<0) return -1;
        else return 0;
    }

    private Position choosePosition(){
        Creature[] creatures;
        Double min  = Double.MAX_VALUE;
        Position res = null;
        if (this instanceof Huluwa || this instanceof Grandpa) {
            creatures = this.space.getEnemy();
        } else {
            creatures = this.space.getHuluwaAndGrandpa();
        }
        for(Creature creature : creatures) {
            if (creature.isDead() || creature.getPower() > getPower()) continue;
            int deltax = sign(creature.position.getPoint().getX() - this.position.getPoint().getX());
            int deltay = sign(creature.position.getPoint().getY() - this.position.getPoint().getY());
            int x = this.position.getPoint().getX() + deltax;
            int y = this.position.getPoint().getY() + deltay;
            if (SpacePanel.outOfBound(x, y)) continue;
            boolean f1 = this.space.getPosition(x, y).getHolder() == null;
            boolean f2 = this.space.getPosition(x, y).getHolder() != null && !this.space.getPosition(x, y).getHolder().isDead();
            //if (f1 || f2) cand.add(space.getPosition(x, y));
            if(f1 || f2){
                double distance = creature.getPosition().distance(this.position);
                if(distance < min){
                    min = distance;
                    res = space.getPosition(x, y);
                }
            }
        }
        if(res != null){
            return res;
        }else{
            int c = new Random().nextInt(OFFSET.length);
            while(true){
                c = (c + 1)%OFFSET.length;
                Position.Point offset = OFFSET[c];
                int x = this.position.getPoint().getX() + offset.getX();
                int y = this.position.getPoint().getY() + offset.getY();
                if(SpacePanel.outOfBound(x, y)) continue;
                boolean f1 = this.space.getPosition(x, y).getHolder() == null;
                boolean f2 = this.space.getPosition(x, y).getHolder() != null && !this.space.getPosition(x, y).getHolder().isDead();
                if(f1 || f2) return this.space.getPosition(x, y);
            }
        }
    }

    private static Position.Point[] OFFSET = {new Position.Point(-1, -1), new Position.Point(-1,0), new Position.Point(-1,1), new Position.Point(0,-1), new Position.Point(0,1),new Position.Point(1,-1),new Position.Point(1,0),new Position.Point(1,1)};

    @Override
    public void run() {
        if(position == null) return;
        running = true;
        while(!dead && running){
            Position pos = choosePosition();
            pos.lock();
            if(pos.getHolder() == null){
                move(pos);
            }else{
                fight(pos.getHolder());
            }
            pos.unlock();
            setChanged();
            notifyObservers("stateChanged");
            //space.updateUI();
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                System.out.println(" interrupted");
            }
        }
    }
}
