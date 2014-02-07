package com.webkonsept.minecraft.konseptbooks;

import com.webkonsept.minecraft.konseptbooks.storage.KonseptBook;
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
        if (player.hasPlayedBefore()){
            if (player.hasPermission("konseptbooks.getupdates")){
                plugin.getLibrary().updateBooksInInventory(player.getInventory());
            }
        }
        else {
            if (plugin.giveNewbieBook){
                KonseptBook newbieBook = plugin.getLibrary().getBook(plugin.newbieBookName);
                if (newbieBook != null){
                    if (player.hasPermission("konseptbooks.getbooks")){
                        ItemStack book = newbieBook.getSigned();
                        int slot = player.getInventory().firstEmpty();
                        if (slot < 0){
                            plugin.getLogger().warning("Your new players don't have space for the book you want to give them.  Seriously?");
                        }
                        else {
                            player.getInventory().setItem(slot,book);
                        }
                    }
                    else {
                        plugin.getLogger().warning("New user "+player.getName()+" does not have permission to get the newbie book!");
                    }
                }
                else {
                    plugin.getLogger().warning("You are configured to give newbie books, but the book '"+plugin.newbieBookName+"' does not exist!");
                }
            }
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
