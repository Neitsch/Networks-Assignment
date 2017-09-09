package graph;

import java.util.HashSet;
import java.util.Set;

public class Vertex {
	private final Set<Edge> edges;
	private final Integer id;

	public Vertex(Integer id) {
		this.id = id;
		edges = new HashSet<>();
	}

	public void add(Edge edge) {
		edges.add(edge);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vertex) {
			final Vertex v = (Vertex) obj;
			return v.id().equals(id) && v.edges.equals(edges);
		}
		return true;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public Integer id() {
		return id;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Node " + id;
	}
}
