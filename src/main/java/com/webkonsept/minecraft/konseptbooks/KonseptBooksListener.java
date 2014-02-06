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

    /*
    @EventHandler
    public void onPlayerHoldingBook(PlayerItemHeldEvent event){
        if (event.getPlayer().isOp()){
            ItemStack inHand = event.getPlayer().getItemInHand();

            if (inHand != null && inHand.getType().equals(Material.WRITTEN_BOOK)){
                if (inHand.getItemMeta() instanceof BookMeta){
                    final Date date = new Date();
                    BookMeta meta = (BookMeta) inHand.getItemMeta();
                    List<String> lore = new ArrayList<String>(){{
                        add("Last updated:");
                        add(date.toString());
                    }};
                    meta.setLore(lore);
                    final String storageName = KonseptBooks.storageName(meta.getTitle());
                    plugin.getLogger().info(event.getPlayer().getName()+" is holding "+ storageName);
                    List<String> pages = new ArrayList<String>(){{
                        add(storageName);
                        add("Page two");
                        add("Page three");
                        add("");
                        add("Page five!");
                    }};
                    meta.setPages(pages);
                    inHand.setItemMeta(meta);
                }
            }
        }
    }
    */

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        plugin.getLibrary().updateBooksInInventory(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onInventoryClose(InventoryOpenEvent event){
        HumanEntity entity = event.getPlayer();
        if (entity instanceof Player){
            plugin.getLibrary().updateBooksInInventory(event.getInventory());
        }
    }
}
