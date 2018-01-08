import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Monster extends Creature {

    public Monster(){
        super();
    }

    public Monster(SpacePanel space){
        super(space);
    }

    @Override
    public int getPower() {
        return 60;
    }

    @Override
    public Image getImage() {
        URL url;
        if(isDead()){
            url = getClass().getClassLoader().getResource("monster_dead.png");
        }else{
            url = getClass().getClassLoader().getResource("monster_alive.png");
        }
        ImageIcon icon = new ImageIcon(url);
        return icon.getImage();
    }

    @Override
    protected boolean sameKind(Creature c) {
        if(c instanceof Soldier) return true;
        if(c instanceof Snake) return true;
        if(c instanceof Monster) return true;
        return false;
    }
}
