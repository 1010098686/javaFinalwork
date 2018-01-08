import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Grandpa extends Creature {

    public Grandpa(){
        super();
    }

    public Grandpa(SpacePanel space){
        super(space);
    }

    @Override
    public int getPower() {
        return 70;
    }

    @Override
    public Image getImage() {
        URL url;
        if(!isDead()){
            url = getClass().getClassLoader().getResource("grandpa_alive.png");
        }else{
            url = getClass().getClassLoader().getResource("grandpa_dead.png");
        }
        ImageIcon icon = new ImageIcon(url);
        return icon.getImage();
    }

    @Override
    protected boolean sameKind(Creature c) {
        if(c instanceof Huluwa || c instanceof Grandpa) return true;
        return false;
    }
}
