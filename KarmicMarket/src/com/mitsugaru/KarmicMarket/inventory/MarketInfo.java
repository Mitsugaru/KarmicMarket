package com.mitsugaru.KarmicMarket.inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.mitsugaru.KarmicMarket.config.PackageConfig;
import com.mitsugaru.KarmicMarket.config.PackageConfig.KMInfo;
import com.mitsugaru.KarmicMarket.config.RootConfig;

public class MarketInfo
{
	private String marketName, packageName;
	private Map<ItemStack, KMInfo> itemList = new HashMap<ItemStack, KMInfo>();
	
	public MarketInfo(String marketName, String packageName)
	{
		this.marketName = marketName;
		this.packageName = packageName;
		populateItemList();
	}
	
	private void populateItemList()
	{
		final PackageConfig pConfig = RootConfig.getPackageConfig(packageName);
		for(Map.Entry<Item, KMInfo> entry : pConfig.getItems().entrySet())
		{
			ItemStack item = entry.getKey().toItemStack();
			item.setAmount(entry.getValue().getStack());
			itemList.put(item, entry.getValue());
		}
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
	
	public Map<ItemStack, KMInfo> getItems()
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
