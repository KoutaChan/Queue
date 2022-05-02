package me.koutachan.queue.command;

import me.koutachan.queue.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SettingsCommand implements CommandExecutor {


    private void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "/settings sendtoplayers <long> - 1回に送る人数");
        commandSender.sendMessage(ChatColor.AQUA + "/settings queuetime <long> - キュータイム");
        commandSender.sendMessage(ChatColor.AQUA + "/settings sendtoplayerstime <long> - 1プレイヤーを送る時間");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {

            try {
                final long parseLong = Long.parseLong(args[1]);

                Queue server = Queue.INSTANCE;

                switch (args[0].toLowerCase()) {
                    case "queuetime": {
                        sender.sendMessage(ChatColor.AQUA + "キュータイムを" + parseLong + "に設定しました！");

                        server.setQueueTime(parseLong);
                        server.send();

                        break;
                    }
                    case "sendtoplayers": {
                        sender.sendMessage(ChatColor.AQUA + "一回に送る人数を" + parseLong + "に設定しました！");

                        server.setSendToPlayers(parseLong);

                        break;
                    }
                    case "sendtoplayerstime": {
                        sender.sendMessage(ChatColor.AQUA + "1プレイヤーを送るのにかかる時間を" + parseLong + "に設定しました！");

                        server.setSendToPlayersTime(parseLong);

                        break;
                    }
                }

            } catch (NumberFormatException ignore) {

            }

        }

        sendHelp(sender);

        return true;
    }
}
