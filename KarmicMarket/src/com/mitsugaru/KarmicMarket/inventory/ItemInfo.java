package com.mitsugaru.KarmicMarket.inventory;

public class ItemInfo
{
	private double amount;
	private int stack;

	public ItemInfo(double amount, int stack)
	{
		this.amount = amount;
		this.stack = stack;
	}

	public double getAmount()
	{
		return amount;
	}

	public int getStack()
	{
		return stack;
	}
}
