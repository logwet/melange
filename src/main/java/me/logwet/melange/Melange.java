package me.logwet.melange;

import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.JFrame;
import me.logwet.melange.commands.CommandManager;
import me.logwet.melange.config.Config;
import me.logwet.melange.divine.provider.DivineProvider;
import me.logwet.melange.gui.MainFrame;
import me.logwet.melange.kernel.SharedKernels;
import me.logwet.melange.render.Heatmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class Melange {
    public static final String VERSION;
    public static final Dimension MIN_WINDOW_DIMENSION = new Dimension(600, 400);
    public static final Dimension WINDOW_DIMENSION = new Dimension(800, 600);
    public static final Logger LOGGER = LogManager.getLogger("melange");

    public static final List<DivineProvider> providerList = new ArrayList<>();

    @Nullable public static CommandManager commandManager;
    @Nullable public static Heatmap heatmap;

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
        commandManager = new CommandManager();
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
