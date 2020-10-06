package hui;
import java.text.DecimalFormat;
import java.util.*;
public class Itemset{
	List<String> items = new ArrayList<String>(); // ordered
	Set<Integer> transactionsIds = new HashSet<Integer>();
public Itemset(){}
public double getRelativeSupport(int nbObject) {
	return ((double)transactionsIds.size()) / ((double) nbObject);
}
public String getSupportRelatifFormatted(int nbObject) {
	int frequence = transactionsIds.size();// ((double)transactionsIds.size()) / ((double) nbObject);
	DecimalFormat format = new DecimalFormat();
	format.setMinimumFractionDigits(0); 
	format.setMaximumFractionDigits(2); 
	//System.out.println(transactionsIds.size()+" "+nbObject);
	return ""+frequence;
}
public int getAbsoluteSupport(){
	return transactionsIds.size();
}
public void addItem(String value){
	items.add(value);
}
public List<String> getItems(){
	return items;
}
public String get(int index){
	return items.get(index);
}
public String toString(){
	StringBuffer r = new StringBuffer ();
	for(String attribute : items){
		r.append(attribute.toString());
		r.append(' ');
	}
	return r.toString();
}
public void setTransactioncount(Set<Integer> listTransactionIds) {
	this.transactionsIds = listTransactionIds;
}
public int size(){
	return items.size();
}
public Set<Integer> getTransactionsIds() {
	return transactionsIds;
}
}