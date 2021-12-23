package me.logwet.melange;

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
        Melange.launch();
    }
}
