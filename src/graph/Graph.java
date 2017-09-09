package graph;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import io.Console;
import io.Console.LOGMODE;
import util.Pair;

public class Graph {
	private static DecimalFormat df = new DecimalFormat(".###");
	private final Map<Edge, Pair<Long, Float>> edgeWeight;

	private final Semaphore sem;
	private final Set<Vertex> vertexSet;

	public Graph() {
		vertexSet = new HashSet<>();
		edgeWeight = new Hashtable<>();
		sem = new Semaphore(1);
	}

	public boolean addEdge(Integer source, Integer destination, Float weight, Long updateTime) {
		boolean update = false;
		try {
			sem.acquire();
			update = true;
			final Vertex s = addVertex(source);
			final Vertex d = addVertex(destination);
			final Edge e = new Edge(s, d);
			if (edgeWeight.containsKey(e) && edgeWeight.get(e).getKey() >= updateTime) {
				update = false;
			}
			if (update) {
				edgeWeight.put(e.establish(), new Pair<>(updateTime, weight));
			}
		} catch (final InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			sem.release();
		}
		return update;
	}

	public Vertex addVertex(Integer id) {
		Vertex h = find(id);
		if (h == null) {
			h = new Vertex(id);
			vertexSet.add(h);
		}
		return h;
	}

	public Hashtable<Vertex, Pair<Vertex, Float>> bellmannFord(Integer self) {
		try {
			sem.acquire();
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final Float[] dist = new Float[vertexSet.size()];
		final Vertex[] pre = new Vertex[vertexSet.size()];
		final HashMap<Vertex, Integer> indexMapping = new HashMap<>();
		Integer count = 0;
		Vertex s = null;
		for (final Vertex v : vertexSet) {
			pre[count] = null;
			if (v.id().equals(self)) {
				s = v;
				dist[count] = 0f;
				pre[count] = v;
			} else {
				dist[count] = Float.MAX_VALUE;
			}
			indexMapping.put(v, count);
			count++;
		}
		count = 0;
		for (int i = 0; i < vertexSet.size(); i++) {
			for (final Edge e : edgeWeight.keySet()) {
				if (dist[indexMapping.get(e.origin())] < Float.MAX_VALUE && dist[indexMapping.get(e.origin())]
						+ edgeWeight.get(e).getValue() < dist[indexMapping.get(e.destination())]) {
					dist[indexMapping.get(e.destination())] = dist[indexMapping.get(e.origin())]
							+ edgeWeight.get(e).getValue();
					pre[indexMapping.get(e.destination())] = e.origin();
				}
				if (dist[indexMapping.get(e.destination())] < Float.MAX_VALUE && dist[indexMapping.get(e.destination())]
						+ edgeWeight.get(e).getValue() < dist[indexMapping.get(e.origin())]) {
					dist[indexMapping.get(e.origin())] = dist[indexMapping.get(e.destination())]
							+ edgeWeight.get(e).getValue();
					pre[indexMapping.get(e.origin())] = e.destination();
				}
			}
		}
		final Hashtable<Vertex, Pair<Vertex, Float>> htbl = new Hashtable<>();
		for (final Vertex v : vertexSet) {
			Vertex ref = v;
			while (pre[indexMapping.get(ref)] != s) {
				ref = pre[indexMapping.get(ref)];
			}
			htbl.put(v, new Pair<>(ref, dist[indexMapping.get(v)]));
			count++;
		}
		sem.release();
		return htbl;
	}

	public void dumpBellmannFord(Integer source) {
		final StringBuilder builder = new StringBuilder();
		final Vertex s = find(source);
		builder.append(s + " Routing Table");
		final Hashtable<Vertex, Pair<Vertex, Float>> routes = bellmannFord(source);
		for (final Vertex v : routes.keySet()) {
			if (v != s) {
				builder.append(System.lineSeparator());
				builder.append("- (" + df.format(routes.get(v).getValue()) + ") -> " + v
						+ (v == routes.get(v).getKey() ? "" : "; Next hop -> " + routes.get(v).getKey()));
			}
		}
		Console.log(builder.toString(), LOGMODE.GRAPH);
	}

	public void dumpEdges() {
		for (final Edge e : edgeWeight.keySet()) {
			System.out.println(e + " --> " + edgeWeight.get(e));
		}
	}

	private Vertex find(Integer source) {
		for (final Vertex v : vertexSet) {
			if (v.id().equals(source)) {
				return v;
			}
		}
		return null;
	}
}
