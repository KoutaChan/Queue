package me.koutachan.queue.command;

import com.velocitypowered.api.command.SimpleCommand;
import me.koutachan.queue.Queue;
import net.kyori.adventure.text.Component;

public class OpenCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {

        final boolean isOpened = !Queue.INSTANCE.isOpened();

        Queue.INSTANCE.setOpened(isOpened);

        invocation.source().sendMessage(Component.text((isOpened) ? "サーバーを新規参加を可能にしました" : "サーバーへの新規参加を禁止しました"));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("command.open");
    }
}
