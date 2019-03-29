
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class RocketTarget extends VisionTarget {
	// TODO specify that this is a hatch target
	int pixelDistanceThreshold = 3;

	public RocketTarget() {
		super("RocketTarget");
		this.height = 28.75; // ROCKET CARGO: 8 inches higher
	}

	@Override
	public ArrayList<Sighting> validateSightings(ArrayList<Sighting> polys) {
		
		for (int i = 0; i < polys.size(); i++) {
			if (polys.get(i).height == 0 || polys.get(i).width == 0 || polys.get(i).area == 0) {
				polys.remove(i);
				i--;
			}
		}
		DisjointUnionSets sets = new DisjointUnionSets(polys.size());
		sets.makeSet();
		for (int i = 0; i < polys.size(); i++)
			for (int j = i + 1; j < polys.size(); j++)
				if (polys.get(i).distanceTo(polys.get(j)) < pixelDistanceThreshold)
					sets.union(i, j);
		HashMap<Integer, Sighting> parentNodes = new HashMap();
		for (int i = 0; i < polys.size(); i++) {
			int parent = sets.find(i);
			parentNodes.putIfAbsent(parent, polys.get(parent));
			parentNodes.get(parent).addSighting(polys.get(i));
		}
		polys= new ArrayList<>(parentNodes.values());
		
		Collections.sort(polys);
		for (int a = 0; a < polys.size() - 1; a++) {
			if (polys.get(a).isRightSighting == 1) {
				if (polys.get(a + 1).isRightSighting == 0) {
					polys.get(a).addSighting(polys.get(a + 1));
					polys.remove(a + 1);
				}
			}
		}
		
		return polys;
	}

	@Override
	public boolean estimationIsGood(Sighting s) {
		return true; // see above
	}

}

class DisjointUnionSets {
	int[] rank, parent;
	int n;

	public DisjointUnionSets(int n) {
		rank = new int[n];
		parent = new int[n];
		this.n = n;
		makeSet();
	}

	void makeSet() {
		for (int i = 0; i < n; i++) {
			parent[i] = i;
		}
	}

	public int find(int x) {
		if (parent[x] != x)
			parent[x] = find(parent[x]);
		return parent[x];
	}

	public void union(int x, int y) {
		int xRoot = find(x), yRoot = find(y);
		if (xRoot == yRoot)
			return;
		if (rank[xRoot] < rank[yRoot])
			parent[xRoot] = yRoot;
		else if (rank[yRoot] < rank[xRoot])
			parent[yRoot] = xRoot;
		else {
			parent[yRoot] = xRoot;
			rank[xRoot] = rank[xRoot] + 1;
		}
	}
}
