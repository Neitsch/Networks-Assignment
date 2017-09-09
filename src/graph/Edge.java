package graph;

public class Edge {
	private final Vertex s, d;

	public Edge(Vertex s, Vertex d) {
		this.s = s;
		this.d = d;
	}

	public Vertex destination() {
		return d;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			final Edge e = (Edge) obj;
			return e.s == s && e.d == d || e.s == d && e.d == s;
		}
		return false;
	}

	public Edge establish() {
		s.add(this);
		d.add(this);
		return this;
	}

	@Override
	public int hashCode() {
		return s.hashCode() + d.hashCode();
	}

	public Vertex origin() {
		return s;
	}

	@Override
	public String toString() {
		return "Edge from " + s.toString() + " to " + d.toString();
	}
}
