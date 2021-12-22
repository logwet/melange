package me.logwet.melange;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.JFrame;
import me.logwet.melange.config.Config;
import me.logwet.melange.gui.MainFrame;
import me.logwet.melange.logging.LoggerConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.spi.LoggerContext;

public class Main {
    public static Logger LOGGER;

    static {
        LoggerContext ctx = Configurator.initialize(LoggerConfiguration.buildConfiguration());

        LOGGER = LogManager.getLogger("main");
    }

    public static void main(String[] args) {
        LOGGER.info("Launching Melange...");
        testLogging();

        Melange.onStarting();

        FlatIntelliJLaf.setup();
        JFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    public static void testLogging() {
        LOGGER.debug("this is  adebug");
        LOGGER.warn("Test");
        LOGGER.info("Test");
        LOGGER.error("Test");
        Config.LOGGER.warn("WGT");
        Config.LOGGER.debug("WGT");
    }
}
