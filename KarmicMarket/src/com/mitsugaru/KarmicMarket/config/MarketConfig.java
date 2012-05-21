package com.mitsugaru.KarmicMarket.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class MarketConfig
{
	private File file;
	private YamlConfiguration config;
	private String marketName = "", receiver = "", owner = "";
	private Set<String> marketPackages = new LinkedHashSet<String>();
	private Set<String> marketManagers = new HashSet<String>();
	private boolean sendToReceiver = false;

	public MarketConfig(File file)
	{
		this.file = file;
		try
		{
			this.config = YamlConfiguration.loadConfiguration(file);
			/*
			 * Thanks to brianegge for the regex
			 * http://stackoverflow.com/questions
			 * /924394/how-to-get-file-name-without-the-extension
			 */
			marketName = file.getName().replaceFirst("[.][^.]+$", "");
			loadDefaults();
			loadSettings();
			loadPackages();
		}
		catch (IllegalArgumentException ia)
		{
			// TODO notify
			ia.printStackTrace();
		}
	}

	private void loadDefaults()
	{
		// Setup defaults
		final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
		defaults.put("market.owner", "nobody");
		defaults.put("market.managers", new ArrayList<String>());
		defaults.put("money.sendToReceiver", false);
		defaults.put("money.receiver", "nobody");
		defaults.put("packages", new ArrayList<String>());
		// Add missing defaults
		for (final Map.Entry<String, Object> entry : defaults.entrySet())
		{
			if (!config.contains(entry.getKey()))
			{
				config.set(entry.getKey(), entry.getValue());
			}
		}
	}

	public void reload()
	{
		try
		{
			config.load(file);
		}
		catch (FileNotFoundException fnf)
		{
			fnf.printStackTrace();
		}
		catch (IOException io)
		{
			io.printStackTrace();
		}
		catch (InvalidConfigurationException ic)
		{
			ic.printStackTrace();
		}
		marketPackages.clear();
		marketManagers.clear();
		loadDefaults();
		loadSettings();
		loadPackages();
	}

	private void loadSettings()
	{
		owner = config.getString("market.owner", "nobody");
		final List<String> managers = config.getStringList("market.managers");
		if(managers != null)
		{
			marketManagers.addAll(managers);
		}
		sendToReceiver = config.getBoolean("money.sendToReceiver", false);
		receiver = config.getString("money.receiver", "nobody");
	}

	private void loadPackages()
	{
		final List<String> list = config.getStringList("packages");
		if (list != null)
		{
			marketPackages.addAll(list);
		}
	}

	public boolean isEmpty()
	{
		return marketPackages.isEmpty();
	}

	public String getName()
	{
		return marketName;
	}

	public String getReceiver()
	{
		return receiver;
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public boolean playerIsManager(String name)
	{
		for(String manager : marketManagers)
		{
			if(manager.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}

	public boolean sendToReceiver()
	{
		return sendToReceiver;
	}
	
	public Set<String> getPackageSet()
	{
		return marketPackages;
	}
}
