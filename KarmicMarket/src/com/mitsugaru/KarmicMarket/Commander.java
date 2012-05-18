package com.mitsugaru.KarmicMarket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mitsugaru.KarmicMarket.config.RootConfig;
import com.mitsugaru.KarmicMarket.permissions.PermCheck;
import com.mitsugaru.KarmicMarket.permissions.PermissionNode;

public class Commander implements CommandExecutor
{
	// Class variables
	private final KarmicMarket plugin;
	private final RootConfig config;
	private final PermCheck perm;
	private final static String bar = "======================";
	private long time = 0;

	public Commander(KarmicMarket plugin)
	{
		this.plugin = plugin;
		this.config = plugin.getPluginConfig();
		this.perm = plugin.getPermissionsHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel,
			String[] args)
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
				this.showVersion(sender, args);
			}
			else if (com.equals("?") || com.equals("help"))
			{
				this.displayHelp(sender);
			}
			else if(com.equals("reload"))
			{
				if(perm.checkPermission(sender, PermissionNode.ADMIN))
				{
					config.reloadConfig();
				}
			}
		}
		if (config.debugTime)
		{
			debugTime(sender, time);
		}
		return false;
	}
	
	private void showVersion(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.BLUE + bar + "=====");
		sender.sendMessage(ChatColor.GREEN + "KarmicMarket v"
				+ plugin.getDescription().getVersion());
		sender.sendMessage(ChatColor.GREEN + "Coded by Mitsugaru");
		sender.sendMessage(ChatColor.BLUE + "===========" + ChatColor.GRAY
				+ "Config" + ChatColor.BLUE + "===========");
		if(config.debugTime)
			sender.sendMessage(ChatColor.GRAY + "Debug time: " + config.debugTime);
	}
	
	
	/**
	 * Show the help menu, with commands and description
	 * 
	 * @param sender
	 *            to display to
	 */
	private void displayHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.WHITE + "==========" + ChatColor.GOLD
				+ "KarmicMarket" + ChatColor.WHITE + "==========");
	}
	
	private void debugTime(CommandSender sender, long time)
	{
		time = System.nanoTime() - time;
		sender.sendMessage("[Debug]" + KarmicMarket.TAG + "Process time: "
				+ time);
	}

}
