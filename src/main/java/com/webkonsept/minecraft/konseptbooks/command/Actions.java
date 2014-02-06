package com.webkonsept.minecraft.konseptbooks.command;

import com.webkonsept.minecraft.konseptbooks.KonseptBooks;
import com.webkonsept.minecraft.konseptbooks.KonseptBooksLibrary;
import com.webkonsept.minecraft.konseptbooks.storage.KonseptBook;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public final class Actions {

    // The list of actionsHelpText used for adaptiveHelp
    private static final Map<String,String> actionsHelpText = new HashMap<String,String>(){{
        put("add",      "Add the book you're holding to the library.");
        put("author",   "Set author of the signed book you're holding.");
        put("help",     "See this fantastic help text!");
        put("list",     "List the books you can get.");
        put("prepend",  "Prepend a page to the book-and-quill you're holding");
        put("reload",   "Reload the settings and library.");
        put("update",   "Update the library with the book you're holding.");
        put("unsigned", "Get an unsigned copy of a book.  Provide a book name!");
    }};

    private Actions(){
        /*
         *  throw new HissyFitException; (just kidding)
         *  What we need to do is nothing.  This is a purely static class.
         *
         */
    }

    /**
     * Tell the provided sender that the given action is not implemented.
     * @param sender Sender to tell all about missing implementation.
     * @param unimplementedAction Specify what action it is that is not implemented.
     * @deprecated You shouldn't be ADDING unimplemented stuff, dummy.
     * @return Always returns false...
     */
    @Deprecated
    public static boolean notImplemented(CommandSender sender,String unimplementedAction){
        sender.sendMessage(ChatColor.YELLOW+"Sorry, -"+unimplementedAction+" is not implemented yet.");
        return false;
    }

    /**
     * Reload settings and library for the given plugin instance.
     * @param sender The CommandSender that is doing the reloading.
     * @param pluginInstance The instance of KonseptBooks that needs to be reloaded.
     * @return Always returns true, just has a return for consistency across actions
     */
    public static boolean reload(CommandSender sender,KonseptBooks pluginInstance){
        pluginInstance.getLibrary().load();
        sender.sendMessage("Library reloaded.");
        pluginInstance.loadConfiguration();
        sender.sendMessage("Configuration reloaded.");
        return true;
    }

    /**
     * Send a list of the books in the library to someone.
     * @param sender The CommandSender that is to receive the list
     * @param library The library to get the book list from.
     * @return Always returns true, just has a return for consistency across actions
     */
    public static boolean sendBookList(CommandSender sender,KonseptBooksLibrary library){
        ArrayList<String> bookList = library.getBookList();
        if (bookList.size() > 0){
            sender.sendMessage(ChatColor.GOLD+"Books in the library: ");
            for (String bookName : bookList){
                sender.sendMessage("  "+bookName);
            }
        }
        else {
            sender.sendMessage(ChatColor.GOLD+"There are no books in the library.");
        }
        return true;
    }

    /**
     * Sends adaptive help to the provided sender.
     * "Adaptive" means it checks each possible action against permissions to actually do those actionsHelpText.
     * Help is not given for any action the sender does not have permission to do.
     * This way, users won't have to wade through 9000 things they can't do in order to see the 3 they can.
     * Yaay!
     * @param sender The CommandSender (a Player, for example) that is in need of help
     * @return Always returns true, just has a return for consistency
     */
    public static boolean adaptiveHelp(CommandSender sender){
        sender.sendMessage(ChatColor.GREEN + "KonseptBooks Help - Possible actions:");
        String[] actions = (String[]) actionsHelpText.keySet().toArray();
        Arrays.sort(actions);
        for (String actionName : actions){
            if (sender.hasPermission("konseptbooks.action."+actionName)){
                sender.sendMessage("  -" + actionName + " > " + actionsHelpText.get(actionName));
            }
        }
        return true;
    }

    /**
     * Change the author of a book
     * @param sender The CommandSender (a Player, hopefully) holding the book in question
     * @param args The title.  Arbitrary non-zero number of strings.
     * @return True if the change happened, false otherwise.
     */
    public static boolean changeAuthor(CommandSender sender,String[] args){
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length > 0){
                String newAuthor = KonseptBooks.join(" ",args);
                ItemStack inHand = player.getItemInHand();
                if (inHand != null){
                    if (inHand.getType().equals(Material.WRITTEN_BOOK)){
                        BookMeta meta = (BookMeta) inHand.getItemMeta();
                        meta.setAuthor(newAuthor);
                        inHand.setItemMeta(meta);
                        sender.sendMessage(ChatColor.GREEN+"New author set.");
                        return true;
                    }
                    else {
                        sender.sendMessage(ChatColor.YELLOW+"This is not a signed book in your hand.");
                    }
                }
            }
            else {
                sender.sendMessage(ChatColor.RED+"You have to name an author.");
            }
        }
        else {
            sender.sendMessage("Changing the author of the book in your hand requires a hand.  Console doesn't have one.");
        }
        return false;
    }

    /**
     * Change the title of the currently held book.
     * Does NOT change anything in the library, you'll need to addBook() it later, or whatever.
     * @param sender The CommandSender (a Player, hopefully) holding the book in question.
     * @param args The new author.  Arbitrary non-zero number of strings.
     * @return True if the new title is set, false otherwise, for example if the held item isn't a book.
     */
    public static boolean changeTitle(CommandSender sender,String[] args){
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length > 0){
                String newTitle = KonseptBooks.join(" ",args);
                ItemStack inHand = player.getItemInHand();
                if (inHand != null){
                    if (inHand.getType().equals(Material.WRITTEN_BOOK)){
                        BookMeta meta = (BookMeta) inHand.getItemMeta();
                        meta.setTitle(newTitle);
                        inHand.setItemMeta(meta);
                        sender.sendMessage(ChatColor.GREEN+"New title set.");
                        return true;
                    }
                    else {
                        sender.sendMessage(ChatColor.YELLOW+"This is not a signed book in your hand.");
                    }
                }
            }
            else {
                sender.sendMessage(ChatColor.RED+"You have to tell me what the title should be.");
            }
        }
        else {
            sender.sendMessage("Changing the title of the book in your hand requires a hand.  Console doesn't have one.");
        }
        return false;
    }

    /**
     * Prepends a page to the given CommandSender's held, unsigned book.
     * @param sender The CommandSender (a Player, hopefully) holding the book in question
     * @return True if the page was added, false otherwise, for example if it's not a player, not a book, or it's signed.
     */
    public static boolean prependPage(CommandSender sender){
        if (sender instanceof Player){
            Player player = (Player) sender;
            ItemStack inHand = player.getItemInHand();
            if (inHand.getType().equals(Material.BOOK_AND_QUILL)){
                BookMeta meta = (BookMeta) inHand.getItemMeta();
                List<String> pages = meta.getPages();
                ArrayList<String> newPages = new ArrayList<>();
                newPages.add(""); // Prepending with a blank page, yaay!
                for (String page : pages){
                    newPages.add(page);
                }
                meta.setPages(newPages);
                inHand.setItemMeta(meta);
                sender.sendMessage(ChatColor.GOLD+"A page was inserted at the start of your book.");
            }
            else {
                sender.sendMessage(ChatColor.YELLOW+"Sorry, you can only prepend pages to unsigned books.");
            }
        }
        else {
            sender.sendMessage("Adding pages to a book can only be done while in-game because it uses the book in your hand.");
        }
        return false;
    }

    /**
     * Add the held book to the library.
     * @param sender The CommandSender (a Player, hopefully) holding the book in question.
     * @param library The KonseptBooksLibrary the book should go in.
     * @return True if the book is added, false otherwise, for example if a book with that title is already in there.
     */
    public static boolean addBook(CommandSender sender,KonseptBooksLibrary library) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            ItemStack inHand = player.getItemInHand();
            if (inHand != null){
                if (inHand.getType().equals(Material.BOOK_AND_QUILL)){
                    sender.sendMessage(ChatColor.GOLD+"You need to sign and title the book first.");
                }
                else if (inHand.getType().equals(Material.WRITTEN_BOOK)){
                    BookMeta meta = (BookMeta) inHand.getItemMeta();
                    KonseptBook book = new KonseptBook(meta.getTitle(),meta.getAuthor(),meta.getPages());
                    if (library.addBook(book)){
                        sender.sendMessage(ChatColor.GOLD+"Book added to library!");
                        return true;
                    }
                    else {
                        sender.sendMessage(ChatColor.RED+"A book of this name is already in the library.  Did you mean to -update?");
                    }
                }
            }
        }
        else {
            sender.sendMessage("Adding books is done from the book in your hand.  Consoles don't have hands, players do.");
        }
        return false;
    }

    /**
     * Delete a book from the given library.
     * @param sender The CommandSender (a Player, hopefully) holding the book in question
     * @param library The library to delete the book from.
     * @return True if the deletion was successful.  False otherwise, for example if the book didn't exist.
     */
    public static boolean deleteBook(CommandSender sender,KonseptBooksLibrary library){
        if (sender instanceof Player){
            Player player = (Player) sender;
            ItemStack inHand = player.getItemInHand();
            if (inHand != null){
                if (inHand.getType().equals(Material.WRITTEN_BOOK)){
                    BookMeta meta = (BookMeta) inHand.getItemMeta();
                    if (library.deleteBook(meta.getTitle())){
                        sender.sendMessage(ChatColor.RED+meta.getTitle()+" deleted. [dramatic music]");
                        return true;
                    }
                    else {
                        sender.sendMessage(ChatColor.RED+"Couldn't delete that book.  Is it in the library?");
                    }
                }
            }
        }
        else {
            sender.sendMessage("Deleting books is done from the book in your hand.  Consoles don't have hands, players do.");
        }
        return false;
    }

    /**
     * Update a book already in the library.
     * @param sender The CommandSender (a Player, hopefully) holding the book in question.
     * @param library The library to update with the new version of the book.
     * @param titleBits The title of the book to update, if the held book is not signed.
     * @return True if the book was updated, false otherwise, for example if there is no such book in there.
     */
    public static boolean updateBook(CommandSender sender,KonseptBooksLibrary library,String[] titleBits){
        if (sender instanceof Player){
            Player player = (Player) sender;
            ItemStack inHand = player.getItemInHand();
            if (titleBits == null){
                sender.sendMessage("There is a bug. .updateBook() doesn't take a null titleBits.  Ever.  Make it an empty array!");
                return false;
            }
            if (inHand != null){
                if (inHand.getType().equals(Material.BOOK_AND_QUILL)){
                    if (titleBits.length > 0){
                        String title = KonseptBooks.join(" ",titleBits);
                        KonseptBook book = library.getBook(title);
                        if (book != null){
                            BookMeta meta = (BookMeta) inHand.getItemMeta();
                            book.setPages(meta.getPages());
                            if (library.updateBook(book)){
                                sender.sendMessage(ChatColor.GOLD+"Book updated!");
                                return true;
                            }
                            else {
                                sender.sendMessage(ChatColor.RED+"Failed to update the book!");
                            }
                        }
                        else {
                            sender.sendMessage(ChatColor.RED+"Sorry, I couldn't find a book called '"+title+"' in the library.");
                        }

                    }
                    else {
                        sender.sendMessage(ChatColor.GOLD+"Sorry, you have to tell me the title of the book, since it's not signed.");
                    }
                }
                else if (inHand.getType().equals(Material.WRITTEN_BOOK)){
                    if (titleBits.length > 0){
                        sender.sendMessage(ChatColor.RED+"Sorry, you can't specify a title when updating with a signed book.");
                    }
                    else {
                        BookMeta meta = (BookMeta) inHand.getItemMeta();
                        KonseptBook book = new KonseptBook(meta.getTitle(),meta.getAuthor(),meta.getPages());
                        if (library.updateBook(book)){
                            sender.sendMessage(ChatColor.GOLD+"Book updated!");
                            return true;
                        }
                        else {
                            sender.sendMessage(ChatColor.RED+"No book of this name is already in the library.  Did you mean to -add?");
                        }
                    }
                }
            }
        }
        else {
            sender.sendMessage("Updating books is done from the book in your hand.  Consoles don't have hands, players do.");
        }
        return false;
    }

    /**
     * Give an unsigned copy of the a book, for editing.
     * @param sender The CommandSender (a Player, hopefully) that is to relieve the unsigned book.
     * @param args The name of the book you want to give.
     * @param library The library to get the book from.
     * @return Returns true if the book was actually given, false otherwise.
     */
    public static boolean getUnsignedBook(CommandSender sender, String[] args, KonseptBooksLibrary library) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            String title = KonseptBooks.storageName(KonseptBooks.join(" ",args));
            KonseptBook book = library.getBook(title);
            if (book != null){
                int slot = player.getInventory().firstEmpty();
                if (slot >= 0){
                    ItemStack bookItem = book.getUnsigned();
                    player.getInventory().setItem(slot,bookItem);
                    sender.sendMessage(ChatColor.GOLD+"There you go, you have an unsigned copy of "+book.getTitle());
                    return true;
                }
                else {
                    sender.sendMessage(ChatColor.RED+"You don't have room in your inventory for this book.");
                }
            }
            else {
                sender.sendMessage(ChatColor.RED+"Sorry, there is no such book.");
            }
        }
        else {
            sender.sendMessage("You can't receive in-game items on the console.  Seriously.");
        }
        return false;
    }
}
