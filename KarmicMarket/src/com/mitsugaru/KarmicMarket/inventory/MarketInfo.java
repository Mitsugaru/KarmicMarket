package com.mitsugaru.KarmicMarket.inventory;

import com.mitsugaru.KarmicMarket.events.KMPlayerListener;

public class MarketInfo
{
	private String marketName, packageName;
	private int viewers = 0;
	
	public MarketInfo(String marketName, String packageName)
	{
		this.marketName = marketName;
		this.packageName = packageName;
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
