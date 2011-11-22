package com.pekalicious.starplanner;
// new file
import org.bwapi.bridge.model.Unit;

import com.pekalicious.starplanner.util.UnitUtils;

public class UpgradeOrder {
	public OrderStatus status;
	public Unit building;
	public UnitUtils.Type3 upgradeType;
	
	public UpgradeOrder() {
		
	}
	
	public UpgradeOrder(UnitUtils.Type3 upgradeType) {
		this.status = OrderStatus.Idle;
		this.upgradeType = upgradeType;
	}
}
