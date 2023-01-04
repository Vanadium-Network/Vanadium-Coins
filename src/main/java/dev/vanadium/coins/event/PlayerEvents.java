/*
Copyright (c) 2015 - 2023 Michael Steinmötzger
All rights are reserved for this project, unless otherwise
stated in a license file.
*/

package dev.vanadium.coins.event;

import dev.vanadium.coins.VanadiumCoins;
import dev.vanadium.coins.api.CoinPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerEvents implements Listener {


    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CoinPlayer coinPlayer = CoinPlayer.fromPlayer(player);

        player.setMetadata("coinObject", new FixedMetadataValue(VanadiumCoins.getInstance(), coinPlayer));
    }


}
