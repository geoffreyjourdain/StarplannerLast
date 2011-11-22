package com.pekalicious.starplanner.managers;

import java.util.ArrayList;
import java.util.List;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.Unit;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.Logger;
import com.pekalicious.starplanner.AddonOrder;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.util.UnitUtils;

public class AddonManager {
	private Game game;
	private StarBlackboard blackboard;
	
	public AddonManager(Game game, StarBlackboard blackboard) {
		this.game = game;
		this.blackboard = blackboard;
	}

	List<AddonOrder> toRemove = new ArrayList<AddonOrder>();

	public void update() {
		toRemove.clear();
		for (AddonOrder order : blackboard.addonQueue) {
			int mineralsDiff = game.self().minerals() - order.unitType.bwapiType.mineralPrice();
			int gasDiff = game.self().gas() - order.unitType.bwapiType.gasPrice();

			switch (order.status) {
			case Idle:
				List<Unit> factories = UnitUtils.getUnitList(Game.getInstance(), UnitType.TERRAN_FACTORY);
				List<Unit> starports = UnitUtils.getUnitList(Game.getInstance(), UnitType.TERRAN_STARPORT);
				List<Unit> laboratories = UnitUtils.getUnitList(Game.getInstance(), UnitType.TERRAN_SCIENCE_FACILITY);

				Unit emptyFactory = null;
				Unit emptyStarport = null;
				Unit emptyLaboratories = null;
				
				if (factories.size() > 0) {
					if (starports.size() > 0){
						if (laboratories.size() > 0){
							for (Unit lab : laboratories) {
								if (lab.getAddon() == null) {
									emptyLaboratories = lab;
								}
							}
						
							if (emptyLaboratories != null) {
							order.building = emptyLaboratories;
							order.status = OrderStatus.Next;
							}else{
							Logger.Debug("ActLab\t:No empty Laboratory found!\n", 1);
							}
							
						}
						else {
							for (Unit starport : starports) {
								if (starport.getAddon() == null) {
									emptyStarport = starport;
								}
							}
							
							if (emptyStarport != null) {
								order.building = emptyStarport;
								order.status = OrderStatus.Next;
							}else{
								Logger.Debug("ActControlTower\t:No empty Starport found!\n", 1);
							}
						}
						
					} else {
					for (Unit factory : factories) {
						if (factory.getAddon() == null) {
							emptyFactory = factory;
						}
					}
					
					if (emptyFactory != null) {
						order.building = emptyFactory;
						order.status = OrderStatus.Next;
					}else{
						Logger.Debug("ActMachineShop\t:No empty factory found!\n", 1);
					}
				}
				}
				
				
				/*if (factories.size() > 0 && order.unitType.bwapiType == UnitType.TERRAN_MACHINE_SHOP) {
					for (Unit factory : factories) {
						if (factory.getAddon() == null) {
							emptyFactory = factory;
						}
					}
					
					if (emptyFactory != null) {
						order.building = emptyFactory;
						order.status = OrderStatus.Next;
					}else{
						Logger.Debug("ActMachineShop\t:No empty factory found!\n", 1);
					}
				}
				else if (starports.size() > 0 && order.unitType.bwapiType == UnitType.TERRAN_CONTROL_TOWER) {
						for (Unit starport : starports) {
							if (starport.getAddon() == null) {
								emptyStarport = starport;
							}
						}
						
						if (emptyStarport != null) {
							order.building = emptyStarport;
							order.status = OrderStatus.Next;
						}else{
							Logger.Debug("ActControlTower\t:No empty Starport found!\n", 1);
						}
					
					}
				else if (laboratories.size() > 0 && order.unitType.bwapiType == UnitType.TERRAN_PHYSICS_LAB) {
						for (Unit lab : laboratories) {
							if (lab.getAddon() == null) {
								emptyStarport = lab;
							}
						}
					
						if (emptyLaboratories != null) {
						order.building = emptyLaboratories;
						order.status = OrderStatus.Next;
						}else{
						Logger.Debug("ActLab\t:No empty Laboratory found!\n", 1);
						}
				
				}*/
				else Logger.Debug("ActAddonManager\t:No factories or starports or laboratories !\n", 1);
				break;
			case Next:
				if (mineralsDiff >= 0 && gasDiff >= 0) {
					if (order.building.buildAddon(order.unitType.bwapiType)) {
						order.status = OrderStatus.Started;
					}else{
						Logger.Debug("AddonMngr:\tBuild declined!\n", 1);
					}
				}
				break;
			case Started:
				Unit addon = order.building.getAddon();
				if (addon != null) {
					if (addon.isCompleted()) {
						order.status = OrderStatus.Ended;
					}
				}else{
					order.status = OrderStatus.Idle;
				}
				break;
			case Ended:
				toRemove.add(order);
				break;
			}
		}
		
		for (AddonOrder order : toRemove)
			blackboard.addonQueue.remove(order);

	}

}
