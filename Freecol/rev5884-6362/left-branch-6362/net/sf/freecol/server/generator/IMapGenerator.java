package net.sf.freecol.server.generator;

import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.model.Game;


public interface IMapGenerator {

	
	public abstract void createMap(Game game) throws FreeColException;

	
	public abstract MapGeneratorOptions getMapGeneratorOptions();

}