/**
 * Class to represent items that are in the pool
 * Mostly used to help differentiate between
 * items that use damage values
 */
package com.mitsugaru.KarmicMarket.inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Item extends MaterialData {
	// Class variables
	private short durability;

	public Item(ItemStack i)
	{
		super(i.getTypeId(), i.getData().getData());
		init(i.getDurability());
	}
	
	/**
	 * Constructor
	 *
	 * @param int of item id
	 * @param byte of data value
	 */
	public Item(int i, byte d, short dur) {
		super(i, d);
		init(dur);
	}
	
	private void init(short dur)
	{
		durability = dur;
	}

	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}

	/**
	 * Custom hashcode method to provide proper Item class
	 * equals check. Especially useful for potions, since data
	 * values are the same, but durability is not.
	 *
	 * @return Object's hashcode
	 */
	@Override
	public int hashCode()
	{
		int hash = 0;
		hash += this.getItemTypeId();
		hash += this.getData();
		hash += this.itemDurability();
		return hash;
	}

	/**
	 * Variant of equals(Object obj)
	 *
	 * @param obj
	 * @return true if they are the same item
	 */
	public boolean areSame(Object obj) {
		// Both blocks
		try
		{
			if (this.getItemType().isBlock()
					&& ((Item) obj).getItemType().isBlock())
			{
				// Check both id and data values
				if (this.getItemTypeId() == ((Item) obj).getItemTypeId())
				{
					if (this.itemId() == 9)
					{
						// Ignore data for leaves
						return true;
					}
					if (this.getData() == ((Item) obj).getData())
					{
						return true;
					}
				}
			}
			else if (!this.getItemType().isBlock()
					&& !((Item) obj).getItemType().isBlock())
			{
				// Both non-block, only check item id
				if (this.getItemTypeId() == ((Item) obj).getItemTypeId())
				{
					//handle if dye or potion
					if(this.itemId() == 351)
					{
						if(this.getData() == ((Item) obj).getData())
						{
							return true;
						}
					}
					else if(this.itemId() == 373)
					{
						if(durability == ((Item) obj).itemDurability())
						{
							return true;
						}
					}
					else
						return true;
				}
			}
		}
		catch (ClassCastException e)
		{
			// Cast failed, so, they're not the same object
			return false;
		}
		return false;
	}
	
	public ItemStack toItemStack()
	{
		ItemStack item = null;
		if(isPotion())
		{
			item = new ItemStack(super.getItemTypeId(), 1, durability);
		}
		else if(isTool())
		{
			item = new ItemStack(super.getItemType(), 1, super.getData());
			//TODO handle enchantments
		}
		else
		{
			item = new ItemStack(super.getItemType(), 1, super.getData());
		}
		return item;
	}

	/**
	 * Method to check if the item is a potion/glass bottle
	 *
	 * @return true if potion, else false;
	 */
	public boolean isPotion() {
		if (this.getItemTypeId() == 373 || this.getItemTypeId() == 374)
			return true;
		return false;
	}

	/**
	 * Method to check if this item is a tool
	 *
	 * @return true if its is a tool item
	 */
	public boolean isTool() {
		return this.isTool(this.getItemTypeId());
	}

	/**
	 * Method that checks a given id to see if its a tool
	 *
	 * @param int of item id
	 * @return true if it matches a known tool, else false
	 */
	public boolean isTool(int id) {
		final int[] tool = { 256, 257, 258, 259, 261, 267, 268, 269, 270, 271, 272,
				273, 274, 275, 276, 277, 278, 279, 283, 284, 285, 286, 290,
				291, 292, 293, 294, 298, 299, 300, 301, 302, 303, 304, 305,
				306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317 };
		if (id >= 256 && id <= 317)
		{
			// within range of "tool" ids
			for (int i = 0; i < tool.length; i++)
			{
				// iterate through array to see if we get a match
				if (id == tool[i])
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Grabs the item id of this Item object
	 *
	 * @return item id
	 */
	public int itemId() {
		return this.getItemType().getId();
	}

	/**
	 * Grabs the data value of this Item object
	 *
	 * @return data value
	 */
	public byte itemData() {
		return this.getData();
	}

	/**
	 * Grabs the durability value of this Item object
	 */
	public short itemDurability() {
		return this.durability;
	}
}
