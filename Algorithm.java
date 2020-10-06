package hui;
import java.util.*;
import javax.swing.table.DefaultTableModel;
public class Algorithm {
	Itemsets frequentItemsets = new Itemsets("FREQUENT ITEMSETS");
	DatasetReader dr;
	int k;
	// variables for counting support of items
	Map<String, Set<Integer>> mapItemTIDS = new HashMap<String, Set<Integer>>();
	int min_support;
	// Special parameter to set the maximum size of itemsets to be discovered
	int maxItemsetSize = Integer.MAX_VALUE;
	long start,end;
public Algorithm(DatasetReader dr) {
	this.dr = dr;
}
public Itemsets frequentPatterns(int min) {
	start = System.currentTimeMillis();
	min_support = 2;
	mapItemTIDS = new HashMap<String, Set<Integer>>(); // id item, count
	for(int i=0;i<dr.getObjects().size();i++){
		Itemset transaction = dr.getObjects().get(i);
		for(int j=0;j<transaction.size();j++){
			Set<Integer> ids = mapItemTIDS.get(transaction.get(j));
			if(ids == null){
				ids = new HashSet<Integer>();
				mapItemTIDS.put(transaction.get(j), ids);
			}
			ids.add(i);
		}
	}
	// To build level 1, we keep only the frequent items.
	// We scan the database one time to calculate the support of each candidate.
	k=1;
	List<Itemset> level = new ArrayList<Itemset>();
	// For each item
	Iterator<Map.Entry<String, Set<Integer>>> iterator = mapItemTIDS.entrySet().iterator();
	while (iterator.hasNext()) {
		Map.Entry<String, Set<Integer>> entry = (Map.Entry<String, Set<Integer>>) iterator.next();
		if(entry.getValue().size() >= min_support){ // if the item is frequent
			String item = entry.getKey();
			Itemset itemset = new Itemset();
			itemset.addItem(item);
			itemset.setTransactioncount(mapItemTIDS.get(item));
			level.add(itemset);
			frequentItemsets.addItemset(itemset, 1);
		}else{
			iterator.remove();  // if the item is not frequent we don't need to keep it into memory.
		}
	}
	Collections.sort(level, new Comparator<Itemset>(){
		public int compare(Itemset o1, Itemset o2) {
			return o1.get(0).compareTo(o2.get(0));
		}
	});
	// Generate candidates with size k = 1 (all itemsets of size 1)
	k = 2;
	// While the level is not empty
	while(!level.isEmpty()  && k <= maxItemsetSize) {
		level = createNodes(level);; // We keep only the last level... 
		k++;
	}
	end = System.currentTimeMillis();
	return frequentItemsets; // Return all frequent itemsets found!
}
// Based on the description of "Efficient mining..."
public List<Itemset> generateCandidateSize1() {
	List<Itemset> candidates = new ArrayList<Itemset>(); // list the itemsets
	for (String item : dr.getAttributes()) {
		Itemset itemset = new Itemset();
		itemset.addItem(item);
		candidates.add(itemset);
	}
	return candidates;
}
public List<Itemset> createNodes(List<Itemset> levelK_1) {
	List<Itemset> candidates = new ArrayList<Itemset>();
	loop1:for(int i=0; i< levelK_1.size(); i++){
			Itemset itemset1 = levelK_1.get(i);
			loop2:for(int j=i+1; j< levelK_1.size(); j++){
				Itemset itemset2 = levelK_1.get(j);
				for(int k=0; k< itemset1.size(); k++){
					if(k == itemset1.size()-1){ 
						if(itemset1.getItems().get(k).compareTo(itemset2.get(k)) >= 0){  
							continue loop1;
						}
					}
					else if(itemset1.getItems().get(k).compareTo(itemset2.get(k)) < 0){ 
						continue loop2; // we continue searching
					}
					else if(itemset1.getItems().get(k).compareTo(itemset2.get(k)) > 0){ 
						continue loop1;  // we stop searching:  because of lexical order
					}
				}
				String missing = itemset2.get(itemset2.size()-1);
				Set<Integer> list = new HashSet<Integer>();
				for(Integer val1 : itemset1.getTransactionsIds()){
					if(itemset2.getTransactionsIds().contains(val1)){
						list.add(val1);
					}
				}
				if(list.size() >= min_support){
					// Create a new candidate by combining itemset1 and itemset2
					Itemset candidate = new Itemset();
					for(int k=0; k < itemset1.size(); k++){
						candidate.addItem(itemset1.get(k));
					}
					candidate.addItem(missing);
					candidate.setTransactioncount(list);
					candidates.add(candidate);
					frequentItemsets.addItemset(candidate, k);
				}
			}
	}
	return candidates;
}
public Itemsets getItemsets() {
	return frequentItemsets;
}
public void setMaxItemsetSize(int maxItemsetSize) {
	this.maxItemsetSize = maxItemsetSize;
}


}