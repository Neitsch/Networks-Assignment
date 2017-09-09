package data;

import java.util.List;

public class MassEdgeBody implements Body {
	private final List<EdgeBody> bodies;

	public MassEdgeBody(List<EdgeBody> bodies) {
		super();
		this.bodies = bodies;
	}

	public List<EdgeBody> getBodies() {
		return bodies;
	}
}
