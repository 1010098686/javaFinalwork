import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Snake extends Creature {

    public Snake(){
        super();
    }

    public Snake(SpacePanel space){
        super(space);
    }

    @Override
    public int getPower() {
        return 70;
    }

    @Override
    public Image getImage() {
        URL url;
        if(isDead()){
            url = getClass().getClassLoader().getResource("snake_dead.png");
        }else{
            url = getClass().getClassLoader().getResource("snake_alive.png");
        }
        ImageIcon icon = new ImageIcon(url);
        return icon.getImage();
    }

    @Override
    protected boolean sameKind(Creature c) {
        if(c instanceof Snake) return true;
        if(c instanceof Soldier) return true;
        if(c instanceof Monster) return true;
        return false;
    }
}
