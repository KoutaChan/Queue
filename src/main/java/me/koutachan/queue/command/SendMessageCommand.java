package me.koutachan.queue.command;

import me.koutachan.queue.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SendMessageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final boolean settings = !Queue.INSTANCE.isSendToConnectingMessage();

        Queue.INSTANCE.setSendToConnectingMessage(settings);

        sender.sendMessage(ChatColor.AQUA + "接続させるときのメッセージを " + (settings ? "ON" : "OFF") + " にしました");

        return true;
    }
}
