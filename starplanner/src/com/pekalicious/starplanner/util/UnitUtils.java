package com.pekalicious.starplanner.util;

import java.util.ArrayList;
import java.util.List;

import org.bwapi.bridge.model.Game;
import org.bwapi.bridge.model.TechType;
import org.bwapi.bridge.model.Unit;
import org.bwapi.bridge.model.UnitType;
import org.bwapi.bridge.model.UpgradeType;

import com.pekalicious.Logger;
import com.pekalicious.starplanner.StarBlackboard;

public class UnitUtils {
	public static Unit getUnitType(Game game, UnitType type) {
		for (Unit unit : game.self().getUnits())
			if (unit.getType().equals(type))
				return unit;
		
		return null;
	}
	
	public static List<Unit> getUnitList(Game game, UnitType type) {
		List<Unit> unitList = new ArrayList<Unit>();
		for (Unit unit : game.self().getUnits())
			if (unit.getType().equals(type))
				unitList.add(unit);
		
		return unitList;
	}

	public static Unit getIdleWorker(Game game, StarBlackboard blackBoard) {
		for (Unit unit : UnitUtils.getUnitList(game, UnitType.TERRAN_SCV))
			if (!blackBoard.isOccupied(unit))
				return unit;

		return null;
	}
	// ajout
	public static Unit getIdleFactory(Game game, StarBlackboard blackBoard) {
		for (Unit unit : UnitUtils.getUnitList(game, UnitType.TERRAN_FACTORY))
			if (!blackBoard.isOccupied(unit))
				return unit;

		return null;
	}
	// fin ajout
	public static Unit getClosestUnitTo(Game game, Unit unit, UnitType type) {
		Unit closestUnit = null;
		double closest = Double.MAX_VALUE;
		
		for (Unit otherUnit : getUnitList(game, type)) {
			double dx = unit.getPosition().x() - otherUnit.getPosition().x();
			double dy = unit.getPosition().y() - otherUnit.getPosition().y();
			double dist = Math.sqrt(dx*dx + dy*dy); 

			if (dist < closest) {
				closestUnit = otherUnit;
				closest = dist;
			}
			
		}
		
		return closestUnit;
	}
	
	public static int countBattleUnits(Game game) {
		int count = 0;
		for (Unit unit : game.self().getUnits()) {
			if (unit.getType().isBuilding()) continue;
			if (unit.getType().equals(UnitType.TERRAN_SCV)) continue;
			if (unit.getType().equals(UnitType.RESOURCE_MINERAL_FIELD)) continue;
			if (unit.getType().equals(UnitType.RESOURCE_VESPENE_GEYSER)) continue;
			if (unit.getHitPoints() <= 0) continue;
			
			Logger.Debug("UnitUtils:\tCounting " + unit.getType().getName() + "\n", 3);
			
			count++;
		}
		
		return count;
	}
	
	public enum Type {
		NONE(UnitType.NONE, -1), 

		TERRAN_SCV(UnitType.TERRAN_SCV, 5), 
		TERRAN_GOLIATH(UnitType.TERRAN_GOLIATH, 4), 
		TERRAN_MARINE(UnitType.TERRAN_MARINE, 4),
		TERRAN_SUPPLY_DEPOT(UnitType.TERRAN_SUPPLY_DEPOT, 1), 
		TERRAN_REFINERY(UnitType.TERRAN_REFINERY, 1),
		TERRAN_ARMORY(UnitType.TERRAN_ARMORY, 4),
		TERRAN_STARPORT(UnitType.TERRAN_STARPORT, 4), 
		TERRAN_BARRACKS(UnitType.TERRAN_BARRACKS, 4), 
		TERRAN_ENGINEERING_BAY(UnitType.TERRAN_ENGINEERING_BAY, 4), 
		TERRAN_FACTORY(UnitType.TERRAN_FACTORY, 4), 
		TERRAN_MACHINE_SHOP(UnitType.TERRAN_MACHINE_SHOP, 4), 
		TERRAN_FIREBAT(UnitType.TERRAN_FIREBAT, 4), 
		TERRAN_MEDIC(UnitType.TERRAN_MEDIC, 4),
		TERRAN_ACADEMY(UnitType.TERRAN_ACADEMY, 4),
		//new units and buildings
		TERRAN_CONTROL_TOWER(UnitType.TERRAN_CONTROL_TOWER, 4), 
		TERRAN_SIEGE_TANK_TANK_MODE(UnitType.TERRAN_SIEGE_TANK_TANK_MODE, 4),
		TERRAN_VULTURE(UnitType.TERRAN_VULTURE, 4), 
		TERRAN_VALKYRIE(UnitType.TERRAN_VALKYRIE, 4),
		TERRAN_SCIENCE_FACILITY(UnitType.TERRAN_SCIENCE_FACILITY, 4),
		TERRAN_PHYSICS_LAB(UnitType.TERRAN_PHYSICS_LAB, 4),
		TERRAN_BATTLECRUISER(UnitType.TERRAN_BATTLECRUISER, 4),
		TERRAN_SCIENCE_VESSEL(UnitType.TERRAN_SCIENCE_VESSEL, 4),
		TERRAN_WRAITH(UnitType.TERRAN_WRAITH, 4), 
		TERRAN_TURRET(UnitType.TERRAN_MISSILE_TURRET, 4); 
		//end of new units and buildings
		
		//TODO: Make these fields private and provide delegates.
		public int priority;
		public UnitType bwapiType;
		
		private Type(UnitType type, int priority) {
			this.bwapiType = type;
			this.priority = priority;
		}
	}
	//new types
	public enum Type2 {
		
		TANK_SIEGE_MODE(TechType.TANK_SIEGE_MODE, 4);
		 
		
		//TODO: Make these fields private and provide delegates.
		public int priority;
		public TechType bwapiType;
		
		private Type2(TechType type2, int priority) {
			this.bwapiType = type2;
			this.priority = priority;
		}
	}
	
	public enum Type3 {
		
		RANGE(UpgradeType.U_238_SHELLS, 4);
		 
		
		//TODO: Make these fields private and provide delegates.
		public int priority;
		public UpgradeType bwapiType;
		
		private Type3(UpgradeType type3, int priority) {
			this.bwapiType = type3;
			this.priority = priority;
		}
	}
	//end of new types
	public static List<Unit> getCompletedUnitList(Game game, UnitType key) {
		List<Unit> tmpUnits = getUnitList(game, key);
		List<Unit> completedUnitList = new ArrayList<Unit>();
		for (Unit tmp : tmpUnits)
			if (tmp.isCompleted())
				completedUnitList.add(tmp);
		
		return completedUnitList;
	}
}
