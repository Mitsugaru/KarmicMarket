package com.mitsugaru.KarmicMarket.logic;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import com.mitsugaru.KarmicMarket.KarmicMarket;
import com.mitsugaru.KarmicMarket.config.RootConfig;

public class EconomyLogic
{
	private static KarmicMarket plugin;
	private static RootConfig rootConfig;
	private static Economy eco;
	private static boolean playerpoints, vault;
	private static Plugin pointsPlugin;

	public static void init(KarmicMarket km)
	{
		plugin = km;
		rootConfig = plugin.getPluginConfig();
	}

	public static boolean setupEconomy()
	{
		// Check vault
		RegisteredServiceProvider<Economy> economyProvider = plugin.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
		{
			eco = economyProvider.getProvider();
			vault = true;
		}
		// Check playerpoints
		Plugin playerPointsPlugin = plugin.getServer().getPluginManager()
				.getPlugin("PlayerPoints");
		if (playerPointsPlugin != null)
		{
			pointsPlugin = playerPointsPlugin;
			playerpoints = true;
		}
		// None fond
		if (!playerpoints && !vault)
		{
			return false;
		}
		return true;
	}

	public static boolean denyPay(Player player, double pay)
	{
		boolean paid = false;
		if (vault)
		{
			// Deny by player balance
			final double balance = eco.getBalance(player.getName());
			if (pay < 0.0)
			{
				// Only care about negatives. Need to change to positive for
				// comparison.
				pay *= -1;
				if (pay > balance)
				{
					paid = true;
				}
			}
		}
		if (playerpoints)
		{
			final int playerPoints = pointsPlugin.getConfig().getInt(
					"Points." + player.getName());
			if (pay < 0.0)
			{
				pay *= 1;
				if (pay > playerPoints)
				{
					paid = true;
				}
			}
		}
		return paid;
	}

	public static boolean pay(Player player, double amount)
	{
		boolean paid = false;
		if (vault)
		{
			EconomyResponse response = null;
			if (amount > 0.0)
			{
				response = eco.depositPlayer(player.getName(), amount);
			}
			else if (amount < 0.0)
			{
				response = eco.withdrawPlayer(player.getName(), (amount * -1));
			}
			if (response != null)
			{
				switch (response.type)
				{
					case FAILURE:
					{
						// TODO notify player
						if (rootConfig.debugEconomy)
						{
							plugin.getLogger().severe(
									"Eco Failure: " + response.errorMessage);
						}
						break;
					}
					case NOT_IMPLEMENTED:
					{
						// TODO notify player
						if (rootConfig.debugEconomy)
						{
							plugin.getLogger().severe(
									"Eco not implemented: "
											+ response.errorMessage);
						}
						break;
					}
					case SUCCESS:
					{
						if (rootConfig.debugEconomy)
						{
							plugin.getLogger().info(
									"Eco success for player '"
											+ player.getName()
											+ "' of amount: " + amount);
						}
						paid = true;
						// TODO notify player, if config allows
					}
					default:
						break;
				}
			}
		}
		if (playerpoints)
		{
			int points = (int) amount;
			if (points == 0)
			{
				return true;
			}
			else
			{
				plugin.getServer().dispatchCommand(
						plugin.getServer().getConsoleSender(),
						"points give " + player.getName() + " " + points);
				paid = true;
				// TODO notify player, if config allows
			}
		}
		return paid;
	}
}
