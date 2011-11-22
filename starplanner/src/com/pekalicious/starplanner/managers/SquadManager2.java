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
import com.pekalicious.starplanner.Squad2;
import com.pekalicious.starplanner.SquadOrder;
import com.pekalicious.starplanner.StarBlackboard;
import com.pekalicious.starplanner.StarMemoryFact;
import com.pekalicious.starplanner.StarMemoryFactType;
import com.pekalicious.starplanner.StarPlanner;

public class SquadManager2 {
	private Squad2 squad2;
	private WorkingMemory workingMemory;
	private StarBlackboard blackboard;
	
	public SquadManager2(StarPlanner planner) {
		this.squad2 = ((StarBlackboard)planner.getBlackBoard()).squad2;
		this.workingMemory = planner.getWorkingMemory();
		this.blackboard = (StarBlackboard)planner.getBlackBoard();
	}
	
	private List<Unit> deadUnits = new ArrayList<Unit>();
	private List<Unit> toFarAway = new ArrayList<Unit>();
	private int waitForOthersCounter;
	//private int waitingForOthersCounter;
	public void update() {
		if (this.squad2 == null) return;
		
		deadUnits.clear();
		for (Unit unit : this.squad2.units){
			
			if (unit.getHitPoints() <= 0){
				Logger.Debug("SquadMngr2:\tUnit dead!\n", 3);
				deadUnits.add(unit);
				if (this.squad2.leader != null && this.squad2.leader.equals(unit))
					this.squad2.leader = null;
			}
		}
		
		for (Unit deadUnit : deadUnits)
			this.squad2.units.remove(deadUnit);
		

		
		
		if (!this.squad2.orderStatus.equals(OrderStatus.Invalid) && !this.squad2.orderStatus.equals(OrderStatus.Ended)) {
			switch (this.squad2.order) {
				case Create:
					//this.squad.units.clear();
					this.squad2.leader = null;
					for (Unit unit : Game.getInstance().self().getUnits()) {
						if (unit.getType().isBuilding()) continue;
						if (unit.getType().equals(UnitType.TERRAN_SCV)) continue;
						if (unit.getType().equals(UnitType.TERRAN_MARINE)) continue;
						if (unit.getType().equals(UnitType.TERRAN_MEDIC)) continue;
						if (unit.getType().equals(UnitType.TERRAN_FIREBAT)) continue;
						if (unit.getHitPoints() <= 0) continue;
						Logger.Debug("SquadMngr2:\tAdding unit " + unit.getType().getName() + " to squad2\n", 3);
							if (this.squad2.units.size() < 10 ) {
								/*for (Unit unit2 : this.squad2.units) {
									if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) {
										Logger.Debug("SquadMngr:\tMODE SIEGE DESACTIF car je dois partirrrrrrrrrrrrrrrrrrrr !!\n", 1);
										unit2.unsiege();
									}
								}*/
								this.squad2.units.add(unit); //ajout
							}
						}// ajout
					//}
					if (this.squad2.units.size() > 0) {
						validLeader2();
						if (blackboard.squadIgnoreUnitDistance) {
							blackboard.squadIgnoreUnitDistance = false;
						}else{
							toFarAway.clear();
							for (Unit unit : this.squad2.units) {
								if (unit == this.squad2.leader) continue;
								if (unit.getDistance(this.squad2.leader)>500) toFarAway.add(unit);
							}
							Logger.Debug("SquadMngr2:\t" + toFarAway.size() + " units are too far.\n", 3);
							for (Unit unit : toFarAway)
								this.squad2.units.remove(unit);
						}
						Logger.Debug("SquadMngr2:\tCreated squad2 with " + this.squad2.units.size() + " unit\n", 3);
						this.squad2.orderStatus = OrderStatus.Ended;
					} 
					else{
						Logger.Debug("SquadMngr2:\tNo units for squad2 found!\n", 1);
						this.squad2.orderStatus = OrderStatus.Invalid;
						
					}
					
				break;
				case GoTo:
					validLeader2();
					
					if (this.squad2.orderStatus.equals(OrderStatus.Idle)) {
						if (this.squad2.destination == null) {
							Logger.Debug("SquadMngr:\tNo destination!\n", 1);
							this.squad2.orderStatus = OrderStatus.Invalid;
							break;
						}else{
							Logger.Debug("SquadMngr2:\tMoving squad2 to " + this.squad2.destination.x() + "," + this.squad2.destination.y() + "\n", 3);
							Logger.Debug("SquadMngr2:\tLeader2 at " + this.squad2.leader.getPosition().x() + "," + this.squad2.leader.getPosition().y() + "\n", 3);
						}
						this.squad2.enemyNearDestination = false;
						for (Unit unit : this.squad2.units) {
							if (!unit.rightClick(squad2.destination)) {
								Logger.Debug("SquadMngr2:\tGoto command not accepted!\n", 1);
							}
						}
						this.squad2.orderStatus = OrderStatus.Started;
					}
					
					
					if (this.squad2.orderStatus.equals(OrderStatus.Started)){
						double dist = this.squad2.leader.getDistance(this.squad2.destination);
						if (dist < 1000) {
							boolean everybodyReached = true;
							for (Unit unit : this.squad2.units) {
								if (unit.getDistance(this.squad2.destination) > 450){
									everybodyReached = false;
									//debut ajout
									
									this.squad2.enemyNearDestination = enemyNear2();
									if (this.squad2.enemyNearDestination) {
										for (Unit unit2 : this.squad2.units) {
											if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
												Logger.Debug("SquadMngr:\tMODE SIEGE ACTIF car ennemy\n", 1);
												unit2.siege();
											}
										}
										everybodyReached = true;
										
									}
									//fin ajout
									
								}
							}
							if (everybodyReached) {
								Logger.Debug("SquadMngr2:\tDestination reached!\n", 1);
								this.squad2.orderStatus = OrderStatus.Ended;
								//debut ajout
								/*for (Unit unit2 : this.squad.units) {
									if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
										Logger.Debug("SquadMngr:\tMODE SIEGE ACTIF car destinationnnn reached !\n", 1);
										unit2.siege();
									}
								}*/
								//fin ajout
								
								//this.squad2.order = SquadOrder.AttackNearbyEnemies;
								//this.squad2.orderStatus = OrderStatus.Idle;
								this.squad2.enemyNearDestination = enemyNear2();
								
								
								if (!this.squad2.enemyNearDestination) {
									Logger.Debug("SquadMngr2:\tNo enemies near destination!\n", 3);
									//debut ajout
									for (Unit unit2 : this.squad2.units) {
										if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) {
											Logger.Debug("SquadMngr:\tMODE SIEGE DESACTIF car je dois partirrrrrrrrrrrrrrrrrrrr !!\n", 1);
											unit2.unsiege();
										}
									}
									//fin ajout
								}
								for (Unit unit2 : this.squad2.units) {
									if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
										Logger.Debug("SquadMngr:\tMODE SIEGE ACTIF car ennemy\n", 1);
										unit2.siege();
									}
								}
								break;
							}
						}else{
							if (this.squad2.leader.getOrder().equals(Order.PLAYER_GUARD)) {
								if (this.squad2.leader.getOrderTimer() == 0) {
									Logger.Debug("SquadMngr2:\tDestination unreachable!\n", 1);
									this.squad2.orderStatus = OrderStatus.Next;
									break;
								}
							}else if (this.squad2.leader.getOrder().equals(Order.MOVE)) {
								if (this.squad2.leader.getOrderTimer() == 0) {
									if (--waitForOthersCounter > 0) break;
									waitForOthersCounter = 60;
									boolean waitForOthers = false;
									for (Unit unit : this.squad2.units)
										if (unit.getDistance(this.squad2.leader) > 400)
											waitForOthers = true;
									
									if (waitForOthers) {
										this.squad2.leader.holdPosition();
										//waitingForOthersCounter = 5;
										for (Unit unit : this.squad2.units) {
											if (unit.getDistance(this.squad2.leader) < 400) {
												unit.holdPosition();
											}else{
												//if (!
														unit.rightClick(this.squad2.leader.getPosition());//) {
													//unit.rightClick(this.squad.destination);
												//}
											}
										}
									}
								}
							}else if ((this.squad2.leader.getOrder().equals(Order.HOLD_POSITION_1))||
									(this.squad2.leader.getOrder().equals(Order.HOLD_POSITION_2)) ||
									(this.squad2.leader.getOrder().equals(Order.HOLD_POSITION_3)) ||
									(this.squad2.leader.getOrder().equals(Order.HOLD_POSITION_4))) {
								if (this.squad2.leader.getOrderTimer() == 0) {
									//if (--waitingForOthersCounter == 0) {
										//this.squad.orderStatus = OrderStatus.Idle;
										//break;
									//}
									boolean waitingForOthers = false;
									for (Unit unit : this.squad2.units)
										if (unit.getDistance(this.squad2.leader) > 400)
											waitingForOthers = unit.getOrder().equals(Order.MOVE);
									
									if (!waitingForOthers) this.squad2.orderStatus = OrderStatus.Idle;
								}
							}else if (!this.squad2.leader.getOrder().equals(Order.MOVE)) {
								Logger.Debug("SquadMngr2:\tUnit not obaying: " + this.squad2.leader.getOrder().getName() + "\n", 1);
							}
						}
					}
					
