package me.koutachan.queue.command;


import me.koutachan.queue.Queue;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OpenCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        final boolean isOpened = !Queue.INSTANCE.isOpened();

        Queue.INSTANCE.setOpened(isOpened);

        sender.sendMessage((isOpened) ? "サーバーを新規参加を可能にしました" : "サーバーへの新規参加を禁止しました");

        return true;
    }
}
