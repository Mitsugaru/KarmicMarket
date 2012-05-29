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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import com.mitsugaru.KarmicMarket.KarmicMarket;
import com.mitsugaru.KarmicMarket.config.MarketConfig;
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
	
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		// TODO player quit event, since that doesn't throw an inventory close
		// event
		// just to double check
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
		if (block == null)
		{
			return;
		}
		// Chest logic
		if (block.getType().equals(Material.CHEST))
		{
			if (block.getRelative(BlockFace.UP).getType() == Material.WALL_SIGN)
			{
				Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
				if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
						KarmicMarket.TAG))
				{
					// Assume activated
					// TODO check if they have permission
					if (left)
					{
						// cycle
						cycleMarketPackage(sign, true);
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
						if (plugin.getPluginConfig().needsChest)
						{
							// Cycle
							cycleMarketPackage(sign, false);
						}
						else
						{
							showMarketInventory(event.getPlayer(), sign);
							event.setCancelled(true);
						}
					}
					else
					{
						// cycle
						cycleMarketPackage(sign, true);
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

	private void cycleMarketPackage(final Sign sign, final boolean backward)
	{
		// Grab market name
		final String marketName = ChatColor.stripColor(sign.getLine(0));
		// Grab package name
		final String packageName = ChatColor.stripColor(sign.getLine(3));
		// Grab config
		final MarketConfig marketConfig = plugin.getPluginConfig()
				.getMarketConfig(marketName);
		if (marketConfig == null)
		{
			return;
		}
		// Check if the market has no packages
		if (marketConfig.isEmpty())
		{
			sign.setLine(3, "");
			sign.update();
		}
		else
		{
			// Grab list
			final String[] packageList = marketConfig.getPackageSet().toArray(
					new String[0]);
			// Find current position. If the package isn't found, we'll default
			// to
			// the first in the list;
			int index = 0;
			for (int i = 0; i < packageList.length; i++)
			{
				if (packageList[i].equals(packageName))
				{
					index = i;
					break;
				}
			}
			// Increment or decrement
			if (backward)
			{
				index--;
				// If negative, loop to end of list
				if (index < 0)
				{
					index = packageList.length - 1;
				}
			}
			else
			{
				index++;
				// If past array length, loop to beginning
				if (index >= packageList.length)
				{
					index = 0;
				}
			}
			// Set and update sign
			sign.setLine(3, packageList[index]);
			sign.update();
		}
	}

	private void showMarketInventory(final Player player, final Sign sign)
	{
		// Grab market name
		final String marketName = ChatColor.stripColor(sign.getLine(0));
		// Grab package name
		final String packageName = ChatColor.stripColor(sign.getLine(3));
		// Ignore if there is no package defined
		if (packageName.equals(""))
		{
			// TODO nofity player
			return;
		}
		// Generate market info object
		final MarketInfo market = new MarketInfo(marketName, packageName);
		// See if the market inventory is already open
		Inventory inventory = null;
		if (openMarkets.containsKey(market))
		{
			// Show the existing inventory to that player
			inventory = openMarkets.get(market).getInventory();
		}
		else
		{
			// Generate new inventory to show
			final MarketInventoryHolder holder = new MarketInventoryHolder(
					market);
			holder.setInventory(plugin.getServer().createInventory(holder, 54,
					marketName + " - " + packageName));
			inventory = holder.getInventory();
		}
		final int id = plugin
				.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(plugin,
						new DelayInventoryOpen(player, inventory), 1);
		if (id == -1)
		{
			plugin.getLogger().warning("Could not open market inventory!");
			player.sendMessage(ChatColor.RED + KarmicMarket.TAG
					+ " Could not open market inventory!");
		}
	}

	private boolean signIsActivated(final Sign sign)
	{
		if (plugin.getPluginConfig().needsChest)
		{
			if (!sign.getBlock().getRelative(BlockFace.DOWN).getType()
					.equals(Material.CHEST))
			{
				return false;
			}
		}
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
			final int i = plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, new Runnable() {

						@Override
						public void run()
						{
							player.openInventory(inventory);
						}

					}, 1);
			if (i == -1)
			{
				player.sendMessage(ChatColor.RED + KarmicMarket.TAG
						+ " Could not open market inventory!");
			}
		}
	}
}
