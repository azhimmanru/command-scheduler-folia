package com.commandscheduler.manager;

import com.commandscheduler.CommandScheduler;
import com.commandscheduler.model.ScheduledCommand;
import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ScheduleManager {
    private final CommandScheduler plugin;
    private final FoliaLib foliaLib;
    private final Map<String, ScheduledCommand> scheduledCommands;
    private final Map<String, WrappedTask> activeTasks;

    public ScheduleManager(CommandScheduler plugin, FoliaLib foliaLib) {
        this.plugin = plugin;
        this.foliaLib = foliaLib;
        this.scheduledCommands = new HashMap<>();
        this.activeTasks = new HashMap<>();
    }

    public void loadSchedules() {
        scheduledCommands.clear();
        
        List<Map<?, ?>> commandsList = plugin.getConfig().getMapList("scheduled-commands");
        
        for (Map<?, ?> commandData : commandsList) {
            String id = (String) commandData.get("id");
            String command = (String) commandData.get("command");
            int interval = (Integer) commandData.get("interval-minutes");
            
            scheduledCommands.put(id, new ScheduledCommand(id, command, interval));
        }
        
        plugin.getLogger().info("Loaded " + scheduledCommands.size() + " scheduled commands");
    }

    public void saveSchedules() {
        List<Map<String, Object>> commandsList = new ArrayList<>();
        
        for (ScheduledCommand cmd : scheduledCommands.values()) {
            Map<String, Object> commandData = new HashMap<>();
            commandData.put("id", cmd.getId());
            commandData.put("command", cmd.getCommand());
            commandData.put("interval-minutes", cmd.getIntervalMinutes());
            commandsList.add(commandData);
        }
        
        plugin.getConfig().set("scheduled-commands", commandsList);
        plugin.saveConfig();
    }

    public boolean addSchedule(String id, String command, int intervalMinutes) {
        if (scheduledCommands.containsKey(id)) {
            return false;
        }
        
        ScheduledCommand scheduledCommand = new ScheduledCommand(id, command, intervalMinutes);
        scheduledCommands.put(id, scheduledCommand);
        saveSchedules();
        startSchedule(scheduledCommand);
        return true;
    }

    public boolean removeSchedule(String id) {
        if (!scheduledCommands.containsKey(id)) {
            return false;
        }
        
        stopSchedule(id);
        scheduledCommands.remove(id);
        saveSchedules();
        return true;
    }

    public void startAllSchedules() {
        for (ScheduledCommand cmd : scheduledCommands.values()) {
            startSchedule(cmd);
        }
    }

    public void startSchedule(ScheduledCommand cmd) {
        if (activeTasks.containsKey(cmd.getId())) {
            return;
        }
        
        long intervalTicks = cmd.getIntervalMinutes() * 60L * 20L;
        
        WrappedTask task = foliaLib.getScheduler().runTimerAsync(() -> {
            executeCommand(cmd.getCommand());
        }, intervalTicks, intervalTicks);
        
        activeTasks.put(cmd.getId(), task);
        plugin.getLogger().info("Started schedule: " + cmd.getId() + " (every " + cmd.getIntervalMinutes() + " minutes)");
    }

    public void stopSchedule(String id) {
        WrappedTask task = activeTasks.remove(id);
        if (task != null) {
            task.cancel();
            plugin.getLogger().info("Stopped schedule: " + id);
        }
    }

    public void stopAllSchedules() {
        for (WrappedTask task : activeTasks.values()) {
            task.cancel();
        }
        activeTasks.clear();
    }

    private void executeCommand(String command) {
        foliaLib.getScheduler().runNextTick(task -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }

    public List<ScheduledCommand> getAllSchedules() {
        return new ArrayList<>(scheduledCommands.values());
    }

    public ScheduledCommand getSchedule(String id) {
        return scheduledCommands.get(id);
    }

    public boolean isScheduleActive(String id) {
        return activeTasks.containsKey(id);
    }
}