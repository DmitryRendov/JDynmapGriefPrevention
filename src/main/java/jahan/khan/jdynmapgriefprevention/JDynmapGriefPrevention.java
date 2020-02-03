package jahan.khan.JDynmapGriefPrevention;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Visualization;
import me.ryanhamshire.GriefPrevention.VisualizationType;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.CircleMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

class JDynmapGriefPrevention extends JavaPlugin {
    private static Plugin dynmap;
    private static DynmapAPI api;
    private static MarkerAPI markerapi;
    private static GriefPrevention gp;
    private static MarkerSet set;
    private static MarkerSet setunused;
    private static MarkerSet playerset;
    private Boolean uuidserver = null;
    private JavaPlugin plugin;
    private static String pluginVersion = "3.1.0-OSS";
    private static String pluginAuthors = "jahangir13,DmitryRendov1";

    private static final String DEF_INFOWINDOW = "<div class=\"infowindow\">Claim Owner: <span style=\"font-weight:bold;\">%owner%</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>";
    private static final String DEF_ADMININFOWINDOW = "<div class=\"infowindow\"><span style=\"font-weight:bold;\">Administrator Claim</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>";
    private static final String DEF_NEWSWINDOW = "= JDynmapGriefPrevention News =";
    private static String infowindowTmpl;
    private static String admininfowindowTmpl;
    private static String newswindowTmpl;
    private static final String ADMIN_ID = "administrator";
    private static AreaStyle defstyle;
    private static Map<String, AreaStyle> ownerstyle;
    private static HashMap<String, OfflinePlayer> hm_offlinePlayers;
    private static int minzoom = 0;
    private static int per = 900;
    private static long updperiod;
    private static Set<String> visible;
    private static HashSet hidden;
    private static boolean stop;
    private static volatile Map<String, AreaMarker> resareas = new HashMap(1000, 0.75F);
    private static volatile Map<String, AreaMarker> presareas = new HashMap(50, 0.75F);

    private static volatile boolean mapPlayerAlreadyRunning = false;
    private static String mapPlayerName = "";
    private static int mapDisplayTime = 30;
    private boolean mapUsePlayerName = true;
    private static String mapLayerName = "GP Player Map";
    private boolean mapHideOtherLayers = true;

    private static CircleMarker newsmarker = null;
    private boolean nwsEnabled = true;
    private static int nwsCoordsX = 0;
    private static int nwsCoordsZ = 0;
    private static int nwsRadius = 100;
    private static String nwsStrokeColor;
    private static double nwsStrokeOpacity = 0.8D;
    private static int nwsStrokeWeight = 10;
    private static String nwsFillColor;
    private static double nwsFillOpacity = 0.8D;

    private static String text = "";

    protected static ResourceBundle jdgpMessages;
    protected static volatile ArrayList<ClaimInfo> claimsInfo;
    private volatile int sz = 0;
    private volatile int idx = 0;
    private static boolean claimUsed = false;
    private static UUID uuidowner = null;
    private static String claimID = "";
    private static String oowner = "n/a";
    private static int ownerdays = 0;
    private static int cwidth = 0;
    private static int cheight = 0;
    private static int csize = 0;
    private static String stringBuilders = "";
    private static String stringContainers = "";
    private static String stringAccessors = "";
    private static String stringManagers = "";
    private static String coords = "";
    private static String coordx = "";
    private static String coordy = "";
    private static String coordz = "";
    private static boolean playerLongAgo = false;

    private static FileConfiguration cfg;
    private boolean use3d = false;
    private boolean useDynmap = true;
    private static boolean publictrust = false;
    public static int absenceDayLimit = 40;
    private static String allAbsentStrokeColor;
    private static String allAbsentFillColor;
    private static String ownerAbsentStrokeColor;
    private static String ownerAbsentFillColor;
    private static String publicFillColor;
    private boolean publicenabled = true;
    private boolean allowBracketsTrust = true;
    private String cfglocale = "en";
    private boolean updconslog = true;
    private boolean showcountonlayer = true;
    private boolean usetwolayers = true;
    private static int layerPrio = 0;
    private static int layerPrioUnused = 0;
    private static boolean hideByDefaultUnused = false;
    private static boolean hideByDefault = false;
    private static boolean reload = false;
    private static volatile boolean reloadwait = false;
    private boolean pluginMetrics = true;
    private boolean updateCheck = true;
    private static volatile boolean taskRunning = false;
    private boolean debug = false;
    private static boolean userDisable = false;
    private boolean getOwnerUuid = false;
    private boolean getClaimSize = true;
    private boolean getClaimID = true;
    private boolean getClaimCoords = true;
    private boolean getBuilders = true;
    private boolean getContainers = true;
    private boolean getAccessors = true;
    private boolean getManagers = true;
    private static int taskid = -1;

    private static long startTimeUC = 0L;
    private static long startTime = 0L;
    private static long startTime1 = 0L;
    private static long startTimeSubclaims = 0L;
    private static long startTimeUCMAP = 0L;
    private static long estimatedTimeUCMAP = 0L;

    private int countadmin = 0;
    private int countbuilder = 0;
    private int countused = 0;
    private int countnormal = 0;
    private int countunused = 0;
    private int numOwners = 0;
    private int numBuilders = 0;
    private int numBuildersCalcDays = 0;
    private int numContainers = 0;
    private int numAccessors = 0;
    private int numManagers = 0;

    public int getSz() {
        return sz;
    }

    private void setSz(int sz) {
        this.sz = sz;
    }

    public int getIdx() {
        return idx;
    }

    private void setIdx(int idx) {
        this.idx = idx;
    }

    public int getNumOwners() {
        return numOwners;
    }

    private void setNumOwners(int numOwners) {
        this.numOwners = numOwners;
    }

    public int getCountadmin() {
        return countadmin;
    }

    private void setCountadmin(int countadmin) {
        this.countadmin = countadmin;
    }

    public int getCountbuilder() {
        return countbuilder;
    }

    private void setCountbuilder(int countbuilder) {
        this.countbuilder = countbuilder;
    }

    public int getCountUsed() {
        return countused;
    }

    private void setCountUsed(int countused) {
        this.countused = countused;
    }

    public int getCountNormal() {
        return countnormal;
    }

    private void setCountNormal(int countnormal) {
        this.countnormal = countnormal;
    }

    public int getCountUnused() {
        return countunused;
    }

    private void setCountUnused(int countunused) {
        this.countunused = countunused;
    }

    public int getNumBuilders() {
        return numBuilders;
    }

    private void setNumBuilders(int numBuilders) {
        this.numBuilders = numBuilders;
    }

    public int getNumBuildersCalcDays() {
        return numBuildersCalcDays;
    }

    private void setNumBuildersCalcDays(int numBuildersCalcDays) {
        this.numBuildersCalcDays = numBuildersCalcDays;
    }

    public int getNumContainers() {
        return numContainers;
    }

    private void setNumContainers(int numContainers) {
        this.numContainers = numContainers;
    }

    public int getNumAccessors() {
        return numAccessors;
    }

    private void setNumAccessors(int numAccessors) {
        this.numAccessors = numAccessors;
    }

    public int getNumManagers() {
        return numManagers;
    }

    private void setNumManagers(int numManagers) {
        this.numManagers = numManagers;
    }

    //adds a server log entry
    private void console(String msg) {
        getServer().getConsoleSender().sendMessage("[JDynmapGriefPrevention] " + msg);
    }

    private class GriefPreventionUpdate extends BukkitRunnable {
        private GriefPreventionUpdate() {
        }

        public void run() {
            if (!JDynmapGriefPrevention.stop) {
                JDynmapGriefPrevention.taskRunning = true;
                JDynmapGriefPrevention.this.updateClaims();
                JDynmapGriefPrevention.taskRunning = false;
            }
        }
    }

    private String getInfoWindowI18(String infowindow) {
        infowindow = infowindow.replace("%userclaimi18%", jdgpMessages.getString("config.infowindow.userclaim"));
        infowindow = infowindow.replace("%owneri18%", jdgpMessages.getString("config.infowindow.owner"));
        infowindow = infowindow.replace("%subownersi18%", jdgpMessages.getString("config.infowindow.subowners"));
        infowindow = infowindow.replace("%buildersi18%", jdgpMessages.getString("config.infowindow.builders"));
        infowindow = infowindow.replace("%containersonlyi18%", jdgpMessages.getString("config.infowindow.containersonly"));
        infowindow = infowindow.replace("%canenteri18%", jdgpMessages.getString("config.infowindow.canenter"));
        return infowindow;
    }

