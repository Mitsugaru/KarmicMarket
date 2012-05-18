package com.mitsugaru.KarmicMarket;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.KarmicMarket.config.RootConfig;
import com.mitsugaru.KarmicMarket.events.KMBlockListener;
import com.mitsugaru.KarmicMarket.events.KMEntityListener;
import com.mitsugaru.KarmicMarket.events.KMInventoryListener;
import com.mitsugaru.KarmicMarket.events.KMPlayerListener;
import com.mitsugaru.KarmicMarket.logic.EconomyLogic;
import com.mitsugaru.KarmicMarket.permissions.PermCheck;

public class KarmicMarket extends JavaPlugin
{
	//Class variables
	public static final String TAG = "[KarmicMarket]";
	private RootConfig config;
	private PermCheck perm;
	
	@Override
	public void onDisable()
	{
		this.reloadConfig();
		this.saveConfig();
		// TODO Disconnect from sql database
		
	}
	@Override
	public void onEnable()
	{
		// Get config
		config = new RootConfig(this);
		// Get permissions
		perm = new PermCheck(this);
		//Get pllugin manager
		final PluginManager pm = this.getServer().getPluginManager();
		// Setup economy
		EconomyLogic.init(this);
		if(!EconomyLogic.setupEconomy())
		{
			//No economy found, disable
			getLogger().warning(TAG + " No economy found!");
			pm.disablePlugin(this);
		}
		// Setup commander
		getCommand("km").setExecutor(new Commander(this));
		// Setup listeners
		pm.registerEvents(new KMBlockListener(this), this);
		pm.registerEvents(new KMEntityListener(this), this);
		pm.registerEvents(new KMPlayerListener(this), this);
		pm.registerEvents(new KMInventoryListener(this), this);
	}
	
	public RootConfig getPluginConfig()
	{
		return config;
	}
	public PermCheck getPermissionsHandler()
	{
		return perm;
	}
}
