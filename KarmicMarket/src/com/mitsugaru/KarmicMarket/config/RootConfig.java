package com.mitsugaru.KarmicMarket.config;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.mitsugaru.KarmicMarket.KarmicMarket;

public class RootConfig
{
	private KarmicMarket plugin;
	private final Set<PackageConfig> packages = new HashSet<PackageConfig>();
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
		//Load settings
		loadSettings(config);
		//load packages
		loadPackages();
	}

	private void loadPackages()
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
				if(!pack.isEmpty())
				{
					packages.add(pack);
				}
			}
		}
	}

	public void reloadConfig()
	{
		// Initial relaod
		plugin.reloadConfig();
		// Grab config
		final ConfigurationSection config = plugin.getConfig();
		//Load settings
		loadSettings(config);
		//TODO close all open inventories
		//Clear set of current packages
		packages.clear();
		//Load packages
		loadPackages();
		plugin.getLogger().info("Config reloaded");
	}
	
	private void loadSettings(ConfigurationSection config)
	{
		needsChest = config.getBoolean("needsChest", true);
		debugTime = config.getBoolean("debug.time", false);
		debugEconomy = config.getBoolean("debug.economy", false);
	}
}
