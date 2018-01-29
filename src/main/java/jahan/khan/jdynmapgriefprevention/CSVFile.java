package jahan.khan.jdynmapgriefprevention;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

public class CSVFile
{
  final org.bukkit.plugin.java.JavaPlugin plugin;
  private JDynmapGriefPrevention jdgp;
  CommandSender sender;
  String claims_filename;
  String stats_filename;
  final String delim = "|";
  
  public CSVFile(JDynmapGriefPrevention plugin, CommandSender sender)
  {
    this.jdgp = plugin;
    this.plugin = plugin;
    this.sender = sender;
    this.claims_filename = (this.jdgp.getDataFolder() + "/" + "export_claims.csv");
    this.stats_filename = (this.jdgp.getDataFolder() + "/" + "export_stats.csv");
  }
  


  protected void generate()
  {
    this.jdgp.getServer().getScheduler().scheduleAsyncDelayedTask(this.jdgp, new Runnable()
    {
      public void run() {
        try {
          FileWriter writer = new FileWriter(CSVFile.this.claims_filename);
          
          writer.append("ID");
          writer.append("|");
          writer.append("Type");
          writer.append("|");
          writer.append("World");
          writer.append("|");
          writer.append("Coordinates");
          writer.append("|");
          writer.append("X");
          writer.append("|");
          writer.append("Y");
          writer.append("|");
          writer.append("Z");
          writer.append("|");
          writer.append("OwnerUUID");
          writer.append("|");
          writer.append("Owner");
          writer.append("|");
          writer.append("OwnerOfflineDays");
          writer.append("|");
          writer.append("OverLimit (" + JDynmapGriefPrevention.absenceDayLimit + " days)");
          writer.append("|");
          writer.append("StillUsedByOthers(Trusted)");
          writer.append("|");
          writer.append("Size");
          writer.append("|");
          writer.append("Width");
          writer.append("|");
          writer.append("Height");
          writer.append("|");
          writer.append("PublicContTrust");
          writer.append("|");
          writer.append("JDGP TP claimid");
          writer.append("|");
          writer.append("JDGP TP coords");
          writer.append("|");
          writer.append("Essentials tppos command");
          writer.append("|");
          writer.append("Builders(Build Trust)");
          writer.append("|");
          writer.append("Containers(Container Trust)");
          writer.append("|");
          writer.append("Accessors(Access Trust)");
          writer.append("|");
          writer.append("Managers(Permission Trust)");
          writer.append('\n');
          


          ClaimInfo hci = null;
          
          for (int i = 0; i < JDynmapGriefPrevention.claimsInfo.size(); i++) {
            hci = (ClaimInfo)JDynmapGriefPrevention.claimsInfo.get(i);
            Claim claim = hci.claim;
            String owner = hci.owner;
            String wname = hci.wname;
            Integer ownerdays = Integer.valueOf(hci.ownerdays);
            Boolean playerLongAgo = Boolean.valueOf(hci.playerLongAgo);
            Boolean claimUsed = Boolean.valueOf(hci.claimUsed);
            
            UUID uuidowner = hci.ownerUuid;
            String uuid;
            if (uuidowner == null) {
              uuid = "null";
            } else {
              uuid = uuidowner.toString();
            }
            String claimID = hci.claimID;
            if (claimID == null) {
              claimID = "Subclaim";
            }
            Integer cwidth = Integer.valueOf(hci.cwidth);
            Integer cheight = Integer.valueOf(hci.cheight);
            Integer csize = Integer.valueOf(hci.csize);
            String coords = hci.coords;
            String coordx = hci.coordx;
            String coordy = hci.coordy;
            String coordz = hci.coordz;
            Boolean publictrust = Boolean.valueOf(hci.publictrust);
            String stringBuilders = hci.stringBuilders;
            String stringContainers = hci.stringContainers;
            String stringAccessors = hci.stringAccessors;
            String stringManagers = hci.stringManagers;
            


            if (hci.claimID == null) {
              claimID = String.valueOf(claim.parent.getID());
            }
            writer.append(claimID);
            writer.append("|");
            
            if (hci.claimID == null) {
              writer.append("Subclaim");
            } else {
              writer.append("Parent");
            }
            
            writer.append("|");
            writer.append(wname);
            writer.append("|");
            writer.append(coords);
            writer.append("|");
            writer.append(coordx);
            writer.append("|");
            writer.append(coordy);
            writer.append("|");
            writer.append(coordz);
            writer.append("|");
            writer.append(uuid);
            writer.append("|");
            writer.append(owner);
            writer.append("|");
            writer.append(ownerdays.toString());
            writer.append("|");
            writer.append(playerLongAgo.toString());
            writer.append("|");
            writer.append(claimUsed.toString());
            writer.append("|");
            writer.append(csize.toString());
            writer.append("|");
            writer.append(cwidth.toString());
            writer.append("|");
            writer.append(cheight.toString());
            writer.append("|");
            writer.append(publictrust.toString());
            writer.append("|");
            writer.append("/jdgp tp " + claimID);
            writer.append("|");
            writer.append("/jdgp tp " + coordx + " " + coordz);
            writer.append("|");
            writer.append("/tppos " + coordx + " " + coordy + " " + coordz);
            writer.append("|");
            writer.append(stringBuilders);
            writer.append("|");
            writer.append(stringContainers);
            writer.append("|");
            writer.append(stringAccessors);
            writer.append("|");
            writer.append(stringManagers);
            writer.append('\n');
          }
          
          writer.flush();
          writer.close();
          

          writer = new FileWriter(CSVFile.this.stats_filename);
          
          writer.append("All claims");
          writer.append("|");
          writer.append("Parent claims");
          writer.append("|");
          writer.append("Sub claims");
          writer.append("|");
          writer.append("Used claims (A+N+B)");
          writer.append("|");
          writer.append("Used A (admin claims)");
          writer.append("|");
          writer.append("Used N (normal user claim");
          writer.append("|");
          writer.append("Used B (builders still active)");
          writer.append("|");
          writer.append("Unused claims");
          writer.append("|");
          writer.append("All Builders");
          writer.append("|");
          writer.append("All Containers");
          writer.append("|");
          writer.append("All Accessors");
          writer.append("|");
          writer.append("All Managers");
          writer.append("|");
          writer.append('\n');
          
          writer.append(String.valueOf(CSVFile.this.jdgp.numOwners));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.sz));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.idx - CSVFile.this.jdgp.sz));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.countused));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.countadmin));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.countnormal));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.countbuilder));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.countunused));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.numBuilders));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.numContainers));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.numAccessors));
          writer.append("|");
          writer.append(String.valueOf(CSVFile.this.jdgp.numManagers));
          writer.append("|");
          writer.append('\n');
          
          writer.flush();
          writer.close();
          



          CSVFile.this.sender.sendMessage(JDynmapGriefPrevention.jdgpMessages.getString("command.export.success"));

        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }, 20L);
  }
}
