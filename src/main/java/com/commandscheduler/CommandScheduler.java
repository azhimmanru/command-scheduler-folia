package com.commandscheduler;

import com.commandscheduler.command.SchedulerCommand;
import com.commandscheduler.manager.ScheduleManager;
import com.tcoded.folialib.FoliaLib;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandScheduler extends JavaPlugin {
    private FoliaLib foliaLib;
    private ScheduleManager scheduleManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        foliaLib = new FoliaLib(this);
        scheduleManager = new ScheduleManager(this, foliaLib);
        
        getCommand("commandscheduler").setExecutor(new SchedulerCommand(this, scheduleManager));
        
        scheduleManager.loadSchedules();
        scheduleManager.startAllSchedules();
        
        getLogger().info("CommandScheduler enabled with Folia support!");
    }

    @Override
    public void onDisable() {
        if (scheduleManager != null) {
            scheduleManager.stopAllSchedules();
        }
        if (foliaLib != null) {
            foliaLib.getScheduler().cancelAllTasks();
        }
        getLogger().info("CommandScheduler disabled!");
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }
}