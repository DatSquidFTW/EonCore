package me.squid.eoncore.listeners;

import me.squid.eoncore.EonCore;
import me.squid.eoncore.events.BackToDeathLocationEvent;
import me.squid.eoncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Objects;

public class DeathBackListener implements Listener {

    EonCore plugin;
    final String prefix = "&7[&5&lEon Survival&7] &r";

    private HashMap<Player, Location> backLocations = new HashMap<>();

    public DeathBackListener(EonCore plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        backLocations.put(e.getEntity(), e.getEntity().getLocation());
        if (e.getEntity().isOp()) e.setKeepInventory(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        p.sendMessage(Utils.chat(prefix + plugin.getConfig().getString("Death-Back-Message")));
        double x = -546.3696854992578;
        double y = 46.0;
        double z = -568.5314282419047;
        e.setRespawnLocation(new Location(Bukkit.getWorld("spawn"), x, y, z));
    }

    @EventHandler
    public void onBackCommand(BackToDeathLocationEvent e) {
        Player p = e.getPlayer();
        Location toTeleport = backLocations.get(p);

        if (!backLocations.containsKey(p)) {
            p.sendMessage(Utils.chat(EonCore.prefix + "&7There is no back location to teleport to"));
            return;
        }

        if (e.hasCooldown()) {
            p.sendMessage(Utils.chat(prefix + Objects.requireNonNull(plugin.getConfig().getString("Cooldown-Teleport-Message")).replace("<seconds>", Long.toString( plugin.getConfig().getLong("Delay-Seconds")))));
            Bukkit.getScheduler().runTaskLater(plugin, doTeleportDelay(p, toTeleport), plugin.getConfig().getLong("Delay-Seconds") * 20);
        } else {
            p.teleport(toTeleport);
            p.sendMessage(Utils.chat(EonCore.prefix + plugin.getConfig().getString("Teleport-Successful")));
        }
    }

    private Runnable doTeleportDelay(Player p, Location toTeleport) {
        return () -> {
            p.teleport(toTeleport);
            p.sendMessage(Utils.chat(EonCore.prefix + plugin.getConfig().getString("Teleport-Successful")));
        };
    }
}