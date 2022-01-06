package me.logwet.melange.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vdurmont.semver4j.Semver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Metadata {
    public static final Logger LOGGER = LoggerFactory.getLogger(Metadata.class);

    public static final ObjectMapper OBJECT_MAPPER;
    public static final String NAME;
    public static final String VERSION;
    public static final String BASE_VERSION;

    static {
        OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, String> map = null;

        try {
            //noinspection unchecked
            map =
                    OBJECT_MAPPER.readValue(
                            Metadata.class.getResource("/melange/metadata.json"), Map.class);
        } catch (IOException e) {
            LOGGER.error("Unable to read metadata file from resources", e);
        }

        if (Objects.nonNull(map)) {
            NAME = map.get("name");
            VERSION = map.get("version");
            BASE_VERSION = map.get("base_version");
        } else {
            NAME = "melange";
            VERSION = "0.0.1";
            BASE_VERSION = "0.0.1";
        }
    }

    public static Update getUpdate() {
        try {
            URL url =
                    new URL(
                            "https://api.github.com/repos/logwet/"
                                    + NAME.toLowerCase(Locale.ROOT)
                                    + "/releases/latest");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestProperty("User-Agent", "logwet/melange/" + VERSION);

            int status = conn.getResponseCode();

            Reader streamReader;

            if (status > 299) {
                streamReader = new InputStreamReader(conn.getErrorStream());
            } else {
                streamReader = new InputStreamReader(conn.getInputStream());
            }

            BufferedReader bufferedReader = new BufferedReader(streamReader);

            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            bufferedReader.close();
            conn.disconnect();

            String content = stringBuilder.toString();

            if (status == 200) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = OBJECT_MAPPER.readValue(content, Map.class);
                Object tagName = data.get("tag_name");
                if (Objects.nonNull(tagName)) {
                    if (tagName instanceof String) {
                        String version = StringUtils.removeStart((String) tagName, "v");

                        Semver latestVer = new Semver(version);
                        Semver currVer = new Semver(BASE_VERSION);

                        if (latestVer.isGreaterThan(currVer)) {
                            Object htmlUrl = data.get("html_url");
                            if (Objects.nonNull(htmlUrl)) {
                                if (htmlUrl instanceof String) {
                                    URL latestURL = new URL((String) htmlUrl);

                                    return new Update(true, true, latestURL, latestVer);
                                }
                            }

                            return new Update(false, false, null, null);
                        }

                        return new Update(true, false, null, null);
                    }
                }
            } else {
                LOGGER.error("Got non 200 status code from HTTP request to GitHub");
                LOGGER.error(content);
            }

        } catch (Exception e) {
            LOGGER.error("Unable to get data from GitHub", e);
        }

        return new Update(false, false, null, null);
    }

    @Value
    public static class Update {
        boolean valid;
        boolean shouldUpdate;
        URL url;
        Semver latestVer;
    }
}
