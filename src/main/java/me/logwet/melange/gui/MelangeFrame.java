package me.logwet.melange.gui;

import ch.qos.logback.classic.Logger;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.kernel.KernelManager;
import com.aparapi.internal.opencl.OpenCLPlatform;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import me.logwet.melange.Melange;
import me.logwet.melange.MelangeConstants;
import me.logwet.melange.config.Config;
import me.logwet.melange.config.Metadata;
import me.logwet.melange.config.Metadata.Update;
import me.logwet.melange.render.Heatmap;
import me.logwet.melange.util.ArrayHelper;
import me.logwet.melange.util.BufferHolder;
import org.apache.commons.math3.util.FastMath;
import org.slf4j.LoggerFactory;

public class MelangeFrame extends JFrame {
    public static final Logger LOGGER = (Logger) LoggerFactory.getLogger(MelangeFrame.class);
    private static final HyperlinkListener HYPERLINK_LISTENER;

    static {
        HYPERLINK_LISTENER =
                e -> {
                    if (e.getEventType() == EventType.ACTIVATED) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (Exception ex) {
                            LOGGER.error("Unable to open hyperlink", ex);
                        }
                    }
                };
    }

    protected JPanel mainPanel;
    protected JLabel heatmapRendererLabel;
    protected JPanel divinePanel;
    protected JPanel settingsPanel;
    protected JSplitPane settingsSplitPane;
    protected JCheckBox checkBox1;
    protected JCheckBox checkBox2;
    protected JButton renderForceRenderButton;
    protected JButton renderResetbutton;
    protected JList providerDisplayList;
    protected JTree providerSelectionTree;
    protected JPanel creditsPanel;
    protected JTextPane creditsTextPane;
    protected JTextArea melangeSystemStatusTextField;
    protected JTextPane melangeVersionTextPane;
    protected JTextField providerSelectionTextField;
    protected JButton providerAddButton;
    protected JButton providerRemoveButton;
    protected JTextPane providerInfoTextPane;
    protected JTextPane divineMetaLeftTextPane;
    protected JTextPane divineMetaRightTextPane;
    protected JSpinner mainRangeSelectionSpinner;
    protected JCheckBox checkBox3;

    protected boolean lightEditsPending = false;
    protected boolean heavyEditsPending = false;

    public MelangeFrame() {
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

        renderForceRenderButton.addActionListener(
                e -> Melange.resetHeatmapAsync(this::updateRender));

        renderResetbutton.addActionListener(
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
                        if (lightEditsPending || heavyEditsPending) {
                            Melange.resetHeatmapAsync(() -> updateRender());
                        }

                        lightEditsPending = false;
                        heavyEditsPending = false;

                        super.componentHidden(e);
                    }
                });

        creditsTextPane.addHyperlinkListener(HYPERLINK_LISTENER);

        heatmapRendererLabel.addMouseMotionListener(
                new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        if (Objects.nonNull(Melange.getHeatmap())) {
                            BufferHolder bufferHolder = Melange.getHeatmap().getBufferHolder();
                            if (Objects.nonNull(bufferHolder)) {
                                Point location = e.getLocationOnScreen();
                                Point offset = e.getComponent().getLocationOnScreen();
                                int x = (location.x - offset.x);
                                int y = (location.y - offset.y);

                                int dx =
                                        (int)
                                                FastMath.round(
                                                        (x - MelangeConstants.HALF_WIDTH)
                                                                * MelangeConstants.SCALING_FACTOR);
                                int dy =
                                        (int)
                                                FastMath.round(
                                                        (y - MelangeConstants.HALF_WIDTH)
                                                                * MelangeConstants.SCALING_FACTOR);

                                double p =
                                        bufferHolder.getBuffer()[ArrayHelper.getIndex(x, y)] * 100D;

                                heatmapRendererLabel.setToolTipText(
                                        dx
                                                + " "
                                                + dy
                                                + " "
                                                + formatDoubleToTwoDecimalPlaces(p)
                                                + "%");
                            }
                        }
                    }
                });

        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ToolTipManager.sharedInstance().setInitialDelay(0);

        this.pack();
        this.addDataToTextLabels();

        this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));

        this.setIconImage(
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/melange/icon.png")));
    }

    private static String formatDoubleToTwoDecimalPlaces(double x) {
        if (x == 0) {
            return String.valueOf(0D);
        } else if (x < 0.01) {
            return String.format(Locale.ROOT, "%.2e", x);
        } else {
            return String.valueOf(FastMath.round(x * 100) / 100D);
        }
    }

    private void addDataToTextLabels() {
        Device device = KernelManager.instance().bestDevice();
        String deviceMessage = device.getShortDescription();
        if (device instanceof OpenCLDevice) {
            String name = ((OpenCLDevice) device).getName();
            if (Objects.nonNull(name)) {
                deviceMessage = name;
            }
            OpenCLPlatform platform = ((OpenCLDevice) device).getOpenCLPlatform();
            if (Objects.nonNull(platform)) {
                deviceMessage += " with " + platform.getVersion();
            }
        } else {
            deviceMessage += " (GPU acceleration not available)";
        }

        melangeSystemStatusTextField.setText("Device: " + deviceMessage);
        melangeSystemStatusTextField.append(
                "\nJRE: "
                        + System.getProperty("java.vendor")
                        + " "
                        + System.getProperty("java.runtime.name")
                        + " "
                        + System.getProperty("java.version"));

        final String versionString = "Melange v" + Metadata.VERSION + " by logwet";
        melangeVersionTextPane.setText(versionString);

        CompletableFuture.runAsync(
                () -> {
                    Update update = Metadata.getUpdate();
                    final Runnable runnable;

                    if (update.isValid()) {
                        if (update.isShouldUpdate()) {
                            final String updateString =
                                    versionString
                                            + "<br> Update available! Get <a href = \""
                                            + update.getUrl().toString()
                                            + "\">v"
                                            + update.getLatestVer().getOriginalValue()
                                            + "</a>";

                            runnable =
                                    () -> {
                                        melangeVersionTextPane.setContentType("text/html");
                                        melangeVersionTextPane.setText(updateString);
                                        melangeVersionTextPane.addHyperlinkListener(
                                                HYPERLINK_LISTENER);
                                    };
                        } else {
                            runnable =
                                    () ->
                                            melangeVersionTextPane.setText(
                                                    versionString
                                                            + "\n You are using the latest version!");
                        }
                    } else {
                        runnable =
                                () ->
                                        melangeVersionTextPane.setText(
                                                versionString
                                                        + "\n Unable to query for latest version!");
                    }

                    SwingUtilities.invokeLater(
                            () -> {
                                runnable.run();

                                StyledDocument style = melangeVersionTextPane.getStyledDocument();
                                SimpleAttributeSet align = new SimpleAttributeSet();
                                StyleConstants.setAlignment(align, StyleConstants.ALIGN_RIGHT);
                                style.setParagraphAttributes(0, style.getLength(), align, false);
                            });
                });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        heatmapRendererLabel = new JLabel();

        try {
            Melange.resetHeatmapAsync(this::updateRender).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Unable to perform initial heatmap initialization", e);
        }
    }

    private void addRender(BufferedImage render) {
        heatmapRendererLabel.setIcon(new ImageIcon(render));
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