				break;
				case AttackNearbyEnemies:
					validLeader2();
				
					if (enemyNear2()) {
						//debut wait
						/*
						if (--waitForOthersCounter > 0) break;
						waitForOthersCounter = 150;
						boolean waitForOthers = false;
						for (Unit unit : this.squad2.units)
							if (unit.getDistance(this.squad2.leader) > 300)
								waitForOthers = true;
						
						if (waitForOthers) {
							this.squad2.leader.holdPosition();
							//waitingForOthersCounter = 5;
							for (Unit unit : this.squad2.units) {
								if (unit.getDistance(this.squad2.leader) < 300) {
									unit.holdPosition();
								}else{
									//if (!
									for (Unit unit2 : this.squad2.units) {
										if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) {
											Logger.Debug("SquadMngr2:\tMODE SIEGE DESACTIF car j'en ai marre!\n", 1);
											unit2.unsiege();
										}
									}
											unit.rightClick(this.squad2.leader.getPosition());//) {
										//unit.rightClick(this.squad.destination);
									//}
								}
							}
						}
					*/
						//fin wait
						if (this.squad2.orderStatus.equals(OrderStatus.Idle)) {
							//this.squad2.enemyTarget = getWeakestNearEnemy(); a ete commente
							this.squad2.enemyTarget = getnearestEnemy();
							Logger.Debug("SquadMngr2:\tJe veux l'ennemi le plus proche !\n", 1);
							//debut ajout
							for (Unit unit2 : this.squad2.units) {
								if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
									Logger.Debug("SquadMngr2:\tMODE SIEGE ACTIF car des ennemis ont �t� rep�r�s !\n", 1);
									unit2.siege();
								}
							}
							//fin ajout
							if (this.squad2.enemyTarget == null) {
								Logger.Debug("SquadMngr2:\tNo more enemies near!\n", 3);
								//debut ajout
								for (Unit unit2 : this.squad2.units) {
									if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) {
										Logger.Debug("SquadMngr2:\tMODE SIEGE DESACTIF car j'en ai marre!\n", 1);
										unit2.unsiege();
									}
								}
								//fin ajout
								this.squad2.orderStatus = OrderStatus.Ended;
								
							}else{
								this.squad2.orderStatus = OrderStatus.Started;
							}
						}else if (this.squad2.orderStatus.equals(OrderStatus.Started)){
							for (Unit unit : this.squad2.units) {
								if (!unit.attackUnit(this.squad2.enemyTarget)) {
									Logger.Debug("SquadMngr2:\tCould not attack unit!\n", 1);
									this.squad2.orderStatus = OrderStatus.Invalid;
									return;
								}
							}
							this.squad2.orderStatus = OrderStatus.Next;
						}else if (this.squad2.orderStatus.equals(OrderStatus.Next)){
							if (!this.squad2.enemyTarget.isVisible()) {
								Logger.Debug("SquadMngr2:\tTarget not visible! NEEEXT\n", 3);
								this.squad2.orderStatus = OrderStatus.Idle;
							}else{
								if (this.squad2.enemyTarget.getDistance(this.squad2.leader) > 500) {
									Logger.Debug("SquadMngr2:\tEnemy too far. NEEEEXT!\n", 3);
									this.squad2.orderStatus = OrderStatus.Idle;
								}else{
									if (this.squad2.enemyTarget.getHitPoints() <= 0) {
										Logger.Debug("SquadMngr2:\tTarget Dead! NEEEXT!\n", 3);
										this.squad2.orderStatus = OrderStatus.Idle;
									}
								}
							}
						}
					}else{
						//debut ajout
						for (Unit unit2 : this.squad2.units) {
							if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) {
								Logger.Debug("SquadMngr2:\tMODE SIEGE DESACTIF car je dois partir !!\n", 1);
								unit2.unsiege();
							}
						}
						
						
						
						//fin ajout
						this.squad2.orderStatus = OrderStatus.Ended;
						
					}
					for (Unit unit2 : this.squad2.units) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) {
							Logger.Debug("SquadMngr2:\tMODE SIEGE DESACTIF car je dois partir !!\n", 1);
							unit2.unsiege();
						}
					}
				break;
			}
		}
		
	}
	
	public boolean enemyNear2() {
		WorkingMemoryFact[] facts = this.workingMemory.getFacts(StarMemoryFactType.ENEMY_UNIT, null);
		if (facts.length == 0) return false;
		
		for (WorkingMemoryFact fact : facts) {
			StarMemoryFact starFact = (StarMemoryFact)fact;
			if (this.squad2.leader.getDistance(starFact.enemyUnit) < 350 && (starFact.enemyType != UnitType.ZERG_OVERLORD)) return true;
		}
		
		Logger.Debug("SquadMngr:\tNo enemies nearby!\n", 3);
		for (Unit unit2 : this.squad2.units) {
			if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) {
				Logger.Debug("SquadMngr2:\tMODE SIEGE DESACTIF car je dois partir !!\n", 1);
				unit2.unsiege();
			}
		}
		return false;
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
					if (this.squad2.leader.getDistance(starFact.enemyUnit) < this.squad2.leader.getDistance(nearest))						
						nearest = starFact.enemyUnit;
				}
			
			}
		//}
		Logger.Debug("SquadMngr2:\tJ'ai trouv� le plus proche !\n", 1);
		return nearest;
	}
	
	//fin ajout fonction
	public void onEnemyUnitShow(Unit enemyUnit) {
		if (this.squad2.orderStatus.equals(OrderStatus.Invalid)) return;
		if (this.squad2.orderStatus.equals(OrderStatus.Ended)) return;
		
		switch (this.squad2.order) {
			case GoTo:
				Position destination = this.squad2.destination;
				if (destination!=null) {
					double dist = enemyUnit.getDistance(destination);
					if (dist > 600) {
						Logger.Debug("SquadMngr:\tEnemy away from destination!\n", 1);
						this.squad2.orderStatus = OrderStatus.Invalid;
						this.squad2.order = SquadOrder.None;
					}else{
						this.squad2.orderStatus = OrderStatus.Ended; 
					}
				}
			break;
		}
	}
	
	public void onEnemyUnitShow2(Unit enemyUnit) {
		if (this.squad2.orderStatus.equals(OrderStatus.Invalid)) return;
		if (this.squad2.orderStatus.equals(OrderStatus.Ended)) return;
		
		switch (this.squad2.order) {
			case GoTo:
				Position destination = this.squad2.destination;
				if (destination!=null) {
					double dist = enemyUnit.getDistance(destination);
					if (dist > 1000) {
						Logger.Debug("SquadMngr:\tEnemy away from destination!\n", 1);
						this.squad2.orderStatus = OrderStatus.Invalid;
						this.squad2.order = SquadOrder.None;
					}else{
						this.squad2.orderStatus = OrderStatus.Ended; 
					}
				}
			break;
		}
	}
	
	
	
	private void validLeader2() {
		if (this.squad2.leader == null || this.squad2.leader.getHitPoints() <= 0) {
			if (this.squad2.units.size() > 0) {
				if (this.squad2.destination == null) {
					//debut
					/*for (Unit unit : this.squad.units){
						if (unit.getType().equals(UnitType.TERRAN_MEDIC)){
							continue;
						}else{
						
							this.squad.leader = this.squad.units.; 
							if(this.squad.leader.equals(unit)){
								
								this.squad.leader = null;
								Logger.Debug("SquadMngr:\tUn Medic ne peut �tre leader !\n", 3);
								
							}
								
						}
					}*/
					//fin ajout
					
					//this.squad.leader = this.squad.units.get(0); a �t� commente
				}else{ 
					Unit closestToDestination = this.squad2.units.get(0);
					for (Unit unit : this.squad2.units) {
						if (!unit.getType().equals(UnitType.TERRAN_MEDIC)) {
						double cDist = closestToDestination.getDistance(this.squad2.destination);
						double uDist = unit.getDistance(this.squad2.destination);
						if (uDist < cDist)
							closestToDestination = unit;
						}
					}
					this.squad2.leader = closestToDestination;
				}
			}else{
				//Logger.Debug("SquadMngr:\tNo units in squad. Cannot assign leader!\n", 1);
				this.squad2.orderStatus = OrderStatus.Invalid;
				this.squad2.order = SquadOrder.None;
			}
		}
	}
}
