package me.koutachan.queue.command;

import me.koutachan.queue.Queue;
import me.koutachan.queue.util.ServerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendPlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 1) {
            Player player = Queue.INSTANCE.getServer().getPlayer(args[0]);

            if (player != null) {
                ServerUtils.sendServer(player, args[0]);
            } else {
                sender.sendMessage(ChatColor.RED + "使い方： /send <player> <server>");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "使い方： /send <player> <server>");
        }

        return true;
    }
}
