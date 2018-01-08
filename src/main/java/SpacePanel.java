import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SpacePanel extends JPanel implements Observer {

    public static final int M = 10;
    public static final int N = 10;

    private Position[][] space;
    private boolean started = false;

    private Creature[] huluwa;
    private Creature grandpa;
    private Creature[] enemy;

    private ExecutorService service;

    private static final String filename = "gameData";
    private ObjectOutputStream outputStream = null;

    public SpacePanel() {
        super();
        space = new Position[M][N];
        for (int i = 0; i < M; ++i) {
            for (int j = 0; j < N; ++j) {
                space[i][j] = new Position(new Position.Point(i, j));
            }
        }
        service = Executors.newCachedThreadPool();
        setSize(N * Position.WIDTH, M * Position.HEIGHT);
    }

    public Position getPosition(int i, int j) {
        return space[i][j];
    }

    public Creature[] getHuluwa() {
        return huluwa;
    }

    public Creature getGrandpa() {
        return grandpa;
    }

    public Creature[] getHuluwaAndGrandpa() {
        Creature[] res = new Creature[8];
        for (int i = 0; i < 8; ++i) {
            if (i == 0) res[i] = grandpa;
            else res[i] = huluwa[i - 1];
        }
        return res;
    }

    public Creature[] getEnemy() {
        return enemy;
    }

    public static boolean outOfBound(int x, int y) {
        if (x < 0 || x >= M) return true;
        if (y < 0 || y >= N) return true;
        return false;
    }

    public boolean isStarted() {
        return started;
    }

    public void stopGame(boolean whoWins) {
        try {
            writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Creature c : huluwa) {
            if (!c.isDead()) {
                c.stop();
            }
        }
        if (!grandpa.isDead()) grandpa.stop();
        for (Creature c : enemy) {
            if (!c.isDead()) {
                c.stop();
            }
        }
        started = false;
        try {
            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        service.shutdownNow();
        String message = whoWins ? "Game Over:Huluwa Wins" : "Game Over Enemy wins";
        JOptionPane.showMessageDialog(this, message, "message", JOptionPane.INFORMATION_MESSAGE);
    }

    public void startGame() {
        started = true;
        for (int i = 0; i < M; ++i) {
            for (int j = 0; j < N; ++j) {
                if (space[i][j].getHolder() != null) {
                    space[i][j].getHolder().setPosition(null);
                    space[i][j].setHolder(null);
                }
            }
        }
        Random random = new Random();
        huluwa = new Creature[7];
        for (int i = 0; i < 7; ++i) {
            huluwa[i] = new Huluwa(this);
        }
        grandpa = new Grandpa(this);
        enemy = new Creature[8];
        for (int i = 0; i < 8; ++i) {
            if (i == 0) enemy[i] = new Snake(this);
            else if (i < 4) enemy[i] = new Monster(this);
            else enemy[i] = new Soldier(this);
        }
        arrangeFormation(huluwa, Formation.CHANGSHE, true);
        arrangeFormation(new Creature[]{grandpa}, Formation.SINGLE, true);
        int c = random.nextInt(Formation.choices.length);
        arrangeFormation(enemy, Formation.choices[c], false);
        //arrangeFormation(enemy, Formation.YANXING, false);
        updateUI();
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Creature h : huluwa) service.execute(h);
        service.execute(grandpa);
        for (Creature e : enemy) service.execute(e);
    }

    private void arrangeFormation(Creature[] creatures, Formation formation, boolean left) {
        Position.Point base = null;
        int len = formation.getPoints().length;
        boolean found = false;
        if (left) {
            for (int j = 0; j < N && !found; ++j) {
                for (int i = 0; i < M; ++i) {
                    base = new Position.Point(i, j);
                    found = suitable(formation, base);
                    if (found) break;
                }
            }
        } else {
            for (int j = N - 1; j >= 0 && !found; --j) {
                for (int i = M - 1; i >= 0; --i) {
                    base = new Position.Point(i, j);
                    found = suitable(formation, base);
                    if (found) break;
                }
            }
        }
        for (int i = 0; i < len; ++i) {
            int x = base.getX() + formation.getPoints()[i].getX();
            int y = base.getY() + formation.getPoints()[i].getY();
            creatures[i].move(space[x][y]);
        }
    }

    private boolean suitable(Formation formation, Position.Point base) {
        int len = formation.getPoints().length;
        for (int i = 0; i < len; ++i) {
            int x = base.getX() + formation.getPoints()[i].getX();
            int y = base.getY() + formation.getPoints()[i].getY();
            if (outOfBound(x, y) || space[x][y].getHolder() != null) return false;
        }
        return true;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < M; ++i) {
            for (int j = 0; j < N; ++j) {
                Image image;
                Position pos = getPosition(i, j);
                if (pos.getHolder() == null) {
                    URL url = this.getClass().getClassLoader().getResource("background.png");
                    ImageIcon icon = new ImageIcon(url);
                    image = icon.getImage();
                } else {
                    image = pos.getHolder().getImage();
                }
                g.drawImage(image, j * Position.WIDTH, i * Position.HEIGHT, Position.WIDTH, Position.HEIGHT, this);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg != null && arg instanceof String) {
            updateUI();
            try {
                writeToFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (o instanceof Huluwa || o instanceof Grandpa) {
                Creature[] creatures = getHuluwaAndGrandpa();
                for (Creature creature : creatures) {
                    if (!creature.isDead()) {
                        return;
                    }
                }
                stopGame(false);
            } else {
                Creature[] creatures = getEnemy();
                for (Creature creature : creatures) {
                    if (!creature.isDead()) {
                        return;
                    }
                }
                stopGame(true);
            }
        }
    }

    enum HolderType {NOTHING, Huluwa, Grandpa, Snake, Monster, Soldier}

    private synchronized void writeToFile() throws IOException {
        if (outputStream == null) return;
        synchronized (outputStream) {
            for (int i = 0; i < M; ++i) {
                for (int j = 0; j < N; ++j) {
                    Position pos = space[i][j];
                    pos.lock();
                    if (pos.getHolder() == null) {
                        outputStream.writeInt(HolderType.NOTHING.ordinal());
                    } else {
                        Creature creature = pos.getHolder();
                        int order = HolderType.valueOf(creature.getClass().getSimpleName()).ordinal();
                        boolean dead = creature.isDead();
                        outputStream.writeInt(order);
                        outputStream.writeBoolean(dead);
                    }
                    pos.unlock();
                }
            }
        }
    }

    private static final long REPLAY_TIME_INTERVAL = 100l;

    public void replayGame(File file) {
        started = true;
        Thread replayThread = new Thread(() -> {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                while (inputStream.available() > 0) {
                    for (int i = 0; i < M; ++i) {
                        for (int j = 0; j < N; ++j) {
                            int ordi = inputStream.readInt();
                            if (HolderType.values()[ordi].compareTo(HolderType.NOTHING) == 0) {
                                space[i][j].setHolder(null);
                            } else {
                                String name = HolderType.values()[ordi].name();
                                boolean dead = inputStream.readBoolean();
                                Class c = Class.forName(name);
                                Creature creature = (Creature) c.newInstance();
                                creature.setDead(dead);
                                creature.move(space[i][j]);
                            }
                            updateUI();
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(REPLAY_TIME_INTERVAL);
                }
                inputStream.close();
                started = false;
                updateUI();
                System.out.println("game over");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        replayThread.start();
    }
}
