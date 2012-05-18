package com.mitsugaru.KarmicMarket.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.mitsugaru.KarmicMarket.KarmicMarket;
import com.mitsugaru.KarmicMarket.inventory.MarketInventoryHolder;

public class KMInventoryListener implements Listener
{
	private KarmicMarket plugin;
	
	public KMInventoryListener(KarmicMarket plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		if(!event.isCancelled())
		{
			final MarketInventoryHolder holder = instanceCheck(event);
			if(holder != null)
			{
				holder.getMarketInfo().addViewer();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		final MarketInventoryHolder holder = instanceCheck(event);
		if(holder != null)
		{
			holder.getMarketInfo().removeViewer();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event)
	{
		if(!event.isCancelled())
		{
			final MarketInventoryHolder holder = instanceCheck(event);
			if(holder != null)
			{
				boolean left, right, shift = false;
				if(event.isLeftClick())
				{
					left = true;
				}
				else if(event.isRightClick())
				{
					right = true;
				}
				if(event.isShiftClick())
				{
					shift = true;
				}
				//TODO market logic
			}
		}
	}
	
	public MarketInventoryHolder instanceCheck(InventoryEvent event)
	{
		MarketInventoryHolder holder = null;
		try
		{
			if (event.getInventory().getHolder() != null)
			{
				if (event.getInventory().getHolder() instanceof MarketInventoryHolder)
				{
					holder = (MarketInventoryHolder) event.getInventory().getHolder();
				}
			}
		}
		catch (NullPointerException n)
		{
			// IGNORE
		}
		return holder;
	}
}
