import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Huluwa extends Creature {

    public Huluwa(){
        super();
    }

    public Huluwa(SpacePanel space){
        super(space);
    }

    @Override
    public int getPower() {
        return 50;
    }

    @Override
    public Image getImage() {
        URL url;
        if(isDead()){
            url = getClass().getClassLoader().getResource("huluwa_dead.png");
        }else{
            url = getClass().getClassLoader().getResource("huluwa_alive.png");
        }
        ImageIcon icon = new ImageIcon(url);
        return icon.getImage();
    }


    @Override
    protected boolean sameKind(Creature c) {
        if(c instanceof Huluwa) return true;
        if(c instanceof Grandpa) return true;
        return false;
    }
}
