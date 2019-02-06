
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RocketTarget extends VisionTarget {
	//TODO specify that this is a hatch target
	int pixelDistanceThreshold=15;
	public RocketTarget() {
		super("RocketTarget");
		this.height=31; //ROCKET CARGO: 8 inches higher
	}

	@Override
	public ArrayList<Sighting> validateSightings(ArrayList<Sighting> polys) {
		return polys;
		/*
		System.out.println("Polys Size"+polys.size());
		for (int i=0; i<polys.size();i++) {
			if (polys.get(i).height==0 || polys.get(i).width==0 || polys.get(i).area==0) {
				polys.remove(i);
				i--;
			}
		}
		System.out.println("Polys size after removal"+polys.size());
		if (polys.size()>0) {
			Sighting s1=polys.get(0);
			for (int i=1; i <polys.size();i++) {
				s1.addSighting(polys.get(i));
			}
			return new ArrayList<Sighting>(Arrays.asList(s1));
		}
		return polys;
		*/
		/*DisjointUnionSets sets = new DisjointUnionSets(polys.size());
        sets.makeSet();
        for(int i = 0; i < polys.size(); i++)
            for(int j = i + 1; j < polys.size(); j++)
                if(polys.get(i).distanceTo(polys.get(j)) < pixelDistanceThreshold)
                    sets.union(i, j);
        HashMap<Integer, Sighting> parentNodes = new HashMap();
        for(int i = 0; i < polys.size(); i++){
            int parent = sets.find(i);
            parentNodes.putIfAbsent(parent, polys.get(parent));
            parentNodes.get(parent).addSighting(polys.get(i));
        }
        return new ArrayList(parentNodes.values());
		*/
	}

	@Override
	public boolean estimationIsGood(Sighting s) {
		return true; //see above
	}
	
}
class DisjointUnionSets {
    int[] rank, parent;
    int n;
    public DisjointUnionSets(int n){
        rank = new int[n];
        parent = new int[n];
        this.n = n;
        makeSet();
    }
    void makeSet(){
        for(int i=0; i<n; i++){
            parent[i] = i;
        }
    }
    public int find(int x){
        if(parent[x]!=x)
            parent[x] = find(parent[x]);
        return parent[x];
    }
    public void union(int x, int y){
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
