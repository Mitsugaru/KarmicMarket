package com.mitsugaru.KarmicMarket.exceptions;

public class MarketPackageNotFoundException extends Exception {
    
    String missingPackage = "";

    /**
     * 
     */
    private static final long serialVersionUID = -5486000078847895236L;

    public MarketPackageNotFoundException() {

    }
    
    public MarketPackageNotFoundException(String packageName, String msg)
    {
	super(msg);
	this.missingPackage = packageName;
    }
    
    public String getBadPackageName()
    {
	return missingPackage;
    }
}
