package com.mitsugaru.KarmicMarket.events;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.mitsugaru.KarmicMarket.KarmicMarket;
import com.mitsugaru.KarmicMarket.permissions.PermissionHandler;
import com.mitsugaru.KarmicMarket.permissions.PermissionNode;
import com.splatbang.betterchest.BetterChest;

public class KMBlockListener implements Listener
{
	private KarmicMarket plugin;
	private static final BlockFace[] nav = { BlockFace.NORTH, BlockFace.SOUTH,
			BlockFace.EAST, BlockFace.WEST };

	public KMBlockListener(KarmicMarket plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(final SignChangeEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (!ChatColor.stripColor(event.getLine(1)).equalsIgnoreCase(
				KarmicMarket.TAG))
		{
			return;
		}
		if (!PermissionHandler.has(event.getPlayer(), PermissionNode.SIGN))
		{
			event.getPlayer().sendMessage(
					ChatColor.RED + KarmicMarket.TAG + " Lack permission: "
							+ PermissionNode.SIGN.getNode());
			event.setCancelled(true);
			return;
		}
		String marketName = null;
		if (!ChatColor.stripColor(event.getLine(0)).equalsIgnoreCase(""))
		{
			marketName = event.getLine(0);
		}
		else
		{
			// Cannot be empty
			event.getPlayer().sendMessage(
					ChatColor.YELLOW + KarmicMarket.TAG
							+ " No market specified!");
			event.setCancelled(true);
			return;
		}
		// validate
		if (!plugin.getRootConfig().marketExists(marketName))
		{
			event.getPlayer().sendMessage(
					ChatColor.RED + KarmicMarket.TAG + " Market '"
							+ ChatColor.WHITE + marketName + ChatColor.RED
							+ "' does not exist.");
			event.setCancelled(true);
			return;
		}
		// get the market's first package if it has any
		final Set<String> packages = plugin.getRootConfig()
				.getMarketConfig(marketName).getPackageSet();
		String firstPackage = "";
		if (!packages.isEmpty())
		{
			firstPackage = packages.toArray(new String[0])[0];
		}
		// Reformat sign
		event.setLine(0, marketName);
		event.setLine(1, KarmicMarket.TAG);
		event.setLine(2, "Package:");
		event.setLine(3, firstPackage);
		// check if there's a chest, if enabled in config
		if (plugin.getRootConfig().needsChest)
		{
			// Thanks to Wolvereness for the following code
			if (event.getBlock().getRelative(BlockFace.DOWN).getType()
					.equals(Material.CHEST))
			{
				event.getPlayer().sendMessage(
						ChatColor.GREEN
								+ KarmicMarket.TAG
								+ " Market linked to "
								+ ChatColor.GRAY
								+ ChatColor.stripColor(marketName)
										.toLowerCase());
			}
			else
			{

				event.getPlayer().sendMessage(
						ChatColor.YELLOW + KarmicMarket.TAG
								+ " No chest found!");
			}
		}
		else
		{
			event.getPlayer().sendMessage(
					ChatColor.GREEN + KarmicMarket.TAG + " Market linked to "
							+ ChatColor.GRAY
							+ ChatColor.stripColor(marketName).toLowerCase());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(final BlockPlaceEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final Material material = event.getBlock().getType();
		if (material.equals(Material.SIGN)
				|| material.equals(Material.WALL_SIGN)
				|| material.equals(Material.SIGN_POST))
		{
			boolean has = false;
			for (BlockFace face : nav)
			{
				if (event.getBlock().getRelative(face).getType()
						.equals(Material.WALL_SIGN))
				{
					Sign sign = (Sign) event.getBlock().getRelative(face)
							.getState();
					if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
							KarmicMarket.TAG))
					{
						has = true;
					}
				}
			}
			if (has)
			{
				event.getPlayer().sendMessage(
						ChatColor.RED + KarmicMarket.TAG
								+ " Cannot have a sign next to a link sign!");
				event.setCancelled(true);
			}
		}
		else if (material.equals(Material.CHEST))
		{
			final Block block = event.getBlock();
			final BetterChest chest = new BetterChest((Chest) block.getState());
			boolean has = false;
			Sign sign = null;
			if (block.getRelative(BlockFace.UP).getType() == Material.WALL_SIGN)
			{

				sign = (Sign) block.getRelative(BlockFace.UP).getState();
				if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
						KarmicMarket.TAG))
				{
					has = true;
				}
			}
			else if (chest.isDoubleChest())
			{
				if (chest.attachedBlock().getRelative(BlockFace.UP).getType()
						.equals(Material.WALL_SIGN))
				{
					sign = (Sign) chest.attachedBlock()
							.getRelative(BlockFace.UP).getState();
					if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
							KarmicMarket.TAG))
					{
						// Sign already exists
						has = true;
					}
				}
			}
			if (has)
			{
				// get the market's first package if it has any
				final Set<String> packages = plugin.getRootConfig()
						.getMarketConfig(ChatColor.stripColor(sign.getLine(0)))
						.getPackageSet();
				String firstPackage = "";
				if (!packages.isEmpty())
				{
					firstPackage = packages.toArray(new String[0])[0];
				}
				// Reformat sign
				sign.setLine(1, KarmicMarket.TAG);
				sign.setLine(2, "Package:");
				sign.setLine(3, firstPackage);
				sign.update();
				event.getPlayer().sendMessage(
						ChatColor.GREEN
								+ KarmicMarket.TAG
								+ " Chest linked to "
								+ ChatColor.GRAY
								+ ChatColor.stripColor(sign.getLine(0))
										.toLowerCase());

			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(final BlockBreakEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final Material material = event.getBlock().getType();
		if (material.equals(Material.CHEST))
		{
			final Block block = event.getBlock();
			if (block.getRelative(BlockFace.UP).getType()
					.equals(Material.WALL_SIGN))
			{
				Sign sign = (Sign) block.getRelative(BlockFace.UP).getState();
				if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
						KarmicMarket.TAG))
				{
					event.getPlayer().sendMessage(
							ChatColor.YELLOW
									+ KarmicMarket.TAG
									+ " Chest unlinked from "
									+ ChatColor.GRAY
									+ ChatColor.stripColor(sign.getLine(0))
											.toLowerCase());

				}
			}
		}
		else if (material.equals(Material.WALL_SIGN))
		{
			final Sign sign = (Sign) event.getBlock().getState();
			if (ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase(
					KarmicMarket.TAG))
			{
				// Is our sign
				if (PermissionHandler.has(event.getPlayer(),
						PermissionNode.SIGN.getNode()))
				{
					event.getPlayer().sendMessage(
							ChatColor.YELLOW
									+ KarmicMarket.TAG
									+ " Chest unlinked from "
									+ ChatColor.GRAY
									+ ChatColor.stripColor(sign.getLine(0))
											.toLowerCase());
				}
				else
				{
					event.getPlayer().sendMessage(
							ChatColor.RED + KarmicMarket.TAG
									+ " Lack permission: "
									+ PermissionNode.SIGN.getNode());
					event.setCancelled(true);
				}
			}
		}
	}
}
