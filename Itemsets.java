package hui;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
public class Itemsets {
	ArrayList<ArrayList<Itemset>> levels = new ArrayList<ArrayList<Itemset>>();  // itemset classé par taille
	int itemsetsCount=0;
	String name;
	int total_estu;
public Itemsets(String name){
	this.name = name;
	levels.add(new ArrayList<Itemset>()); 
}
public int getSupport(String item,DefaultTableModel dtm1){
	int support = 0;
	for(int i=0;i<dtm1.getRowCount();i++){
		String it = dtm1.getValueAt(i,0).toString().trim();
		if(item.equals(it)){
			support = Integer.parseInt(dtm1.getValueAt(i,1).toString().trim());
			break;
		}
	}
	return support;
}
public int miu(String arr[],DefaultTableModel transaction_table,DefaultTableModel profit_table){
	int value = 0;
	total_estu = 0;
	for(int i=0;i<arr.length;i++){
		ArrayList<Integer> miu = new ArrayList<Integer>();
		for(int j=0;j<transaction_table.getRowCount();j++){
			String tid = transaction_table.getValueAt(j,0).toString().trim();
			String items = transaction_table.getValueAt(j,1).toString().trim();
			int profit = Integer.parseInt(transaction_table.getValueAt(j,2).toString().trim());
			String arr1[] = items.split("\\s+");
			for(int k=0;k<arr1.length;k++){
				String ar[] = arr1[k].split(",");
				if(ar[0].equals(arr[i])){
					int total = Integer.parseInt(ar[1])*getSupport(arr[i],profit_table);
					miu.add(total);
					total_estu = total_estu + total;
				}
			}
		}
		java.util.Collections.sort(miu);
		value = value + miu.get(0);
		miu.clear();
	}
	return value;
}
public void tko_function(int nbObject,DefaultTableModel view_table,DefaultTableModel profit_table,int min_util,DefaultTableModel transaction_table,ArrayList<TWU> twu_list,int top){
	System.out.println(" ------- " + name + " -------");
	int patternCount=0;
	int levelCount=0;
	int top_value = 0;
	for(List<Itemset> level : levels){
		for(Itemset itemset : level){
			int sum = getTWU(itemset.toString().trim().split("\\s+"),transaction_table,profit_table);
			int ruc = miu(itemset.toString().trim().split("\\s+"),transaction_table,profit_table);
			int sc = Integer.parseInt(itemset.getSupportRelatifFormatted(nbObject).trim());
			ruc = sc * ruc;
			if(sum >= min_util && top_value < top){
				String row2[]={itemset.toString(),sc+"",Integer.toString(sum)};
				view_table.addRow(row2);
				top_value = top_value + 1;
				if(ruc > min_util){
					min_util = ruc;
					System.out.println("TKO min util border adjusted to "+min_util);
				}
			}
		}
		levelCount++;
	}
}
public void tku_function(int nbObject,DefaultTableModel view_table,DefaultTableModel profit_table,int min_util,DefaultTableModel transaction_table,ArrayList<TWU> twu_list,int top){
	System.out.println(" ------- " + name + " -------");
	int patternCount=0;
	int levelCount=0;
	int top_value = 0;
	for(List<Itemset> level : levels){
		for(Itemset itemset : level){
			int profit_value = getTWU(itemset.toString().trim().split("\\s+"),transaction_table,profit_table);
			int miu = miu(itemset.toString().trim().split("\\s+"),transaction_table,profit_table);
			int sc = Integer.parseInt(itemset.getSupportRelatifFormatted(nbObject).trim());
			miu = sc * miu;
			if(total_estu >= min_util  && top_value < top){
				String row2[]={itemset.toString(),sc+"",total_estu+""};
				view_table.addRow(row2);
				top_value = top_value + 1;
				if(miu > min_util){
					min_util = miu;
					System.out.println("TKU min util border adjusted to "+min_util);
				}
			}
		}
		levelCount++;
	}
}

public boolean containsAll(String items[],String arr[]){
	StringBuilder sb = new StringBuilder();
	for(int j=0;j<items.length;j++){
		String st[] = items[j].split(",");
		sb.append(st[0]+" ");
	}
	String temp[] = sb.toString().trim().split("\\s+");
	return java.util.Arrays.asList(temp).containsAll(java.util.Arrays.asList(arr));
}
public int getProfit(String item,DefaultTableModel profit_table){
	int profit = 0;
	for(int i=0;i<profit_table.getRowCount();i++){
		String it = profit_table.getValueAt(i,0).toString().trim();
		if(item.equals(it)){
			profit = Integer.parseInt(profit_table.getValueAt(i,1).toString().trim());
			break;
		}
	}
	return profit;
}
public int getTWU(String arr[],DefaultTableModel transaction_table,DefaultTableModel profit_table){
	int total = 0;
	for(int i=0;i<transaction_table.getRowCount();i++){
		String items[] = transaction_table.getValueAt(i,1).toString().trim().split("\\s+");
		boolean flag = containsAll(items,arr);
		if(flag){
			for(int j=0;j<items.length;j++){
				String s1[] = items[j].split(",");
				int quantity = Integer.parseInt(s1[1].trim());
				int profit = getProfit(s1[0],profit_table);
				total = total + (quantity * profit);
			}
		}
	}
	return total;
}
public void addItemset(Itemset itemset, int k){
	//check if it can fit in memory
	while(levels.size() <= k){
		levels.add(new ArrayList<Itemset>());
	}
	levels.get(k).add(itemset);
	itemsetsCount++;
}
public ArrayList<ArrayList<Itemset>> getLevels() {
	return levels;
}
public int getItemsetsCount() {
	return itemsetsCount;
}
}