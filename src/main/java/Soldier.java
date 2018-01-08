import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Soldier extends Creature {

    public Soldier(){
        super();
    }

    public Soldier(SpacePanel space){
        super(space);
    }

    @Override
    public int getPower() {
        return 40;
    }

    @Override
    public Image getImage() {
        URL url;
        if(!isDead()){
            url = getClass().getClassLoader().getResource("soldier_alive.png");
        }else{
            url = getClass().getClassLoader().getResource("soldier_dead.png");
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
