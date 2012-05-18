package com.mitsugaru.KarmicMarket.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.mitsugaru.KarmicMarket.KarmicMarket;
import com.mitsugaru.KarmicMarket.inventory.MarketInfo;
import com.mitsugaru.KarmicMarket.inventory.MarketInventoryHolder;

public class KMPlayerListener implements Listener
{
	private KarmicMarket plugin;
	public static Map<MarketInfo, MarketInventoryHolder> openMarkets = new HashMap<MarketInfo, MarketInventoryHolder>();

	public KMPlayerListener(KarmicMarket plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		// Grab type of click
		boolean right = false, left = false;
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			right = true;
		}
		else if (event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			left = true;
		}
		// Grab type of block
		final Block block = event.getClickedBlock();
		if (block != null)
		{
			// Chest logic
			if (block.getType().equals(Material.CHEST))
			{
				if (block.getRelative(BlockFace.UP).getType() == Material.WALL_SIGN)
				{
					Sign sign = (Sign) block.getRelative(BlockFace.UP)
							.getState();
					if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
							KarmicMarket.TAG))
					{
						// Assume activated
						// TODO check if they have permission
						if (left)
						{
							// TODO cycle
						}
						else if (right)
						{
							// Stop them from opening the chest since we have
							// our
							// own inventory to show
							event.setCancelled(true);
							// Show inventory
							showMarketInventory(event.getPlayer(), sign);
						}
					}
				}
			}
			else if (block.getType().equals(Material.WALL_SIGN)
					|| block.getType().equals(Material.SIGN)
					|| block.getType().equals(Material.SIGN_POST))
			{
				final Sign sign = (Sign) block.getState();
				if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
						KarmicMarket.TAG))
				{
					// Check if its activated via the chat color
					if (signIsActivated(sign))
					{
						// TODO check if they have permission
						// Show inventory IF chests are disabled
						if (right)
						{
							if (!plugin.getPluginConfig().needsChest)
							{
								showMarketInventory(event.getPlayer(), sign);
							}
							else
							{
								// TODO cycle
							}

						}
					}
					else
					{
						// IGNORE
						event.setCancelled(true);
					}
				}
			}
		}
	}

	private void showMarketInventory(final Player player, final Sign sign)
	{
		// Grab market name
		final String marketName = ChatColor.stripColor(sign.getLine(0));
		// Grab package name
		final String packageName = ChatColor.stripColor(sign.getLine(3));
		// Generate market info object
		MarketInfo market = new MarketInfo(marketName, packageName);
		// See if the market inventory is already open
		if (openMarkets.containsKey(market))
		{
			// Show the existing inventory to that player
			final int id = plugin
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(
							plugin,
							new DelayInventoryOpen(player, openMarkets.get(
									market).getInventory()), 1);
			if (id == -1)
			{
				plugin.getLogger().warning("Could not open market inventory!");
				player.sendMessage(ChatColor.RED + KarmicMarket.TAG
						+ " Could not open market inventory!");
			}
		}
		else
		{
			// Generate new inventory to show
			final MarketInventoryHolder holder = new MarketInventoryHolder(
					market);
			holder.setInventory(plugin.getServer().createInventory(holder, 54,
					marketName + " - " + packageName));
			final int id = plugin
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(
							plugin,
							new DelayInventoryOpen(player, holder
									.getInventory()), 1);
			if (id == -1)
			{
				plugin.getLogger().warning("Could not open market inventory!");
				player.sendMessage(ChatColor.RED + KarmicMarket.TAG
						+ " Could not open market inventory!");
			}
		}
	}

	private boolean signIsActivated(final Sign sign)
	{
		final String tag = sign.getLine(1);
		// TODO replace with chest checking, if necessary. This is because the
		// extra chat color doesn't work with the long tag
		/*
		 * if (tag.contains(ChatColor.DARK_RED + "")) { return false; } else if
		 * (tag.contains(ChatColor.AQUA + "")) { return true; }
		 */
		return true;
	}

	private class DelayInventoryOpen implements Runnable
	{
		private final Player player;
		private final Inventory inventory;

		public DelayInventoryOpen(Player player, Inventory inventory)
		{
			this.player = player;
			this.inventory = inventory;
		}

		@Override
		public void run()
		{
			player.closeInventory();
			player.openInventory(inventory);
		}
	}
}
