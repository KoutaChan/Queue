package me.koutachan.queue.command;

import me.koutachan.queue.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShowActionBarCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final boolean settings = !Queue.INSTANCE.isShowActionBar();

        Queue.INSTANCE.setSendToConnectingMessage(settings);

        sender.sendMessage(ChatColor.AQUA + "アクションバーを " + (settings ? "ON" : "OFF") + " にしました");

        return true;
    }
}
