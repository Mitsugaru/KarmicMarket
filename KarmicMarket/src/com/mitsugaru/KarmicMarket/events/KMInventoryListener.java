package com.mitsugaru.KarmicMarket.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.mitsugaru.KarmicMarket.KarmicMarket;
import com.mitsugaru.KarmicMarket.inventory.MarketInventoryHolder;
import com.mitsugaru.KarmicMarket.inventory.Item;

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
		if (!event.isCancelled())
		{
			plugin.getLogger().info("open inventory");
			final MarketInventoryHolder holder = instanceCheck(event);
			if (holder != null)
			{
				holder.getMarketInfo().addViewer();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent event)
	{
		plugin.getLogger().info("close inventory");
		final MarketInventoryHolder holder = instanceCheck(event);
		if (holder != null)
		{
			holder.getMarketInfo().removeViewer();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event)
	{
		plugin.getLogger().info("inventory");
		if (!event.isCancelled())
		{
			final MarketInventoryHolder holder = instanceCheck(event);
			if (holder != null)
			{
				plugin.getLogger().info("market");
				// Check if they are interacting with top or bottom inventory
				boolean fromChest = false;
				if (event.getRawSlot() < 54)
				{
					fromChest = true;
					plugin.getLogger().info("from chest");
				}
				/**
				 * Market logic
				 */
				// Handle shift click
				if (event.isShiftClick())
				{
					plugin.getLogger().info("shift click");
					if (event.isLeftClick())
					{
						// handle shift left click
						if (fromChest)
						{
							buyItem(event);
						}
						else if (event.getInventory().firstEmpty() >= 0)
						{
							sellItem(event);
						}
					}
					else if (event.isRightClick())
					{
						// handle shift right click
					}
				}
				else
				{
					if (event.isLeftClick() && fromChest)
					{
						// handle left click
							buyItem(event);
					}
					else if (event.isRightClick() && fromChest)
					{
						// handle right click
						sellItem(event);
					}
				}
			}
		}
	}

	private void buyItem(InventoryClickEvent event)
	{
		plugin.getLogger().info("buy");
		if (!event.getCurrentItem().getType().equals(Material.AIR)
				&& !event.getCursor().getType().equals(Material.AIR))
		{
			final Item a = new Item(event.getCurrentItem());
			final Item b = new Item(event.getCursor());
			if (a.areSame(b))
			{
				//Add to cursor
				event.getCursor().setAmount(
						event.getCursor().getAmount()
								+ event.getCurrentItem().getAmount());
			}
			else if (event.getInventory().firstEmpty() >= 0)
			{
				//Not the same, try to move it to their inventory
				event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
			}
		}
		event.setCancelled(true);
	}

	private void sellItem(InventoryClickEvent event)
	{
		plugin.getLogger().info("sell");
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
					holder = (MarketInventoryHolder) event.getInventory()
							.getHolder();
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
