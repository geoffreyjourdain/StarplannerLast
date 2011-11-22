package com.pekalicious.starplanner.actions.terran;

import java.io.Serializable;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.Logger;
import com.pekalicious.agent.Agent;
import com.pekalicious.agent.WorkingMemoryFact;
import com.pekalicious.agent.WorldState;
import com.pekalicious.agent.WorldStateValue;
import com.pekalicious.starplanner.BuildOrder;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.StarMemoryFact;
import com.pekalicious.starplanner.StarMemoryFactType;
import com.pekalicious.starplanner.StarPlanner;
import com.pekalicious.starplanner.TrainingOrder;
import com.pekalicious.starplanner.WSKey;
import com.pekalicious.starplanner.actions.StarAction;
import com.pekalicious.starplanner.util.UnitUtils;

public class ActionTrainBattlecruiser extends StarAction implements Serializable {
	private static final long serialVersionUID = -82342335441738L;

	TrainingOrder order;
	BuildOrder order2;

	@Override
	public void setupConditions() {
		this.preconditions = new WorldState();
		this.preconditions.setProperty(WSKey.T_PHYSICS_LAB, new WorldStateValue<Boolean>(true));
		
		
		this.effects = new WorldState();
		this.effects.setProperty(WSKey.T_BATTLECRUISER, new WorldStateValue<Boolean>(true));
		this.cost = 10.0f;
		this.precedence = 1;
	}

	@Override
	public void activateAction(Agent aiManager, WorldState state) {
		int count = 1;
		count -= Game.getInstance().self().completedUnitCount(UnitType.TERRAN_BATTLECRUISER);

		if (count > 0) {
			StarBlackboard bb = (StarBlackboard)((StarPlanner)aiManager).getBlackBoard(); 
			order = bb.addToTrainingQueue(UnitUtils.Type.TERRAN_BATTLECRUISER, count);
		}else{
			order = new TrainingOrder();
			order.status = OrderStatus.Ended;
		}
	}
	
	@Override
	public boolean isActionComplete(Agent aiManager) {
		return order.status == OrderStatus.Ended;
	}

	@Override
	public boolean validateAction(Agent aiManager) {
		return !order.status.equals(OrderStatus.Invalid);
	}
	
	@Override
	public boolean validateContextPreconditions(Agent aiManager, WorldState currentState, WorldState goalState, boolean planning) {
		
		if (planning) {
		}
		
		return true;
	}
	
	public String toString() {
		return "TrainBattlecruiser";
	}
	
	@Override
	public void deactivateAction(Agent agent) {
		if (!order.status.equals(OrderStatus.Ended))
			((StarBlackboard)agent.getBlackBoard()).trainingQueue.remove(order);
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
