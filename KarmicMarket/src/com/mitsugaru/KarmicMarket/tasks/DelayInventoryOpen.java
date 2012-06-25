package com.mitsugaru.KarmicMarket.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.mitsugaru.KarmicMarket.KarmicMarket;

public class DelayInventoryOpen implements Runnable
{
	private final KarmicMarket plugin;
	private final Player player;
	private final Inventory inventory;

	public DelayInventoryOpen(KarmicMarket plugin, Player player, Inventory inventory)
	{
		this.plugin = plugin;
		this.player = player;
		this.inventory = inventory;
	}

	@Override
	public void run()
	{
		player.closeInventory();
		final int i = plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(plugin, new Runnable() {

					@Override
					public void run()
					{
						player.openInventory(inventory);
					}

				}, 3);
		if (i == -1)
		{
			player.sendMessage(ChatColor.RED + KarmicMarket.TAG
					+ " Could not open market inventory!");
		}
	}
}