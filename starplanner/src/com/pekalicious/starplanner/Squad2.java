package com.pekalicious.starplanner;

import java.util.ArrayList;
import java.util.List;

import org.bwapi.bridge.model.Position;
import org.bwapi.bridge.model.Unit;

public class Squad2 {
	public SquadOrder order;
	public OrderStatus orderStatus;
	public List<Unit> units;
	public Position destination;
	public Unit leader;
	public Unit medic; //ajout
	public boolean enemyNearDestination;
	
	public int gotoTimer;
	public Unit enemyTarget;
	
	public Squad2() {
		orderStatus = OrderStatus.Waiting;
		order = SquadOrder.None;
		units = new ArrayList<Unit>();
	}
}
