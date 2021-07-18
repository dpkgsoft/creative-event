package com.dpkgsoft.eventcreative;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Plugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onAchievement(PlayerAdvancementDoneEvent e) {
        Advancement advancement = e.getAdvancement();
        if(!advancement.getKey().toString().startsWith("minecraft:recipes") && !advancement.getKey().toString().contains("root")) {
            ItemStack creativeItem = new ItemStack(Material.BAT_SPAWN_EGG);
            ItemMeta meta = creativeItem.getItemMeta();
            meta.setDisplayName("Creative");
            creativeItem.setItemMeta(meta);
            creativeItem.setAmount(1);
            e.getPlayer().getInventory().addItem(creativeItem);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() != null) {
                if (e.getItem().getType() == Material.BAT_SPAWN_EGG) {
                    e.setCancelled(true);
                    consumeItem(e.getPlayer(), 1, Material.BAT_SPAWN_EGG);
                    e.getPlayer().setGameMode(GameMode.CREATIVE);
                    Bukkit.getScheduler().runTaskLater(this, () -> e.getPlayer().setGameMode(GameMode.SURVIVAL),
                            20L);
                }
            }
        }
    }

    public boolean consumeItem(Player player, int count, Material mat) {
        Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(mat);

        int found = 0;
        for (ItemStack stack : ammo.values())
            found += stack.getAmount();
        if (count > found)
            return false;

        for (Integer index : ammo.keySet()) {
            ItemStack stack = ammo.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed)
                player.getInventory().setItem(index, null);
            else
                stack.setAmount(stack.getAmount() - removed);

            if (count <= 0)
                break;
        }

        player.updateInventory();
        return true;
    }
}
