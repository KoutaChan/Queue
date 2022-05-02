package me.koutachan.queue.data;

import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.Setter;

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