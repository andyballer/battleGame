package gameplayer.action;

import display.Constant;
import engine.ViewController;

public class DestroyRockAction extends DestroyTerrainAction {

	public DestroyRockAction(String l, ViewController c) {
		super(l, c);
		myTag = Constant.rockTag;
		myDestroyTag = Constant.destroyRockTag;
	}
}
