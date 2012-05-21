package com.mitsugaru.KarmicMarket.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.mitsugaru.KarmicMarket.config.PackageConfig;
import com.mitsugaru.KarmicMarket.config.PackageConfig.KMInfo;
import com.mitsugaru.KarmicMarket.config.RootConfig;
import com.mitsugaru.KarmicMarket.events.KMPlayerListener;

public class MarketInfo
{
	private String marketName, packageName;
	private int viewers = 0;
	private List<ItemStack> itemList = new ArrayList<ItemStack>();
	
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
			item.setAmount(entry.getValue().stack);
			itemList.add(item);
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
	
	public List<ItemStack> getItemList()
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
	
	public void addViewer()
	{
		viewers += 1;
	}
	
	public void removeViewer()
	{
		viewers -= 1;
		if(viewers <= 0)
		{
			//Remove from player listener hashmap as there are no more viewers
			KMPlayerListener.openMarkets.remove(this);
		}
	}
}
