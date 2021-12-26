package me.logwet.melange;

import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Dimension;
import java.util.Objects;
import javax.swing.JFrame;
import me.logwet.melange.config.Config;
import me.logwet.melange.gui.MainFrame;
import me.logwet.melange.parallelization.SharedKernels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Melange {
    public static final String VERSION;
    public static final Dimension MIN_WINDOW_DIMENSION = new Dimension(600, 400);
    public static final Dimension WINDOW_DIMENSION = new Dimension(800, 600);
    public static Logger LOGGER = LogManager.getLogger("melange");

    static {
        String version = Melange.class.getPackage().getImplementationVersion();

        VERSION = Objects.nonNull(version) ? version : "DEVELOPMENT";
    }

    public static void launch() {
        Melange.onStarting();

        FlatIntelliJLaf.setup();
        JFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    public static void onStarting() {
        SharedKernels.forceLoad();
        Config.load();
    }

    public static void onClosing() {
        try {
            updateConfig();
        } catch (Exception e) {
            LOGGER.error("Unable to shut Melange down properly.", e);
        }
    }

    public static void updateConfig() {
        Config.save();
    }
}
