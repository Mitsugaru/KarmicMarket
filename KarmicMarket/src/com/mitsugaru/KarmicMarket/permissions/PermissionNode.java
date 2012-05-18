package com.mitsugaru.KarmicMarket.permissions;

public enum PermissionNode
{
	ADMIN(".admin"), SIGN(".sign");
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
