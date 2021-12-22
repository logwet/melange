package me.logwet.melange.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import me.logwet.melange.Melange;

public class MainFrame extends JFrame {
    protected JPanel mainPanel;
    protected JButton button1;
    protected JButton button2;
    protected JButton button3;
    protected JSlider slider1;
    protected JComboBox comboBox1;

    public MainFrame() {
        super("Melange");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);

        this.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowGainedFocus(WindowEvent e) {
                        Melange.updateConfig();
                        super.windowGainedFocus(e);
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        Melange.onClosing();
                        super.windowClosing(e);
                    }
                });

        this.pack();
    }
}
