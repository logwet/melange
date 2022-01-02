package me.logwet.melange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static Logger LOGGER;

    static {
        //        LoggerContext ctx =
        // Configurator.initialize(LoggerConfiguration.buildConfiguration());

        LOGGER = LoggerFactory.getLogger("main");
    }

    public static void main(String[] args) {
        LOGGER.info("Launching Melange...");
        Melange.launch();
    }
}
