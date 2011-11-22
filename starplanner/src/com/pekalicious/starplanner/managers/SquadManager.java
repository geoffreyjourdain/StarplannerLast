package com.pekalicious.starplanner.managers;

import java.util.ArrayList;
import java.util.List;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.Order;
import org.bwapi.bridge.model.Position;
import org.bwapi.bridge.model.Unit;
import org.bwapi.bridge.model.UnitType;

import com.pekalicious.Logger;
import com.pekalicious.agent.WorkingMemory;
import com.pekalicious.agent.WorkingMemoryFact;
import com.pekalicious.starplanner.OrderStatus;
import com.pekalicious.starplanner.Squad;
import com.pekalicious.starplanner.SquadOrder;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.StarMemoryFact;
import com.pekalicious.starplanner.StarMemoryFactType;
import com.pekalicious.starplanner.StarPlanner;

public class SquadManager {
	private Squad squad;
	private WorkingMemory workingMemory;
	private StarBlackboard blackboard;

	public SquadManager(StarPlanner planner) {
		this.squad = ((StarBlackboard)planner.getBlackBoard()).squad;
		this.workingMemory = planner.getWorkingMemory();
		this.blackboard = (StarBlackboard)planner.getBlackBoard();
	}
	
	private List<Unit> deadUnits = new ArrayList<Unit>();
	private List<Unit> toFarAway = new ArrayList<Unit>();
	private int waitForOthersCounter;
	//private int waitingForOthersCounter;
	public void update() {
		if (this.squad == null) return;
		
		deadUnits.clear();
		for (Unit unit : this.squad.units){
			/*if (unit.getType().equals(UnitType.TERRAN_MEDIC)){
				if(this.squad.leader.equals(unit)){
					
					this.squad.leader = null;
					Logger.Debug("SquadMngr:\tUn Medic ne peut être leader !\n", 1);
					
				}
					
			}*/
			if (unit.getHitPoints() <= 0){
				Logger.Debug("SquadMngr:\tUnit dead!\n", 3);
				deadUnits.add(unit);
				if (this.squad.leader != null && this.squad.leader.equals(unit))
					this.squad.leader = null;
			}
		}
		
		for (Unit deadUnit : deadUnits)
			this.squad.units.remove(deadUnit);

		
		
		if (!this.squad.orderStatus.equals(OrderStatus.Invalid) && !this.squad.orderStatus.equals(OrderStatus.Ended)) {
			switch (this.squad.order) {
				case Create:
					//this.squad.units.clear();
					this.squad.leader = null;
					//debut ajout défense
					/*
		    		for (Unit unit : Game.getInstance().self().getUnits()) {
		    			if (unit.getType().equals(UnitType.TERRAN_MARINE)) continue;
		    			if (unit.getType().equals(UnitType.TERRAN_FIREBAT)) continue;
		    			if (unit.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) continue;
		    			if (unit.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) continue;
		    			if (unit.getType().equals(UnitType.TERRAN_MEDIC)) continue;
		    			if (unit.getHitPoints() < 60){
		    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
		    				if (unit2.getType().equals(UnitType.TERRAN_SCV)) {
		    					this.squad.units.add(unit2);
		    					Logger.Debug("SquadMngr:\tAdding unit " + unit.getType().getName() + " to squadDEF\n", 1);
		    				}
		    				}
		    			}
		    		}
		    		*/
		    		//fin ajout défense
					
					for (Unit unit : Game.getInstance().self().getUnits()) {
						if (unit.getType().isBuilding()) continue;
						if (unit.getType().equals(UnitType.TERRAN_SCV)) continue;
						if (unit.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) continue;
						if (unit.getHitPoints() <= 0) continue;
						Logger.Debug("SquadMngr:\tAdding unit " + unit.getType().getName() + " to squad\n", 3);
							if (this.squad.units.size() < 15 ) {
							this.squad.units.add(unit); //ajout
							}
						}// ajout
					//}
					if (this.squad.units.size() > 0) {
						validLeader();
						if (blackboard.squadIgnoreUnitDistance) {
							blackboard.squadIgnoreUnitDistance = false;
						}else{
							toFarAway.clear();
							for (Unit unit : this.squad.units) {
								if (unit == this.squad.leader) continue;
								if (unit.getDistance(this.squad.leader)>500) toFarAway.add(unit);
							}
							Logger.Debug("SquadMngr:\t" + toFarAway.size() + " units are too far.\n", 3);
							for (Unit unit : toFarAway)
								this.squad.units.remove(unit);
						}
						Logger.Debug("SquadMngr:\tCreated squad with " + this.squad.units.size() + " unit\n", 3);
						this.squad.orderStatus = OrderStatus.Ended;
					} 
					else{
						Logger.Debug("SquadMngr:\tNo units for squad found!\n", 1);
						this.squad.orderStatus = OrderStatus.Invalid;
						
					}
				break;
				case GoTo:
					validLeader();
				
					if (this.squad.orderStatus.equals(OrderStatus.Idle)) {
						if (this.squad.destination == null) {
							Logger.Debug("SquadMngr:\tNo destination!\n", 1);
							this.squad.orderStatus = OrderStatus.Invalid;
							break;
						}else{
							Logger.Debug("SquadMngr:\tMoving squad to " + this.squad.destination.x() + "," + this.squad.destination.y() + "\n", 3);
							Logger.Debug("SquadMngr:\tLeader at " + this.squad.leader.getPosition().x() + "," + this.squad.leader.getPosition().y() + "\n", 3);
						}
						this.squad.enemyNearDestination = false;
						for (Unit unit : this.squad.units) {
							if (!unit.rightClick(squad.destination)) {
								Logger.Debug("SquadMngr:\tGoto command not accepted!\n", 1);
							}
						}
						this.squad.orderStatus = OrderStatus.Started;
					}
					
					
					if (this.squad.orderStatus.equals(OrderStatus.Started)){
						double dist = this.squad.leader.getDistance(this.squad.destination);
						if (dist < 600) {
							boolean everybodyReached = true;
							for (Unit unit : this.squad.units) {
								if (unit.getDistance(this.squad.destination) > 350){
									everybodyReached = false;
//debut ajout
									
									this.squad.enemyNearDestination = enemyNear();
									if (this.squad.enemyNearDestination) {
										
										everybodyReached = true;
										
									}
									//fin ajout
								}
							}
							if (everybodyReached) {
								Logger.Debug("SquadMngr:\tDestination reached!\n", 1);
								this.squad.orderStatus = OrderStatus.Ended;
								
								for (Unit unit : this.squad.units) { // ajout
									if (unit.getType().equals(UnitType.TERRAN_MEDIC)) { //ajout
									//this.squad.units.remove(unit); //ajout
									this.squad.medic = unit;
									this.squad.medic.follow(this.squad.leader);
									this.squad.units.remove(unit);
									Logger.Debug("SquadMngr:\tLe médic suit vos ordres chef !!\n", 1);//ajout
									
									break;
									}//ajout
									
								}//ajout
								//this.squad.order = SquadOrder.AttackNearbyEnemies;
								//this.squad.orderStatus = OrderStatus.Idle;
								this.squad.enemyNearDestination = enemyNear();
								
								
								if (!this.squad.enemyNearDestination) {
									Logger.Debug("SquadMngr:\tNo enemies near destination!\n", 3);
								}
								break; //a été redécommenté
						}else{
							if (this.squad.leader.getOrder().equals(Order.PLAYER_GUARD)) {
								if (this.squad.leader.getOrderTimer() == 0) {
									Logger.Debug("SquadMngr:\tDestination unreachable!\n", 1);
									this.squad.orderStatus = OrderStatus.Next;
									break;
								}
							}else if (this.squad.leader.getOrder().equals(Order.MOVE)) {
								if (this.squad.leader.getOrderTimer() == 0) {
									if (--waitForOthersCounter > 0) break;
									waitForOthersCounter = 60;
									boolean waitForOthers = false;
									for (Unit unit : this.squad.units)
										if (unit.getDistance(this.squad.leader) > 250)
											waitForOthers = true;
									
									if (waitForOthers) {
										this.squad.leader.holdPosition();
										//waitingForOthersCounter = 5;
										for (Unit unit : this.squad.units) {
											if (unit.getDistance(this.squad.leader) < 250) {
												unit.holdPosition();
											}else{
												//if (!
														unit.rightClick(this.squad.leader.getPosition());//) {
													//unit.rightClick(this.squad.destination);
												//}
											}
										}
									}
								}
							}else if ((this.squad.leader.getOrder().equals(Order.HOLD_POSITION_1))||
									(this.squad.leader.getOrder().equals(Order.HOLD_POSITION_2)) ||
									(this.squad.leader.getOrder().equals(Order.HOLD_POSITION_3)) ||
									(this.squad.leader.getOrder().equals(Order.HOLD_POSITION_4))) {
								if (this.squad.leader.getOrderTimer() == 0) {
									//if (--waitingForOthersCounter == 0) {
										//this.squad.orderStatus = OrderStatus.Idle;
										//break;
									//}
									boolean waitingForOthers = false;
									for (Unit unit : this.squad.units)
										if (unit.getDistance(this.squad.leader) > 300)
											waitingForOthers = unit.getOrder().equals(Order.MOVE);
									
									if (!waitingForOthers) this.squad.orderStatus = OrderStatus.Idle;
								}
							}else if (!this.squad.leader.getOrder().equals(Order.MOVE)) {
								Logger.Debug("SquadMngr:\tUnit not obaying: " + this.squad.leader.getOrder().getName() + "\n", 1);
							}
						}
					}
					}
				break;
				case AttackNearbyEnemies:
					validLeader();
					
					if (enemyNear()) {
						//debut wait
						/*
						if (--waitForOthersCounter > 0) break;
						waitForOthersCounter = 150;
						boolean waitForOthers = false;
						for (Unit unit : this.squad.units)
							if (unit.getDistance(this.squad.leader) > 150)
								waitForOthers = true;
						
						if (waitForOthers) {
							this.squad.leader.holdPosition();
							//waitingForOthersCounter = 5;
							for (Unit unit : this.squad.units) {
								if (unit.getDistance(this.squad.leader) < 150) {
									unit.holdPosition();
								}else{
									//if (!
									
											unit.rightClick(this.squad.leader.getPosition());//) {
										//unit.rightClick(this.squad.destination);
									//}
								}
							}
						}
					*/
						//fin wait
						if (this.squad.orderStatus.equals(OrderStatus.Idle)) {
							//this.squad.enemyTarget = getWeakestNearEnemy(); a ete commente
							this.squad.enemyTarget = getnearestEnemy();
							Logger.Debug("SquadMngr:\tJe veux l'ennemi le plus proche !\n", 1);
							
							if (this.squad.enemyTarget == null) {
								Logger.Debug("SquadMngr:\tNo more enemies near!\n", 3);
								
								this.squad.orderStatus = OrderStatus.Ended;
								
							}else{
								this.squad.orderStatus = OrderStatus.Started;
							}
						}else if (this.squad.orderStatus.equals(OrderStatus.Started)){
							for (Unit unit : this.squad.units) {
								if (!unit.attackUnit(this.squad.enemyTarget)) {
									Logger.Debug("SquadMngr:\tCould not attack unit!\n", 1);
									this.squad.orderStatus = OrderStatus.Invalid;
									return;
								}
							}
							this.squad.orderStatus = OrderStatus.Next;
						}else if (this.squad.orderStatus.equals(OrderStatus.Next)){
							if (!this.squad.enemyTarget.isVisible()) {
								Logger.Debug("SquadMngr:\tTarget not visible! NEEEXT\n", 3);
								this.squad.orderStatus = OrderStatus.Idle;
							}else{
								if (this.squad.enemyTarget.getDistance(this.squad.leader) > 500) {
									Logger.Debug("SquadMngr:\tEnemy too far. NEEEEXT!\n", 3);
									this.squad.orderStatus = OrderStatus.Idle;
								}else{
									if (this.squad.enemyTarget.getHitPoints() <= 0) {
										Logger.Debug("SquadMngr:\tTarget Dead! NEEEXT!\n", 3);
										this.squad.orderStatus = OrderStatus.Idle;
									}
								}
							}
						}
					}else{
						this.squad.orderStatus = OrderStatus.Ended;
						
					}

				break;
			}
		}
		
	}
	
