package com.mitsugaru.KarmicMarket.logic;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mitsugaru.KarmicMarket.KarmicMarket;
import com.mitsugaru.KarmicMarket.config.RootConfig;
import com.mitsugaru.KarmicMarket.inventory.MarketInfo;

public class MarketLogic {
    private static KarmicMarket plugin;
    private static RootConfig rootConfig;

    public static void init(KarmicMarket km) {
        plugin = km;
        rootConfig = plugin.getRootConfig();
    }

    public static boolean buyItem(Player player, ItemStack product,
            MarketInfo market) {
        double price = -1;
        try {
            price *= market.getItems().get(product).getAmount();
        } catch (NullPointerException npe) {
            player.sendMessage(ChatColor.RED + KarmicMarket.TAG
                    + " Something went wrong...");
            return false;
        }
        // check if they can pay
        if (!EconomyLogic.denyPay(player, price)) {
            return true;
        }
        return false;
    }
}
