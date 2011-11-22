package com.pekalicious.starplanner.managers;

import java.util.ArrayList;
import java.util.List;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.TechType;
import org.bwapi.bridge.model.Unit;
import org.bwapi.bridge.model.UnitType;
import org.bwapi.bridge.model.UpgradeType;

import com.pekalicious.Logger;
import com.pekalicious.starplanner.AddonOrder;
import com.pekalicious.starplanner.BuildOrder;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.StarPlanner;
import com.pekalicious.starplanner.TrainingOrder;
import com.pekalicious.starplanner.UpgradeOrder;
import com.pekalicious.starplanner.util.UnitUtils;

public class TrainingManager {
	private Game game;
	private StarBlackboard blackBoard;
	
	public TrainingManager(Game game, StarBlackboard blackBoard) {
		this.game = game;
		this.blackBoard = blackBoard;
	}
	
	private List<TrainingOrder> toRemove = new ArrayList<TrainingOrder>();
	
	UpgradeOrder order2;
	TrainingOrder order;
	boolean passe = true;
	private BuildOrder supplyOrder;
	private BuildOrder turretOrder;
	private BuildOrder engOrder;
	private BuildOrder academyOrder;
	private BuildOrder factoryOrder;
	private BuildOrder barrackOrder;
	private BuildOrder valkyrieOrder;
	private AddonOrder battlecruiserOrder;
	public void update() {
		toRemove.clear();
		//beginning of adding buildings
		/*
			if (Game.getInstance().self().allUnitCount(UnitType.TERRAN_FACTORY) >= 1 && (valkyrieOrder == null || valkyrieOrder.status == OrderStatus.Ended) && (Game.getInstance().self().allUnitCount(UnitType.TERRAN_ARMORY) < 1)){
				valkyrieOrder = blackBoard.addToBuildQueue(UnitUtils.Type.TERRAN_ARMORY, 1);
				Logger.Debug("TrainManager:\tAdding Armory\n", 1);
			}
			*/
			
			if ((Game.getInstance().self().allUnitCount(UnitType.TERRAN_SUPPLY_DEPOT) >= 1) && (Game.getInstance().self().allUnitCount(UnitType.TERRAN_BARRACKS) == 3) && (academyOrder == null || academyOrder.status == OrderStatus.Ended) && (Game.getInstance().self().allUnitCount(UnitType.TERRAN_ACADEMY) < 1)){
				academyOrder = blackBoard.addToBuildQueue(UnitUtils.Type.TERRAN_ACADEMY, 1);
				Logger.Debug("TrainManager:\tAdding Academy\n", 1);
			}
			
			if ((Game.getInstance().self().allUnitCount(UnitType.TERRAN_ACADEMY) >= 1) && (engOrder == null || engOrder.status == OrderStatus.Ended) && (Game.getInstance().self().allUnitCount(UnitType.TERRAN_ENGINEERING_BAY) < 1)){
				engOrder = blackBoard.addToBuildQueue(UnitUtils.Type.TERRAN_ENGINEERING_BAY, 1);
				Logger.Debug("TrainManager:\tAdding Engineering Bay\n", 1);
			}
			
			if ((Game.getInstance().self().allUnitCount(UnitType.TERRAN_ENGINEERING_BAY) >= 1) && (turretOrder == null || turretOrder.status == OrderStatus.Ended) && (Game.getInstance().self().allUnitCount(UnitType.TERRAN_MISSILE_TURRET) < 3)){
				turretOrder = blackBoard.addToBuildQueue(UnitUtils.Type.TERRAN_TURRET, 1);
				Logger.Debug("TrainManager:\tAdding Turret\n", 1);
			}
			
			if (Game.getInstance().self().allUnitCount(UnitType.TERRAN_STARPORT) >= 1 && (battlecruiserOrder == null || battlecruiserOrder.status == OrderStatus.Ended) && (Game.getInstance().self().allUnitCount(UnitType.TERRAN_STARPORT) > Game.getInstance().self().allUnitCount(UnitType.TERRAN_CONTROL_TOWER))){
				battlecruiserOrder = blackBoard.addToAddonQueue(UnitUtils.Type.TERRAN_CONTROL_TOWER);
				Logger.Debug("TrainManager:\tAdding Control Tower\n", 1);
			}
			
			// end of adding buildings
			
			
		//beginning adding of supply depot before needed
		int supplyDiff2 = (game.self().supplyTotal()) - ((game.self().supplyUsed()));
		if (supplyDiff2 <= 10  && (supplyOrder == null || supplyOrder.status == OrderStatus.Ended) && (Game.getInstance().self().allUnitCount(UnitType.TERRAN_SCV) > 8)) {
			Logger.Debug("TrainManager:\tAdding Supply Depot before Needed\n", 1);
			supplyOrder = blackBoard.addToBuildQueue(UnitUtils.Type.TERRAN_SUPPLY_DEPOT, 1);
		}
		//end of adding supply depot before needed
		
		// adding range for marine
		
		if ((Game.getInstance().self().allUnitCount(UnitType.TERRAN_ACADEMY) == 1) && order2 == null && passe) {
			 order2 = blackBoard.addToUpgradeQueue(UnitUtils.Type3.RANGE);
			 if (Game.getInstance().self().isUpgrading(UpgradeType.U_238_SHELLS))
			 {
				 passe = false;
			 }
		}
		
		//end of range
	
		for (TrainingOrder order : blackBoard.trainingQueue) {
			List<Unit> whatBuilds = UnitUtils.getCompletedUnitList(game, order.unitType.bwapiType.whatBuilds().getKey());
			if (whatBuilds.size() == 0) {
				if (order.status != OrderStatus.Invalid) {
					Logger.Debug("TrainManager:\tNo building for " + order.unitType.bwapiType.getName() + "\n", 1);
					order.status = OrderStatus.Invalid;
				}
			}else{
				if (order.status == OrderStatus.Invalid)
					order.status = OrderStatus.Idle;
			}

			switch (order.status) {
				case Idle:
				
					if (game.self().minerals() >= order.unitType.bwapiType.mineralPrice()) {
						if (game.self().gas() >= order.unitType.bwapiType.gasPrice()) {
							int supplyDiff = (game.self().supplyTotal()/2) - ((game.self().supplyUsed()/2)+order.unitType.bwapiType.supplyRequired());
							
							if (supplyDiff >= 0) {
								if (this.blackBoard.buildPriority < order.unitType.priority) {
									if (order.unitType == UnitUtils.Type.TERRAN_SCV) {
										if (Game.getInstance().self().allUnitCount(UnitType.TERRAN_SCV) < 8)
											order.status = OrderStatus.Next;
										else
											Logger.Debug("Build priority is " + this.blackBoard.buildPriority + "\n", 5);
									}
								}else{
									order.status = OrderStatus.Next;
								}
							}else{
								if (supplyOrder == null || supplyOrder.status == OrderStatus.Ended) { // this "if" doesn't matter
									Logger.Debug("TrainManager:\tAdding Supply Depot ancien...\n", 2);
									//supplyOrder = blackBoard.addToBuildQueue(UnitUtils.Type.TERRAN_SUPPLY_DEPOT, 1);
								}
							}
						}else{
							Logger.Debug("TrainManager:\tGas NOT OK: (" + order.unitType.bwapiType.getName() 
									+ ") Need " + order.unitType.bwapiType.gasPrice() + "/ Have " + game.self().gas() + "\n", 5);
						}
					}else{
						Logger.Debug("TrainManager:\tMinerals NOT OK: (" + order.unitType.bwapiType.getName() 
								+ ") Need " + order.unitType.bwapiType.mineralPrice() + "/ Have " + game.self().minerals() + "\n", 5);
					}
					
				break;
				case Next:
					Logger.Debug("TrainManager:\tLooking for empty queue\n", 5);
					for (Unit building : whatBuilds) {
						if (building.getTrainingQueue().size() == 0 && building.isCompleted()) {
							building.train(order.unitType.bwapiType);
							order.unitCount--;
							order.waitingFor++;
							Logger.Debug("TrainManager:\tFound empty queue. Training " + order.unitType.bwapiType.getName() + "...\n", 4);
							break;
						}
					}
					
					if (order.unitCount > 0) {
						order.status = OrderStatus.Idle;
						Logger.Debug("TrainManager:\t" + order.unitCount + " more " + order.unitType.bwapiType.getName() + " to go...\n", 5);
					} else {
						order.status = OrderStatus.Waiting;
						Logger.Debug("TrainManager:\tDone training. Now waiting\n", 5);
					}
				break;
				case Ended:
					toRemove.add(order);
				break;
			}

		}
		
		for (TrainingOrder order : toRemove) {
			Logger.Debug("TrainManager:\tRemoving " + order.unitType.bwapiType.getName() + "\n", 4);
			blackBoard.trainingQueue.remove(order);
		}
		
	}
	
	private boolean found;
	public void onUnitCreate(Unit unit) {
		if (unit.getType().isBuilding()) return;
		if (!unit.getPlayer().equals(game.self())) return;
		
		found = false;
		for (TrainingOrder order : blackBoard.trainingQueue) {
			if (order.unitType.bwapiType.equals(unit.getType())) {
				if (order.waitingFor > 0) {
					Logger.Debug("TrainManager:\tAdding to trained units: " + unit.getType().getName() + "\n", 3);
					order.units.add(unit);
					order.waitingFor--;
					found = true;
					if (order.waitingFor == 0 && order.unitCount == 0)
						order.status = OrderStatus.Ended;
				}
			}
		}
		
		if (!found) {
			Logger.Debug("TrainManager:\tCould not find training order for " + unit.getType().getName() + "\n", 1);
		}
	}
}
