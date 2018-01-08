import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class TestHuluwa {

    @Test
    public void testOutOfBound(){
        int M = SpacePanel.M;
        int N = SpacePanel.N;

        int x = M/2, y = N/2;
        assertEquals(SpacePanel.outOfBound(x,y), false);

        x = -1;
        assertEquals(SpacePanel.outOfBound(x, y), true);

        y = -1;
        assertEquals(SpacePanel.outOfBound(x, y), true);
    }

    @Test
    public void testPosition(){
        Position pos = new Position(new Position.Point(0,0));
        TestThread t1 = new TestThread(pos);
        TestThread t2 = new TestThread(pos);
        t1.start();
        t2.start();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread.State state1 = t1.getState();
        Thread.State state2 = t2.getState();
        boolean f1 = state1.compareTo(Thread.State.RUNNABLE)==0 && state2.compareTo(Thread.State.WAITING)==0;
        boolean f2 = state1.compareTo(Thread.State.WAITING)==0 && state2.compareTo(Thread.State.RUNNABLE)==0;
        assertEquals(f1 || f2, true);
    }

    class TestThread extends Thread{

        private Position pos;

        public TestThread(Position pos){
            this.pos = pos;
        }

        @Override
        public void run() {
            pos.lock();
            while(true){

            }
        }
    }
}
