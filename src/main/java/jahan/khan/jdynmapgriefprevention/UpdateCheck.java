package jahan.khan.jdynmapgriefprevention;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class UpdateCheck {
    private final JavaPlugin plugin;
    private static final String KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
    private static final String REQUEST_METHOD = "POST";
    private String RESOURCE_ID = "";

    private static final String HOST = "http://www.spigotmc.org";

    private static final String QUERY = "/api/general.php";
    private String WRITE_STRING;
    private String version;
    private String oldVersion;
    private UpdateResult result = UpdateResult.DISABLED;
    private HttpURLConnection connection;

    public static enum UpdateResult {
        NO_UPDATE,
        DISABLED,
        FAIL_SPIGOT,
        FAIL_NOVERSION,
        BAD_RESOURCEID,
        UPDATE_AVAILABLE;
    }

    public UpdateCheck(JavaPlugin plugin, String resourceId, boolean disabled) {
        this.RESOURCE_ID = resourceId;
        this.plugin = plugin;
        this.oldVersion = this.plugin.getDescription().getVersion();

        if (disabled) {
            this.result = UpdateResult.DISABLED;
            return;
        }
        try {
            this.connection = ((HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection());
        } catch (IOException e) {
            this.result = UpdateResult.FAIL_SPIGOT;
            return;
        }

        this.WRITE_STRING = ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource="
                + this.RESOURCE_ID);
        run();
    }

    private void run() {
        this.connection.setDoOutput(true);
        try {
            this.connection.setRequestMethod("POST");
            this.connection.getOutputStream().write(this.WRITE_STRING.getBytes("UTF-8"));
        } catch (ProtocolException e1) {
            this.result = UpdateResult.FAIL_SPIGOT;
        } catch (UnsupportedEncodingException e) {
            this.result = UpdateResult.FAIL_SPIGOT;
        } catch (IOException e) {
            this.result = UpdateResult.FAIL_SPIGOT;
        }
        String version = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            version = br.readLine();
            br.close();
        } catch (Exception e) {
            this.result = UpdateResult.BAD_RESOURCEID;
            return;
        }
        BufferedReader br;
        if (version.length() <= 7) {
            this.version = version;
            version.replace("[^A-Za-z]", "").replace("|", "");
            versionCheck();
            return;
        }
        this.result = UpdateResult.BAD_RESOURCEID;
    }

    private void versionCheck() {
        if (shouldUpdate(this.oldVersion, this.version)) {
            this.result = UpdateResult.UPDATE_AVAILABLE;
        } else {
            this.result = UpdateResult.NO_UPDATE;
        }
    }

    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        String[] localVersionParts = localVersion.split("\\.");
        String localVersionBeforeFirstDot = localVersionParts[0];
        int lv1 = 0;
        int lv2 = 0;
        lv1 = Integer.parseInt(localVersionBeforeFirstDot);
        String localVersionAfterFirstDot = localVersionParts[1];
        lv2 = Integer.parseInt(localVersionAfterFirstDot);

        String[] remoteVersionParts = remoteVersion.split("\\.");
        String remoteVersionBeforeFirstDot = remoteVersionParts[0];
        int rv1 = 0;
        int rv2 = 0;
        rv1 = Integer.parseInt(remoteVersionBeforeFirstDot);
        String remoteVersionAfterFirstDot = remoteVersionParts[1];
        rv2 = Integer.parseInt(remoteVersionAfterFirstDot);

        if (lv1 < rv1)
            return true;
        if (lv1 > rv1) {
            return false;
        }
        if (lv1 == rv1) {
            return lv2 < rv2;
        }
        return false;
    }

    public UpdateResult getResult() {
        return this.result;
    }

    public String getVersion() {
        return this.version;
    }
}