    private String getAdminInfoWindowI18(String admininfowindow) {
        admininfowindow = admininfowindow.replace("%adminclaimi18%", jdgpMessages.getString("config.admininfowindow.adminclaim"));
        admininfowindow = admininfowindow.replace("%permissiontrusti18%", jdgpMessages.getString("config.admininfowindow.permissiontrust"));
        admininfowindow = admininfowindow.replace("%trusti18%", jdgpMessages.getString("config.admininfowindow.trust"));
        admininfowindow = admininfowindow.replace("%containertrusti18%", jdgpMessages.getString("config.admininfowindow.containertrust"));
        admininfowindow = admininfowindow.replace("%accesstrusti18%", jdgpMessages.getString("config.admininfowindow.accesstrust"));
        return admininfowindow;
    }

    private String formatInfoWindow(Claim claim) {
        String v;

        if (claim.isAdminClaim()) {
            v = "<div class=\"regioninfo\">" + this.getAdminInfoWindowI18(admininfowindowTmpl) + "</div>";
        } else {
            v = "<div class=\"regioninfo\">" + this.getInfoWindowI18(infowindowTmpl) + "</div>";
        }

        if (this.getOwnerUuid) {
            if (uuidowner != null) {
                v = v.replace("%owneruuid%", uuidowner.toString());
            } else {
                v = v.replace("%owneruuid%", "unknown");
            }
        }

        if (this.getClaimID) {
            if (claimID != null) {
                v = v.replace("%claimid%", claimID);
            } else {
                v = v.replace("%claimid%", "SubClaim");
            }
        }

        v = v.replace("%owner%", claim.isAdminClaim() ? ADMIN_ID : oowner);
        v = v.replace("%ownerdays%", claim.isAdminClaim() ? ADMIN_ID : String.valueOf(ownerdays));

        if (this.getClaimSize) {
            v = v.replace("%cwidth%", String.valueOf(cwidth));
            v = v.replace("%cheight%", String.valueOf(cheight));
            v = v.replace("%csize%", String.valueOf(csize));
        }

        if (this.getClaimCoords) {
            v = v.replace("%coordx%", coordx);
            v = v.replace("%coordy%", coordy);
            v = v.replace("%coordz%", coordz);
            v = v.replace("%coords%", coords);
        }

        if (this.getBuilders) {
            v = v.replace("%builders%", stringBuilders);
        }

        if (this.getContainers) {
            v = v.replace("%containers%", stringContainers);
        }

        if (this.getAccessors) {
            v = v.replace("%accessors%", stringAccessors);
        }

        if (this.getManagers) {
            v = v.replace("%managers%", stringManagers);
        }
        return v;
    }

    private boolean isVisible(String owner, String worldname) {
        if ((visible != null) && (visible.size() > 0) && (!visible.contains(owner))
                && (!visible.contains("world:" + worldname)) && (!visible.contains(worldname + "/" + owner))) {
            return false;
        }

        return (hidden == null) || (hidden.size() <= 0) || ((!hidden.contains(owner))
                && (!hidden.contains("world:" + worldname)) && (!hidden.contains(worldname + "/" + owner)));
    }

