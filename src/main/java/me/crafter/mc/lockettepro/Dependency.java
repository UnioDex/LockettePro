package me.crafter.mc.lockettepro;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.snowgears.shop.Shop;
import com.snowgears.shop.ShopObject;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class Dependency {

    protected static WorldGuardPlugin worldguard = null;
    protected static Plugin factions = null;
    protected static Plugin vault = null;
    protected static Permission permission = null;
    protected static Plugin askyblock = null;
    protected static Plugin plotsquared = null;
    protected static PlotAPI plotapi;
    protected static Shop shop;

    public Dependency(Plugin plugin) {
        // WorldGuard
        Plugin worldguardplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldguardplugin == null || !(worldguardplugin instanceof WorldGuardPlugin)) {
            worldguard = null;
        } else {
            worldguard = (WorldGuardPlugin) worldguardplugin;
        }
        // Factions
        factions = plugin.getServer().getPluginManager().getPlugin("Factions");
        // Vault
        vault = plugin.getServer().getPluginManager().getPlugin("Vault");
        if (vault != null) {
            RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
            permission = rsp.getProvider();
        }
        // ASkyblock
        askyblock = plugin.getServer().getPluginManager().getPlugin("ASkyblock");
        // PlotSquared
        plotsquared = plugin.getServer().getPluginManager().getPlugin("PlotSquared");
        if (plotsquared != null) {
            plotapi = new PlotAPI();
        }
        //Shop
        Plugin shopPlugin = Bukkit.getPluginManager().getPlugin("Shop");
        if (shopPlugin != null) {
            shop = (Shop) Bukkit.getPluginManager().getPlugin("Shop");
        }
    }

    public static boolean isProtectedFrom(Block block, Player player) {
        if (worldguard != null) {
            if (!worldguard.canBuild(player, block)) return true;
        }

        if (factions != null) {
            if (!FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), "place", true)) {
                return true;
            }
        }

        if (askyblock != null) {
            if (block.getWorld().equals(ASkyBlockAPI.getInstance().getIslandWorld())) {
                Island island = ASkyBlockAPI.getInstance().getIslandAt(block.getLocation());
                if (island != null) {
                    boolean shouldReturn = true;
                    for (UUID memberuuid : island.getMembers()) {
                        if (memberuuid.equals(player.getUniqueId())) {
                            shouldReturn = false;
                            break;
                        }
                    }
                    if (shouldReturn) return true;
                }
            }
        }

        if (plotsquared != null) {
            Plot plot = plotapi.getPlot(block.getLocation());
            if (plot != null) {
                boolean shouldReturn = true;
                for (UUID uuid : plot.getOwners()) {
                    if (uuid.equals(player.getUniqueId())) {
                        shouldReturn = false;
                        break;
                    }
                }
                for (UUID uuid : plot.getMembers()) {
                    if (uuid.equals(player.getUniqueId())) {
                        shouldReturn = false;
                        break;
                    }
                }
                for (UUID uuid : plot.getTrusted()) {
                    if (uuid.equals(player.getUniqueId())) {
                        shouldReturn = false;
                        break;
                    }
                }
                if (shouldReturn) return true;
            }
        }

        if (shop != null) {
            ShopObject shopObject = shop.getShopHandler().getShopByChest(block);
            if (shopObject != null) {
                return true;
            }
            ShopObject shopObject2 = shop.getShopHandler().getShop(block.getLocation());
            if (shopObject2 != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPermissionGroupOf(String line, Player player) {
        if (vault != null) {
            try {
                String[] groups = permission.getPlayerGroups(player);
                for (String group : groups) {
                    if (line.equals("[" + group + "]")) return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public static boolean isScoreboardTeamOf(String line, Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
        if (team != null) {
            if (line.equals("[" + team.getName() + "]")) return true;
        }
        return false;
    }
}
