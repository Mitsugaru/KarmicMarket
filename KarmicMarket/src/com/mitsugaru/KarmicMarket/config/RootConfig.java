package com.mitsugaru.KarmicMarket.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import com.mitsugaru.KarmicMarket.KarmicMarket;

public class RootConfig
{
	private KarmicMarket plugin;
	private final Map<String, PackageConfig> packages = new HashMap<String, PackageConfig>();
	private final Map<String, MarketConfig> markets = new HashMap<String, MarketConfig>();
	public boolean debugTime, debugEconomy, needsChest;

	public RootConfig(KarmicMarket plugin)
	{
		this.plugin = plugin;
		// Grab config
		final ConfigurationSection config = plugin.getConfig();
		// LinkedHashmap of defaults
		final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
		// TODO defaults
		defaults.put("needsChest", true);
		defaults.put("debug.economy", false);
		defaults.put("debug.time", false);
		defaults.put("version", plugin.getDescription().getVersion());
		// Insert defaults into config file if they're not present
		for (final Entry<String, Object> e : defaults.entrySet())
		{
			if (!config.contains(e.getKey()))
			{
				config.set(e.getKey(), e.getValue());
			}
		}
		// Save config
		plugin.saveConfig();
		// Load settings
		loadSettings(config);
		// load packages
		loadPackages();
		loadMarkets();
	}

	private void loadPackages()
	{
		try
		{
			final File directory = new File(plugin.getDataFolder()
					.getAbsolutePath() + "/packages");
			if (!directory.exists())
			{
				directory.mkdir();
			}
			// Grab all files
			for (final File file : directory.listFiles())
			{
				if (file.isFile())
				{
					PackageConfig pack = new PackageConfig(file);
					if (!pack.isEmpty())
					{
						packages.put(pack.getName(), pack);
					}
					else
					{
						// notify
						plugin.getLogger()
								.warning(
										"Package file '"
												+ file.getName()
												+ "' appears to be empty? Not added...");
					}
				}
			}
		}
		catch (SecurityException s)
		{
			plugin.getLogger().warning("Cannot access packages folder/files!");
			s.printStackTrace();
		}
	}

	private void loadMarkets()
	{
		try
		{
			final File directory = new File(plugin.getDataFolder()
					.getAbsolutePath() + "/shops");
			if (!directory.exists())
			{
				directory.mkdir();
			}
			// Grab all files
			for (final File file : directory.listFiles())
			{
				if (file.isFile())
				{
					final MarketConfig market = new MarketConfig(file);
					markets.put(market.getName(), market);
				}
			}
		}
		catch (SecurityException s)
		{
			plugin.getLogger().warning("Cannot access shops folder/files!");
			s.printStackTrace();
		}
	}

	public void reloadConfig()
	{
		// Initial relaod
		plugin.reloadConfig();
		// Grab config
		final ConfigurationSection config = plugin.getConfig();
		// Load settings
		loadSettings(config);
		// TODO close all open inventories
		// Clear set of current packages
		packages.clear();
		// Load packages
		loadPackages();
		// Clear set of current markets
		markets.clear();
		// load markets
		loadMarkets();
		plugin.getLogger().info("Config reloaded");
	}

	private void loadSettings(ConfigurationSection config)
	{
		needsChest = config.getBoolean("needsChest", true);
		debugTime = config.getBoolean("debug.time", false);
		debugEconomy = config.getBoolean("debug.economy", false);
	}

	public MarketConfig getMarketConfig(String name)
	{
		for(String market : markets.keySet())
		{
			if(market.equalsIgnoreCase(name))
			{
				return markets.get(market);
			}
		}
		return null;
	}

	public PackageConfig getPackageConfig(String name)
	{
		for(String p : packages.keySet())
		{
			if(p.equalsIgnoreCase(name))
			{
				return packages.get(p);
			}
		}
		return packages.get(name);
	}

	public boolean createMarket(String name)
	{
		boolean created = false;
		final File market = new File(plugin.getDataFolder().getAbsolutePath()
				+ "/shops/" + name + ".yml");
		try
		{
			// Attempt to create file
			if (market.createNewFile())
			{
				final MarketConfig marketConfig = new MarketConfig(market);
				markets.put(marketConfig.getName(), marketConfig);
				created = true;
			}
		}
		catch (IOException io)
		{
			// TODO notify
			io.printStackTrace();
		}
		return created;
	}
	
	public boolean marketExists(String name)
	{
		for(String market : markets.keySet())
		{
			if(market.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}
}
