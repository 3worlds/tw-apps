package au.edu.anu.twapps.mm;

import au.edu.anu.twcore.graphState.IGraphState;

public class MockGraphState implements IGraphState{

	private boolean changed = false;
	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public void setChanged(boolean state) {
		changed = state;
	}

}
