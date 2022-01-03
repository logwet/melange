package me.logwet.melange.config;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.logwet.melange.commands.commands.alias.AliasManager;
import me.logwet.melange.config.Config.ConfigInstance.ConfigData;
import net.harawata.appdirs.AppDirsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    public static final Logger LOGGER = LoggerFactory.getLogger("config");
    public static final File LOG_DIR;
    private static final ObjectMapper OBJECT_MAPPER = Metadata.OBJECT_MAPPER;
    private static final File CONFIG_FILE;
    private static ConfigInstance CONFIG;

    static {
        File rootDir =
                new File(
                        AppDirsFactory.getInstance().getUserConfigDir("melange", null, null, true));

        CONFIG_FILE = new File(rootDir, "config.json");
        LOG_DIR = new File(rootDir, "logs");

        boolean dirsExisting = LOG_DIR.mkdirs();
    }

    public static void load() {
        LOGGER.info("Loading config...");

        CONFIG = new ConfigInstance(ConfigInstance.load());
        ConfigInstance.save();

        AliasManager.load();
    }

    public static void save() {
        LOGGER.info("Saving config...");

        AliasManager.save();

        ConfigInstance.save();
        CONFIG = new ConfigInstance(ConfigInstance.load());
    }

    public static ConfigData getData() {
        return CONFIG.data;
    }

    public static Object getProperty(String key) {
        return getData().get(key);
    }

    public static void setProperty(String key, Object value) {
        getData().put(key, value);
    }

    @RequiredArgsConstructor
    protected static class ConfigInstance {
        private static final int SCHEMA = 1;

        protected final ConfigData data;

        protected static ConfigData load() {
            ConfigData data = null;

            try {
                data = OBJECT_MAPPER.readValue(CONFIG_FILE, ConfigData.class);
            } catch (FileNotFoundException e) {
                LOGGER.warn("Config file not found, will eventually generate one.");
            } catch (IOException e) {
                LOGGER.error("Unable to load config", e);
            }

            if (Objects.nonNull(data)) {
                int schema = data.getSchema();
                if (schema != SCHEMA) {
                    LOGGER.warn(
                            "Config file schema does not match up with Melange!, Attempting to convert...");
                    data = ConfigData.migrate(data);
                }

                data.checkPropertiesStatus();
                data.fillNullDefaults();
                data.trimNonDefaults();

                return data;
            }

            LOGGER.warn("Unable to load config file. Using defaults...");
            return ConfigData.fromDefaults();
        }

        protected static void save() {
            try {
                OBJECT_MAPPER.writeValue(CONFIG_FILE, getData());
            } catch (IOException e) {
                LOGGER.error("Unable to save config", e);
            }
        }

        public static class ConfigData {
            private static final ImmutableMap<String, Object> DEFAULT_PROPERTIES;

            static {
                Builder<String, Object> builder = ImmutableMap.builder();

                builder.put("stronghold_count", 3);
                builder.put("range", 100);
                builder.put("command_prefix", "?");

                builder.put("twitch_oauth_token", "");
                builder.put("twitch_channel_name", "");
                builder.put("twitch_user_name", "");

                builder.put("twitch_min_role", 1);

                builder.put("command_aliases", AliasManager.DEFAULT_ALIASES);

                DEFAULT_PROPERTIES = builder.build();
            }

            @Getter protected int schema;

            protected Map<String, Object> properties = new HashMap<>();

            public ConfigData() {
                this.schema = SCHEMA;
                this.properties = new HashMap<>(DEFAULT_PROPERTIES);
            }

            public ConfigData(int schema, Map<String, Object> properties) {
                this.schema = schema;
                this.properties.putAll(properties);
            }

            public static ConfigData fromDefaults() {
                return new ConfigData(SCHEMA, DEFAULT_PROPERTIES);
            }

            public static ConfigData migrate(ConfigData oldConfig) {
                if (oldConfig.schema > SCHEMA) {
                    return fromDefaults();
                }

                oldConfig.schema = SCHEMA;

                return oldConfig;
            }

            public void checkPropertiesStatus() {
                if (Objects.isNull(properties)) {
                    properties = new HashMap<>();
                }
            }

            public void fillNullDefaults() {
                for (Entry<String, Object> entry : DEFAULT_PROPERTIES.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (Objects.isNull(properties.get(key))) {
                        properties.put(key, value);
                    }
                }
            }

            public void trimNonDefaults() {
                for (String key : ImmutableSet.copyOf(properties.keySet())) {
                    if (!DEFAULT_PROPERTIES.containsKey(key)) {
                        properties.remove(key);
                    }
                }
            }

            @JsonAnyGetter
            public Map<String, Object> getProperties() {
                return properties;
            }

            public Object get(String key) {
                return properties.get(key);
            }

            @JsonAnySetter
            public void put(String key, Object value) {
                properties.put(key, value);
            }
        }
    }
}
