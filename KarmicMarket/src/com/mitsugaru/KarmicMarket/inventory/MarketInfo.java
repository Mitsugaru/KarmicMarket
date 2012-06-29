package com.mitsugaru.KarmicMarket.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.mitsugaru.KarmicMarket.config.PackageConfig;
import com.mitsugaru.KarmicMarket.config.RootConfig;
import com.mitsugaru.KarmicMarket.exceptions.MarketPackageNotFoundException;

public class MarketInfo
{
	private String marketName, packageName;
	private Map<ItemStack, ItemInfo> itemList = new HashMap<ItemStack, ItemInfo>();
	
	public MarketInfo(String marketName, String packageName) throws MarketPackageNotFoundException
	{
		this.marketName = marketName;
		this.packageName = packageName;
		populateItemList();
	}
	
	private void populateItemList() throws MarketPackageNotFoundException
	{
		final PackageConfig pConfig = RootConfig.getPackageConfig(packageName);
		if(pConfig == null)
		{
		    throw new MarketPackageNotFoundException(packageName, "Package '" + packageName + "' not found.");
		}
		for(Map.Entry<Item, ItemInfo> entry : pConfig.getItems().entrySet())
		{
			ItemStack item = entry.getKey().toItemStack();
			item.setAmount(entry.getValue().getStack());
			itemList.put(item, entry.getValue());
		}
	}
	
	@Override
	public int hashCode()
	{
		int hash = 0;
		hash += marketName.hashCode() + packageName.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof MarketInfo)
		{
			final MarketInfo market = (MarketInfo) obj;
			if (market.getMarketName().equalsIgnoreCase(marketName)
					&& market.getPackageName()
							.equalsIgnoreCase(packageName))
			{
				return true;
			}
		}
		return false;
	}
	
	public Map<ItemStack, ItemInfo> getItems()
	{
		return itemList;
	}

	public String getMarketName()
	{
		return marketName;
	}

	public String getPackageName()
	{
		return packageName;
	}
}