	public boolean enemyNear() {
		WorkingMemoryFact[] facts = this.workingMemory.getFacts(StarMemoryFactType.ENEMY_UNIT, null);
		if (facts.length == 0) return false;
		
		for (WorkingMemoryFact fact : facts) {
			StarMemoryFact starFact = (StarMemoryFact)fact;
			if (this.squad.leader.getDistance(starFact.enemyUnit) < 350 && (starFact.enemyType != UnitType.ZERG_OVERLORD)) return true;
		}
		
		Logger.Debug("SquadMngr:\tNo enemies nearby!\n", 3);
		
		return false;
	}
	
	
	public Unit getWeakestNearEnemy() {
		WorkingMemoryFact[] facts = this.workingMemory.getFacts(StarMemoryFactType.ENEMY_UNIT, null);
		if (facts.length == 0) return null;
		
		Unit weakest = null;
		for (WorkingMemoryFact fact : facts) {
			StarMemoryFact starFact = (StarMemoryFact)fact;
			if (this.squad.leader.getDistance(starFact.enemyUnit) < 500) {
				if (weakest == null) {
					weakest = starFact.enemyUnit;
				}else{
					if (starFact.enemyUnit.getHitPoints() > 0 && starFact.enemyUnit.getHitPoints() < weakest.getHitPoints())
						weakest = starFact.enemyUnit;
				}
			}
		}
		
		return weakest;
	}
	
