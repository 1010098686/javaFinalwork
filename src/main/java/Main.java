import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class Main extends JFrame {

    private static SpacePanel panel;
    private static Main m;

    public static void main(String[] args) {
        m = new Main();
        m.addKeyListener(listener);
        m.setVisible(true);
    }

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new SpacePanel();
        add(panel);
        panel.requestFocus();
        setSize(panel.getWidth() + Position.WIDTH, panel.getHeight() + Position.HEIGHT);
        setLocationRelativeTo(null);
    }

    private static KeyAdapter listener = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            super.keyTyped(e);
            char ch = e.getKeyChar();
            if (ch == ' ') {
                if (!panel.isStarted()) {
                    panel.startGame();
                } else {
                    JOptionPane.showMessageDialog(null, "the game has started", "warning", JOptionPane.WARNING_MESSAGE);
                }
            } else if (ch == 'L' || ch == 'l') {
                if (panel.isStarted()) {
                    JOptionPane.showMessageDialog(null, "the game has started", "warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (chooser.showDialog(null, "choose file") == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    panel.replayGame(file);
                }
            }
        }
    };
}
