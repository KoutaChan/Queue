package me.koutachan.queue.data;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

@Getter
@UtilityClass
public class PlayerDataUtil {
    public static final Map<UUID, PlayerData> playerDataHashMap = new HashMap<>();

    public PlayerData getPlayerData(Player player) {
        return playerDataHashMap.get(player.getUniqueId());
    }

    public PlayerData getPlayerData(UUID UUID) {
        return playerDataHashMap.get(UUID);
    }

    public void createPlayerData(Player player) {
        playerDataHashMap.put(player.getUniqueId(), new PlayerData(player));
    }

    public void createPlayerData(Player player, long position) {
        playerDataHashMap.put(player.getUniqueId(), new PlayerData(player, position));
    }

    public PlayerData removePlayerData(Player player) {
        return playerDataHashMap.remove(player.getUniqueId());
    }

    public Collection<PlayerData> getAll() {
        return playerDataHashMap.values();
    }

    public Stream<PlayerData> sort() {
        return playerDataHashMap.values().stream().sorted(Comparator.comparingLong(PlayerData::getPosition));
    }
}
