package com.pekalicious.starplanner.actions.terran;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.agent.Agent;
import com.pekalicious.agent.WorldState;
import com.pekalicious.agent.WorldStateValue;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.StarPlanner;
import com.pekalicious.starplanner.WSKey;
import com.pekalicious.starplanner.actions.StarAction;
import com.pekalicious.starplanner.AddonOrder;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.util.UnitUtils;

public class ActionBuildControlTower extends StarAction {
	private static final long serialVersionUID = -35164550637026L;

	AddonOrder order;
	
	@Override
	public void setupConditions() {
		this.preconditions = new WorldState();
		this.preconditions.setProperty(WSKey.T_STARPORT, new WorldStateValue<Boolean>(true));
		
		this.effects = new WorldState();
		this.effects.setProperty(WSKey.T_CONTROL_TOWER, new WorldStateValue<Boolean>(true));
		
		this.cost = 1.0f;
		this.precedence = 1;
	}

	@Override
	public void activateAction(Agent aiManager, WorldState state) {
		int count = Game.getInstance().self().completedUnitCount(UnitType.TERRAN_CONTROL_TOWER);

		if (count == 0) {
			StarBlackboard bb = (StarBlackboard)((StarPlanner)aiManager).getBlackBoard(); 
			order = bb.addToAddonQueue(UnitUtils.Type.TERRAN_CONTROL_TOWER);
		}else{
			order = new AddonOrder();
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
			//if (!aiManager.getWorkingMemory().containsKey(WSKey.T_FACTORY)) return true;
			
			//int barracksCount = aiManager.<Integer>getWorkingMemoryValue(WSKey.T_FACTORY).getValue();
			//return barracksCount < 1;
		}
		
		return true;
	}
	
	@Override
	public void applyContextEffect(Agent agent, WorldState state, WorldState worldState) {
		state.setProperty(WSKey.T_CONTROL_TOWER, new WorldStateValue<Boolean>(true));
	}

	public String toString() {
		return "BuildControlTower";
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
