package jahan.khan.jdynmapgriefprevention;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class UTF8Control extends ResourceBundle.Control {
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IOException {
        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, "properties");
        ResourceBundle bundle = null;
        InputStream stream = null;
        URLConnection connection;

        if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
                connection = url.openConnection();
                if (connection != null) {
                    connection.setUseCaches(false);
                    stream = connection.getInputStream();
                }
            }
        } else {
            stream = loader.getResourceAsStream(resourceName);
        }
        if (stream != null) {
            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(stream, "UTF-8");

                bundle = new PropertyResourceBundle(isr);
            } finally {
                stream.close();
                isr.close();
            }
        }
        return bundle;
    }
}
