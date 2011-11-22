package com.pekalicious.starplanner.actions.terran;

import java.io.Serializable;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.Logger;
import com.pekalicious.agent.Agent;
import com.pekalicious.agent.WorkingMemoryFact;
import com.pekalicious.agent.WorldState;
import com.pekalicious.agent.WorldStateValue;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.StarMemoryFact;
import com.pekalicious.starplanner.StarMemoryFactType;
import com.pekalicious.starplanner.StarPlanner;
import com.pekalicious.starplanner.TrainingOrder;
import com.pekalicious.starplanner.WSKey;
import com.pekalicious.starplanner.actions.StarAction;
import com.pekalicious.starplanner.util.UnitUtils;

public class ActionTrainFirebat extends StarAction implements Serializable {
	private static final long serialVersionUID = -7560261995935171736L;

	TrainingOrder order;
	
	@Override
	public void setupConditions() {
		this.preconditions = new WorldState();
		this.preconditions.setProperty(WSKey.T_BARRACKS, new WorldStateValue<Boolean>(true));
		this.preconditions.setProperty(WSKey.T_ACADEMY, new WorldStateValue<Boolean>(true));
		
		this.effects = new WorldState();
		this.effects.setProperty(WSKey.T_FIREBAT, new WorldStateValue<Boolean>(true));

		this.cost = 2.0f;
		this.precedence = 1;
	}

	@Override
	public void activateAction(Agent agent, WorldState state) {
		int count = Game.getInstance().self().completedUnitCount(UnitType.TERRAN_BARRACKS);
		WorkingMemoryFact[] needFacts = agent.getWorkingMemory().getFacts(StarMemoryFactType.NEED_TYPE, null);
		if (needFacts.length > 0) {
			for (WorkingMemoryFact fact : needFacts) {
				StarMemoryFact starFact = (StarMemoryFact)fact;
				if (starFact.needType.equals(UnitType.TERRAN_FIREBAT)) {
					if (starFact.needTypeCount > 0) {
						count = starFact.needTypeCount;
					}else{
						Logger.Debug("TrainFirebat:\tNeed Firebat without count!\n", 1);
						//count = Game.getInstance().self().completedUnitCount(UnitType.TERRAN_BARRACKS);
					}
				}
			}
		}

		StarBlackboard bb = (StarBlackboard)((StarPlanner)agent).getBlackBoard(); 
		order = bb.addToTrainingQueue(UnitUtils.Type.TERRAN_FIREBAT, count);
		order = bb.addToTrainingQueue(UnitUtils.Type.TERRAN_FIREBAT, count);
		order = bb.addToTrainingQueue(UnitUtils.Type.TERRAN_MEDIC, count);
		

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
	
	@Override
	public void deactivateAction(Agent agent) {
		if (!order.status.equals(OrderStatus.Ended))
			((StarBlackboard)agent.getBlackBoard()).trainingQueue.remove(order);
	}
	
	@Override
	public String toString() {
		return "TrainFirebat";
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

