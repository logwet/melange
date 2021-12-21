package me.logwet.melange;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.JFrame;
import me.logwet.melange.gui.MainFrame;

public class Main {
    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        JFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}