	//ajout fonction
	public Unit getnearestEnemy() {
		WorkingMemoryFact[] facts = this.workingMemory.getFacts(StarMemoryFactType.ENEMY_UNIT, null);
		if (facts.length == 0) return null;
		
		Unit nearest = null;
		for (WorkingMemoryFact fact : facts) {
			StarMemoryFact starFact = (StarMemoryFact)fact;
			//if (this.squad.leader.getDistance(starFact.enemyUnit) < 500) {
			
				if (nearest == null) {
					nearest = starFact.enemyUnit;
				}else{
					if (this.squad.leader.getDistance(starFact.enemyUnit) < this.squad.leader.getDistance(nearest))						
						nearest = starFact.enemyUnit;
				}
			
			}
		//}
		Logger.Debug("SquadMngr:\tJ'ai trouvé le plus proche !\n", 1);
		return nearest;
	}
	
	//fin ajout fonction
	public void onEnemyUnitShow(Unit enemyUnit) {
		if (this.squad.orderStatus.equals(OrderStatus.Invalid)) return;
		if (this.squad.orderStatus.equals(OrderStatus.Ended)) return;
		
		switch (this.squad.order) {
			case GoTo:
				Position destination = this.squad.destination;
				if (destination!=null) {
					double dist = enemyUnit.getDistance(destination);
					if (dist > 600) {
						Logger.Debug("SquadMngr:\tEnemy away from destination!\n", 1);
						this.squad.orderStatus = OrderStatus.Invalid;
						this.squad.order = SquadOrder.None;
					}else{
						this.squad.orderStatus = OrderStatus.Ended; 
					}
				}
			break;
		}
	}
	
