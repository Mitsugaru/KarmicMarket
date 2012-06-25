package com.mitsugaru.KarmicMarket.permissions;

public enum PermissionNode
{
	SIGN(".sign"),
	BUY(".buy"),
	SELL(".sell"),
	/**
	 * Admin nodes
	 */
	ADMIN_RELOAD(".admin.reload"),
	/**
	 * Market nodes
	 */
	MARKET_CREATE(".market.create");
	private static final String prefix = "KarmicMarket";
	private String node;

	private PermissionNode(String node)
	{
		this.node = prefix + node;
	}

	public String getNode()
	{
		return node;
	}

}