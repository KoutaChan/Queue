package me.koutachan.queue.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.koutachan.queue.Queue;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.Opt;

import java.util.Optional;

public class SendPlayerCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length > 1) {
            Optional<Player> player = Queue.INSTANCE.getServer().getPlayer(args[0]);

            Optional<RegisteredServer> registeredServer = Queue.INSTANCE.getServer().getServer(args[1]);

            if (registeredServer.isPresent() && player.isPresent()) {

                player.get().createConnectionRequest(registeredServer.get()).fireAndForget();

            } else {
                invocation.source().sendMessage(Component.text("§c使い方： /send <player> <server>"));
            }
        } else {
            invocation.source().sendMessage(Component.text("§c使い方： /send <player> <server>"));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("command.send");
    }
}
