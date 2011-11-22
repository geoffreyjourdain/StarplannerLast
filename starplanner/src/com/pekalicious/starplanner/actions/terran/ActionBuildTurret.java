package com.pekalicious.starplanner.actions.terran;

import java.io.Serializable;

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

public class ActionBuildTurret extends StarAction implements Serializable {
	private static final long serialVersionUID = -188546465965196L;

	BuildOrder order;
	
	@Override
	public void setupConditions() {
		this.preconditions = new WorldState();
		this.preconditions.setProperty(WSKey.T_ENGINEERINGBAY, new WorldStateValue<Boolean>(true));
		
		this.effects = new WorldState();
		this.effects.setProperty(WSKey.T_TURRET, new WorldStateValue<Void>());

		this.cost = 4.0f;
		this.precedence = 1;
	}

	@Override
	public void activateAction(Agent aiManager, WorldState state) {
		StarBlackboard bb = (StarBlackboard)((StarPlanner)aiManager).getBlackBoard();
		int buildCount = state.<Integer>getPropertyValue(WSKey.T_TURRET).getValue();
		order = bb.addToBuildQueue(UnitUtils.Type.TERRAN_TURRET, buildCount);
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
			//if (!aiManager.getWorkingMemory().containsKey(WSKey.T_FACTORY)) return true;
			
			//int barracksCount = aiManager.<Integer>getWorkingMemoryValue(WSKey.T_FACTORY).getValue();
			//return barracksCount < 1;
		}
		
		return true;
	}
	
	
	@Override
	public void applyContextEffect(Agent agent, WorldState state, WorldState worldState) {
		state.setProperty(WSKey.T_TURRET, new WorldStateValue<Boolean>(true));
	}
	
	
	@Override
	public String toString() {
		return "BuildTurret";
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
