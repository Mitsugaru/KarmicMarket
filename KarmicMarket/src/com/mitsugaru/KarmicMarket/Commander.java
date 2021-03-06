package com.mitsugaru.KarmicMarket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mitsugaru.KarmicMarket.config.RootConfig;
import com.mitsugaru.KarmicMarket.permissions.PermissionHandler;
import com.mitsugaru.KarmicMarket.permissions.PermissionNode;

public class Commander implements CommandExecutor
{
	// Class variables
	private final KarmicMarket plugin;
	private final RootConfig config;
	private final static String bar = "======================";
	private long time = 0;

	public Commander(KarmicMarket plugin)
	{
		this.plugin = plugin;
		this.config = plugin.getRootConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args)
	{
		if (config.debugTime)
		{
			time = System.nanoTime();
		}
		// See if any arguments were given
		if (args.length == 0)
		{
			// Check if they have "karma" permission
			this.displayHelp(sender);
		}
		else
		{
			final String com = args[0].toLowerCase();
			if (com.equals("version") || com.equals("ver"))
			{
				// Version and author
				return showVersion(sender, args);
			}
			else if (com.equals("?") || com.equals("help"))
			{
				return displayHelp(sender);
			}
			else if (com.equals("reload"))
			{
				if (PermissionHandler.has(sender, PermissionNode.ADMIN_RELOAD))
				{
					config.reloadConfig();
				}
				else
				{
					sender.sendMessage(ChatColor.RED + KarmicMarket.TAG
							+ " Lack permission: "
							+ PermissionNode.ADMIN_RELOAD.getNode());
				}
			}
			else if (com.equals("create"))
			{
				return createMarketCommand(sender, args);
			}
			else
			{
				sender.sendMessage(ChatColor.RED + KarmicMarket.TAG
						+ " Unknown command '" + ChatColor.AQUA + com
						+ ChatColor.RED + "'");
			}
		}
		if (config.debugTime)
		{
			debugTime(sender, time);
		}
		return true;
	}

	private boolean createMarketCommand(CommandSender sender, String[] args)
	{
		if (PermissionHandler.has(sender, PermissionNode.MARKET_CREATE))
		{
			try
			{
				final String marketName = args[1];
				if (!marketName.matches(KarmicMarket.MARKET_NAME_REGEX))
				{
					sender.sendMessage(ChatColor.RED + KarmicMarket.TAG
							+ " Market name must be alphanumeric.");
				}
				else if (marketName.length() > 15)
				{
					// Restrict length to sign character limit
					sender.sendMessage(ChatColor.RED + KarmicMarket.TAG
							+ " Market name must be 15 characters or less.");
				}
				else
				{
					if (config.marketExists(marketName))
					{
						sender.sendMessage(ChatColor.RED + KarmicMarket.TAG
								+ " Market '" + ChatColor.GOLD + marketName
								+ ChatColor.RED + "' already exists.");
						return true;
					}
					if (config.createMarket(marketName))
					{
						sender.sendMessage(ChatColor.GREEN + KarmicMarket.TAG
								+ " Market '" + ChatColor.GOLD + marketName
								+ ChatColor.GREEN + "' created.");
					}
					else
					{
						sender.sendMessage(ChatColor.RED + KarmicMarket.TAG
								+ " Market '" + ChatColor.GOLD + marketName
								+ ChatColor.RED + "' could not be created!");
					}
				}
			}
			catch (ArrayIndexOutOfBoundsException aioob)
			{
				sender.sendMessage(ChatColor.RED + KarmicMarket.TAG
						+ " Market name not given.");
			}
		}
		else
		{
			sender.sendMessage(ChatColor.RED + KarmicMarket.TAG
					+ " Lack Permission: "
					+ PermissionNode.MARKET_CREATE.getNode());
		}
		return true;
	}

	private boolean showVersion(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.BLUE + bar + "=====");
		sender.sendMessage(ChatColor.GREEN + "KarmicMarket v"
				+ plugin.getDescription().getVersion());
		sender.sendMessage(ChatColor.GREEN + "Coded by Mitsugaru");
		sender.sendMessage(ChatColor.BLUE + "===========" + ChatColor.GRAY
				+ "Config" + ChatColor.BLUE + "===========");
		if (config.debugTime)
			sender.sendMessage(ChatColor.GRAY + "Debug time: "
					+ config.debugTime);
		return true;
	}

	/**
	 * Show the help menu, with commands and description
	 * 
	 * @param sender
	 *            to display to
	 */
	private boolean displayHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.WHITE + "==========" + ChatColor.GOLD
				+ "KarmicMarket" + ChatColor.WHITE + "==========");
		return true;
	}

	private void debugTime(CommandSender sender, long time)
	{
		time = System.nanoTime() - time;
		sender.sendMessage("[Debug]" + KarmicMarket.TAG + "Process time: "
				+ time);
	}

}
