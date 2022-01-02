package me.logwet.melange.gui;

import ch.qos.logback.classic.Logger;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.opencl.OpenCLPlatform;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
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
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent.EventType;
import me.logwet.melange.Melange;
import me.logwet.melange.config.Config;
import me.logwet.melange.render.Heatmap;
import org.slf4j.LoggerFactory;

public class MainFrame extends JFrame {
    public static final Logger LOGGER = (Logger) LoggerFactory.getLogger(MainFrame.class);

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
    protected JPanel creditsPanel;
    protected JTextPane creditsTextPane;

    protected boolean lightEditsPending = false;
    protected boolean heavyEditsPending = false;

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

        divineForceRenderButton.addActionListener(
                e -> Melange.resetHeatmapAsync(this::updateRender));

        divineResetbutton.addActionListener(
                e -> {
                    Melange.removeAllProviders();
                    Melange.resetHeatmapAsync(this::updateRender);
                });

        settingsPanel.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentShown(ComponentEvent e) {
                        LOGGER.info("Settings panel opened");

                        lightEditsPending = false;
                        heavyEditsPending = false;

                        super.componentShown(e);
                    }

                    @Override
                    public void componentHidden(ComponentEvent e) {
                        LOGGER.info("Settings panel closed");

                        if (lightEditsPending || heavyEditsPending) {
                            refreshLightSettings();
                        }
                        if (heavyEditsPending) {
                            refreshHeavySettings();
                        }

                        Melange.resetHeatmapAsync(() -> updateRender());

                        lightEditsPending = false;
                        heavyEditsPending = false;

                        super.componentHidden(e);
                    }
                });

        creditsTextPane.addHyperlinkListener(
                e -> {
                    if (e.getEventType() == EventType.ACTIVATED) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException | URISyntaxException ex) {
                            ex.printStackTrace();
                        }
                    }
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

        try {
            Melange.resetHeatmapAsync(this::updateRender).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Unable to perform initial heatmap initialization", e);
        }
    }

    private void addRender(BufferedImage render) {
        divineRendererLabel.setIcon(new ImageIcon(render));
        LOGGER.info("Updated image object");
    }

    public void updateRender() {
        BufferedImage render;

        if (Objects.nonNull(Melange.getHeatmap())) {
            if (!Objects.equals(Thread.currentThread().getName(), "melange")) {
                try {
                    render = Melange.submit(() -> Melange.getHeatmap().getRender()).get();
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Unable to update render", e);
                    render = Heatmap.newRawImage();
                }
            } else {
                render = Melange.getHeatmap().getRender();
            }
        } else {
            render = Heatmap.newRawImage();
        }

        BufferedImage finalRender = render;
        SwingUtilities.invokeLater(() -> addRender(finalRender));

        LOGGER.info("Updated render");
    }

    private void refreshLightSettings() {
        try {
            Melange.submit(Config::save).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Unable to save light settings", e);
        }
    }

    private void refreshHeavySettings() {
        try {
            Melange.resetCommandManager().get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Unable to save heavy settings", e);
        }
    }
}
