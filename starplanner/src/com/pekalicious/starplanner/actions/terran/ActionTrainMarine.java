package com.pekalicious.starplanner.actions.terran;

import java.io.Serializable;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.TechType;
import org.bwapi.bridge.model.UnitType;
import org.bwapi.bridge.model.UpgradeType;

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
import com.pekalicious.starplanner.UpgradeOrder;
import com.pekalicious.starplanner.WSKey;
import com.pekalicious.starplanner.actions.StarAction;
import com.pekalicious.starplanner.util.UnitUtils;

public class ActionTrainMarine extends StarAction implements Serializable {
	private static final long serialVersionUID = -7560261995935171736L;
	UpgradeOrder order2;
	TrainingOrder order;
	boolean passe = true;
	@Override
	public void setupConditions() {
		this.preconditions = new WorldState();
		this.preconditions.setProperty(WSKey.T_BARRACKS, new WorldStateValue<Boolean>(true));
		
		this.effects = new WorldState();
		this.effects.setProperty(WSKey.T_MARINES, new WorldStateValue<Boolean>(true));

		this.cost = 1.0f;
		this.precedence = 1;
	}

	@Override
	public void activateAction(Agent agent, WorldState state) {
		int count = Game.getInstance().self().completedUnitCount(UnitType.TERRAN_BARRACKS);
		WorkingMemoryFact[] needFacts = agent.getWorkingMemory().getFacts(StarMemoryFactType.NEED_TYPE, null);
		
		StarBlackboard bb = (StarBlackboard)((StarPlanner)agent).getBlackBoard();
		if (Game.getInstance().self().allUnitCount(UnitType.TERRAN_ACADEMY) == 1) {
			 order2 = bb.addToUpgradeQueue(UnitUtils.Type3.RANGE);
		}
		
		
		if (needFacts.length > 0) {
			for (WorkingMemoryFact fact : needFacts) {
				StarMemoryFact starFact = (StarMemoryFact)fact;
				if (starFact.needType.equals(UnitType.TERRAN_MARINE)) {
					if (starFact.needTypeCount > 0) {
						count = starFact.needTypeCount;
					}else{
						Logger.Debug("TrainMarine:\tNeed Maring without count!\n", 1);
						//count = Game.getInstance().self().completedUnitCount(UnitType.TERRAN_BARRACKS);
					}
				}
			}
		} else{
			Logger.Debug("TrainMarine:\tPas de needfact, 1 Marine par Barrack !!\n", 1);

			count = Game.getInstance().self().completedUnitCount(UnitType.TERRAN_BARRACKS);
		}

		
		order = bb.addToTrainingQueue(UnitUtils.Type.TERRAN_MARINE, count);
		if (Game.getInstance().self().completedUnitCount(UnitType.TERRAN_ACADEMY) > 0){
			order = bb.addToTrainingQueue(UnitUtils.Type.TERRAN_MARINE, count);
			order = bb.addToTrainingQueue(UnitUtils.Type.TERRAN_MEDIC, count);
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
	
	@Override
	public void deactivateAction(Agent agent) {
		if (!order.status.equals(OrderStatus.Ended))
			((StarBlackboard)agent.getBlackBoard()).trainingQueue.remove(order);
	}
	
	@Override
	public String toString() {
		return "TrainMarine";
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
