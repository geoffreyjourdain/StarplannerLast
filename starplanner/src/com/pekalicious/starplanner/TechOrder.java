package com.pekalicious.starplanner;
// new file
import org.bwapi.bridge.model.Unit;

import com.pekalicious.starplanner.util.UnitUtils;

public class TechOrder {
	public OrderStatus status;
	public Unit building;
	public UnitUtils.Type2 techType;
	
	public TechOrder() {
		
	}
	
	public TechOrder(UnitUtils.Type2 techType) {
		this.status = OrderStatus.Idle;
		this.techType = techType;
	}
}
