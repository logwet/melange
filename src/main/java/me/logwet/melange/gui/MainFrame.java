package me.logwet.melange.gui;

import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.opencl.OpenCLPlatform;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import me.logwet.melange.Melange;
import me.logwet.melange.render.Heatmap;

public class MainFrame extends JFrame {
    protected JPanel mainPanel;
    protected JTabbedPane tabbedPane;
    protected JLabel divineRendererLabel;
    protected JPanel divinePanel;
    protected JPanel settingsPanel;
    protected JSplitPane settingsSplitPane;
    protected JCheckBox checkBox1;
    protected JCheckBox checkBox2;
    protected JLabel melangeMetaLabel;
    protected JPanel divineLeftPanel;
    protected JTextArea divineMetaTextArea;
    protected JPanel divineRightPanel;
    protected JPanel divineInputPanel;
    protected JLabel divineSelectionMetaLabel;
    protected JTextField divineSelectionTextField;
    protected JButton divineForceRenderButton;
    protected JButton divineResetbutton;
    protected JScrollPane divineSelectionScrollPane;
    protected JList divineSelectionList;
    protected JTree divineSelectionTree;
    protected JLabel melangeOpenCLStatusLabel;
    protected JPanel melangeMetaPanel;

    public MainFrame() {
        super("Melange");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setMinimumSize(Melange.MIN_WINDOW_DIMENSION);

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

        divineForceRenderButton.addActionListener(e -> {
            Melange.heatmap = new Heatmap();
            updateRender();
        });

        divineResetbutton.addActionListener(e -> {
            Melange.providerList.clear();
            Melange.heatmap = new Heatmap();
            updateRender();
        });

        this.pack();
        this.addDataToTextLabels();

        this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));

        this.setIconImage(
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/melange/icon.png")));
    }

    private void addDataToTextLabels() {
        melangeMetaLabel.setText("Melange v" + Melange.VERSION + " by logwet");

        Device device = KernelManager.instance().bestDevice();
        String deviceMessage = device.getShortDescription();
        if (device instanceof OpenCLDevice) {
            String name = ((OpenCLDevice) device).getName();
            if (Objects.nonNull(name)) {
                deviceMessage = name;
            }

            OpenCLPlatform platform = (new OpenCLPlatform()).getOpenCLPlatforms().get(0);
            if (Objects.nonNull(platform)) {
                deviceMessage += " with " + platform.getVersion();
            }
        } else {
            deviceMessage += " (GPU not found or usable)";
        }

        melangeOpenCLStatusLabel.setText("Using " + deviceMessage);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        divineRendererLabel = new JLabel();

        Melange.heatmap = new Heatmap();
        updateRender();
    }

    private void addRender(BufferedImage render) {
        divineRendererLabel.setIcon(new ImageIcon(render));
    }

    public void updateRender() {
        if (Objects.nonNull(Melange.heatmap)) {
            addRender(Melange.heatmap.getRender());
        } else {
            addRender(Heatmap.newRawImage());
        }
    }
}
