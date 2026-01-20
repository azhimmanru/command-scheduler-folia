package com.commandscheduler.command;

import com.commandscheduler.CommandScheduler;
import com.commandscheduler.manager.ScheduleManager;
import com.commandscheduler.model.ScheduledCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SchedulerCommand implements CommandExecutor, TabCompleter {
    private final CommandScheduler plugin;
    private final ScheduleManager scheduleManager;

    public SchedulerCommand(CommandScheduler plugin, ScheduleManager scheduleManager) {
        this.plugin = plugin;
        this.scheduleManager = scheduleManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("commandscheduler.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                return handleAdd(sender, args);
            case "remove":
                return handleRemove(sender, args);
            case "list":
                return handleList(sender);
            case "reload":
                return handleReload(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleAdd(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /cs add <id> <interval-minutes> <command>");
            sender.sendMessage("§cExample: /cs add broadcast1 5 say Hello World!");
            return true;
        }

        String id = args[1];
        int intervalMinutes;
        
        try {
            intervalMinutes = Integer.parseInt(args[2]);
            if (intervalMinutes <= 0) {
                sender.sendMessage("§cInterval must be greater than 0!");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid interval! Must be a number.");
            return true;
        }

        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            if (i > 3) commandBuilder.append(" ");
            commandBuilder.append(args[i]);
        }
        String scheduledCommand = commandBuilder.toString();

        if (scheduleManager.addSchedule(id, scheduledCommand, intervalMinutes)) {
            sender.sendMessage("§aSuccessfully added scheduled command!");
            sender.sendMessage("§7ID: §f" + id);
            sender.sendMessage("§7Command: §f" + scheduledCommand);
            sender.sendMessage("§7Interval: §f" + intervalMinutes + " minutes");
        } else {
            sender.sendMessage("§cA schedule with ID '" + id + "' already exists!");
        }

        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /cs remove <id>");
            return true;
        }

        String id = args[1];

        if (scheduleManager.removeSchedule(id)) {
            sender.sendMessage("§aSuccessfully removed scheduled command: §f" + id);
        } else {
            sender.sendMessage("§cNo schedule found with ID: §f" + id);
        }

        return true;
    }

    private boolean handleList(CommandSender sender) {
        List<ScheduledCommand> schedules = scheduleManager.getAllSchedules();

        if (schedules.isEmpty()) {
            sender.sendMessage("§eNo scheduled commands configured.");
            return true;
        }

        sender.sendMessage("§6§l=== Scheduled Commands ===");
        for (ScheduledCommand cmd : schedules) {
            boolean active = scheduleManager.isScheduleActive(cmd.getId());
            String status = active ? "§a✓ Active" : "§c✗ Inactive";
            sender.sendMessage("§7[" + status + "§7] §f" + cmd.getId());
            sender.sendMessage("  §7Command: §f" + cmd.getCommand());
            sender.sendMessage("  §7Interval: §f" + cmd.getIntervalMinutes() + " minutes");
        }

        return true;
    }

    private boolean handleReload(CommandSender sender) {
        scheduleManager.stopAllSchedules();
        plugin.reloadConfig();
        scheduleManager.loadSchedules();
        scheduleManager.startAllSchedules();
        sender.sendMessage("§aConfiguration reloaded successfully!");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== CommandScheduler Help ===");
        sender.sendMessage("§e/cs add <id> <minutes> <command> §7- Add a scheduled command");
        sender.sendMessage("§e/cs remove <id> §7- Remove a scheduled command");
        sender.sendMessage("§e/cs list §7- List all scheduled commands");
        sender.sendMessage("§e/cs reload §7- Reload configuration");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("commandscheduler.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "reload").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return scheduleManager.getAllSchedules().stream()
                    .map(ScheduledCommand::getId)
                    .filter(id -> id.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}