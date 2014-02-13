package com.webkonsept.minecraft.konseptbooks;

import com.webkonsept.minecraft.konseptbooks.storage.KonseptBook;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class KonseptBooksLibrary {
    private File bookFile;
    private KonseptBooks plugin;
    private TreeMap<String,KonseptBook> books = new TreeMap<>();

    private final static String bookEntryName = "library";

    /**
     * Initialise a fresh book list.
     * @param instance An instance of the KonseptBooks plugin through which to
     * @param filename The filename to use.  Will be relative to the plugin instance's data directory.
     */
    public KonseptBooksLibrary(KonseptBooks instance, String filename){
        plugin = instance;
        bookFile = new File(plugin.getDataFolder(),filename);
        load();
    }

    /**
     * Loads the list of books from the file specified in the constructor, if possible.
     * If such a file does not exist, it will attempt to create it.
     */
    // @SuppressWarnings("unchecked")
    public void load(){
        if (bookFile != null){
            books = new TreeMap<>();
            if (!bookFile.exists()){
                save();
                plugin.getLogger().info("No book file! Created a new one.");
            }
            else {
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(bookFile);
                MemorySection section = (MemorySection) fileConfiguration.get(bookEntryName);
                for (String entry : section.getKeys(false)){
                    books.put(entry,(KonseptBook)section.get(entry));
                }
                plugin.getLogger().info("Loaded "+books.size()+" books.");
            }
        }
        else {
            plugin.getLogger().warning("Attempt at loading NULL bookFile!  CRUD!");
        }
    }

    /**
     * Saves the books to the file specified in the constructor, if possible.
     * If the file does not exist, it will be attempted created.
     */
    public void save(){
        if (books == null){
            books = new TreeMap<>();
        }

        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(bookFile);

        fileConfiguration.set(bookEntryName,books);
        try {
            fileConfiguration.save(bookFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Could not save books: "+e.getMessage());
        }
    }

    /**
     * Get the number of books in the library
     * @return Integer sizer of the library.
     */
    public int size(){
        if (books == null){
            return 0;
        }
        else {
            return books.size();
        }
    }

    /**
     * Get a collection of all the books to iterate over for your own damn self.
     * @return A generic Collection of all the KonseptBook objects in the library.
     */
    public Collection<KonseptBook> getAll(){
        return books.values();
    }

    /**
     * Update the books held by ALL players.
     * Permissions are checked.
     */
    public void updatePlayerBooks(){
        for (Player player : plugin.getServer().getOnlinePlayers()){
            if (player.hasPermission("konseptbooks.getupdates")){
                updateBooksInInventory(player.getInventory());
            }
        }
    }

    /**
     * Update all the books in the given inventory.
     * Doesn't return anything because it's updated in place.
     * @param inventory The inventory that might hold some books.
     */
    public void updateBooksInInventory(Inventory inventory){
        // TODO: Perhaps check if the book is updated etc etc.
        // Profiling seems to indicate that doing those checks might actually be more costly than just doing it.
        // Besides, it's not a proven performance issue, so let's optimize when we need to, shall we?
        if (inventory != null){
            for (ItemStack item : inventory.getContents()){
                if (item != null && item.getType().equals(Material.WRITTEN_BOOK)){
                    applyMeta(item);
                }
            }
        }
    }

    /**
     * Add a new book to the book list.
     * Note:  Only considers title, nothing else.
     * Will save if the controlling plugin instance has autosave = true
     * @param book The book to add
     * @return True if the book was added, false if a book with that title already exists.
     */
    public boolean addBook(KonseptBook book){
        String title = KonseptBooks.storageName(book.getTitle());
        if (books.containsKey(title)){
            return false;
        }
        else {
            books.put(title,book);
            if (plugin.autosave){
                save();
            }
            return true;
        }
    }

    /**
     * Will update a book already in the book list.
     * Note:  Only considers title, nothing else.
     * Will save afterwards, if the controlling plugin instance has autosave = true
     * @param book The book to update
     * @return True if the book was updated, false if the book with that title did not already exist.
     */
    public boolean updateBook(KonseptBook book){
        String title = KonseptBooks.storageName(book.getTitle());
        plugin.getLogger().info("Update requested for "+title);
        if (books.containsKey(title)){
            book.updatedNow();
            books.put(title,book);
            if (plugin.autosave){
                save();
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Deletes a book with the given book's title.
     * Technically it just calls the deleteBook(String title) method, so be sure to look that up as well.
     * @param book KonseptBook with the same title as the one you want to delete
     * @return True if book was successfully deleted, false otherwise (did not exist)
     */
    /* Not actually ever used.
    public boolean deleteBook(KonseptBook book){
        return deleteBook(book.getTitle());
    }
    */

    /**
     * Deletes a book with the given title.
     * Will save afterwards, if the controlling plugin instance has autosave = true
     * @param title Title of the book you want to delete.  Not case sensitive or anything.
     * @return True if the book was successfully deleted, false if no book with that title existed.
     */
    public boolean deleteBook(String title){
        title = KonseptBooks.storageName(title);
        if (books.containsKey(title)){
            books.remove(title);
            if (plugin.autosave){
                save();
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Get a KonseptBook by it's title
     * @param title The title of the book you want to get
     * @return Either the KonseptBook object that describes the given book, or null if there is no such book int he library.
     */
    public KonseptBook getBook(String title){
        return books.get(KonseptBooks.storageName(title));
    }


    /**
     * @return Returns a sorted ArrayList of the names of books in this book library.
     */
    public ArrayList<String> getBookList(){
        ArrayList<String> bookList = new ArrayList<>();
        for (String bookName : books.keySet()){
            bookList.add(books.get(bookName).getTitle());
        }
        Collections.sort(bookList);

        return bookList;
    }

    /**
     * Apply updated book meta to the an item.
     * Will use the title of the written book to determine what book in the library it represents.
     * @param item The WRITTEN_BOOK you want to set the meta of.
     */
    public void applyMeta(ItemStack item){
        if (item.getType().equals(Material.WRITTEN_BOOK)){
            BookMeta meta = (BookMeta) item.getItemMeta();
            String title = KonseptBooks.storageName(meta.getTitle());
            if (books.containsKey(title)){
                KonseptBook book = books.get(title);
                book.applyMeta(item);
            }
        }
    }
}
