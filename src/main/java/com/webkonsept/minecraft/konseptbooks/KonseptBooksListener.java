package com.webkonsept.minecraft.konseptbooks;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KonseptBooksListener implements Listener {

    private KonseptBooks plugin;

    public KonseptBooksListener(KonseptBooks instance){
        plugin = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (player.hasPermission("konseptbooks.getupdates")){
            plugin.getLibrary().updateBooksInInventory(player.getInventory());
        }
    }


    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event){
        // TODO: Is this really the best way?  Seriously?
        HumanEntity entity = event.getPlayer();
        if (entity instanceof Player){
            Player player = (Player) entity;
            if (player.hasPermission("konseptbooks.getupdates")){
                plugin.getLibrary().updateBooksInInventory(event.getInventory());
            }
        }
    }
}
