package hui;
import java.io.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;
public class DatasetReader {
	private final Set<String> attributes = new HashSet<String>();
	private final List<Itemset> objects = new ArrayList<Itemset>();
public void addItemset(Itemset itemset){
	objects.add(itemset);
	attributes.addAll(itemset.getItems());
}
public void readFile(String path){
	try{
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = null;
		while((line = br.readLine())!=null){
			addObject(line.split(" "));
		}
		br.close();
	}catch(Exception e){
		e.printStackTrace();
	}
}
public void addObject(String items[]){
	Itemset itemset = new Itemset();
	for(int i=0;i<items.length;i++){
		itemset.addItem(items[i].trim());
		attributes.add(items[i].trim());
	}
	objects.add(itemset);
}
public void printTransaction(DefaultTableModel dtm){
	int count = 0;
	for(Itemset itemset : objects){
		String row[] = {"0"+count,itemset.toString()};
		dtm.addRow(row);
		count++;
	}
}
public int size(){
	return objects.size();
}
public List<Itemset> getObjects() {
	return objects;
}
public Set<String> getAttributes() {
	return attributes;
}
}