package com.webkonsept.minecraft.konseptbooks;

import com.webkonsept.minecraft.konseptbooks.command.Executor;
import com.webkonsept.minecraft.konseptbooks.storage.KonseptBook;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

public class KonseptBooks extends JavaPlugin {

    private KonseptBooksLibrary library;

    // FIXME Why are these even here?
    private KonseptBooksListener eventListener;
    private Executor commandExecutor;

    // TODONE - Made this stuff configurable!
    protected boolean autosave = true;
    protected boolean giveNewbieBook = false;
    protected String newbieBookName = "Welcome";

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(KonseptBook.class);
        library = new KonseptBooksLibrary(this, "books.yaml");
        eventListener = new KonseptBooksListener(this);
        commandExecutor = new Executor(this);

        getServer().getPluginManager().registerEvents(eventListener,this);

        loadConfiguration();
    }
    @Override
    public void onDisable() {
        // library.save(); // If autosave is on, it's already saved.  If it's off, we shouldn't save without prompting.
    }

    /**
     * Load The Configuration!
     */
    public void loadConfiguration(){
        saveDefaultConfig();
        autosave = getConfig().getBoolean("autosave",true);
        giveNewbieBook = getConfig().getBoolean("giveNewbieBook",false);
        newbieBookName = getConfig().getString("newbieBookName","Welcome");
    }

    /**
     * Get a hold of the library associated with this plugin instance
     * @return The currently ruling library
     */
    public KonseptBooksLibrary getLibrary(){
        return library;
    }

    /**
     * Get a hold of the event listener associated with this plugin instance
     * @return The registered event listener
     */
    /* UNUSED
    public KonseptBooksListener getEventListener(){
        return eventListener;
    }
    */

    /**
     * Get your grubby mitts on the command executor this plugin instance uses.
     * @return The command executor that executes commands for this plugin instance.
     */
    /* UNUSED
    public Executor getCommandExecutor(){
        return commandExecutor;
    }
    */

    /**
     * Join up an array of strings.
     * @param delimiter The string to put between each of the provided ones.
     * @param strings The array of strings to join together.
     * @return Returns a string made from the passed array of strings, with a delimiter between each of them.
     */
    public static String join(String delimiter,String[] strings){
        Iterator it = Arrays.asList(strings).iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()){
            String element = (String) it.next();
            sb.append(element);
            if (it.hasNext()){
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    /**
     * Takes a book title, or any string for that matter, and turns it into a standardized form used to store book names in YAML.
     * @param title The string you want standardized
     * @return The standardized string
     */
    public static String storageName(String title){
        title = title.toLowerCase();
        return title.replaceAll("[^a-z_]+","_");
    }
}