	private void validLeader() {
		if (this.squad.leader == null || this.squad.leader.getHitPoints() <= 0) {
			if (this.squad.units.size() > 0) {
				if (this.squad.destination == null) {
					//debut
					/*for (Unit unit : this.squad.units){
						if (unit.getType().equals(UnitType.TERRAN_MEDIC)){
							continue;
						}else{
						
							this.squad.leader = this.squad.units.; 
							if(this.squad.leader.equals(unit)){
								
								this.squad.leader = null;
								Logger.Debug("SquadMngr:\tUn Medic ne peut être leader !\n", 3);
								
							}
								
						}
					}*/
					//fin ajout
					
					//this.squad.leader = this.squad.units.get(0); a été commente
				}else{
					Unit closestToDestination = this.squad.units.get(0);
					for (Unit unit : this.squad.units) {
						if (!unit.getType().equals(UnitType.TERRAN_MEDIC)) {
						double cDist = closestToDestination.getDistance(this.squad.destination);
						double uDist = unit.getDistance(this.squad.destination);
						if (uDist < cDist)
							closestToDestination = unit;
						}
					}
					this.squad.leader = closestToDestination;
				}
			}else{
				Logger.Debug("SquadMngr:\tNo units in squad. Cannot assign leader!\n", 1);
				this.squad.orderStatus = OrderStatus.Invalid;
				this.squad.order = SquadOrder.None;
			}
		}
	}
	
}
