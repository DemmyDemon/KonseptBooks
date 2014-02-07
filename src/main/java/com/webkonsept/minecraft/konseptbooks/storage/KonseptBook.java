package com.webkonsept.minecraft.konseptbooks.storage;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KonseptBook implements ConfigurationSerializable {

    private String title;
    private String author;
    private List<String> pages;
    private Date updated;

    /**
     * Initialize a brand new KonseptBook
     * @param title The title of the book
     * @param author The signing author of this book
     * @param pages The pages contained in this book
     */
    public KonseptBook(String title,String author,List<String> pages){
        this.title = title;
        this.author = author;
        this.pages = pages;
        updated = new Date();
    }

    /**
     * Don't ever call this directly, unless you're a Bukkit configuration loader deserializeing an
     * instance of this class.
     * @param serialization The serialized book you want this book to be.
     */
    @SuppressWarnings("unchecked")
    public KonseptBook(Map<String,Object> serialization){
        title = (String) serialization.get("title");
        author = (String) serialization.get("author");
        pages = (ArrayList<String>) serialization.get("pages");
        updated = new Date((long) serialization.get("updated"));
    }

    /**
     * As required by, and documented in, the ConfigurationSerializable class
     * @return A map of key/values accurately representing this book
     */
    public Map<String,Object> serialize(){
        Map<String,Object> serialization = new HashMap<>();

        serialization.put("title",title);
        serialization.put("author",author);
        serialization.put("pages",pages);
        serialization.put("updated",updated.getTime());

        return serialization;
    }

    /**
     * Get an ItemStack representation of this book, for example for giving to players to read.
     * It has been signed by the defined author, and can not be amended.
     * @return A single "Written book" representing this book.
     */
    public ItemStack getSigned(){
        return applyMeta(new ItemStack(Material.WRITTEN_BOOK));
    }

    /**
     * Get an ItemStack representation this book, for example for editing the pages.
     * It is NOT signed, and can be edited freely.
     * @return A single "Book and quill" representing this book.
     */
    public ItemStack getUnsigned(){
        return applyMeta(new ItemStack(Material.BOOK_AND_QUILL));
    }

    /**
     * Applies all the relevant meta values of this KonseptBook to the given ItemStack
     * @param book An ItemStack with books in them.  It's .getItemMeta() result must be or extend BookMeta.
     * @return The same stack, but with the relevant meta set, if possible.  If not possible, it returns what it got.
     */
    public ItemStack applyMeta(ItemStack book){
        ItemMeta meta = book.getItemMeta();
        if (meta instanceof BookMeta){
            BookMeta bookMeta = (BookMeta) book.getItemMeta();
            bookMeta.setTitle(title);
            bookMeta.setAuthor(author);
            bookMeta.setPages(pages);
            bookMeta.setLore(new ArrayList<String>(){{
                add("Last updated");
                add(updated.toString());
            }});
            book.setItemMeta(bookMeta);
        }
        return book;
    }

    /**
     * Get the title of the book
     * @return The specified title
     */
    public String getTitle(){
        return title;
    }

    /**
     * Set the title of this book.
     * Will NOT update the book list with the new title, so it is advised to sign a new book and use the
     * updateBook method on the book loader.
     * Be sure to have a look at the getUnsigned method.
     * @see com.webkonsept.minecraft.konseptbooks.KonseptBooksLibrary
     * @param title The new title for this book.
     */
    public void setTitle(String title){
        this.title = title;
        updated = new Date();
    }

    /**
     * Get the signing author of this book
     * @return The author for this book
     */
    public String getAuthor(){
        return author;
    }

    /**
     * Set the author of this book.
     * This has no effect at all unless you get the book by it's getSigned method later.
     * @param author The name of the book's author
     */
    public void setAuthor(String author){
        this.author = author;
    }

    /**
     * Get a clone of the page list of this book.
     * Note that editing this list does not, in fact, change the book.
     * You need to setPages after editing.
     * @return A copy of this book's pages
     */
    public List<String> getPages(){
        return pages;
    }

    /**
     * Set all the pages of this book at once.
     * @param pages The list of pages to overwrite the content of this book with.
     */
    public void setPages(List<String> pages){
        this.pages = pages;
    }

    /**
     * Add a single page to the end of this book.
     * @param page The page to add
     */
    public void addPage(String page){
        this.pages.add(page);
    }

    /**
     * Remove a single page from this book.
     * Will check if such a page exists first.
     * @param pageIndex Index of the page in question, starting at 0 for the first page.
     */
    public void removePage(int pageIndex){
        if (pageIndex >= 0 && pageIndex < pages.size()){
            this.pages.remove(pageIndex);
        }
    }
    public void updatedNow(){
        updated = new Date();
    }
}
