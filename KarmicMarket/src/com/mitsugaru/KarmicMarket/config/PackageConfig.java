package com.mitsugaru.KarmicMarket.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mitsugaru.KarmicMarket.inventory.Item;

public class PackageConfig
{
	private File file;
	private YamlConfiguration config;
	private Map<Item, KMInfo> items = new LinkedHashMap<Item, KMInfo>();

	public PackageConfig(File file)
	{
		this.file = file;
		this.config = YamlConfiguration.loadConfiguration(file);
		this.loadItems();
	}
	
	public void reload()
	{
		try
		{
			config.load(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isEmpty()
	{
		return items.isEmpty();
	}
	
	public void loadItems()
	{
		//TODO load items from config
	}
	
	public class KMInfo
	{
		//TODO define info per item
	}
}
