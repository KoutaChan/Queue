package me.koutachan.queue.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import me.koutachan.queue.Queue;
import org.bukkit.entity.Player;

@UtilityClass
public class ServerUtils {

    public void sendServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(Queue.INSTANCE, "BungeeCord", out.toByteArray());
    }

}
