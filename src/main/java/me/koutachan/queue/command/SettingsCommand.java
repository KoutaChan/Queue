package me.koutachan.queue.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import me.koutachan.queue.Queue;
import net.kyori.adventure.text.Component;

public class SettingsCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {

        String[] args = invocation.arguments();

        if (args.length > 1) {

            try {
                final long parseLong = Long.parseLong(args[1]);

                Queue server = Queue.INSTANCE;

                switch (args[0].toLowerCase()) {
                    case "queuetime": {
                        invocation.source().sendMessage(Component.text("§bキュータイムを" + parseLong + "に設定しました！"));

                        server.setQueueTime(parseLong);
                        server.send();

                        break;
                    }
                    case "sendtoplayers": {
                        invocation.source().sendMessage(Component.text("§b一回に送る人数を" + parseLong + "に設定しました！"));

                        server.setSendToPlayers(parseLong);

                        break;
                    }
                    case "sendtoplayerstime": {
                        invocation.source().sendMessage(Component.text("§b1プレイヤーを送るのにかかる時間を" + parseLong + "に設定しました！"));

                        server.setSendToPlayersTime(parseLong);

                        break;
                    }
                    default: {
                        sendHelp(invocation.source());
                    }
                }

            } catch (NumberFormatException ex) {
                sendHelp(invocation.source());
            }

        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("command.settings");
    }

    private void sendHelp(CommandSource source) {
    }
}
