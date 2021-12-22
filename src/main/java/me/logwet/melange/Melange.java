package me.logwet.melange;

import me.logwet.melange.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Melange {
    public static Logger LOGGER = LogManager.getLogger("melange");

    public static void onStarting() {
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
