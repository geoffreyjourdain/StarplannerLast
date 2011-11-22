package com.pekalicious.starplanner.managers;
// new file
import java.util.ArrayList;
import java.util.List;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.TechType;
import org.bwapi.bridge.model.Unit;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.Logger;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.TechOrder;
import com.pekalicious.starplanner.util.UnitUtils;

public class TechManager {
	private Game game;
	private StarBlackboard blackboard;
	
	public TechManager(Game game, StarBlackboard blackboard) {
		this.game = game;
		this.blackboard = blackboard;
	}

	List<TechOrder> toRemove = new ArrayList<TechOrder>();

	public void update() {
		toRemove.clear();
		for (TechOrder order : blackboard.techQueue) {
			int mineralsDiff = game.self().minerals() - order.techType.bwapiType.mineralPrice();
			int gasDiff = game.self().gas() - order.techType.bwapiType.gasPrice();

			switch (order.status) {
			case Idle:
				List<Unit> machineshops = UnitUtils.getUnitList(Game.getInstance(), UnitType.TERRAN_MACHINE_SHOP);
				
				Unit emptyMachineshop = null;
				
				
				
				if (machineshops.size() > 0) {
					for (Unit machineshop : machineshops) {
						
							emptyMachineshop = machineshop;
						
					}
					
					if (emptyMachineshop != null) {
						order.building = emptyMachineshop;
						order.status = OrderStatus.Next;
					}else{
						Logger.Debug("ActTechShop\t:No empty machineshop found!\n", 1);
					}
				}
				
				else Logger.Debug("ActTechManager\t:No machine shops !\n", 1);
				break;
			case Next:
				if (mineralsDiff >= 0 && gasDiff >= 0) {
					if (order.building.research(order.techType.bwapiType)) {
						order.status = OrderStatus.Started;
					}else{
						//Logger.Debug("TechMngr:\tBuild declined!\n", 1);
					}
				}
				break;
			case Started:
				TechType tech = order.building.getTech();
				if (tech != null) {
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
		
		for (TechOrder order : toRemove)
			blackboard.techQueue.remove(order);

	}

}
