package com.pekalicious.starplanner.sensors;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.Unit;
import org.bwapi.bridge.model.UnitType;

import smile.Network;

import com.pekalicious.Logger;
import com.pekalicious.agent.WorkingMemory;
import com.pekalicious.agent.WorkingMemoryFact;
import com.pekalicious.starplanner.StarMemoryFact;
import com.pekalicious.starplanner.StarMemoryFactType;
import com.pekalicious.starplanner.StarBlackboard;

public class UnitSensor {
	private WorkingMemory workingMemory;
	private long temps;
	private Network networkPrediction;
	//private long startTime = System.nanoTime();
	
    public UnitSensor(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }
    double colo = 0;
	double dro = 0;
	double hatch = 0;
    private int countdown = time;
    private static final int time = 20;
    public void update() {
    	if (--countdown > 0) return;
    	countdown = time;
    	/*
    	temps = System.nanoTime() - startTime;
    	Logger.Debug("SquadMngr:\ttemps �coul� (en nanosecondes) : " + temps + " !\n", 1);*/
    	
    	
    	
    	
    	WorkingMemoryFact[] typeFacts = workingMemory.getFacts(StarMemoryFactType.ENEMY_TYPE, null);
    	for (WorkingMemoryFact fact : typeFacts) {
    		StarMemoryFact starFact = (StarMemoryFact)fact;
    		if (starFact.enemyType.equals(UnitType.ZERG_LAIR)) continue;
    		if (starFact.enemyType.equals(UnitType.ZERG_DRONE)) continue;
    		if (starFact.enemyType.equals(UnitType.ZERG_LAIR)) continue;

    		
    		//debut ajout
    		loadNetworks();
    		if (starFact.enemyType.equals(UnitType.ZERG_CREEP_COLONY)) {
    			colo = 1 - (starFact.enemyTypeCount) * 0.25;
    			
    			if (colo >= 1){
        			colo = 1;
        		}
    			Logger.Debug("BayesianNetwork:\tcolo = " + colo + "\n", 1);
    		}
    		
  
    		double time = 0;
    		
    		if (starFact.enemyType.equals(UnitType.ZERG_HATCHERY)) {
    			hatch = (starFact.enemyTypeCount) * 0.5;
    			
    			if (hatch >= 1){
        			hatch =1;
        		}
    			Logger.Debug("BayesianNetwork:\thatch = " + hatch + "\n", 1);
    		}
    		
    		//double hatch = (Game.getInstance().enemy().completedUnitCount(UnitType.ZERG_HATCHERY))/2;
    	
    		if (starFact.enemyType.equals(UnitType.ZERG_DRONE)) {
    			dro = 1 - (starFact.enemyTypeCount) * 0.05;
    			if (dro >= 1){
        			dro =1;
        		}
    			Logger.Debug("BayesianNetwork:\tdro = " + dro + "\n", 1);

    		}
    		
    		//double dro = (Game.getInstance().enemy().completedUnitCount(UnitType.ZERG_DRONE))/20;
    		
    		double predictioneni = getPredictionValue(colo,time, hatch,dro);
    		
    		
    		if (predictioneni >= 0.5) {
    			Logger.Debug("BayesianNetwork:\tEnnemy is defending !!!\n", 1);
    		}else{
    			Logger.Debug("BayesianNetwork:\tEnnemy is attacking !!!\n", 1);
    		}
    		
    		//fin ajout
    		
    		
    		
    		
    		UnitType needType = null;
    		int needCount = 0;
    		
    		//debut ajout d�fense
    		
    		for (Unit unit : Game.getInstance().self().getUnits()) {
    			if (unit.getType().equals(UnitType.TERRAN_BATTLECRUISER)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_GHOST)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_GOLIATH)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_VALKYRIE)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_VULTURE)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_SCIENCE_VESSEL)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_MARINE)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_SCV)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_FIREBAT)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_SIEGE_TANK_SIEGE_MODE)) continue;
    			if (unit.getType().equals(UnitType.TERRAN_MEDIC)) continue;
    			
    			if (unit.getType().equals(UnitType.TERRAN_SUPPLY_DEPOT) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 500) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t Supply Depot is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    			if (unit.getType().equals(UnitType.TERRAN_COMMAND_CENTER) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 1500) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t Command Center is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    			if (unit.getType().equals(UnitType.TERRAN_BARRACKS) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 1000) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t Barrack is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    			if (unit.getType().equals(UnitType.TERRAN_REFINERY) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 750) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t Refinery is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    			if (unit.getType().equals(UnitType.TERRAN_ACADEMY) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 600) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t Academy is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    			if (unit.getType().equals(UnitType.TERRAN_ENGINEERING_BAY) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 850) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t Engineering Bay is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    			if (unit.getType().equals(UnitType.TERRAN_MISSILE_TURRET) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 200) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t Turret is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    			if (unit.getType().equals(UnitType.TERRAN_FACTORY) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 1250) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t Factory is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    			if (unit.getType().equals(UnitType.TERRAN_MACHINE_SHOP) && (!unit.isBeingConstructed()) && unit.getHitPoints() < 750) {
    				for (Unit unit2 : Game.getInstance().self().getUnits()) {
						if (unit2.getType().equals(UnitType.TERRAN_SIEGE_TANK_TANK_MODE)) {
							unit2.siege();
						}
    				}
    				Logger.Debug("UnitSnsr:\t MachineShop is being attacked ! Tank siege defense ! \n", 1);
    			}
    			
    		}
    		
    		
    		//fin ajout d�fense
    			
    		/*
       		if (starFact.enemyType.equals(UnitType.ZERG_ZERGLING)) { 
    			needType = UnitType.TERRAN_FIREBAT;
    			needCount = starFact.enemyTypeCount;
       		}*/
    		if (starFact.enemyType.equals(UnitType.ZERG_SUNKEN_COLONY)) {
    			needType = UnitType.TERRAN_SIEGE_TANK_TANK_MODE;
    			needCount = starFact.enemyTypeCount;
    		/*if (starFact.enemyType.equals(UnitType.ZERG_HYDRALISK)) {
    			needType = UnitType.TERRAN_SIEGE_TANK_TANK_MODE;
    			needCount = starFact.enemyTypeCount;*/
    		}
    		if (starFact.enemyType.equals(UnitType.PROTOSS_ZEALOT)) {
    			needType = UnitType.TERRAN_FIREBAT;
    			needCount = ((int) starFact.enemyTypeCount) * 2;
    		}
    		if (starFact.enemyType.equals(UnitType.PROTOSS_DRAGOON)) {
    			needType = UnitType.TERRAN_SIEGE_TANK_TANK_MODE;
    			needCount = ((int) starFact.enemyTypeCount) * 2;
    		}
    		if (starFact.enemyType.equals(UnitType.TERRAN_MARINE)) {
    			needType = UnitType.TERRAN_SIEGE_TANK_TANK_MODE;
    			needCount = starFact.enemyTypeCount;
    		}

    		
    		if (needType == null) {
    			Logger.Debug("UnitSnsr:\tNo need type for " + starFact.enemyType.getName() + "\n", 1);
    		}else{
        		StarMemoryFact needFact = getNeedTypeFact(needType);
        		if (needFact == null) {
        			needFact = new StarMemoryFact(StarMemoryFactType.NEED_TYPE);
        			workingMemory.addFact(needFact);
        		}

        		int countDiff = needCount - Game.getInstance().self().completedUnitCount(needType);
    			if (countDiff > 0) {
    				Logger.Debug("UnitSnsr:\t" + countDiff + " " + needType.getName() + " needed!\n", 3);
            		needFact.needType = needType;
        			needFact.needTypeCount = countDiff;
    			}else{
    				workingMemory.removeFact(needFact);
    			}
    		}
    	}
    }
    
    //beginning of bayesian

	public void loadNetworks(){
		String currentFolder = "c:\\netss\\";		
				
		networkPrediction = new Network();
		networkPrediction.readFile(currentFolder + "Prediction.xdsl");
	}
	
	public double getPredictionValue(double colony, double time, double hatchery, double drones){
		
		double theProbs [] = new double[2];
		double outputValue [];
		double Colony = colony;
		double Time = time;
		double Hatchery = hatchery;
		double Drones = drones;	
		
		
		/* Node colony */
		theProbs[0] = Colony;
		theProbs[1] = (1 - Colony);
		networkPrediction.setNodeDefinition("colony", theProbs);

		
		/* Node time */	
		theProbs[0] = Time;
		theProbs[1] = (1 - Time);
		networkPrediction.setNodeDefinition("time", theProbs);

		
		/* Node hatchery */
		theProbs[0] = Hatchery;
		theProbs[1] = (1 - Hatchery);
		networkPrediction.setNodeDefinition("hatchery", theProbs);
					
		/* Node drones */
		theProbs[0] = Drones;
		theProbs[1] = (1 - Drones);
		networkPrediction.setNodeDefinition("drones", theProbs);
		
		//Update the network
		networkPrediction.updateBeliefs();
		
		
		// final Value
		outputValue = networkPrediction.getNodeValue("pred");

		//System.out.println("Valor success [" + outputValue[0] + "]");
		
		return outputValue[0];
	}
	
	//end of Bayesian
    
    
    private StarMemoryFact getNeedTypeFact(UnitType type) {
    	WorkingMemoryFact[] facts = workingMemory.getFacts(StarMemoryFactType.NEED_TYPE, null);
    	for (WorkingMemoryFact fact : facts) {
    		StarMemoryFact starFact = (StarMemoryFact)fact;
    		if (starFact.needType.equals(type)) return starFact;
    	}
    	return null;	
    }
    
}
