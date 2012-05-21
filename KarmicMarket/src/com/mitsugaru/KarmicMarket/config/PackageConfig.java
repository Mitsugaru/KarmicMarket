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
	private String packageName = "";
	private Map<Item, KMInfo> items = new LinkedHashMap<Item, KMInfo>();
	private double defaultAmount = 10.0;
	private int defaultStack = 1;

	public PackageConfig(File file)
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
			packageName = file.getName().replaceFirst("[.][^.]+$", "");
			loadDefaults();
			loadSettings();
			loadItems();
		}
		catch (IllegalArgumentException ia)
		{
			// TODO notify
			ia.printStackTrace();
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
		loadDefaults();
		loadSettings();
		loadItems();
	}

	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	public String getName()
	{
		return packageName;
	}
	
	public Map<Item, KMInfo> getItems()
	{
		return items;
	}

	private void loadDefaults()
	{
		// Setup defaults
		final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
		defaults.put("defaults.amount", 10.0);
		defaults.put("defaults.stack", 1);
		// Add missing defaults
		for (final Map.Entry<String, Object> entry : defaults.entrySet())
		{
			if (!config.contains(entry.getKey()))
			{
				config.set(entry.getKey(), entry.getValue());
			}
		}
	}

	private void loadSettings()
	{
		defaultAmount = config.getDouble("defaults.amount", 10.0);
		defaultStack = config.getInt("defaults.stack", 1);
	}

	private void loadItems()
	{
		// load items from config
		for (final String entry : config.getKeys(false))
		{
			try
			{
				// Attempt to parse non data value nodes
				int key = Integer.parseInt(entry);
				if (key <= 0)
				{
					// plugin.getLogger().warning(
					// Karmiconomy.TAG
					// + " Zero or negative item id for entry: "
					// + entry);
				}
				else
				{
					items.put(new Item(key, Byte.parseByte("" + 0), (short) 0),
							parseInfo(entry));
				}
			}
			catch (final NumberFormatException ex)
			{
				// Potential data value entry
				if (entry.contains("&"))
				{
					try
					{
						final String[] split = entry.split("&");
						final int item = Integer.parseInt(split[0]);
						final int data = Integer.parseInt(split[1]);
						if (item <= 0)
						{
							// plugin.getLogger()
							// .warning(
							// Karmiconomy.TAG
							// + " Zero or negative item id for entry: "
							// + entry);
						}
						else
						{
							if (item != 373)
							{
								items.put(
										new Item(item, Byte
												.parseByte("" + data),
												(short) data), parseInfo(entry));
							}
							else
							{
								items.put(new Item(item,
										Byte.parseByte("" + 0), (short) data),
										parseInfo(entry));
							}
						}
					}
					catch (ArrayIndexOutOfBoundsException a)
					{
						// plugin.getLogger()
						// .warning(
						// "Wrong format for "
						// + entry
						// + ". Must follow '<itemid>&<datavalue>:' entry.");
					}
					catch (NumberFormatException exa)
					{
						// plugin.getLogger().warning(
						// "Non-integer number for " + entry);
					}
				}
				else
				{
					// plugin.getLogger().warning("Invalid entry for " + entry);
				}
			}
		}
	}

	private KMInfo parseInfo(final String path)
	{
		final double amount = config.getDouble(path + ".amount", defaultAmount);
		final int stack = config.getInt(path + ".stack", defaultStack);
		return new KMInfo(amount, stack);
	}

	public class KMInfo
	{
		// TODO define info per item
		public double amount;
		public int stack;

		public KMInfo(double amount, int stack)
		{
			this.amount = amount;
			this.stack = stack;
		}
	}
}
