package com.mitsugaru.KarmicMarket.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MarketInventoryHolder implements InventoryHolder
{
	private Inventory inventory = null;
	private MarketInfo info;
	
	public MarketInventoryHolder(MarketInfo info)
	{
		this.info = info;
	}
	
	public void setInventory(Inventory inventory)
	{
		this.inventory = inventory;
		for(ItemStack i : info.getItemList())
		{
			inventory.addItem(i);
		}
		//TODO populate with appropriate package items of the market
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

}
