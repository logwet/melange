package me.logwet.melange.gui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

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
        this.pack();
    }
}
