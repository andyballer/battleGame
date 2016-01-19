package gameplayer.action;

import display.Constant;
import engine.ViewController;

public class DestroyTreeAction extends DestroyTerrainAction {

	public DestroyTreeAction(String l, ViewController c) {
		super(l, c);
		myTag = Constant.treeTag;
		myDestroyTag = Constant.destroyTreeTag;
	}
}
