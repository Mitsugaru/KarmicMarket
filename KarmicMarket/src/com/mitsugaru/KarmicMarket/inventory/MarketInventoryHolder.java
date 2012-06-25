package com.mitsugaru.KarmicMarket.inventory;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.mitsugaru.KarmicMarket.KarmicMarket;

public class MarketInventoryHolder implements InventoryHolder
{
	private Inventory inventory = null;
	private MarketInfo info;
	private Set<String> viewers = new HashSet<String>();
	
	public MarketInventoryHolder(MarketInfo info)
	{
		this.info = info;
	}
	
	public void setInventory(Inventory inventory)
	{
		this.inventory = inventory;
		for(ItemStack i : info.getItems().keySet())
		{
			inventory.addItem(i);
		}
		//populate with appropriate package items of the market
	}
	
	@Override
	public Inventory getInventory()
	{
		return inventory;
	}
	
	public MarketInfo getMarketInfo()
	{
		return info;
	}

	public void addViewer(String name)
	{
		viewers.add(name);
	}
	
	public void removeViewer(String name)
	{
		viewers.remove(name);
		if(viewers.size() <= 0)
		{
			//Remove from player listener hashmap as there are no more viewers
			KarmicMarket.openMarkets.remove(info);
		}
	}
	
	public Set<String> getViewers()
	{
		return viewers;
	}
}
