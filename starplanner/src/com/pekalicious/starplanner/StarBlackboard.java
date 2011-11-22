package com.pekalicious.starplanner;

import java.util.ArrayList;
import java.util.List;

import org.bwapi.bridge.model.Unit;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.Logger;
import com.pekalicious.agent.BlackBoard;
import com.pekalicious.goap.PlannerGoal;
import com.pekalicious.starplanner.util.UnitUtils;


public class StarBlackboard implements BlackBoard {
	public List<BuildOrder> buildQueue;
	public int buildPriority;
	public List<TrainingOrder> trainingQueue;
	public Unit scout;
	
	public boolean buildPlanReplan;
	public PlannerGoal buildGoal;
	public boolean buildPlanInvalid;
	public boolean buildPlanComplete;
	
	public int minerals;
	public int gas;
    public int supplyUsed;
    public int supplyTotal;
    
    public Squad squad;
    public Squad2 squad2;
	public boolean squadIgnoreUnitDistance;
	public List<AddonOrder> addonQueue;
	public List<TechOrder> techQueue;
	public List<UpgradeOrder> upgradeQueue;
	
	public StarBlackboard() {
		resetValues();
		squad = new Squad();
		squad2 = new Squad2();
	}
	
	public void resetValues() {
		buildQueue = new ArrayList<BuildOrder>();
		trainingQueue = new ArrayList<TrainingOrder>();
		addonQueue = new ArrayList<AddonOrder>();
		techQueue = new ArrayList<TechOrder>();
		upgradeQueue = new ArrayList<UpgradeOrder>();
	}
	
	public BuildOrder addToBuildQueue(UnitUtils.Type type, int buildCount) {
		BuildOrder order = new BuildOrder(type, buildCount);
		this.buildQueue.add(order);
		Logger.Debug("Blackboard:\tAdded to buildQueue: " + buildCount + " " + type.bwapiType.getName() + "\n", 2);
		return order;
	}
	
	public AddonOrder addToAddonQueue(UnitUtils.Type type) {
		AddonOrder order = new AddonOrder(type);
		this.addonQueue.add(order);
		Logger.Debug("Blackboard:\tAdded to addonQueue: " + type.bwapiType.getName() + "\n", 2);
		return order;
	}
	// new functions
	public TechOrder addToTechQueue(UnitUtils.Type2 tech) {
		TechOrder order = new TechOrder(tech);
		this.techQueue.add(order);
		Logger.Debug("Blackboard:\tAdded to techQueue: " + tech.bwapiType.getName() + "\n", 2);
		return order;
	}
	
	public UpgradeOrder addToUpgradeQueue(UnitUtils.Type3 upgrade) {
		UpgradeOrder order = new UpgradeOrder(upgrade);
		this.upgradeQueue.add(order);
		Logger.Debug("Blackboard:\tAdded to upgradeQueue: " + upgrade.bwapiType.getName() + "\n", 2);
		return order;
	}
	// end of new functions
	public BuildOrder addToBuildQueue(UnitUtils.Type type, Unit onUnit, int buildCount) {
		Logger.Debug("Blackboard:\tAdded to buildQueue: " + buildCount + " " + type.bwapiType.getName() + "\n", 2);
		BuildOrder order = new BuildOrder(type, buildCount);
		order.onUnit = onUnit;
		this.buildQueue.add(order);
		return order;
	}
	
	public TrainingOrder addToTrainingQueue(UnitUtils.Type type, int trainingCount) {
		Logger.Debug("Blackboard:\tAdded to trainQueue: " + trainingCount + " " + type.bwapiType.getName() + "\n", 2);
		TrainingOrder order = new TrainingOrder(type, trainingCount);
		trainingQueue.add(order);
		return order;
	}

	public boolean isInBuildQueue(UnitType type) {
		for (BuildOrder order : buildQueue)
			if (order.worker != null && order.worker.equals(type)) return true;
		
		return false;
	}

	public boolean isOccupied(Unit unit) {
		for (BuildOrder order : buildQueue)
			if (order.worker != null 
					&& order.worker.equals(unit) 
					&& order.status != OrderStatus.Ended)
				return true;
		
		if (unit.equals(scout))
			return true;
		
		return false;
	}

}
