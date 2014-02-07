package com.webkonsept.minecraft.konseptbooks.command;

import com.webkonsept.minecraft.konseptbooks.KonseptBooks;
import com.webkonsept.minecraft.konseptbooks.storage.KonseptBook;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;

public class Executor implements CommandExecutor{
    private KonseptBooks plugin;

    public Executor(KonseptBooks instance){
        plugin = instance;
        String[] commands = {"konseptbooks"};
        for (String command : commands){
            plugin.getCommand(command).setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean showUsage = true;
        if (command.getName().equals("konseptbooks")){
            if (args.length > 0){
                if (args[0].startsWith("-")){
                    String action = args[0];
                    String[] actionArgs = new String[]{};
                    if (args.length > 1){
                        actionArgs = Arrays.copyOfRange(args,1,args.length);
                    }
                    showUsage = callAction(sender,action.toLowerCase().replaceFirst("-",""),actionArgs,label);
                }
                else {
                    showUsage = false;  // Showing usage is inherently not useful when someone requests a specific book.
                    if (sender instanceof Player){
                        if (sender.hasPermission("konseptbooks.getbooks")){
                            if (!giveBook((Player)sender,KonseptBooks.join(" ",args))){
                                sender.sendMessage(ChatColor.RED+"Sorry, no such book.  Try /"+label+" -list");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED+"Permission denied!");
                        }
                    }
                    else {
                        sender.sendMessage("Sorry, you can't request a copy of a book on the console.  Try the -help action.");
                    }
                }
            }
        }
        return !showUsage; // It makes sense to me that showing usage is the default, but "true" means "don't" here.
    }

    /**
     * Responsible for dispatching the different actions.
     * @param sender The schmuck that this gets done to.
     * @param action What gets done to the schmuck
     * @param actionArgs How it gets done to the poor bastard
     * @return Returns true if the action made sense, false if not.
     */
    private boolean callAction(CommandSender sender,String action,String[] actionArgs,String label){
        boolean showUsage = true;

        if (!sender.hasPermission("konseptbooks.action."+action)){
            sender.sendMessage(ChatColor.RED+"Sorry, permission denied to -"+action);
            return false;
        }

        switch (action) {
            case "help":
                showUsage = false;
                Actions.adaptiveHelp(sender,label);
                break;
            case "reload":
                showUsage = false;
                Actions.reload(sender, plugin);
                break;
            case "list":
                showUsage = false;
                Actions.sendBookList(sender, plugin.getLibrary());
                break;
            case "update":
                showUsage = false;
                if (Actions.updateBook(sender, plugin.getLibrary(),actionArgs)) {
                    plugin.getLibrary().updatePlayerBooks();
                }
                break;
            case "add":
                showUsage = false;
                if (Actions.addBook(sender, plugin.getLibrary())) {
                    plugin.getLibrary().updatePlayerBooks();
                }
                break;
            case "delete":
                showUsage = false;
                Actions.deleteBook(sender,plugin.getLibrary(),actionArgs);
                break;
            case "unsigned":
                showUsage = false;
                Actions.getUnsignedBook(sender, actionArgs, plugin.getLibrary());
                break;
            case "author":
                showUsage = false;
                Actions.changeAuthor(sender, actionArgs);
                break;
            case "title":
                showUsage = false;
                Actions.changeTitle(sender, actionArgs);
                break;
            case "prepend":
                showUsage = false;
                Actions.prependPage(sender);
                break;
        }
        return showUsage;
    }

    /**
     * Gives a player a book, if at all possible.
     * @param player The player that should receive the book.
     * @param bookName The name of the book in question.  Not case sensitive or anything.
     * @return Returns true if such a book exists and was give, false otherwise, for example on full inventory.
     */
    private boolean giveBook(Player player,String bookName){
        KonseptBook book = plugin.getLibrary().getBook(bookName);
        if (book != null){
            PlayerInventory inventory = player.getInventory();
            int availableSlot = inventory.firstEmpty();
            if (availableSlot >= 0){
                inventory.setItem(availableSlot,book.getSigned());
                player.sendMessage(ChatColor.GOLD+"You have been given the book "+book.getTitle());
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }
}
