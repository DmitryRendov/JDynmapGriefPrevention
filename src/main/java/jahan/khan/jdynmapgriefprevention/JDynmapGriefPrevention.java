package jahan.khan.jdynmapgriefprevention;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.Visualization;
import me.ryanhamshire.GriefPrevention.VisualizationType;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.CircleMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class JDynmapGriefPrevention extends JavaPlugin {
	private static Plugin dynmap;
	private static DynmapAPI api;
	private static MarkerAPI markerapi;
	private static GriefPrevention gp;
	private static MarkerSet set;
	private static MarkerSet setunused;
	private static MarkerSet playerset;
	protected Boolean uuidserver = null;
	protected String versionString = "";
	protected Metrics metrics;
	protected JavaPlugin plugin;
	private static String pluginVersion = "3.0.0";
	private static String pluginAuthors = "jahangir13,WaterDemon";

	private static String checkedPluginVersion;
	private static final String DEF_INFOWINDOW = "div class=\"infowindow\">Claim Owner: <span style=\"font-weight:bold;\">%owner%</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>";
	private static final String DEF_ADMININFOWINDOW = "<div class=\"infowindow\"><span style=\"font-weight:bold;\">Administrator Claim</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>";
	private static final String ADMIN_ID = "administrator";
	private static String infowindow = "div class=\"infowindow\">Claim Owner: <span style=\"font-weight:bold;\">%owner%</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>";
	private static String admininfowindow = "<div class=\"infowindow\"><span style=\"font-weight:bold;\">Administrator Claim</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>";
	private static AreaStyle defstyle;
	private static Map<String, AreaStyle> ownerstyle;
	private static HashMap<String, OfflinePlayer> hm_offlinePlayers;
	int minzoom = 0;
	int per = 900;
	long updperiod;
	private static Set<String> visible;
	private static Set<String> hidden;
	private static boolean stop;
	private static volatile Map<String, AreaMarker> resareas = new HashMap(1000, 0.75F);
	private static volatile Map<String, AreaMarker> presareas = new HashMap(50, 0.75F);

	private static volatile boolean mapPlayerAlreadyRunning = false;
	private static String mapPlayerName = "";
	private static int mapDisplayTime = 30;
	protected boolean mapUsePlayerName = true;
	private static String mapLayerName = "GP Player Map";
	protected boolean mapHideOtherLayers = true;

	private static CircleMarker newsmarker = null;
	private static String newswindow = "";
	protected boolean nwsEnabled = true;
	private static int nwsCoordsX = 0;
	private static int nwsCoordsZ = 0;
	private static int nwsRadius = 100;
	private static String nwsStrokeColor;
	private static double nwsStrokeOpacity = 0.8D;
	private static int nwsStrokeWeight = 10;
	private static String nwsFillColor;
	private static double nwsFillOpacity = 0.8D;

	private static Locale jdgpLocale;
	private static String text = "";

	protected static ResourceBundle jdgpMessages;
	protected static volatile ArrayList<ClaimInfo> claimsInfo;
	protected volatile int sz = 0;
	protected volatile int idx = 0;
	private static boolean claimUsed = false;
	private static UUID uuidowner = null;
	private static String claimID = "";
	private static String layerUsedName = "GP used Claims";
	private static String layerUnusedName = "GP unused Claims";
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
	boolean use3d = false;
	boolean useDynmap = true;
	private static boolean publictrust = false;
	protected static int absenceDayLimit = 40;
	private static String allAbsentStrokeColor;
	private static String allAbsentFillColor;
	private static String ownerAbsentStrokeColor;
	private static String ownerAbsentFillColor;
	private static String publicFillColor;
	protected boolean publicenabled = true;
	protected boolean allowBracketsTrust = true;
	protected String cfglocale = "en";
	protected boolean updconslog = true;
	protected boolean showcountonlayer = true;
	protected boolean usetwolayers = true;
	private static int layerPrio = 0;
	private static int layerPrioUnused = 0;
	private static boolean hideByDefaultUnused = false;
	private static boolean hideByDefault = false;
	private static boolean reload = false;
	private static volatile boolean reloadwait = false;
	protected boolean pluginMetrics = true;
	protected boolean updateCheck = true;
	private static volatile boolean taskRunning = false;
	protected boolean debug = false;
	private static boolean userDisable = false;
	protected boolean getOwnerUuid = false;
	protected boolean getClaimSize = true;
	protected boolean getClaimID = true;
	protected boolean getClaimCoords = true;
	protected boolean getBuilders = true;
	protected boolean getContainers = true;
	protected boolean getAccessors = true;
	protected boolean getManagers = true;
	private static int taskid = -1;

	private static long startTimeUC = 0L;
	private static long startTime = 0L;
	private static long estimatedTime = 0L;
	private static long startTime1 = 0L;
	private static long estimatedTime1 = 0L;
	private static long estimatedTimeClaims = 0L;
	private static long startTimeSubclaims = 0L;
	private static long estimatedTimeSubclaims = 0L;
	private static long estimatedTimeUC = 0L;
	private static long startTimeUCMAP = 0L;
	private static long estimatedTimeUCMAP = 0L;

	protected int countadmin = 0;
	protected int countbuilder = 0;
	protected int countused = 0;
	protected int countnormal = 0;
	protected int countunused = 0;
	protected int numOwners = 0;
	protected int numBuilders = 0;
	protected int numBuildersCalcDays = 0;
	protected int numContainers = 0;
	protected int numAccessors = 0;
	protected int numManagers = 0;

	private static class AreaStyle {
		String strokecolor;

		double strokeopacity;
		int strokeweight;
		String fillcolor;
		double fillopacity;
		String label;

		AreaStyle(FileConfiguration cfg, String path, AreaStyle def) {
			this.strokecolor = cfg.getString(path + ".strokeColor", def.strokecolor);
			this.strokeopacity = cfg.getDouble(path + ".strokeOpacity", def.strokeopacity);
			this.strokeweight = cfg.getInt(path + ".strokeWeight", def.strokeweight);
			this.fillcolor = cfg.getString(path + ".fillColor", def.fillcolor);
			this.fillopacity = cfg.getDouble(path + ".fillOpacity", def.fillopacity);
			this.label = cfg.getString(path + ".label", null);
		}

		AreaStyle(FileConfiguration cfg, String path) {
			this.strokecolor = cfg.getString(path + ".strokeColor", "#FF0000");
			this.strokeopacity = cfg.getDouble(path + ".strokeOpacity", 0.8D);
			this.strokeweight = cfg.getInt(path + ".strokeWeight", 3);
			this.fillcolor = cfg.getString(path + ".fillColor", "#FF0000");
			this.fillopacity = cfg.getDouble(path + ".fillOpacity", 0.35D);
		}
	}

	public void console(String msg) {
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

	private String formatInfoWindow(Claim claim, AreaMarker m) {
		String v = "";

		if (claim.isAdminClaim()) {
			v = "<div class=\"regioninfo\">" + admininfowindow + "</div>";
		} else {
			v = "<div class=\"regioninfo\">" + infowindow + "</div>";
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

		v = v.replace("%owner%", claim.isAdminClaim() ? "administrator" : oowner);
		v = v.replace("%ownerdays%", claim.isAdminClaim() ? "administrator" : String.valueOf(ownerdays));

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

		if ((hidden != null) && (hidden.size() > 0) && ((hidden.contains(owner))
				|| (hidden.contains("world:" + worldname)) || (hidden.contains(worldname + "/" + owner)))) {
			return false;
		}

		return true;
	}

	private void addStyle(String owner, String worldid, AreaMarker m, Claim claim, boolean isPlayerMapClaim) {
		AreaStyle as = null;
		int sc = 0;
		int fc = 0;

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
		} catch (NumberFormatException localNumberFormatException) {
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
				} catch (NumberFormatException localNumberFormatException1) {
				}
			} else {
				sc = 0;
				fc = 6723891;
				try {
					sc = Integer.parseInt(ownerAbsentStrokeColor.substring(1), 16);
					fc = Integer.parseInt(ownerAbsentFillColor.substring(1), 16);
				} catch (NumberFormatException localNumberFormatException2) {
				}
			}
		}

		if ((this.publicenabled) && (publictrust)) {
			fc = 10104574;
			try {
				fc = Integer.parseInt(publicFillColor.substring(1), 16);
			} catch (NumberFormatException localNumberFormatException3) {
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
		ownerdays = 0;
		int builderdays = 0;
		stringBuilders = "";
		stringContainers = "";
		stringAccessors = "";
		stringManagers = "";
		uuidowner = null;

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

			if (((oowner != null) && (oowner.equals("somebody"))) || (oowner == null)) {

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
			} catch (Exception localException1) {
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

		ArrayList<String> builders = new ArrayList();
		ArrayList<String> containers = new ArrayList();
		ArrayList<String> accessors = new ArrayList();
		ArrayList<String> managers = new ArrayList();
		claim.getPermissions(builders, containers, accessors, managers);

		OfflinePlayer op = null;

		String accum = "";

		if (this.getBuilders) {
			for (int i = 0; i < builders.size(); i++) {
				this.numBuilders += 1;
				builderdays = 0;
				op = null;

				String builderName = (String) builders.get(i);
				op = getUuidOfflinePlayer(builderName);
				if (op != null) {
					builderName = op.getName();
				}

				if (ownerdays > absenceDayLimit) {
					this.numBuildersCalcDays += 1;
					lastLogin = 0L;
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
					accum = accum + ", ";
				if (ownerdays > absenceDayLimit) {
					accum = accum + builderName + " (" + builderdays + ")";
				} else {
					accum = accum + builderName;
				}
			}
			stringBuilders = accum;
		}

		if (this.getContainers) {
			accum = "";
			for (int i = 0; i < containers.size(); i++) {
				this.numContainers += 1;
				if (i > 0)
					accum = accum + ", ";
				String containerName = (String) containers.get(i);
				op = null;
				op = getUuidOfflinePlayer(containerName);
				if (op != null) {
					containerName = op.getName();
				}

				if ((this.publicenabled) && (((String) containers.get(i)).equals("public"))) {
					publictrust = true;
				}

				accum = accum + containerName;
			}
			stringContainers = accum;
		}

		if (this.getAccessors) {
			accum = "";
			for (int i = 0; i < accessors.size(); i++) {
				this.numAccessors += 1;
				if (i > 0)
					accum = accum + ", ";
				String accessorName = (String) accessors.get(i);
				op = null;
				op = getUuidOfflinePlayer(accessorName);
				if (op != null) {
					accessorName = op.getName();
				}
				accum = accum + accessorName;
			}
			stringAccessors = accum;
		}

		if (this.getManagers) {
			accum = "";
			for (int i = 0; i < managers.size(); i++) {
				this.numManagers += 1;
				if (i > 0)
					accum = accum + ", ";
				String managerName = (String) managers.get(i);
				op = null;
				op = getUuidOfflinePlayer(managerName);
				if (op != null) {
					managerName = op.getName();
				}
				accum = accum + managerName;
			}
			stringManagers = accum;
		}

		if (claim.isAdminClaim()) {
			this.countadmin += 1;
			this.countused += 1;
		} else if (!playerLongAgo) {
			this.countnormal += 1;
			this.countused += 1;
		} else if (claimUsed) {
			this.countbuilder += 1;
			this.countused += 1;
		} else if (!claimUsed) {
			this.countunused += 1;
		}

		String owner = claim.isAdminClaim() ? "administrator" : oowner;

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
		ownerdays = 0;
		int builderdays = 0;
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
			} catch (Exception localException1) {
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

		ArrayList<String> builders = new ArrayList();
		ArrayList<String> containers = new ArrayList();
		ArrayList<String> accessors = new ArrayList();
		ArrayList<String> managers = new ArrayList();
		claim.getPermissions(builders, containers, accessors, managers);

		OfflinePlayer op = null;

		String accum = "";

		if (this.getBuilders) {
			for (int i = 0; i < builders.size(); i++) {
				this.numBuilders += 1;
				builderdays = 0;
				op = null;

				String builderName = (String) builders.get(i);
				op = getNameOfflinePlayer(builderName);

				if (ownerdays > absenceDayLimit) {
					this.numBuildersCalcDays += 1;
					lastLogin = 0L;
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
					accum = accum + ", ";
				if (ownerdays > absenceDayLimit) {
					accum = accum + builderName + " (" + builderdays + ")";
				} else {
					accum = accum + builderName;
				}
			}
			stringBuilders = accum;
		}

		if (this.getContainers) {
			accum = "";
			for (int i = 0; i < containers.size(); i++) {
				this.numContainers += 1;
				if (i > 0)
					accum = accum + ", ";
				String containerName = (String) containers.get(i);

				if ((this.publicenabled) && (((String) containers.get(i)).equals("public"))) {
					publictrust = true;
				}

				accum = accum + containerName;
			}
			stringContainers = accum;
		}

		if (this.getAccessors) {
			accum = "";
			for (int i = 0; i < accessors.size(); i++) {
				this.numAccessors += 1;
				if (i > 0)
					accum = accum + ", ";
				String accessorName = (String) accessors.get(i);
				accum = accum + accessorName;
			}
			stringAccessors = accum;
		}

		if (this.getManagers) {
			accum = "";
			for (int i = 0; i < managers.size(); i++) {
				this.numManagers += 1;
				if (i > 0)
					accum = accum + ", ";
				String managerName = (String) managers.get(i);
				accum = accum + managerName;
			}
			stringManagers = accum;
		}

		if (claim.isAdminClaim()) {
			this.countadmin += 1;
			this.countused += 1;
		} else if (!playerLongAgo) {
			this.countnormal += 1;
			this.countused += 1;
		} else if (claimUsed) {
			this.countbuilder += 1;
			this.countused += 1;
		} else if (!claimUsed) {
			this.countunused += 1;
		}

		String owner = claim.isAdminClaim() ? "administrator" : oowner;

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

	private Map<String, AreaMarker> updatePlayerClaimsMap(ArrayList<ClaimInfo> playerClaims) {
		Map<String, AreaMarker> tmpnewmap = new HashMap(claimsInfo.size());

		ClaimInfo hplayerci = null;

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

					String desc = formatInfoWindow(claim, pm);

					addStyle(owner, wname, pm, claim, true);

					pm.setDescription(desc);

					tmpnewmap.put(markerid, pm);
				}
			}
		}
		return tmpnewmap;
	}

	private Map<String, AreaMarker> updateClaimsMap() {
		Map<String, AreaMarker> tmpnewmap = new HashMap(claimsInfo.size());

		ClaimInfo hci = null;

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

				String desc = formatInfoWindow(claim, m);

				addStyle(owner, wname, m, claim, false);

				m.setDescription(desc);

				tmpnewmap.put(markerid, m);
			}
		}
		return tmpnewmap;
	}

	private void updateClaims() {
		this.idx = 0;
		this.sz = 0;
		this.numBuilders = 0;
		this.numBuildersCalcDays = 0;
		this.numContainers = 0;
		this.numAccessors = 0;
		this.numManagers = 0;
		this.numOwners = 0;

		claimsInfo = new ArrayList();

		if (this.debug) {
			System.out.println("JDGP: ---------------------------------------------");
			System.out.println("JDGP: UpdateClaims Start");
		}
		if (this.debug) {
			startTimeUC = System.nanoTime();
		}

		DataStore ds = gp.dataStore;
		OfflinePlayer[] offlinePlayers = null;

		if (this.debug) {
			startTime = System.nanoTime();
		}
		offlinePlayers = getServer().getOfflinePlayers();
		if (this.debug) {
			estimatedTime = System.nanoTime() - startTime;
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
			estimatedTime1 = System.nanoTime() - startTime1;
			System.out.println("JDGP:   Async: Build Offline Players Map : "
					+ TimeUnit.NANOSECONDS.toMillis(estimatedTime1) + " ms");
		}

		this.countadmin = 0;
		this.countbuilder = 0;
		this.countused = 0;
		this.countunused = 0;
		this.countnormal = 0;

		ArrayList<Claim> claims = null;
		try {
			Field fld = DataStore.class.getDeclaredField("claims");
			fld.setAccessible(true);
			Object o = fld.get(ds);
			if ((o instanceof ArrayList)) {
				claims = (ArrayList) o;
			} else {
				Object ca = (ArrayList) o;
				claims = new ArrayList();
				for (int i = 0; i < ((ArrayList) ca).size(); i++) {
					claims.add((Claim) ((ArrayList) ca).get(i));
				}
			}
		} catch (NoSuchFieldException localNoSuchFieldException1) {
		} catch (IllegalArgumentException localIllegalArgumentException1) {
		} catch (IllegalAccessException localIllegalAccessException1) {
		}

		if (claims != null) {
			long startTimeClaims = System.nanoTime();
			this.sz = claims.size();
			for (int i = 0; i < this.sz; i++) {
				Claim claim = (Claim) claims.get(i);
				if (this.uuidserver.booleanValue()) {
					handleClaimUuid(i, claim);
				} else {
					handleClaimName(i, claim);
				}
			}
			if (this.debug) {
				estimatedTimeClaims = System.nanoTime() - startTimeClaims;
				System.out.println("JDGP:   Async: Handle Parent Claims      : "
						+ TimeUnit.NANOSECONDS.toMillis(estimatedTimeClaims) + " ms");
			}

			this.idx = this.sz;
			if (this.debug) {
				startTimeSubclaims = System.nanoTime();
			}
			for (int i = 0; i < this.sz; i++) {
				Claim claim = (Claim) claims.get(i);
				if ((claim.children != null) && (claim.children.size() > 0)) {
					for (int l = 0; l < claim.children.size(); l++) {
						if (this.uuidserver.booleanValue()) {
							handleClaimUuid(this.idx, (Claim) claim.children.get(l));
						} else {
							handleClaimName(this.idx, (Claim) claim.children.get(l));
						}
						this.idx += 1;
					}
				}
			}
			if (this.debug) {
				estimatedTimeSubclaims = System.nanoTime() - startTimeSubclaims;
				System.out.println("JDGP:   Async: Handle SubDivision Claims : "
						+ TimeUnit.NANOSECONDS.toMillis(estimatedTimeSubclaims) + " ms");
			}
		}
		this.numOwners = claimsInfo.size();

		new BukkitRunnable() {
			public void run() {
				if (JDynmapGriefPrevention.this.debug) {
					JDynmapGriefPrevention.startTimeUCMAP = System.nanoTime();
				}

				if (JDynmapGriefPrevention.dynmap != null) {

					Map<String, AreaMarker> newmap = new HashMap(JDynmapGriefPrevention.this.idx + 1, 1.0F);
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
							JDynmapGriefPrevention.newsmarker.setDescription(JDynmapGriefPrevention.newswindow);
						}
					} catch (Exception localException) {
					}

					if ((JDynmapGriefPrevention.this.useDynmap) && (JDynmapGriefPrevention.this.showcountonlayer)
							&& (JDynmapGriefPrevention.this.usetwolayers)) {
						JDynmapGriefPrevention.set.setMarkerSetLabel(JDynmapGriefPrevention.layerUsedName + " ("
								+ JDynmapGriefPrevention.this.countused + ")");
						JDynmapGriefPrevention.setunused.setMarkerSetLabel(JDynmapGriefPrevention.layerUnusedName + " ("
								+ JDynmapGriefPrevention.this.countunused + ")");
					}
				}

				if (JDynmapGriefPrevention.this.debug) {
					System.out.println("JDGP: " + JDynmapGriefPrevention.this.numOwners + " claims processed (Par:"
							+ JDynmapGriefPrevention.this.sz + "/Sub:"
							+ (JDynmapGriefPrevention.this.idx - JDynmapGriefPrevention.this.sz) + ").");
					System.out.println("JDGP: ---------------------------------------------");
				}
				if (JDynmapGriefPrevention.this.updconslog) {
					JDynmapGriefPrevention.this
							.console(JDynmapGriefPrevention.jdgpMessages.getString("jdgp.claims.upd.log"));
					JDynmapGriefPrevention.reloadwait = false;
				}
			}
		}.runTaskLater(this, 20L);

		if (this.debug) {
			estimatedTimeUC = System.nanoTime() - startTimeUC;
			System.out.println("JDGP:   Async: Update Claims Time Total  : "
					+ TimeUnit.NANOSECONDS.toMillis(estimatedTimeUC) + " ms");
		}

		if (this.debug) {
			System.out.println("JDGP: UpdateClaims Stop");
			System.out.println("JDGP: --------------------------------");
			System.out.println("JDGP: Number of OfflinePlayers: " + offlinePlayers.length);
			System.out.println("JDGP: Number of Owners        : " + this.numOwners);
			System.out.println("JDGP: Number of Builders      : " + this.numBuilders);
			System.out.println("JDGP: # of Builders Days Calc : " + this.numBuildersCalcDays);
			System.out.println("JDGP: Number of Containers    : " + this.numContainers);
			System.out.println("JDGP: Number of Accessors     : " + this.numAccessors);
			System.out.println("JDGP: Number of Managers      : " + this.numManagers);
			System.out.println("JDGP: Config absenceDayLimit  : " + absenceDayLimit);
			System.out.println("JDGP: --------------------------------");
		}

		BukkitTask task = new GriefPreventionUpdate().runTaskLaterAsynchronously(this, this.updperiod);
		taskid = task.getTaskId();
	}

	public void onEnable() {
		this.plugin = this;

		try {
			File configFile = null;
			configFile = new File(getDataFolder(), "config.yml");
			if (!configFile.exists()) {
				configFile.getParentFile().mkdirs();
				copy("config.yml", getDataFolder() + "/" + "config.yml");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			File langFile = null;
			langFile = new File(getDataFolder() + "/lang/", "Messages_en.properties");
			if (!langFile.exists()) {
				langFile.getParentFile().mkdirs();
				copy("lang/Messages.properties", getDataFolder() + "/lang/" + "Messages_en.properties");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			File langFile = null;
			langFile = new File(getDataFolder() + "/lang/", "Messages_de.properties");
			if (!langFile.exists()) {
				langFile.getParentFile().mkdirs();
				copy("lang/Messages_de.properties", getDataFolder() + "/lang/" + "Messages_de.properties");
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

		String dynVersion = "";
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

		this.versionString = (mcVersion + "+" + dynVersion + "+" + gpVersion);

		if (this.pluginMetrics) {
			final JMetrics jmetrics = new JMetrics(this);
			if (jmetrics.isOptOutOk()) {

				new BukkitRunnable() {
					public void run() {
						jmetrics.send();
					}
				}.runTaskLaterAsynchronously(this, 6000L);
			}
		}

		if (this.updateCheck) {

			new BukkitRunnable() {
				public void run() {
					JDynmapGriefPrevention.this.checkForUpdate();
				}
			}.runTaskLaterAsynchronously(this, 3000L);
		}
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
				set = markerapi.createMarkerSet("griefprevention.markerset", layerUsedName, null, false);
			} else
				set.setMarkerSetLabel(layerUsedName);
			if (set == null) {
				console(jdgpMessages.getString("enable.dyn.marker.create"));
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			if (this.minzoom > 0)
				set.setMinZoom(this.minzoom);
			set.setLayerPriority(layerPrio);
			set.setHideByDefault(hideByDefault);

			if (this.usetwolayers) {
				setunused = markerapi.getMarkerSet("griefprevention2.markerset");
				if (setunused == null) {
					setunused = markerapi.createMarkerSet("griefprevention2.markerset", layerUnusedName, null, false);
				} else
					setunused.setMarkerSetLabel(layerUnusedName);
				if (setunused == null) {
					console(jdgpMessages.getString("enable.dyn.marker.create"));
					return;
				}
				if (this.minzoom > 0)
					setunused.setMinZoom(this.minzoom);
				setunused.setLayerPriority(layerPrioUnused);
				setunused.setHideByDefault(hideByDefaultUnused);
			}
		}

		defstyle = new AreaStyle(cfg, "regionstyle");
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

		if (this.per < 15)
			this.per = 15;
		this.updperiod = (this.per * 20);
		stop = false;

		new GriefPreventionUpdate().runTaskLaterAsynchronously(this, 40L);

		if (!reload) {
			text = MessageFormat.format(jdgpMessages.getString("jdgp.activated"),
					new Object[] { getDescription().getVersion() });
			console(text);
		}
		reload = false;
	}

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

	public int getDayDifference(long timeStamp) {
		if (timeStamp != 0L) {
			Date logoutDate = new Date(Long.valueOf(timeStamp).longValue());
			Date now = new Date();
			long difference = now.getTime() - logoutDate.getTime();
			int days = (int) (difference / 86400000L);
			return days;
		}
		return 0;
	}

	public OfflinePlayer getUuidOfflinePlayer(String uuidString) {
		try {
			return (OfflinePlayer) hm_offlinePlayers.get(uuidString);
		} catch (Exception e) {
		}
		return null;
	}

	public OfflinePlayer getNameOfflinePlayer(String playername) {
		try {
			return (OfflinePlayer) hm_offlinePlayers.get(playername.toLowerCase());
		} catch (Exception e) {
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
				} catch (Exception localException) {
				}
			}
			try {
				inStream.close();
			} catch (Exception localException1) {
			}
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception localException2) {
				}
			}
			try {
				inStream.close();
			} catch (Exception localException3) {
			}
		}
	}

	public Metrics getMetrics() {
		return this.metrics;
	}

	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}

	private void checkForUpdate() {
		console(jdgpMessages.getString("updater.checkmsg"));
		UpdateCheck updatechecker = new UpdateCheck(this, "818", false);
		UpdateCheck.UpdateResult checkresult = updatechecker.getResult();
		switch (checkresult) {
		case FAIL_NOVERSION:
			console(jdgpMessages.getString("updater.connection_failed"));
			break;

		case NO_UPDATE:
			console(jdgpMessages.getString("updater.connection_failed"));
			break;

		case BAD_RESOURCEID:
			console(jdgpMessages.getString("updater.no_new_version"));
			break;

		case UPDATE_AVAILABLE:
			checkedPluginVersion = updatechecker.getVersion();

			text = MessageFormat.format(jdgpMessages.getString("updater.new_version"),
					new Object[] { checkedPluginVersion });
			console(text);

			console(jdgpMessages.getString("updater.url.color") + "http://goo.gl/5lI8Mh");
			break;
		case DISABLED:
		case FAIL_SPIGOT:
		default:
			console(checkresult.toString());
		}

	}

	private void readConfigOptions() {
		cfg = getConfig();

		cfg.options().copyDefaults(true);

		this.use3d = cfg.getBoolean("use3dregions", false);
		infowindow = cfg.getString("infowindow",
				"div class=\"infowindow\">Claim Owner: <span style=\"font-weight:bold;\">%owner%</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>");
		admininfowindow = cfg.getString("admininfowindow",
				"<div class=\"infowindow\"><span style=\"font-weight:bold;\">Administrator Claim</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>");
		newswindow = cfg.getString("newswindow",
				"div class=\"infowindow\">Claim Owner: <span style=\"font-weight:bold;\">%owner%</span><br/>Permission Trust: <span style=\"font-weight:bold;\">%managers%</span><br/>Trust: <span style=\"font-weight:bold;\">%builders%</span><br/>Container Trust: <span style=\"font-weight:bold;\">%containers%</span><br/>Access Trust: <span style=\"font-weight:bold;\">%accessors%</span></div>");
		this.per = cfg.getInt("update.period", 300);
		layerUsedName = cfg.getString(layerUsedName, "GP used Claims");
		layerUnusedName = cfg.getString(layerUnusedName, "GP unused Claims");
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
		layerUsedName = cfg.getString("layer.name", "GP used Claims");
		layerUnusedName = cfg.getString("layer.unusedname", "GP used Claims");
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
		newswindow = cfg.getString("newswindow", "= JDynmapGriefPrevention News =");
		this.nwsEnabled = cfg.getBoolean("newswindowstyle.enabled", true);
		nwsCoordsX = cfg.getInt("newswindowstyle.coordinates.x", 64536);
		nwsCoordsZ = cfg.getInt("newswindowstyle.coordinates.z", 64536);
		nwsRadius = cfg.getInt("newswindowstyle.radius", 80);
		nwsStrokeColor = cfg.getString("newswindowstyle.strokeColor", "#FFCC00");
		nwsStrokeOpacity = cfg.getDouble("newswindowstyle.strokeOpacity", 0.8D);
		nwsStrokeWeight = cfg.getInt("newswindowstyle.strokeWeight", 3);
		nwsFillColor = cfg.getString("newswindowstyle.fillColor", "#FFFF33");
		nwsFillOpacity = cfg.getDouble("newswindowstyle.fillOpacity", 0.8D);
		this.minzoom = cfg.getInt("layer.minzoom", 0);
	}

	private void setLocale() {
		jdgpLocale = new Locale(this.cfglocale);
		Locale.setDefault(jdgpLocale);
		try {
			File file = new File(getDataFolder() + "/lang/");
			URL[] urls = { file.toURI().toURL() };
			ClassLoader loader = new URLClassLoader(urls);
			jdgpMessages = ResourceBundle.getBundle("Messages", Locale.getDefault(), loader, new UTF8Control());
		} catch (Exception e) {
			text = MessageFormat.format(jdgpMessages.getString("jdgp.locale.notfound"),
					new Object[] { this.cfglocale });
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

	private boolean isOfflinePlayer(String name) {
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
				new Object[] { String.valueOf(claim.getID()), claim.getOwnerName() });
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
					new Object[] { String.valueOf(x), String.valueOf(y), String.valueOf(z) });
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
		} catch (Exception localException) {
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

				boolean check = false;
				check = JDynmapGriefPrevention.this.isOfflinePlayer(JDynmapGriefPrevention.mapPlayerName);
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
							new Object[] { String.valueOf(size) });
					msender.sendMessage(JDynmapGriefPrevention.text);

					msender.sendMessage(JDynmapGriefPrevention.jdgpMessages.getString("command.claims.ok2"));
				} else {
					msender.sendMessage(JDynmapGriefPrevention.jdgpMessages.getString("command.claims.notok"));
					JDynmapGriefPrevention.mapPlayerAlreadyRunning = false;
					return;
				}

				Map<String, AreaMarker> newmap = new HashMap(100, 1.0F);

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
								new Object[] { String.valueOf(JDynmapGriefPrevention.mapDisplayTime) });
						msender.sendMessage(JDynmapGriefPrevention.text);
					}
				}.runTaskLaterAsynchronously(JDynmapGriefPrevention.this.plugin,
						JDynmapGriefPrevention.mapDisplayTime * 20L);
			}
		}.runTaskLaterAsynchronously(this, 0L);
	}

	private boolean isStringInteger(String str) {
		String pattern = "-?[0-9]+";
		if (Pattern.matches(pattern, str)) {
			return true;
		}
		return false;
	}

	public static boolean isStringNumber(String string) {
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
						new Object[] { pluginVersion }) + " (by " + pluginAuthors + ")";
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
							new Object[] { Long.valueOf(this.updperiod / 20L) });
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
							new Object[] { String.valueOf(this.numOwners) });
					sender.sendMessage(text);
					text = MessageFormat.format(jdgpMessages.getString("command.stats.trusted"),
							new Object[] { String.valueOf(this.numBuilders), String.valueOf(this.numContainers),
									String.valueOf(this.numAccessors), String.valueOf(this.numManagers) });
					sender.sendMessage(text);
					text = MessageFormat.format(jdgpMessages.getString("command.stats.parent_subdiv"),
							new Object[] { String.valueOf(this.sz), String.valueOf(this.idx - this.sz) });
					sender.sendMessage(text);
					text = MessageFormat.format(jdgpMessages.getString("command.stats.used"),
							new Object[] { String.valueOf(this.countused), String.valueOf(this.countadmin),
									String.valueOf(this.countnormal), String.valueOf(this.countbuilder) });
					sender.sendMessage(text);
					text = MessageFormat.format(jdgpMessages.getString("command.stats.unused"),
							new Object[] { String.valueOf(this.countunused) });
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
								new Object[] { String.valueOf(claimID) });
						sender.sendMessage(text);
						return true;
					}

					Claim claim = getClaimById(claimID);
					if (claim == null) {
						text = MessageFormat.format(jdgpMessages.getString("command.tp.noclaim.id"),
								new Object[] { String.valueOf(claimID) });
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
