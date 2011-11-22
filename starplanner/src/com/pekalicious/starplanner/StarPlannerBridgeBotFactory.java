package com.pekalicious.starplanner;

import org.bwapi.bridge.BridgeBot;
import org.bwapi.bridge.BridgeBotFactory;
import org.bwapi.bridge.model.Game;

import com.pekalicious.LogFilePrinter;
import com.pekalicious.Logger;

public class StarPlannerBridgeBotFactory implements BridgeBotFactory {

	@Override
	public BridgeBot getBridgeBot(Game game) {
		Logger.printer = new LogFilePrinter();
		Logger.Debug("This is a file test", 0);
		
		return new StarPlannerBridgeBot();
	}
}
