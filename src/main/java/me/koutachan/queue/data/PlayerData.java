package me.koutachan.queue.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

//taken from buildingwordbattle
@Getter @Setter
public class PlayerData {

    private Player player;

    private long position;

    public PlayerData(Player player) {
        this.player = player;
    }

    public PlayerData(Player player, long position) {
        this.player = player;
        this.position = position;
    }
}