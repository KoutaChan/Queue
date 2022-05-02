package me.koutachan.queue;

import com.amihaiemil.eoyaml.YamlMapping;
import lombok.Getter;
import lombok.Setter;
import me.koutachan.queue.command.*;
import me.koutachan.queue.data.PlayerData;
import me.koutachan.queue.data.PlayerDataUtil;
import me.koutachan.queue.util.ConfigManager;
import me.koutachan.queue.util.ServerUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter @Setter
public class Queue extends JavaPlugin {
    public static Queue INSTANCE;

    private long queueTime, onlinePlayers, position, sendToPlayers, sendToPlayersTime;

    private String sendServer;

    private BukkitTask task;

    private YamlMapping configManager = ConfigManager.of("config.yml", Paths.get("", "plugins", "queue").toAbsolutePath());
    private boolean isOpened, sendToConnectingMessage, showActionBar;

    @Override
    public void onEnable() {
        INSTANCE = this;

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getCommand("open").setExecutor(new OpenCommand());
        getCommand("send").setExecutor(new SendPlayerCommand());
        getCommand("sendmessage").setExecutor(new SendMessageCommand());
        getCommand("settings").setExecutor(new SettingsCommand());
        getCommand("actionbar").setExecutor(new ShowActionBarCommand());

        sendServer = configManager.string("sendServer");

        queueTime = configManager.longNumber("queueTime");
        sendToPlayers = configManager.longNumber("sendToPlayers");
        sendToPlayersTime = configManager.longNumber("sendToPlayersTime");
        sendToConnectingMessage = Boolean.parseBoolean(configManager.string("sendToConnectingMessage"));
        showActionBar = Boolean.parseBoolean(configManager.string("showActionBar"));

        timer();

        send();
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void timer() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            List<Player> players = getPlayerList();

            final long count = position;

            for (Player player : players) {
                final boolean register = PlayerDataUtil.playerDataHashMap.containsKey(player.getUniqueId());

                if (!register) {
                    position = this.position + 1;

                    if (player.hasPermission("queue.bypass")) PlayerDataUtil.createPlayerData(player, 0);
                    else PlayerDataUtil.createPlayerData(player, position);
                }
            }

            if (count != position || onlinePlayers != players.size()) {
                recalculate();
            }

            PlayerDataUtil.getAll().forEach(data -> data.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent((ChatColor.YELLOW + "あなたの順番: " + data.getPosition() + "番"))));

            this.onlinePlayers = players.size();
        }, 0, 20);
    }

    public List<Player> getPlayerList() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public void recalculate() {
        List<PlayerData> playerData = PlayerDataUtil.sort().collect(Collectors.toList());

        if (playerData.size() > 0) {

            long position = 0;

            for (PlayerData data : playerData) {
                final boolean online = data.getPlayer().isOnline();


                if (online) {
                    if (data.getPosition() != 0) {
                        final long allowed = data.getPosition() - 1;

                        if (position != allowed) {
                            data.setPosition(position == 0 || position == data.getPosition() ? position + 1 : position);
                        }
                    } else {
                        final long allowed = position + 1;

                        data.setPosition(allowed);
                    }
                    position = data.getPosition();
                } else {
                    PlayerDataUtil.removePlayerData(data.getPlayer());
                }
            }

            this.position = position;
        }
    }

    public void send() {
        stop();

        task = Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (isOpened) {

                List<PlayerData> playerData = new ArrayList<>(PlayerDataUtil.getAll());

                for (int i = 0; i < sendToPlayers; i++) {

                    if (i >= playerData.size()) break;

                    final int id = i;

                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        PlayerData data = playerData.get(id);

                        if (data.getPlayer().isOnline()) {
                            ServerUtils.sendServer(data.getPlayer(), sendServer);

                            if (sendToConnectingMessage) data.getPlayer().sendMessage(ChatColor.AQUA + "[!] あなたを" + sendServer + "に送っています");
                        }
                    },sendToPlayersTime * i);
                }
            }

        }, 0, queueTime);

    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}