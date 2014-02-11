package com.webkonsept.minecraft.konseptbooks;

import com.webkonsept.minecraft.konseptbooks.command.Executor;
import com.webkonsept.minecraft.konseptbooks.storage.KonseptBook;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

public class KonseptBooks extends JavaPlugin {

    private KonseptBooksLibrary library;

    public static int curseProjectID = 74108;

    // FIXME Why are these even here?
    private KonseptBooksListener eventListener;
    private Executor commandExecutor;
    protected Updater updater;
    protected boolean updateWaiting = false;

    // TODONE - Made this stuff configurable!
    protected boolean autosave = true;
    protected boolean giveNewbieBook = false;
    protected String newbieBookName = "Welcome";

    @Override
    public void onEnable() {

        loadConfiguration();

        ConfigurationSerialization.registerClass(KonseptBook.class);
        library = new KonseptBooksLibrary(this, "books.yaml");
        eventListener = new KonseptBooksListener(this);
        commandExecutor = new Executor(this);

        getServer().getPluginManager().registerEvents(eventListener,this);



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
        boolean checkForUpdates = getConfig().getBoolean("checkForUpdates", true);

        if (checkForUpdates){
            getLogger().info("Update checking is enabled, but I will not download it for you automatically.");
            checkUpdates();
        }
        else {
            getLogger().info("Update checking has been disabled.  Okay, fine.  Be that way.");
        }
    }

    /**
     * Check for updates and handle the result of that check.
     * Entirely self-contained; nothing passed and nothing returned.
     * It does have the side-effect of murdering this.updater and replacing it with a new one.
     */
    private void checkUpdates(){

        // I realize this clobbers the existing updater on every reload or whatever, but I want that.
        // I want it to check again in those cases.
        updater = new Updater(this, curseProjectID, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);

        Updater.UpdateResult result = updater.getResult();
        switch (result){

            case SUCCESS:
                getLogger().info("A new update was downloaded, and you can restart your server to apply it now.");
                break;

            case NO_UPDATE:
                getLogger().info("You are running the latest released version.");
                break;

            case DISABLED:
                // I don't want to tell people this as they could have turned it off to avoid spam...
                // getLogger().info("You have disabled updates");
                break;

            case FAIL_DOWNLOAD:
                getLogger().warning("There is an update available to "+updater.getLatestName()+" available, but I failed to download it!");
                break;

            case FAIL_DBO:
                getLogger().warning("I can't get a hold of dev.bukkit.org, so there is an update I can't fetch for you!");
                break;

            case FAIL_NOVERSION:
                getLogger().warning("I'm so sorry, but the file on dev.bukkit.org is a bit silly, so I have no idea what version it is.");
                getLogger().warning("You should probably report this so it can get fixed sooner rather than later.");
                break;

            case FAIL_BADID:
                getLogger().warning("I could not find this project on dev.bukkit.org - this is a bad thing and should be reported, please.");
                break;

            case FAIL_APIKEY:
                getLogger().warning("There was a problem with the API key provided for updates.  Please report this error message.");
                break;

            case UPDATE_AVAILABLE:
                updateWaiting = true;  //TODO do something fancy with this onLogin, or whatever.  Might be a nuisance?  TEST MOAR!
                if (updater.getLatestType().equals(Updater.ReleaseType.RELEASE)){
                    getLogger().info("A new and fresh update is available right here:  "+updater.getLatestFileLink());
                }
                else if (updater.getLatestType().equals(Updater.ReleaseType.BETA)){
                    getLogger().info("You might want to consider the latest BETA release:  "+updater.getLatestFileLink());
                }
                else {
                    getLogger().info("Feel like an adventure?  Try the new "+updater.getLatestType().toString()+" at "+updater.getLatestFileLink());
                }
                break;

            default:
                getLogger().warning("Unexpected updater result: "+result.toString()+"...  Please report this!");

        }
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
