package com.pekalicious.starplanner.managers;

import java.util.ArrayList;
import java.util.List;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.TechType;
import org.bwapi.bridge.model.UpgradeType;
import org.bwapi.bridge.model.Unit;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.Logger;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.TechOrder;
import com.pekalicious.starplanner.UpgradeOrder;
import com.pekalicious.starplanner.util.UnitUtils;

public class UpgradeManager {
	private Game game;
	private StarBlackboard blackboard;
	
	public UpgradeManager(Game game, StarBlackboard blackboard) {
		this.game = game;
		this.blackboard = blackboard;
	}

	List<UpgradeOrder> toRemove = new ArrayList<UpgradeOrder>();

	public void update() {
		toRemove.clear();
		for (UpgradeOrder order : blackboard.upgradeQueue) {
			
			int mineralsDiff = game.self().minerals() - order.upgradeType.bwapiType.mineralPriceBase();
			int gasDiff = game.self().gas() - order.upgradeType.bwapiType.gasPriceBase();

			switch (order.status) {
			case Idle:
				List<Unit> academys = UnitUtils.getUnitList(Game.getInstance(), UnitType.TERRAN_ACADEMY);
				
				Unit emptyAcademy = null;
				
				
				
				if (academys.size() > 0) {
					for (Unit academy : academys) {
						
							emptyAcademy = academy;
						
					}
					
					if (emptyAcademy != null) {
						order.building = emptyAcademy;
						order.status = OrderStatus.Next;
					}else{
						Logger.Debug("ActAcademy\t:No empty Academy found!\n", 1);
					}
				}
				
				else Logger.Debug("ActUpgradeManager\t:No Academy !\n", 1);
				break;
			case Next:
				if (mineralsDiff >= 0 && gasDiff >= 0) {
					Logger.Debug("UpgradeMngr:\tJe suis dans next!\n", 1);
					if (order.building.upgrade(order.upgradeType.bwapiType)) {
						order.status = OrderStatus.Started;
					}else{
						Logger.Debug("UpgradeMngr:\tBuild declined!\n", 1);
					}
				}
				break;
			case Started:
				UpgradeType upgrade = order.building.getUpgrade();
				if (upgrade != null) {
					//if (tech.isCompleted()) {
						order.status = OrderStatus.Ended;
					//}
				}else{
					order.status = OrderStatus.Idle;
				}
				break;
			case Ended:
				toRemove.add(order);
				break;
			}
		}
		
		for (UpgradeOrder order : toRemove)
			blackboard.upgradeQueue.remove(order);

	}

}
