package com.commandscheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduledCommand {
    private String id;
    private String command;
    private int intervalMinutes;
}