    private void addStyle(String owner, AreaMarker m) {
        AreaStyle as = null;
        int sc;
        int fc;

        if (!ownerstyle.isEmpty()) {
            as = (AreaStyle) ownerstyle.get(owner);
        }
        if (as == null) {
            as = defstyle;
        }

        sc = 16711680;
        fc = 16711680;
        try {
            sc = Integer.parseInt(as.strokecolor.substring(1), 16);
            fc = Integer.parseInt(as.fillcolor.substring(1), 16);
        } catch (NumberFormatException ignored) {
        }

        if (ownerdays > absenceDayLimit) {
            playerLongAgo = true;
        }

        if (playerLongAgo) {
            if (!claimUsed) {
                sc = 6723840;
                fc = 6723891;
                try {
                    sc = Integer.parseInt(allAbsentStrokeColor.substring(1), 16);
                    fc = Integer.parseInt(allAbsentFillColor.substring(1), 16);
                } catch (NumberFormatException ignored) {
                }
            } else {
                sc = 0;
                fc = 6723891;
                try {
                    sc = Integer.parseInt(ownerAbsentStrokeColor.substring(1), 16);
                    fc = Integer.parseInt(ownerAbsentFillColor.substring(1), 16);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if ((this.publicenabled) && (publictrust)) {
            fc = 10104574;
            try {
                fc = Integer.parseInt(publicFillColor.substring(1), 16);
            } catch (NumberFormatException ignored) {
            }
        }

        m.setLineStyle(as.strokeweight, as.strokeopacity, sc);
        m.setFillStyle(as.fillopacity, fc);
        if (as.label != null) {
            m.setLabel(as.label);
        }
    }

    private void handleClaimUuid(int index, Claim claim) {
        Location l0 = claim.getLesserBoundaryCorner();
        Location l1 = claim.getGreaterBoundaryCorner();
        if (l0 == null) {
            return;
        }
        String wname = l0.getWorld().getName();

        if (wname.startsWith("world_myst")) {
            int i = wname.indexOf("age");
            i += 3;
            String agenumber = wname.substring(i, wname.length());
            wname = wname.replace('/', '-');
            wname = wname + "_" + agenumber;
        }

        long lastLogin = 0L;
        oowner = "somebody";
        ownerdays = 0;
        claimID = "";
        coords = "";
        coordx = "";
        coordy = "";
        coordz = "";
        cwidth = 0;
        cheight = 0;
        csize = 0;
        claimUsed = false;
        publictrust = false;
        playerLongAgo = false;
        int builderdays;
        stringBuilders = "";
        stringContainers = "";
        stringAccessors = "";
        stringManagers = "";
        uuidowner = claim.ownerID;

        if ((uuidowner == null) && (claim.parent != null)) {
            oowner = claim.parent.getOwnerName();
            uuidowner = claim.parent.ownerID;
        }

        if (uuidowner != null) {
            OfflinePlayer op = getUuidOfflinePlayer(uuidowner.toString());
            if (op != null) {
                oowner = op.getName();
                lastLogin = op.getLastPlayed();
            }

            ownerdays = getDayDifference(lastLogin);

            if (oowner == null || (oowner.equals("somebody"))) {
                oowner = "-= unknown =-";
                ownerdays = 666;
            }

            if (ownerdays > absenceDayLimit) {
                playerLongAgo = true;
            }
        }

        if (this.getClaimID) {
            try {
                claimID = claim.getID().toString();
            } catch (Exception e) {
                claimID = null;
            }
        }

        if (this.getClaimSize) {
            try {
                cwidth = claim.getWidth();
                cheight = claim.getHeight();
                csize = claim.getHeight() * claim.getWidth();
            } catch (Exception ignored) {
            }
        }

        if (this.getClaimCoords) {
            long X = Math.round(claim.getLesserBoundaryCorner().getX());
            long Y = Math.round(claim.getLesserBoundaryCorner().getY());
            long Z = Math.round(claim.getLesserBoundaryCorner().getZ());
            if (Y < 0L) {
                Y = 64L;
            }
            coordx = String.valueOf(X);
            coordy = String.valueOf(Y);
            coordz = String.valueOf(Z);
            coords = coordx + " " + coordy + " " + coordz;
        }

        ArrayList builders = new ArrayList();
        ArrayList containers = new ArrayList();
        ArrayList accessors = new ArrayList();
        ArrayList managers = new ArrayList();
        claim.getPermissions(builders, containers, accessors, managers);

        OfflinePlayer op = null;

        StringBuilder accum = new StringBuilder();

        if (this.getBuilders) {
            for (int i = 0; i < builders.size(); i++) {
                this.setNumBuilders(this.getNumBuilders() + 1);
                builderdays = 0;

                String builderName = (String) builders.get(i);
                if (op != null) {
                    builderName = op.getName();
                }

                if (ownerdays > absenceDayLimit) {
                    this.setNumBuildersCalcDays(this.getNumBuildersCalcDays() + 1);

                    if (op == null) {
                        if ((builderName.length() > 0) && (builderName.charAt(0) == '[')) {
                            if (this.allowBracketsTrust) {
                                builderdays = 0;
                            } else {
                                builderdays = 999;
                            }
                        } else {
                            builderName = "unknown";
                            builderdays = 666;
                        }
                    } else {
                        builderName = op.getName();
                        lastLogin = op.getLastPlayed();
                        builderdays = getDayDifference(lastLogin);
                    }

                    if (builderdays < absenceDayLimit) {
                        claimUsed = true;
                    }
                }

                if (i > 0)
                    accum.append(", ");
                if (ownerdays > absenceDayLimit) {
                    accum.append(builderName).append(" (").append(builderdays).append(")");
                } else {
                    accum.append(builderName);
                }
            }
            stringBuilders = accum.toString();
        }

        if (this.getContainers) {
            accum = new StringBuilder();
            for (int i = 0; i < containers.size(); i++) {
                this.setNumContainers(this.getNumContainers() + 1);
                if (i > 0)
                    accum.append(", ");
                String containerName = (String) containers.get(i);
                op = getUuidOfflinePlayer(containerName);
                if (op != null) {
                    containerName = op.getName();
                }

                if ((this.publicenabled) && (((String) containers.get(i)).equals("public"))) {
                    publictrust = true;
                }

                accum.append(containerName);
            }
            stringContainers = accum.toString();
        }

        if (this.getAccessors) {
            accum = new StringBuilder();
            for (int i = 0; i < accessors.size(); i++) {
                this.setNumAccessors(this.getNumAccessors() + 1);
                if (i > 0)
                    accum.append(", ");
                String accessorName = (String) accessors.get(i);
                op = getUuidOfflinePlayer(accessorName);
                if (op != null) {
                    accessorName = op.getName();
                }
                accum.append(accessorName);
            }
            stringAccessors = accum.toString();
        }

        if (this.getManagers) {
            accum = new StringBuilder();
            for (int i = 0; i < managers.size(); i++) {
                this.setNumManagers(this.getNumManagers() + 1);
                if (i > 0)
                    accum.append(", ");
                String managerName = (String) managers.get(i);
                op = getUuidOfflinePlayer(managerName);
                if (op != null) {
                    managerName = op.getName();
                }
                accum.append(managerName);
            }
            stringManagers = accum.toString();
        }

        if (claim.isAdminClaim()) {
            this.setCountadmin(this.getCountadmin() + 1);
            this.setCountUsed(this.getCountUsed() + 1);
        } else if (!playerLongAgo) {
            this.setCountNormal(this.getCountNormal() + 1);
            this.setCountUsed(this.getCountUsed() + 1);
        } else if (claimUsed) {
            this.setCountbuilder(this.getCountbuilder() + 1);
            this.setCountUsed(this.getCountUsed() + 1);
        } else {
            this.setCountUnused(this.getCountUnused() + 1);
        }

        String owner = claim.isAdminClaim() ? ADMIN_ID : oowner;

        ClaimInfo hci = new ClaimInfo();
        hci.claim = claim;
        hci.claimUsed = claimUsed;
        hci.index = index;
        hci.l0 = l0;
        hci.l1 = l1;
        hci.owner = owner;
        hci.oowner = oowner;
        hci.ownerdays = ownerdays;
        hci.playerLongAgo = playerLongAgo;
        hci.wname = wname;
        hci.ownerUuid = uuidowner;
        hci.claimID = claimID;
        hci.cwidth = cwidth;
        hci.cheight = cheight;
        hci.csize = csize;
        hci.coords = coords;
        hci.coordx = coordx;
        hci.coordy = coordy;
        hci.coordz = coordz;
        hci.publictrust = publictrust;
        hci.stringBuilders = stringBuilders;
        hci.stringContainers = stringContainers;
        hci.stringAccessors = stringAccessors;
        hci.stringManagers = stringManagers;

        claimsInfo.add(hci);
    }

    private void handleClaimName(int index, Claim claim) {
        Location l0 = claim.getLesserBoundaryCorner();
        Location l1 = claim.getGreaterBoundaryCorner();
        if (l0 == null) {
            return;
        }
        String wname = l0.getWorld().getName();

        if (wname.startsWith("world_myst")) {
            int i = wname.indexOf("age");
            i += 3;
            String agenumber = wname.substring(i, wname.length());
            wname = wname.replace('/', '-');
            wname = wname + "_" + agenumber;
        }

        long lastLogin = 0L;
        oowner = "somebody";
        ownerdays = 0;
        claimID = "";
        coords = "";
        coordx = "";
        coordy = "";
        coordz = "";
        cwidth = 0;
        cheight = 0;
        csize = 0;
        claimUsed = false;
        publictrust = false;
        playerLongAgo = false;
        int builderdays;
        stringBuilders = "";
        stringContainers = "";
        stringAccessors = "";
        stringManagers = "";
        uuidowner = null;

        oowner = claim.getOwnerName();

        if (claim.parent != null) {
            oowner = claim.parent.getOwnerName();
        }

        if (!oowner.equalsIgnoreCase("somebody")) {
            OfflinePlayer op = getNameOfflinePlayer(oowner);
            if (op != null) {
                lastLogin = op.getLastPlayed();
            }

            ownerdays = getDayDifference(lastLogin);
            if ((op == null) && (!claim.isAdminClaim()) && (!oowner.equalsIgnoreCase("an administrator"))
                    && (oowner.charAt(0) != '[')) {
                ownerdays = 666;
            }
            if (ownerdays > absenceDayLimit) {
                playerLongAgo = true;
            }
        }

        if (this.getClaimID) {
            try {
                claimID = claim.getID().toString();
            } catch (Exception e) {
                claimID = null;
            }
        }

        if (this.getClaimSize) {
            try {
                cwidth = claim.getWidth();
                cheight = claim.getHeight();
                csize = claim.getHeight() * claim.getWidth();
            } catch (Exception ignored) {
            }
        }

        if (this.getClaimCoords) {
            long X = Math.round(claim.getLesserBoundaryCorner().getX());
            long Y = Math.round(claim.getLesserBoundaryCorner().getY());
            long Z = Math.round(claim.getLesserBoundaryCorner().getZ());
            if (Y < 0L) {
                Y = 64L;
            }
            coordx = String.valueOf(X);
            coordy = String.valueOf(Y);
            coordz = String.valueOf(Z);
            coords = coordx + " " + coordy + " " + coordz;
        }

        ArrayList builders = new ArrayList();
        ArrayList containers = new ArrayList();
        ArrayList accessors = new ArrayList();
        ArrayList managers = new ArrayList();
        claim.getPermissions(builders, containers, accessors, managers);

        OfflinePlayer op;

        StringBuilder accum = new StringBuilder();

        if (this.getBuilders) {
            for (int i = 0; i < builders.size(); i++) {
                this.setNumBuilders(this.getNumBuilders() + 1);
                builderdays = 0;

                String builderName = (String) builders.get(i);
                op = getNameOfflinePlayer(builderName);

                if (ownerdays > absenceDayLimit) {
                    this.setNumBuildersCalcDays(this.getNumBuildersCalcDays() + 1);
                    if (op == null) {
                        if ((builderName.length() > 0) && (builderName.charAt(0) == '[')) {
                            if (this.allowBracketsTrust) {
                                builderdays = 0;
                            } else {
                                builderdays = 999;
                            }
                        } else {
                            builderName = "unknown";
                            builderdays = 666;
                        }
                    } else {
                        lastLogin = op.getLastPlayed();
                        builderdays = getDayDifference(lastLogin);
                    }

                    if (builderdays < absenceDayLimit) {
                        claimUsed = true;
                    }
                }

                if (i > 0)
                    accum.append(", ");
                if (ownerdays > absenceDayLimit) {
                    accum.append(builderName).append(" (").append(builderdays).append(")");
                } else {
                    accum.append(builderName);
                }
            }
            stringBuilders = accum.toString();
        }

        if (this.getContainers) {
            accum = new StringBuilder();
            for (int i = 0; i < containers.size(); i++) {
                this.setNumContainers(this.getNumContainers() + 1);
                if (i > 0)
                    accum.append(", ");
                String containerName = (String) containers.get(i);

                if ((this.publicenabled) && (((String) containers.get(i)).equals("public"))) {
                    publictrust = true;
                }

                accum.append(containerName);
            }
            stringContainers = accum.toString();
        }

        if (this.getAccessors) {
            accum = new StringBuilder();
            for (int i = 0; i < accessors.size(); i++) {
                this.setNumAccessors(this.getNumAccessors() + 1);
                if (i > 0)
                    accum.append(", ");
                String accessorName = (String) accessors.get(i);
                accum.append(accessorName);
            }
            stringAccessors = accum.toString();
        }

        if (this.getManagers) {
            accum = new StringBuilder();
            for (int i = 0; i < managers.size(); i++) {
                this.setNumManagers(this.getNumManagers() + 1);
                if (i > 0)
                    accum.append(", ");
                String managerName = (String) managers.get(i);
                accum.append(managerName);
            }
            stringManagers = accum.toString();
        }

        if (claim.isAdminClaim()) {
            this.setCountadmin(this.getCountadmin() + 1);
            this.setCountUsed(this.getCountUsed() + 1);
        } else if (!playerLongAgo) {
            this.setCountNormal(this.getCountNormal() + 1);
            this.setCountUsed(this.getCountUsed() + 1);
        } else if (claimUsed) {
            this.setCountbuilder(this.getCountbuilder() + 1);
            this.setCountUsed(this.getCountUsed() + 1);
        } else {
            this.setCountUnused(this.getCountUnused() + 1);
        }

        String owner = claim.isAdminClaim() ? ADMIN_ID : oowner;

        ClaimInfo hci = new ClaimInfo();
        hci.claim = claim;
        hci.claimUsed = claimUsed;
        hci.index = index;
        hci.l0 = l0;
        hci.l1 = l1;
        hci.owner = owner;
        hci.oowner = oowner;
        hci.ownerdays = ownerdays;
        hci.playerLongAgo = playerLongAgo;
        hci.wname = wname;
        hci.ownerUuid = uuidowner;
        hci.claimID = claimID;
        hci.cwidth = cwidth;
        hci.cheight = cheight;
        hci.csize = csize;
        hci.coords = coords;
        hci.coordx = coordx;
        hci.coordy = coordy;
        hci.coordz = coordz;
        hci.publictrust = publictrust;
        hci.stringBuilders = stringBuilders;
        hci.stringContainers = stringContainers;
        hci.stringAccessors = stringAccessors;
        hci.stringManagers = stringManagers;

        claimsInfo.add(hci);
    }

    private HashMap updatePlayerClaimsMap(ArrayList<ClaimInfo> playerClaims) {
        HashMap tmpnewmap = new HashMap(claimsInfo.size());

        ClaimInfo hplayerci;

        for (int i = 0; i < playerClaims.size(); i++) {
            hplayerci = (ClaimInfo) playerClaims.get(i);

            String owner = hplayerci.owner;
            oowner = hplayerci.oowner;
            String wname = hplayerci.wname;
            Location l0 = hplayerci.l0;
            Location l1 = hplayerci.l1;
            int index = hplayerci.index;
            ownerdays = hplayerci.ownerdays;
            playerLongAgo = hplayerci.playerLongAgo;
            claimUsed = hplayerci.claimUsed;
            Claim claim = hplayerci.claim;
            uuidowner = hplayerci.ownerUuid;
            claimID = hplayerci.claimID;
            cwidth = hplayerci.cwidth;
            cheight = hplayerci.cheight;
            csize = hplayerci.csize;
            coords = hplayerci.coords;
            coordx = hplayerci.coordx;
            coordy = hplayerci.coordy;
            coordz = hplayerci.coordz;
            publictrust = hplayerci.publictrust;
            stringBuilders = hplayerci.stringBuilders;
            stringContainers = hplayerci.stringContainers;
            stringAccessors = hplayerci.stringAccessors;
            stringManagers = hplayerci.stringManagers;

            if (isVisible(owner, wname)) {
                double[] x = new double[4];
                double[] z = new double[4];
                x[0] = l0.getX();
                z[0] = l0.getZ();
                x[1] = l0.getX();
                z[1] = (l1.getZ() + 1.0D);
                x[2] = (l1.getX() + 1.0D);
                z[2] = (l1.getZ() + 1.0D);
                x[3] = (l1.getX() + 1.0D);
                z[3] = l0.getZ();
                String markerid = "P" + owner + "_" + wname + "_" + index;

                AreaMarker pm = playerset.createAreaMarker(markerid, owner, false, wname, x, z, false);

                if (pm != null) {

                    if (this.use3d) {
                        pm.setRangeY(l1.getY() + 1.0D, l0.getY());
                    }

                    String desc = formatInfoWindow(claim);

                    addStyle(owner, pm);

                    pm.setDescription(desc);

                    tmpnewmap.put(markerid, pm);
                }
            }
        }
        return tmpnewmap;
    }

    private HashMap updateClaimsMap() {
        HashMap tmpnewmap = new HashMap(claimsInfo.size());

        ClaimInfo hci;

        for (int i = 0; i < claimsInfo.size(); i++) {
            hci = (ClaimInfo) claimsInfo.get(i);

            String owner = hci.owner;
            oowner = hci.oowner;
            String wname = hci.wname;
            Location l0 = hci.l0;
            Location l1 = hci.l1;
            int index = hci.index;
            ownerdays = hci.ownerdays;
            playerLongAgo = hci.playerLongAgo;
            claimUsed = hci.claimUsed;
            Claim claim = hci.claim;
            uuidowner = hci.ownerUuid;
            claimID = hci.claimID;
            cwidth = hci.cwidth;
            cheight = hci.cheight;
            csize = hci.csize;
            coords = hci.coords;
            coordx = hci.coordx;
            coordy = hci.coordy;
            coordz = hci.coordz;
            publictrust = hci.publictrust;
            stringBuilders = hci.stringBuilders;
            stringContainers = hci.stringContainers;
            stringAccessors = hci.stringAccessors;
            stringManagers = hci.stringManagers;

            if (isVisible(owner, wname)) {
                double[] x = new double[4];
                double[] z = new double[4];
                x[0] = l0.getX();
                z[0] = l0.getZ();
                x[1] = l0.getX();
                z[1] = (l1.getZ() + 1.0D);
                x[2] = (l1.getX() + 1.0D);
                z[2] = (l1.getZ() + 1.0D);
                x[3] = (l1.getX() + 1.0D);
                z[3] = l0.getZ();

                String markerid = owner + "_" + wname + "_" + index;
                AreaMarker m = (AreaMarker) resareas.remove(markerid);

                if (m == null) {
                    if ((ownerdays > absenceDayLimit) && (!claimUsed) && (this.usetwolayers)) {
                        m = setunused.createAreaMarker(markerid, owner, false, wname, x, z, false);
                    } else {
                        m = set.createAreaMarker(markerid, owner, false, wname, x, z, false);
                    }
                    if (m == null)
                        continue;
                } else {
                    m.setCornerLocations(x, z);
                    m.setLabel(owner);
                    if ((ownerdays <= absenceDayLimit) || (claimUsed)) {
                    }
                }

                if (this.use3d) {
                    m.setRangeY(l1.getY() + 1.0D, l0.getY());
                }

                String desc = formatInfoWindow(claim);

                addStyle(owner, m);

                m.setDescription(desc);

                tmpnewmap.put(markerid, m);
            }
        }
        return tmpnewmap;
    }

    private void updateClaims() {
        this.setIdx(0);
        this.setSz(0);
        this.setNumBuilders(0);
        this.setNumBuildersCalcDays(0);
        this.setNumContainers(0);
        this.setNumAccessors(0);
        this.setNumManagers(0);
        this.setNumOwners(0);

        claimsInfo = new ArrayList();

        if (this.debug) {
            System.out.println("JDGP: ---------------------------------------------");
            System.out.println("JDGP: UpdateClaims Start");
        }
        if (this.debug) {
            startTimeUC = System.nanoTime();
        }

        DataStore ds = gp.dataStore;
        OfflinePlayer[] offlinePlayers;

        if (this.debug) {
            startTime = System.nanoTime();
        }
        offlinePlayers = getServer().getOfflinePlayers();
        if (this.debug) {
            long estimatedTime = System.nanoTime() - startTime;
            System.out.println("JDGP:   Async: Load Offline Players List : "
                    + TimeUnit.NANOSECONDS.toMillis(estimatedTime) + " ms");
        }

        if (this.debug) {
            startTime1 = System.nanoTime();
        }

        hm_offlinePlayers = new HashMap(offlinePlayers.length);
        OfflinePlayer[] arrayOfOfflinePlayer1;
        int j = (arrayOfOfflinePlayer1 = offlinePlayers).length;
        for (int i = 0; i < j; i++) {
            OfflinePlayer op = arrayOfOfflinePlayer1[i];
            if (this.uuidserver.booleanValue()) {
                hm_offlinePlayers.put(op.getUniqueId().toString(), op);
            } else {
                hm_offlinePlayers.put(op.getName().toString().toLowerCase(), op);
            }
        }
        if (this.debug) {
            long estimatedTime1 = System.nanoTime() - startTime1;
            System.out.println("JDGP:   Async: Build Offline Players Map : "
                    + TimeUnit.NANOSECONDS.toMillis(estimatedTime1) + " ms");
        }

        this.setCountadmin(0);
        this.setCountbuilder(0);
        this.setCountUsed(0);
        this.setCountUnused(0);
        this.setCountNormal(0);

        ArrayList claims = null;
        try {
            Field fld = DataStore.class.getDeclaredField("claims");
            fld.setAccessible(true);
            Object o = fld.get(ds);
            if ((o instanceof ArrayList)) {
                claims = (ArrayList) o;
            } else {
                assert o instanceof ArrayList;
                Object ca = (ArrayList) o;
                claims = new ArrayList();
                for (int i = 0; i < ((ArrayList) ca).size(); i++) {
                    claims.add((Claim) ((ArrayList) ca).get(i));
                }
            }
        } catch (NoSuchFieldException ignored) {
        } catch (IllegalArgumentException ignored) {
        } catch (IllegalAccessException ignored) {
        }

        if (claims != null) {
            long startTimeClaims = System.nanoTime();
            this.setSz(claims.size());
            for (int i = 0; i < this.getSz(); i++) {
                Claim claim = (Claim) claims.get(i);
                if (this.uuidserver.booleanValue()) {
                    handleClaimUuid(i, claim);
                } else {
                    handleClaimName(i, claim);
                }
            }
            if (this.debug) {
                long estimatedTimeClaims = System.nanoTime() - startTimeClaims;
                System.out.println("JDGP:   Async: Handle Parent Claims      : "
                        + TimeUnit.NANOSECONDS.toMillis(estimatedTimeClaims) + " ms");
            }

            this.setIdx(this.getSz());
            if (this.debug) {
                startTimeSubclaims = System.nanoTime();
            }
            for (int i = 0; i < this.getSz(); i++) {
                Claim claim = (Claim) claims.get(i);
                if ((claim.children != null) && (claim.children.size() > 0)) {
                    for (int l = 0; l < claim.children.size(); l++) {
                        if (this.uuidserver.booleanValue()) {
                            handleClaimUuid(this.getIdx(), (Claim) claim.children.get(l));
                        } else {
                            handleClaimName(this.getIdx(), (Claim) claim.children.get(l));
                        }
                        this.setIdx(this.getIdx() + 1);
                    }
                }
            }
            if (this.debug) {
                long estimatedTimeSubclaims = System.nanoTime() - startTimeSubclaims;
                System.out.println("JDGP:   Async: Handle SubDivision Claims : "
                        + TimeUnit.NANOSECONDS.toMillis(estimatedTimeSubclaims) + " ms");
            }
        }
        this.setNumOwners(claimsInfo.size());

        new BukkitRunnable() {
            public void run() {
                if (JDynmapGriefPrevention.this.debug) {
                    JDynmapGriefPrevention.startTimeUCMAP = System.nanoTime();
                }

                if (JDynmapGriefPrevention.dynmap != null) {

                    HashMap newmap = new HashMap(JDynmapGriefPrevention.this.getIdx() + 1, 1.0F);
                    if (JDynmapGriefPrevention.this.useDynmap) {
                        newmap = JDynmapGriefPrevention.this.updateClaimsMap();
                    }

                    if (JDynmapGriefPrevention.this.debug) {
                        JDynmapGriefPrevention.estimatedTimeUCMAP = System.nanoTime()
                                - JDynmapGriefPrevention.startTimeUCMAP;
                        System.out.println("JDGP:    Sync: Update Claims on Dynmap   : "
                                + TimeUnit.NANOSECONDS.toMillis(JDynmapGriefPrevention.estimatedTimeUCMAP) + " ms");
                    }

                    for (AreaMarker oldm : JDynmapGriefPrevention.resareas.values()) {
                        oldm.deleteMarker();
                    }

                    if (JDynmapGriefPrevention.this.useDynmap) {
                        JDynmapGriefPrevention.resareas = newmap;
                    }

                    try {
                        if (JDynmapGriefPrevention.this.nwsEnabled) {
                            JDynmapGriefPrevention.newsmarker = JDynmapGriefPrevention.set.createCircleMarker(
                                    "jdgp_newswindow", "text", false, "world", JDynmapGriefPrevention.nwsCoordsX, 64.0D,
                                    JDynmapGriefPrevention.nwsCoordsZ, JDynmapGriefPrevention.nwsRadius,
                                    JDynmapGriefPrevention.nwsRadius, true);
                            JDynmapGriefPrevention.newsmarker.setLineStyle(JDynmapGriefPrevention.nwsStrokeWeight,
                                    JDynmapGriefPrevention.nwsStrokeOpacity,
                                    Integer.parseInt(JDynmapGriefPrevention.nwsStrokeColor.substring(1), 16));
                            JDynmapGriefPrevention.newsmarker.setFillStyle(JDynmapGriefPrevention.nwsFillOpacity,
                                    Integer.parseInt(JDynmapGriefPrevention.nwsFillColor.substring(1), 16));
                            JDynmapGriefPrevention.newsmarker.setDescription(JDynmapGriefPrevention.newswindowTmpl);
                        }
                    } catch (Exception ignored) {
                    }

                    if ((JDynmapGriefPrevention.this.useDynmap) && (JDynmapGriefPrevention.this.showcountonlayer)
                            && (JDynmapGriefPrevention.this.usetwolayers)) {
                        JDynmapGriefPrevention.set.setMarkerSetLabel(jdgpMessages.getString("config.claims.usedname") + " ("
                                + JDynmapGriefPrevention.this.getCountUsed() + ")");
                        JDynmapGriefPrevention.setunused.setMarkerSetLabel(jdgpMessages.getString("config.claims.unusedname") + " ("
                                + JDynmapGriefPrevention.this.getCountUnused() + ")");
                    }
                }

                if (JDynmapGriefPrevention.this.debug) {
                    System.out.println("JDGP: " + JDynmapGriefPrevention.this.getNumOwners() + " claims processed (Par:"
                            + JDynmapGriefPrevention.this.getSz() + "/Sub:"
                            + (JDynmapGriefPrevention.this.getIdx() - JDynmapGriefPrevention.this.getSz()) + ").");
                    System.out.println("JDGP: ---------------------------------------------");
                }
                if (JDynmapGriefPrevention.this.updconslog) {
                    JDynmapGriefPrevention.this.console(JDynmapGriefPrevention.jdgpMessages.getString("jdgp.claims.upd.log"));
                    JDynmapGriefPrevention.reloadwait = false;
                }
            }
        }.runTaskLater(this, 20L);

        if (this.debug) {
            long estimatedTimeUC = System.nanoTime() - startTimeUC;
            System.out.println("JDGP:   Async: Update Claims Time Total  : "
                    + TimeUnit.NANOSECONDS.toMillis(estimatedTimeUC) + " ms");
        }

        if (this.debug) {
            System.out.println("JDGP: UpdateClaims Stop");
            System.out.println("JDGP: --------------------------------");
            System.out.println("JDGP: Number of OfflinePlayers: " + offlinePlayers.length);
            System.out.println("JDGP: Number of Owners        : " + this.getNumOwners());
            System.out.println("JDGP: Number of Builders      : " + this.getNumBuilders());
            System.out.println("JDGP: # of Builders Days Calc : " + this.getNumBuildersCalcDays());
            System.out.println("JDGP: Number of Containers    : " + this.getNumContainers());
            System.out.println("JDGP: Number of Accessors     : " + this.getNumAccessors());
            System.out.println("JDGP: Number of Managers      : " + this.getNumManagers());
            System.out.println("JDGP: Config absenceDayLimit  : " + absenceDayLimit);
            System.out.println("JDGP: --------------------------------");
        }

        BukkitTask task = new GriefPreventionUpdate().runTaskLaterAsynchronously(this, updperiod);
        taskid = task.getTaskId();
    }

    @Override
    public void onEnable() {
        this.plugin = this;

        try {
            File configFile;
            configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                copy("config.yml", getDataFolder() + "/" + "config.yml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File langFile;
            langFile = new File(getDataFolder() + "/lang/", "Messages_en.properties");
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                copy("lang/Messages.properties", getDataFolder() + "/lang/" + "Messages_en.properties");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File langFile;
            langFile = new File(getDataFolder() + "/lang/", "Messages_de.properties");
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                copy("lang/Messages_de.properties", getDataFolder() + "/lang/" + "Messages_de.properties");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File langFile;
            langFile = new File(getDataFolder() + "/lang/", "Messages_ru.properties");
            if (!langFile.exists()) {
                langFile.getParentFile().mkdirs();
                copy("lang/Messages_ru.properties", getDataFolder() + "/lang/" + "Messages_ru.properties");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        readConfigOptions();

        setLocale();

        console(jdgpMessages.getString("enable.init"));

        PluginManager pm = getServer().getPluginManager();

        dynmap = pm.getPlugin("dynmap");
        if (dynmap == null) {
            if (this.useDynmap) {
                console(jdgpMessages.getString("enable.dyn.notfound"));
            }
            this.useDynmap = false;
        }
        api = (DynmapAPI) dynmap;

        Plugin p = pm.getPlugin("GriefPrevention");
        if (p == null) {
            console(jdgpMessages.getString("enable.gp.notfound"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        String dynVersion;
        if (dynmap != null) {
            PluginDescriptionFile dynpdf = dynmap.getDescription();
            dynVersion = dynpdf.getVersion();
        } else {
            dynVersion = "nodynmap";
        }

        String mcVersion = getServer().getVersion();
        mcVersion = mcVersion.substring(mcVersion.indexOf("(MC: ") + 5, mcVersion.indexOf(")"));

        PluginDescriptionFile gppdf = p.getDescription();
        String gpVersion = gppdf.getVersion();

        VersionComparator cmp = new VersionComparator();

        int mcresult = cmp.compare("1.7.5", mcVersion);

        int gpresult = cmp.compare("8.1", gpVersion);

        if ((mcresult == 1) && (gpresult == 1)) {
            this.uuidserver = Boolean.valueOf(false);
        } else if ((mcresult == -1) && (gpresult == -1)) {
            this.uuidserver = Boolean.valueOf(true);
        } else {
            console(jdgpMessages.getString("enable.bad.version"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        gp = (GriefPrevention) p;

        if (gp.isEnabled()) {
            pluginVersion = getDescription().getVersion();
            activate();
        }

        String versionString = (mcVersion + "+" + dynVersion + "+" + gpVersion);

    }

    private void activate() {
        if (reload) {
            reloadConfig();
            readConfigOptions();

            setLocale();
            if (dynmap != null) {
                if (set != null) {
                    set.deleteMarkerSet();
                    set = null;
                }

                if (setunused != null) {
                    setunused.deleteMarkerSet();
                    setunused = null;
                }

                if (playerset != null) {
                    playerset.deleteMarkerSet();
                    playerset = null;
                }
            }

            resareas.clear();
        }

        PluginManager pm = getServer().getPluginManager();

        dynmap = pm.getPlugin("dynmap");
        if (dynmap == null) {
            this.useDynmap = false;
        }

        api = (DynmapAPI) dynmap;

        if (dynmap != null) {
            markerapi = api.getMarkerAPI();
            if (markerapi == null) {
                console(jdgpMessages.getString("enable.dyn.marker.notfound"));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        if ((dynmap != null) && (this.useDynmap)) {
            set = markerapi.getMarkerSet("griefprevention.markerset");
            if (set == null) {
                set = markerapi.createMarkerSet("griefprevention.markerset", jdgpMessages.getString("config.claims.name"), null, false);
            } else
                set.setMarkerSetLabel(jdgpMessages.getString("config.claims.name"));
            if (set == null) {
                console(jdgpMessages.getString("enable.dyn.marker.create"));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            if (minzoom > 0)
                set.setMinZoom(minzoom);
            set.setLayerPriority(layerPrio);
            set.setHideByDefault(hideByDefault);

            if (this.usetwolayers) {
                setunused = markerapi.getMarkerSet("griefprevention2.markerset");
                if (setunused == null) {
                    setunused = markerapi.createMarkerSet("griefprevention2.markerset", jdgpMessages.getString("config.claims.unusedname"), null, false);
                } else
                    setunused.setMarkerSetLabel(jdgpMessages.getString("config.claims.unusedname"));
                if (setunused == null) {
                    console(jdgpMessages.getString("enable.dyn.marker.create"));
                    return;
                }
                if (minzoom > 0)
                    setunused.setMinZoom(minzoom);
                setunused.setLayerPriority(layerPrioUnused);
                setunused.setHideByDefault(hideByDefaultUnused);
            }
        }

        defstyle = new AreaStyle(cfg);
        ownerstyle = new HashMap();
        ConfigurationSection sect = cfg.getConfigurationSection("ownerstyle");
        if (sect != null) {
            Set<String> ids = sect.getKeys(false);

            for (String id : ids) {
                ownerstyle.put(id.toLowerCase(), new AreaStyle(cfg, "ownerstyle." + id, defstyle));
            }
        }
        List<String> vis = cfg.getStringList("visibleregions");
        if (vis != null) {
            visible = new HashSet(vis);
        }
        List<String> hid = cfg.getStringList("hiddenregions");
        if (hid != null) {
            hidden = new HashSet(hid);
        }

        if (per < 15)
            per = 15;
        updperiod = (per * 20);
        stop = false;

        new GriefPreventionUpdate().runTaskLaterAsynchronously(this, 40L);

        if (!reload) {
            text = MessageFormat.format(jdgpMessages.getString("jdgp.activated"),
                    new Object[]{getDescription().getVersion()});
            console(text);
        }
        reload = false;
    }

    @Override
    public void onDisable() {
        console(jdgpMessages.getString("jdgp.disabled"));
        if (set != null) {
            set.deleteMarkerSet();
            set = null;
        }
        if (setunused != null) {
            setunused.deleteMarkerSet();
            setunused = null;
        }
        if (playerset != null) {
            playerset.deleteMarkerSet();
            playerset = null;
        }
        resareas.clear();
        stop = true;

        if (taskid != -1) {
            getServer().getScheduler().cancelTask(taskid);
        }
    }

    private int getDayDifference(long timeStamp) {
        if (timeStamp != 0L) {
            Date logoutDate = new Date(Long.valueOf(timeStamp).longValue());
            Date now = new Date();
            long difference = now.getTime() - logoutDate.getTime();
            int days = (int) (difference / 86400000L);
            return days;
        }
        return 0;
    }

    private OfflinePlayer getUuidOfflinePlayer(String uuidString) {
        try {
            return (OfflinePlayer) hm_offlinePlayers.get(uuidString);
        } catch (Exception ignored) {
        }
        return null;
    }

    private OfflinePlayer getNameOfflinePlayer(String playername) {
        try {
            return (OfflinePlayer) hm_offlinePlayers.get(playername.toLowerCase());
        } catch (Exception ignored) {
        }
        return null;
    }

    private void copy(String fileFromJar, String outfile) {
        InputStream inStream = JDynmapGriefPrevention.class.getClassLoader().getResourceAsStream(fileFromJar);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outfile);
            byte[] buf = new byte['?'];
            int r = inStream.read(buf);
            while (r != -1) {
                fos.write(buf, 0, r);
                r = inStream.read(buf);
            }
        } catch (IOException localIOException) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ignored) {
                }
            }
            try {
                inStream.close();
            } catch (Exception ignored) {
            }
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ignored) {
                }
            }
            try {
                inStream.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void readConfigOptions() {
        cfg = getConfig();

        cfg.options().copyDefaults(true);
        this.use3d = cfg.getBoolean("use3dregions", false);
        infowindowTmpl = cfg.getString("infowindow", DEF_INFOWINDOW);
        admininfowindowTmpl = cfg.getString("admininfowindow", DEF_ADMININFOWINDOW);
        newswindowTmpl = cfg.getString("newswindow", DEF_NEWSWINDOW);
        per = cfg.getInt("update.period", 300);
        this.updconslog = cfg.getBoolean("update.consolelog", true);
        absenceDayLimit = cfg.getInt("absenceDayLimit", 40);
        allAbsentStrokeColor = cfg.getString("allAbsent.strokeColor", "#669900");
        allAbsentFillColor = cfg.getString("allAbsent.fillColor", "#669933");
        ownerAbsentStrokeColor = cfg.getString("ownerAbsent.strokeColor", "#000000");
        ownerAbsentFillColor = cfg.getString("ownerAbsent.fillColor", "#669933");
        publicFillColor = cfg.getString("publicTrusted.fillColor", "#9A2EFE");
        this.publicenabled = cfg.getBoolean("publicTrusted.enabled", true);
        this.getOwnerUuid = cfg.getBoolean("getOwnerUuid", true);
        this.getClaimSize = cfg.getBoolean("getClaimSize", true);
        this.getClaimCoords = cfg.getBoolean("getClaimCoords", true);
        this.getClaimID = cfg.getBoolean("getClaimID", true);
        this.getBuilders = cfg.getBoolean("getBuilders", true);
        this.getContainers = cfg.getBoolean("getContainers", true);
        this.getAccessors = cfg.getBoolean("getAccessors", true);
        this.getManagers = cfg.getBoolean("getManagers", true);
        this.allowBracketsTrust = cfg.getBoolean("allowBracketsTrust", true);
        this.showcountonlayer = cfg.getBoolean("layer.showcountonlayer", true);
        this.usetwolayers = cfg.getBoolean("layer.usetwolayers", true);
        layerPrio = cfg.getInt("layer.layerprio");
        layerPrioUnused = cfg.getInt("layer.layerpriounused");
        hideByDefault = cfg.getBoolean("layer.hidebydefault", false);
        hideByDefaultUnused = cfg.getBoolean("layer.hidebydefaultunused", false);
        this.pluginMetrics = cfg.getBoolean("pluginmetrics", true);
        this.useDynmap = cfg.getBoolean("useDynmap", true);
        this.debug = cfg.getBoolean("debug", false);
        this.cfglocale = cfg.getString("locale", "en");
        this.updateCheck = cfg.getBoolean("updateCheck", true);
        mapDisplayTime = cfg.getInt("playerMap.mapDisplayTime", 30);
        this.mapUsePlayerName = cfg.getBoolean("playerMap.mapUsePlayerName", true);
        mapLayerName = cfg.getString("playerMap.mapLayerName", "GP Player Map");
        this.mapHideOtherLayers = cfg.getBoolean("playerMap.mapHideOtherLayers", true);
        this.nwsEnabled = cfg.getBoolean("newswindowstyle.enabled", true);
        nwsCoordsX = cfg.getInt("newswindowstyle.coordinates.x", 64536);
        nwsCoordsZ = cfg.getInt("newswindowstyle.coordinates.z", 64536);
        nwsRadius = cfg.getInt("newswindowstyle.radius", 80);
        nwsStrokeColor = cfg.getString("newswindowstyle.strokeColor", "#FFCC00");
        nwsStrokeOpacity = cfg.getDouble("newswindowstyle.strokeOpacity", 0.8D);
        nwsStrokeWeight = cfg.getInt("newswindowstyle.strokeWeight", 3);
        nwsFillColor = cfg.getString("newswindowstyle.fillColor", "#FFFF33");
        nwsFillOpacity = cfg.getDouble("newswindowstyle.fillOpacity", 0.8D);
        minzoom = cfg.getInt("layer.minzoom", 0);
    }

    private void setLocale() {
        Locale jdgpLocale = new Locale(this.cfglocale);
        Locale.setDefault(jdgpLocale);
        try {
            File file = new File(getDataFolder() + "/lang/");
            URL[] urls = {file.toURI().toURL()};
            ClassLoader loader = new URLClassLoader(urls);
            jdgpMessages = ResourceBundle.getBundle("Messages", Locale.getDefault(), loader, new UTF8Control());
        } catch (Exception e) {
            text = MessageFormat.format(jdgpMessages.getString("jdgp.locale.notfound"),
                    new Object[]{this.cfglocale});
            console(text);
            this.cfglocale = "en";
            jdgpMessages = ResourceBundle.getBundle("lang.Messages");
        }
    }

    private void createPlayerLayer(String name, int claimnumber) {
        if (playerset != null) {
            playerset.deleteMarkerSet();
            playerset = null;
        }
        if (this.showcountonlayer) {
            playerset = markerapi.createMarkerSet("griefprevention.markersetplayer", name + " (" + claimnumber + ")",
                    null, false);
        } else {
            playerset = markerapi.createMarkerSet("griefprevention.markersetplayer", name, null, false);
        }
        playerset.setMinZoom(0);
        playerset.setLayerPriority(20);
        playerset.setHideByDefault(false);

        if (this.mapHideOtherLayers) {
            set.setHideByDefault(true);
            setunused.setHideByDefault(true);
        }
    }

    private void deletePlayerLayer() {
        if (playerset != null) {
            playerset.deleteMarkerSet();
            playerset = null;

            if (this.mapHideOtherLayers) {
                set.setHideByDefault(hideByDefault);
                setunused.setHideByDefault(hideByDefaultUnused);
            }
        }
    }

    private boolean isOfflinePlayer() {
        for (Map.Entry<String, OfflinePlayer> entry : hm_offlinePlayers.entrySet()) {
            if (mapPlayerName.equalsIgnoreCase("public")) {
                return true;
            }
            OfflinePlayer op = (OfflinePlayer) entry.getValue();
            if (op.getName().equalsIgnoreCase(mapPlayerName)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<ClaimInfo> lookupInvolvedClaims(String playername) {
        ArrayList<ClaimInfo> involvedClaims = new ArrayList();

        StringSearch strsearch = new StringSearch(playername.toLowerCase());

        for (ClaimInfo claim : claimsInfo) {
            if (claim.oowner.equalsIgnoreCase(playername)) {
                involvedClaims.add(claim);

            } else if (strsearch.search(claim.stringBuilders.toLowerCase()) >= 0) {
                involvedClaims.add(claim);

            } else if (strsearch.search(claim.stringContainers.toLowerCase()) >= 0) {
                involvedClaims.add(claim);

            } else if (strsearch.search(claim.stringAccessors.toLowerCase()) >= 0) {
                involvedClaims.add(claim);

            } else if (strsearch.search(claim.stringManagers.toLowerCase()) >= 0) {
                involvedClaims.add(claim);
            }
        }

        return involvedClaims;
    }

    private Claim getClaimById(String claimID) {
        Claim claim = null;

        for (int i = 0; i < claimsInfo.size(); i++) {
            if (claimID.equals(((ClaimInfo) claimsInfo.get(i)).claimID)) {
                claim = ((ClaimInfo) claimsInfo.get(i)).claim;
                break;
            }
        }
        return claim;
    }

    private void tpPlayerToClaim(final Player p, final Claim claim) {
        int x = (claim.getLesserBoundaryCorner().getBlockX() + claim.getGreaterBoundaryCorner().getBlockX()) / 2;
        int z = (claim.getLesserBoundaryCorner().getBlockZ() + claim.getGreaterBoundaryCorner().getBlockZ()) / 2;
        World w = claim.getLesserBoundaryCorner().getWorld();
        int y = w.getHighestBlockYAt(x, z);
        final Location newloc = new Location(w, x, y, z);

        p.teleport(newloc);

        text = MessageFormat.format(jdgpMessages.getString("command.tp.claim.id"),
                new Object[]{String.valueOf(claim.getID()), claim.getOwnerName()});
        p.sendMessage(text);

        new BukkitRunnable() {
            public void run() {
                Visualization.Revert(p);
                Visualization visualization = Visualization.FromClaim(claim, newloc.getBlockY(),
                        VisualizationType.Claim, newloc);
                Visualization.Apply(p, visualization);
            }
        }.runTaskLaterAsynchronously(this, 60L);
    }

    private void tpPlayerToCoordinates(final Player p, int x, int z) {
        try {
            World w = p.getWorld();
            int y = w.getHighestBlockYAt(x, z);
            final Location newloc = new Location(w, x, y, z);

            p.teleport(newloc);

            text = MessageFormat.format(jdgpMessages.getString("command.tp.coords"),
                    new Object[]{String.valueOf(x), String.valueOf(y), String.valueOf(z)});
            p.sendMessage(text);

            final Claim claim = GriefPrevention.instance.dataStore.getClaimAt(newloc, true, null);
            if (claim == null) {
                return;
            }

            new BukkitRunnable() {
                public void run() {
                    Visualization.Revert(p);
                    Visualization visualization = Visualization.FromClaim(claim, newloc.getBlockY(),
                            VisualizationType.Claim, newloc);
                    Visualization.Apply(p, visualization);
                }
            }.runTaskLaterAsynchronously(this, 60L);
        } catch (Exception ignored) {
        }
    }

    private void showPlayerClaims(CommandSender sender, String playername) {
        final CommandSender msender = sender;
        mapPlayerName = "";
        mapPlayerName = playername;

        new BukkitRunnable() {
            public void run() {
                if (JDynmapGriefPrevention.mapPlayerAlreadyRunning) {
                    msender.sendMessage(JDynmapGriefPrevention.jdgpMessages.getString("command.claims.alreadyused"));
                    return;
                }
                JDynmapGriefPrevention.mapPlayerAlreadyRunning = true;

                boolean check;
                check = JDynmapGriefPrevention.this.isOfflinePlayer();
                if (!check) {
                    msender.sendMessage(JDynmapGriefPrevention.jdgpMessages.getString("command.claims.notfound"));
                    JDynmapGriefPrevention.mapPlayerAlreadyRunning = false;
                    return;
                }

                ArrayList<ClaimInfo> invClaimsList = JDynmapGriefPrevention.this
                        .lookupInvolvedClaims(JDynmapGriefPrevention.mapPlayerName);
                int size = invClaimsList.size();
                if (size > 0) {
                    JDynmapGriefPrevention.text = MessageFormat.format(
                            JDynmapGriefPrevention.jdgpMessages.getString("command.claims.ok1"),
                            new Object[]{String.valueOf(size)});
                    msender.sendMessage(JDynmapGriefPrevention.text);

                    msender.sendMessage(JDynmapGriefPrevention.jdgpMessages.getString("command.claims.ok2"));
                } else {
                    msender.sendMessage(JDynmapGriefPrevention.jdgpMessages.getString("command.claims.notok"));
                    JDynmapGriefPrevention.mapPlayerAlreadyRunning = false;
                    return;
                }

                HashMap newmap;

                if (JDynmapGriefPrevention.this.mapUsePlayerName) {
                    JDynmapGriefPrevention.this.createPlayerLayer(JDynmapGriefPrevention.mapPlayerName, size);
                } else {
                    JDynmapGriefPrevention.this.createPlayerLayer(JDynmapGriefPrevention.mapLayerName, size);
                }
                newmap = JDynmapGriefPrevention.this.updatePlayerClaimsMap(invClaimsList);

                JDynmapGriefPrevention.presareas = JDynmapGriefPrevention.resareas;

                JDynmapGriefPrevention.resareas.putAll(newmap);

                new BukkitRunnable() {
                    public void run() {
                        JDynmapGriefPrevention.this.deletePlayerLayer();

                        JDynmapGriefPrevention.resareas = JDynmapGriefPrevention.presareas;
                        JDynmapGriefPrevention.presareas = null;

                        JDynmapGriefPrevention.mapPlayerAlreadyRunning = false;

                        JDynmapGriefPrevention.text = MessageFormat.format(
                                JDynmapGriefPrevention.jdgpMessages.getString("command.claims.ok3"),
                                new Object[]{String.valueOf(JDynmapGriefPrevention.mapDisplayTime)});
                        msender.sendMessage(JDynmapGriefPrevention.text);
                    }
                }.runTaskLaterAsynchronously(JDynmapGriefPrevention.this.plugin,
                        JDynmapGriefPrevention.mapDisplayTime * 20L);
            }
        }.runTaskLaterAsynchronously(this, 0L);
    }

    private boolean isStringInteger(String str) {
        String pattern = "-?[0-9]+";
        return Pattern.matches(pattern, str);
    }

    private static boolean isStringNumber(String string) {
        try {
            Long.parseLong(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("jdgp")) {
            if (args.length == 0) {
                text = MessageFormat.format(jdgpMessages.getString("command.help.title"),
                        new Object[]{pluginVersion}) + " (by " + pluginAuthors + ")";
                sender.sendMessage(text);
                if (sender.hasPermission("jdynmapgriefprevention.admin.reload")) {
                    sender.sendMessage(jdgpMessages.getString("command.help.reload"));
                }
                if (sender.hasPermission("jdynmapgriefprevention.player.stats")) {
                    sender.sendMessage(jdgpMessages.getString("command.help.stats"));
                }
                if ((dynmap != null) && (this.useDynmap)
                        && (sender.hasPermission("jdynmapgriefprevention.player.claims"))) {
                    sender.sendMessage(jdgpMessages.getString("command.help.claims1"));
                }
                if ((dynmap != null) && (this.useDynmap)
                        && (sender.hasPermission("jdynmapgriefprevention.admin.claims"))) {
                    sender.sendMessage(jdgpMessages.getString("command.help.claims2"));
                }
                if (sender.hasPermission("jdynmapgriefprevention.admin.teleport")) {
                    sender.sendMessage(jdgpMessages.getString("command.help.tp1"));

                    sender.sendMessage(jdgpMessages.getString("command.help.tp2"));
                }
                if (sender.hasPermission("jdynmapgriefprevention.admin.export")) {
                    sender.sendMessage(jdgpMessages.getString("command.help.export"));
                }
                if (sender.hasPermission("jdynmapgriefprevention.admin.disable")) {
                    sender.sendMessage(jdgpMessages.getString("command.help.disable"));
                }
                if (sender.hasPermission("jdynmapgriefprevention.admin.cancel")) {
                    sender.sendMessage(jdgpMessages.getString("command.help.cancel"));
                }
                sender.sendMessage(jdgpMessages.getString("command.help.url") + "http://goo.gl/5lI8Mh");
                return true;
            }
            if (args.length == 1) {
                if ((args[0].equalsIgnoreCase("?")) || (args[0].equalsIgnoreCase("help"))) {
                    sender.sendMessage(jdgpMessages.getString("command.help.help"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("jdynmapgriefprevention.admin.reload")) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }
                    if ((reloadwait) || (taskRunning)) {
                        sender.sendMessage(jdgpMessages.getString("command.reload.taskrunning"));
                        return true;
                    }
                    reloadwait = true;
                    reload = true;

                    if (taskid != -1) {
                        getServer().getScheduler().cancelTask(taskid);
                    }
                    activate();
                    sender.sendMessage(jdgpMessages.getString("command.reload.success1"));
                    text = MessageFormat.format(jdgpMessages.getString("command.reload.success2"),
                            new Object[]{Long.valueOf(updperiod / 20L)});
                    sender.sendMessage(text);
                    return true;
                }

                if (args[0].equalsIgnoreCase("disable")) {
                    if (!sender.hasPermission("jdynmapgriefprevention.admin.disable")) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }
                    if (userDisable) {
                        getServer().getPluginManager().disablePlugin(this);
                    } else {
                        userDisable = true;
                        sender.sendMessage(jdgpMessages.getString("command.disable1"));
                        sender.sendMessage(jdgpMessages.getString("command.disable2"));
                        return true;
                    }
                    sender.sendMessage(jdgpMessages.getString("command.disable.ok"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("cancel")) {
                    if (!sender.hasPermission("jdynmapgriefprevention.admin.cancel")) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }
                    if (userDisable) {
                        userDisable = false;
                        sender.sendMessage(jdgpMessages.getString("command.cancel.ok"));
                        return true;
                    }
                    sender.sendMessage(jdgpMessages.getString("command.cancel.cancel"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("stats")) {
                    if (!sender.hasPermission("jdynmapgriefprevention.player.stats")) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }
                    sender.sendMessage("");
                    sender.sendMessage(jdgpMessages.getString("command.stats.title"));
                    sender.sendMessage(jdgpMessages.getString("command.stats.separator"));
                    text = MessageFormat.format(jdgpMessages.getString("command.stats.claims_owner"),
                            new Object[]{String.valueOf(this.getNumOwners())});
                    sender.sendMessage(text);
                    text = MessageFormat.format(jdgpMessages.getString("command.stats.trusted"),
                            new Object[]{String.valueOf(this.getNumBuilders()), String.valueOf(this.getNumContainers()),
                                    String.valueOf(this.getNumAccessors()), String.valueOf(this.getNumManagers())});
                    sender.sendMessage(text);
                    text = MessageFormat.format(jdgpMessages.getString("command.stats.parent_subdiv"),
                            new Object[]{String.valueOf(this.getSz()), String.valueOf(this.getIdx() - this.getSz())});
                    sender.sendMessage(text);
                    text = MessageFormat.format(jdgpMessages.getString("command.stats.used"),
                            new Object[]{String.valueOf(this.getCountUsed()), String.valueOf(this.getCountadmin()),
                                    String.valueOf(this.getCountNormal()), String.valueOf(this.getCountbuilder())});
                    sender.sendMessage(text);
                    text = MessageFormat.format(jdgpMessages.getString("command.stats.unused"),
                            new Object[]{String.valueOf(this.getCountUnused())});
                    sender.sendMessage(text);
                    sender.sendMessage(jdgpMessages.getString("command.stats.separator"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("claims")) {
                    if (!sender.hasPermission("jdynmapgriefprevention.player.claims")) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }

                    if ((dynmap == null) || (!this.useDynmap)) {
                        sender.sendMessage(jdgpMessages.getString("command.depend.dynmap"));
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(jdgpMessages.getString("command.claims.useconsole"));
                        return true;
                    }

                    showPlayerClaims(sender, sender.getName());

                    return true;
                }

                if (args[0].equalsIgnoreCase("export")) {
                    if (!sender.hasPermission("jdynmapgriefprevention.admin.export")) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }

                    CSVFile csv = new CSVFile(this, sender);
                    csv.generate();
                    return true;
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("claims")) {
                    if (((sender instanceof Player))
                            && (!sender.hasPermission("jdynmapgriefprevention.admin.claims"))) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }

                    if ((dynmap == null) || (!this.useDynmap)) {
                        sender.sendMessage(jdgpMessages.getString("command.depend.dynmap"));
                        return true;
                    }
                    showPlayerClaims(sender, args[1]);
                    return true;
                }

                if (args[0].equalsIgnoreCase("tp")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(jdgpMessages.getString("command.not.console"));
                        return true;
                    }
                    if (!sender.hasPermission("jdynmapgriefprevention.admin.teleport")) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }
                    String claimID = args[1];

                    if (!isStringInteger(claimID)) {
                        text = MessageFormat.format(jdgpMessages.getString("command.tp.noclaim.id"),
                                new Object[]{String.valueOf(claimID)});
                        sender.sendMessage(text);
                        return true;
                    }

                    Claim claim = getClaimById(claimID);
                    if (claim == null) {
                        text = MessageFormat.format(jdgpMessages.getString("command.tp.noclaim.id"),
                                new Object[]{String.valueOf(claimID)});
                        sender.sendMessage(text);
                        return true;
                    }

                    tpPlayerToClaim((Player) sender, claim);

                    return true;
                }
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("tp")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(jdgpMessages.getString("command.not.console"));
                        return true;
                    }
                    if (!sender.hasPermission("jdynmapgriefprevention.admin.teleport")) {
                        sender.sendMessage(jdgpMessages.getString("command.noperm"));
                        return true;
                    }
                    String X = args[1];
                    String Z = args[2];

                    if ((!isStringNumber(X)) || (!isStringNumber(Z))) {
                        sender.sendMessage(jdgpMessages.getString("command.tp.num.coords"));
                        return true;
                    }

                    tpPlayerToCoordinates((Player) sender, Integer.valueOf(X).intValue(),
                            Integer.valueOf(Z).intValue());

                    return true;
                }
            }

            sender.sendMessage(jdgpMessages.getString("command.unknown"));
            return false;
        }

        return false;
    }
}
