package jahan.khan.JDynmapGriefPrevention;

import org.bukkit.configuration.file.FileConfiguration;

class AreaStyle {
    final String strokecolor;

    final double strokeopacity;
    final int strokeweight;
    final String fillcolor;
    final double fillopacity;
    String label;

    AreaStyle(FileConfiguration cfg, String path, AreaStyle def) {
        this.strokecolor = cfg.getString(path + ".strokeColor", def.strokecolor);
        this.strokeopacity = cfg.getDouble(path + ".strokeOpacity", def.strokeopacity);
        this.strokeweight = cfg.getInt(path + ".strokeWeight", def.strokeweight);
        this.fillcolor = cfg.getString(path + ".fillColor", def.fillcolor);
        this.fillopacity = cfg.getDouble(path + ".fillOpacity", def.fillopacity);
        this.label = cfg.getString(path + ".label", null);
    }

    AreaStyle(FileConfiguration cfg) {
        this.strokecolor = cfg.getString("regionstyle" + ".strokeColor", "#FF0000");
        this.strokeopacity = cfg.getDouble("regionstyle" + ".strokeOpacity", 0.8D);
        this.strokeweight = cfg.getInt("regionstyle" + ".strokeWeight", 3);
        this.fillcolor = cfg.getString("regionstyle" + ".fillColor", "#FF0000");
        this.fillopacity = cfg.getDouble("regionstyle" + ".fillOpacity", 0.35D);
    }
}
