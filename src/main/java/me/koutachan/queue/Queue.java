package me.koutachan.queue;

import com.amihaiemil.eoyaml.YamlMapping;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;
import me.koutachan.queue.command.OpenCommand;
import me.koutachan.queue.command.SendPlayerCommand;
import me.koutachan.queue.data.PlayerData;
import me.koutachan.queue.data.PlayerDataUtil;
import me.koutachan.queue.util.ConfigManager;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Plugin(
        id = "queue",
        name = "Queue",
        version = "1.0"
)

@Getter @Setter
public class Queue {

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer server;

    public static Queue INSTANCE;

    private RegisteredServer registeredServer;

    private List<Optional<RegisteredServer>> registeredServers = new ArrayList<>();

    private long queueTime, onlinePlayers, position, sendToPlayers, sendToPlayersTime;

    private RegisteredServer sendServer;

    private ScheduledTask task;

    private YamlMapping configManager = ConfigManager.of("config.yml", Paths.get("", "plugins", "queue").toAbsolutePath());
    private boolean isOpened;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        INSTANCE = this;

        CommandManager commandManager = server.getCommandManager();

        commandManager.register("open", new OpenCommand());
        commandManager.register("send", new SendPlayerCommand());

        Optional<RegisteredServer> sendServer = server.getServer(configManager.string("sendServer"));

        sendServer.ifPresent(value -> this.sendServer = value);

        queueTime = configManager.longNumber("queueTime");
        sendToPlayers = configManager.longNumber("sendToPlayers");
        sendToPlayersTime = configManager.longNumber("sendToPlayersTime");

        timer();

        send();
    }

    public void timer() {
        configManager.literalBlockScalar("targetServer").forEach(string -> registeredServers.add(server.getServer(string)));

        server.getScheduler().buildTask(this, () -> {
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

            PlayerDataUtil.getAll().forEach(data -> data.getPlayer().sendActionBar(Component.text("§eあなたの順番: " + data.getPosition() + "番")));

            this.onlinePlayers = players.size();
        }).repeat(1, TimeUnit.SECONDS).schedule();
    }

    public List<Player> getPlayerList() {
        final List<Player> players = new ArrayList<>();

        for (Optional<RegisteredServer> registeredServer : registeredServers) {

            if (registeredServer.isPresent()) {
                RegisteredServer server = registeredServer.get();

                players.addAll(server.getPlayersConnected());
            }
        }

        return players;
    }

    public boolean matched(final String serverName) {

        for (Optional<RegisteredServer> registeredServer : registeredServers) {

            if (registeredServer.isPresent()) {
                RegisteredServer server = registeredServer.get();

                return server.getServerInfo().getName().equals(serverName);
            }
        }

        return false;
    }

    public void recalculate() {
        List<PlayerData> playerData = PlayerDataUtil.sort().collect(Collectors.toList());

        if (playerData.size() > 0) {

            long position = 0;

            for (PlayerData data : playerData) {
                final boolean online = data.getPlayer().isActive();

                //オンラインなのにserverConnectionがないのはおかしい... だけど確認..
                Optional<ServerConnection> serverConnection = data.getPlayer().getCurrentServer();

                if (online && serverConnection.isPresent() && matched(serverConnection.get().getServerInfo().getName())) {
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

        task = server.getScheduler().buildTask(this, () -> {

            if (isOpened) {

                List<PlayerData> playerData = new ArrayList<>(PlayerDataUtil.getAll());

                for (AtomicInteger atomicInteger = new AtomicInteger(); atomicInteger.get() < sendToPlayers; atomicInteger.addAndGet(1)) {
                    server.getScheduler().buildTask(this, () -> {
                        PlayerData data = playerData.get(atomicInteger.get());

                        if (data.getPlayer().isActive() && data.getPlayer().getCurrentServer().isPresent() && matched(data.getPlayer().getCurrentServer().get().getServerInfo().getName())) {
                            data.getPlayer().createConnectionRequest(sendServer).fireAndForget();

                            data.getPlayer().sendMessage(Component.text("§b[!] あなたを" + sendServer.getServerInfo().getName() + "に送っています"));
                        }
                    }).delay(sendToPlayersTime * atomicInteger.get(), TimeUnit.MILLISECONDS);
                }
            }

        }).repeat(queueTime, TimeUnit.MILLISECONDS).schedule();

    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}