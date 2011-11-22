package com.pekalicious.starplanner.actions.terran;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.agent.Agent;
import com.pekalicious.agent.WorldState;
import com.pekalicious.agent.WorldStateValue;
import com.pekalicious.starplanner.BuildOrder;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.StarPlanner;
import com.pekalicious.starplanner.WSKey;
import com.pekalicious.starplanner.actions.StarAction;
import com.pekalicious.starplanner.util.UnitUtils;

public class ActionBuildLaboratory extends StarAction {
    private static final long serialVersionUID = 4308152181367747L;

	BuildOrder order;
	
	@Override
	public void setupConditions() {
		this.preconditions = new WorldState();
		this.preconditions.setProperty(WSKey.T_STARPORT, new WorldStateValue<Boolean>(true));
		
		this.effects = new WorldState();
		this.effects.setProperty(WSKey.T_SCIENCE_FACILITY, new WorldStateValue<Boolean>(true));
		
		this.cost = 15;
		this.precedence = 1;
	}

	@Override
	public void activateAction(Agent aiManager, WorldState state) {
		int count = Game.getInstance().self().completedUnitCount(UnitType.TERRAN_SCIENCE_FACILITY);

		if (count == 0) {
			StarBlackboard bb = (StarBlackboard)((StarPlanner)aiManager).getBlackBoard(); 
			order = bb.addToBuildQueue(UnitUtils.Type.TERRAN_SCIENCE_FACILITY, count);
		}else{
			order = new BuildOrder();
			order.status = OrderStatus.Ended;
		}
	}

	int count;
	@Override
	public boolean isActionComplete(Agent aiManager) {
		//int currentBarracks = ((StarPlanner)aiManager).<Integer>getWorldStateValue(WSKey.T_BARRACKS).getValue();
		return order.status == OrderStatus.Ended;
	}
	
	@Override
	public boolean validateAction(Agent ai) {
		return true;
	}

	@Override
	public boolean validateContextPreconditions(Agent aiManager, WorldState currentState, WorldState goalState, boolean planning) {

		if (planning) {
			/*
			if (!aiManager.getWorkingMemory().containsKey(WSKey.T_ARMORY)) return true;
			
			int barracksCount = aiManager.<Integer>getWorkingMemoryValue(WSKey.T_ARMORY).getValue();
			return barracksCount < 1;
			*/
		}
		
		return true;
	}
	
	@Override
	public void applyContextEffect(Agent agent, WorldState state, WorldState worldState) {
		state.setProperty(WSKey.T_SCIENCE_FACILITY, new WorldStateValue<Boolean>(true));
	}

	public String toString() {
		return "BuildScienceFacility";
	}

	@Override
	public boolean interrupt() {
		return true;
	}

	@Override
	public boolean canBeDisabled() {
		return true;
	}
}
