package com.deathraid.deathraid;

import com.deathraid.deathraid.commands.DeathCountCommand;
import com.deathraid.deathraid.commands.RaidConfigCommand;
import com.deathraid.deathraid.commands.RaidStatusCommand;
import com.deathraid.deathraid.commands.ResetDeathsCommand;
import com.deathraid.deathraid.integration.SimpleClaimsHook;
import com.deathraid.deathraid.listeners.DeathEventHandler;
import com.deathraid.deathraid.managers.DataManager;
import com.deathraid.deathraid.managers.DeathManager;
import com.deathraid.deathraid.managers.RaidManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.prefab.event.EntityDeathEvent;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * DeathRaid Plugin - Enables death-based raiding mechanics for Hytale servers.
 * 
 * When a party accumulates a configurable number of deaths (default: 20),
 * their Simple Claims protections are temporarily disabled, allowing other
 * players to raid them for a configurable time window (default: 30 minutes).
 * 
 * @author DeathRaid Team
 * @version 1.0.0
 */
public class DeathRaidPlugin extends JavaPlugin {

    private static DeathRaidPlugin instance;
    
    // Managers
    private DataManager dataManager;
    private DeathManager deathManager;
    private RaidManager raidManager;
    private SimpleClaimsHook simpleClaimsHook;
    
    // Configuration values (loaded from config)
    private int deathThreshold = 20;
    private int raidDurationMinutes = 30;
    private boolean enableBroadcastAlerts = true;
    private boolean deathAnnouncementToParty = true;
    private int deathAnnouncementInterval = 5;
    private int saveIntervalMinutes = 5;
    
    /**
     * Plugin constructor - Called when the plugin is first loaded.
     * 
     * @param init The plugin initialization context
     */
    public DeathRaidPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().info("DeathRaid Plugin loading...");
    }
    
    /**
     * Called during the setup phase.
     * Use this method to register commands, events, and initialize resources.
     * Dependencies are guaranteed to be in the SETUP state or later.
     */
    @Override
    protected void setup() {
        getLogger().info("Setting up DeathRaid systems...");
        
        // Initialize Simple Claims integration
        simpleClaimsHook = new SimpleClaimsHook(this);
        if (!simpleClaimsHook.initialize()) {
            getLogger().warn("Simple Claims integration failed! Some features may not work.");
            getLogger().warn("Make sure Simple Claims mod is installed and loaded.");
        } else {
            getLogger().info("Simple Claims integration successful!");
        }
        
        // Initialize managers
        dataManager = new DataManager(this);
        deathManager = new DeathManager(this);
        raidManager = new RaidManager(this);
        
        // Load persistent data and configuration
        dataManager.loadAll();
        
        // Register commands using the proper registry
        getCommandRegistry().registerCommand(new DeathCountCommand(this));
        getCommandRegistry().registerCommand(new RaidStatusCommand(this));
        getCommandRegistry().registerCommand(new ResetDeathsCommand(this));
        getCommandRegistry().registerCommand(new RaidConfigCommand(this));
        
        getLogger().info("Commands registered: /deathcount, /raidstatus, /resetdeaths, /raidconfig");
        
        // Register event handlers using the proper registry
        DeathEventHandler deathHandler = new DeathEventHandler(this);
        getEventRegistry().register(EntityDeathEvent.class, deathHandler::onEntityDeath);
        
        getLogger().info("Event handlers registered.");
        
        // Register scheduled tasks using the task registry
        // Task for checking expired raid windows (every 60 seconds)
        getTaskRegistry().registerRepeatingTask(
            "raid_window_check",
            () -> {
                try {
                    raidManager.checkExpiredRaidWindows();
                } catch (Exception e) {
                    getLogger().error("Error checking raid windows: " + e.getMessage());
                }
            },
            60, // initial delay in seconds
            60, // period in seconds
            TimeUnit.SECONDS
        );
        
        // Task for auto-saving data
        getTaskRegistry().registerRepeatingTask(
            "auto_save",
            () -> {
                try {
                    dataManager.saveAll();
                    getLogger().info("Auto-saved death raid data.");
                } catch (Exception e) {
                    getLogger().error("Error during auto-save: " + e.getMessage());
                }
            },
            saveIntervalMinutes * 60L,
            saveIntervalMinutes * 60L,
            TimeUnit.SECONDS
        );
        
        getLogger().info("Scheduled tasks registered.");
    }
    
    /**
     * Called during the start phase.
     * Dependencies are guaranteed to be in the ENABLED state.
     * Asset packs should be registered here.
     */
    @Override
    protected void start() {
        getLogger().info("DeathRaid Plugin started successfully!");
        getLogger().info("Death threshold: " + deathThreshold);
        getLogger().info("Raid duration: " + raidDurationMinutes + " minutes");
    }
    
    /**
     * Called when the plugin is shutting down.
     * Save data and release resources here.
     */
    @Override
    protected void shutdown() {
        getLogger().info("Shutting down DeathRaid Plugin...");
        
        // Save all data
        if (dataManager != null) {
            dataManager.saveAll();
        }
        
        // Restore all active raid windows (re-enable protections)
        if (raidManager != null) {
            raidManager.restoreAllProtections();
        }
        
        getLogger().info("DeathRaid Plugin shutdown complete.");
    }
    
    /**
     * Reloads the plugin configuration.
     */
    public void reloadConfig() {
        dataManager.loadConfiguration();
        getLogger().info("Configuration reloaded.");
    }
    
    // ==================== Getters and Setters ====================
    
    public static DeathRaidPlugin getInstance() {
        return instance;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public DeathManager getDeathManager() {
        return deathManager;
    }
    
    public RaidManager getRaidManager() {
        return raidManager;
    }
    
    public SimpleClaimsHook getSimpleClaimsHook() {
        return simpleClaimsHook;
    }
    
    public int getDeathThreshold() {
        return deathThreshold;
    }
    
    public void setDeathThreshold(int threshold) {
        this.deathThreshold = Math.max(1, threshold);
    }
    
    public int getRaidDurationMinutes() {
        return raidDurationMinutes;
    }
    
    public void setRaidDurationMinutes(int minutes) {
        this.raidDurationMinutes = Math.max(1, minutes);
    }
    
    public boolean isEnableBroadcastAlerts() {
        return enableBroadcastAlerts;
    }
    
    public void setEnableBroadcastAlerts(boolean enable) {
        this.enableBroadcastAlerts = enable;
    }
    
    public boolean isDeathAnnouncementToParty() {
        return deathAnnouncementToParty;
    }
    
    public void setDeathAnnouncementToParty(boolean announce) {
        this.deathAnnouncementToParty = announce;
    }
    
    public int getDeathAnnouncementInterval() {
        return deathAnnouncementInterval;
    }
    
    public void setDeathAnnouncementInterval(int interval) {
        this.deathAnnouncementInterval = Math.max(1, interval);
    }
    
    public int getSaveIntervalMinutes() {
        return saveIntervalMinutes;
    }
    
    public void setSaveIntervalMinutes(int minutes) {
        this.saveIntervalMinutes = Math.max(1, minutes);
    }
}
