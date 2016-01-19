package gameplayer.action;

import display.Constant;
import engine.ViewController;

public class PlaceRockAction extends PlaceTerrainAction {

	public PlaceRockAction(String l, ViewController c) {
		super(Constant.rockTag, c);
	}

}
