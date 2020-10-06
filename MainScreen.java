package hui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import net.miginfocom.swing.MigLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.table.TableColumn;
import org.jfree.ui.RefineryUtilities;
import java.io.BufferedReader;
import java.io.FileReader;
public class MainScreen extends JFrame implements Runnable{
	JLabel l1;
	JButton b1,b2,b3,b4,b5,b6,b7,b8;
	Font f1,f2;
	JPanel p1,p2,p3,p4;
	Thread thread;
	DefaultTableModel dtm;
	JScrollPane jsp;
	JTable table;
	DefaultTableModel dtm1;
	JScrollPane jsp1;
	JTable table1;
	LineBorder border;
	TitledBorder title;
	ArrayList<String> dataset = new ArrayList<String>();
	ArrayList<TWU> twu = new ArrayList<TWU>();
	DatasetReader dr;
	Itemsets patterns;
	ArrayList<ArrayList<Itemset>> candidates;
	int min_util;
	ArrayList<String> exists = new ArrayList<String>();
	ArrayList<String> item_list = new ArrayList<String>();
	long st1,et1,st2,et2;
	int top,min;
public MainScreen(){
	setTitle("mining top-k high utility itemsets");
	getContentPane().setLayout(new BorderLayout());
	f1 = new Font("Times New Roman", Font.BOLD,16);
	p1 = new JPanel();
    
    
	
    f2 = new Font("Times New Roman",Font.BOLD,14);
    p2 = new JPanel();
	p2.setLayout(new BorderLayout());
    dtm = new DefaultTableModel(){
		public boolean isCellEditable(){
			return false;
		}
	};
/* creating transaction table*/

	dtm.addColumn("TID");
	dtm.addColumn("Transaction");
	dtm.addColumn("TU");
	table = new JTable(dtm);
	table.setFont(f2);
	table.setRowHeight(30);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	TableColumn col = table.getColumnModel().getColumn(0);
	col.setPreferredWidth(50);
	col = table.getColumnModel().getColumn(1);
	col.setPreferredWidth(350);
	col = table.getColumnModel().getColumn(2);
	col.setPreferredWidth(50);
	jsp = new JScrollPane(table);
	//jsp.setPreferredSize(new java.awt.Dimension(400,400));
	p2.add(jsp,BorderLayout.WEST);
	dtm1 = new DefaultTableModel(){
		public boolean isCellEditable(){
			return false;
		}
	};
/* creating profit table*/

	dtm1.addColumn("Item");
	dtm1.addColumn("Profit");
	table1 = new JTable(dtm1);
	table1.setFont(f2);
	table1.setRowHeight(30);
	jsp1 = new JScrollPane(table1);
	p2.add(jsp1,BorderLayout.EAST);
	//jsp1.setPreferredSize(new java.awt.Dimension(300,400));

	border = new LineBorder(new Color(42,140,241),1,true);
	title = new TitledBorder (border,"Transaction Table",TitledBorder.CENTER,TitledBorder.DEFAULT_POSITION, new Font("Courier New",Font.BOLD,16),Color.darkGray);
	jsp.setBorder(title);
	border = new LineBorder(new Color(42,140,241),1,true);
	title = new TitledBorder (border,"Profit Table",TitledBorder.CENTER,TitledBorder.DEFAULT_POSITION, new Font("Courier New",Font.BOLD,16),Color.darkGray);
	jsp1.setBorder(title);

	p3 = new JPanel();
	p3.setLayout(new MigLayout("wrap 2"));

/* reading transactions from the database*/

	b1 = new JButton("Read Transaction Database");
	b1.setFont(f2);
	p3.add(b1,"wrap");
	b1.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			try{
				clearTable();
				ArrayList<String> transaction = DBCon.readTransaction();
				for(int i=0;i<transaction.size();i++){
					String row[] = transaction.get(i).split("#");
					dtm.addRow(row);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	});
/* reading profits from the databese*/

	b2 = new JButton("Read Profit Database");
	b2.setFont(f2);
	p3.add(b2,"wrap");
	b2.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			try{
				clearTable1();
				ArrayList<String> profit = DBCon.readProfit();
				for(int i=0;i<profit.size();i++){
					String row[] = profit.get(i).split(",");
					dtm1.addRow(row);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	});
/* computing transaction utility value*/

	b5 = new JButton("Compute TU");
	b5.setFont(f2);
	p3.add(b5,"wrap");
	b5.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			computeTU();
		}
	});
/* computing transaction weighted utilizationn*/

	b7 = new JButton("Compute TWU");
	b7.setFont(f2);
	p3.add(b7,"wrap");
	b7.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			computeTWU();
			
		}
	});
	

	b3 = new JButton("Run TKU");
	b3.setFont(f2);
	p3.add(b3,"span,split 2");
	b3.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			String support = JOptionPane.showInputDialog(MainScreen.this,"Enter Top K Value");
			top = 0;
			try{
				top = Integer.parseInt(support.trim());
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(MainScreen.this,"Top K Value must be numeric only");
				return;
			}
			st1 = System.currentTimeMillis();
			twu.clear();
			for(int i=0;i<item_list.size();i++){
				int total = 0;
				for(int j=0;j<dtm.getRowCount();j++){
					String tid = dtm.getValueAt(j,0).toString().trim();
					String items = dtm.getValueAt(j,1).toString().trim();
					int profit = Integer.parseInt(dtm.getValueAt(j,2).toString().trim());
					String arr[] = items.split("\\s+");
					for(int k=0;k<arr.length;k++){
						String ar[] = arr[k].split(",");
						if(ar[0].equals(item_list.get(i))){
							total = total + (Integer.parseInt(ar[1])*getSupport(item_list.get(i)));
						}
					}
				}
				TWU obj = new TWU();
				obj.setItem(item_list.get(i));
				obj.setTWU(total);
				twu.add(obj);
			}
			java.util.Collections.sort(twu,new TWU());
			min = 0;
			int index = 1;
			for(int i=twu.size()-1;i>=0;i--){
				TWU obj = twu.get(i);
				System.out.println(obj.getItem()+" "+obj.getTWU());
				if(index == top){
					min = obj.getTWU();
					break;
				}
				index = index + 1;
			}
			System.out.println("top "+min);
			dr = new DatasetReader();
			dr.readFile("temp.txt");
			Algorithm tku = new Algorithm(dr);
			patterns = tku.frequentPatterns(min);
			ViewCandidates vfr = new ViewCandidates();
			vfr.setVisible(true);
			vfr.setSize(600,600);
			patterns.tku_function(dr.size(),vfr.dtm,dtm1,min,dtm,twu,top);
			et1 = System.currentTimeMillis();
			
		}
	});

	b8 = new JButton("Run TKO");
	b8.setFont(f2);
	p3.add(b8,"span");
	b8.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			top = 0;
			String support = JOptionPane.showInputDialog(MainScreen.this,"Enter Top K Value");
			try{
				top = Integer.parseInt(support.trim());
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(MainScreen.this,"Top K Value must be numeric only");
				return;
			}
			st2 = System.currentTimeMillis();
			Algorithm tko = new Algorithm(dr);
			patterns = tko.frequentPatterns(min);
			ViewCandidates vfr = new ViewCandidates();
			vfr.setVisible(true);
			vfr.setSize(600,600);
			patterns.tko_function(dr.size(),vfr.dtm,dtm1,min,dtm,twu,top);
			et2 = System.currentTimeMillis();
		}
	});

	

	b4 = new JButton("Close");
	b4.setFont(f2);
	p3.add(b4,"span");
	b4.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			System.exit(0);
		}
	});
	
	
    getContentPane().add(p1, BorderLayout.NORTH);
    getContentPane().add(p2, BorderLayout.CENTER);
	getContentPane().add(p3, BorderLayout.SOUTH);
	thread = new Thread(this);
	thread.start();
}
public void clearTable(){
	for(int i=dtm.getRowCount()-1;i>=0;i--){
		dtm.removeRow(i);
	}
}
public void clearTable1(){
	for(int i=dtm1.getRowCount()-1;i>=0;i--){
		dtm1.removeRow(i);
	}
}
public void run(){
	try{
		while(true){
			l1.setForeground(Color.black);
			thread.sleep(500);
			l1.setForeground(Color.blue);
			thread.sleep(500);
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
public void computeTU(){
	for(int i=0;i<dtm.getRowCount();i++){
		String items[] = dtm.getValueAt(i,1).toString().trim().split("\\s+");
		int count = getCount(items);
		dtm.setValueAt(Integer.toString(count),i,2);
	}
}
public void computeTWU(){
	dataset.clear();
	twu.clear();
	item_list.clear();
	ArrayList<String> temp = new ArrayList<String>();
	for(int i=0;i<dtm.getRowCount();i++){
		String tid = dtm.getValueAt(i,0).toString().trim();
		String items = dtm.getValueAt(i,1).toString().trim();
		int profit = Integer.parseInt(dtm.getValueAt(i,2).toString().trim());
		temp.add(tid+"#"+items+"#"+profit);
		String arr[] = items.split("\\s+");
		for(String str : arr){
			String st[] = str.split(",");
			if(!item_list.contains(st[0]))
				item_list.add(st[0]);
		}
	}
	for(int i=0;i<item_list.size();i++){
		int total = 0;
		for(int j=0;j<dtm.getRowCount();j++){
			String tid = dtm.getValueAt(j,0).toString().trim();
			String items = dtm.getValueAt(j,1).toString().trim();
			int profit = Integer.parseInt(dtm.getValueAt(j,2).toString().trim());
			if(items.indexOf(item_list.get(i)) != -1){
				total = total + profit;
			}
		}
		TWU obj = new TWU();
		obj.setItem(item_list.get(i));
		obj.setTWU(total);
		twu.add(obj);
	}
	for(int i=0;i<temp.size();i++){
		String arr[] = temp.get(i).split("#");
		String str[] = arr[1].split("\\s+");
		StringBuilder sb = new StringBuilder();
		for(int j=0;j<str.length;j++){
			String st[] = str[j].split(",");
			sb.append(st[0]+" ");
		}
		dataset.add(sb.toString().trim());
	}
	try{
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<dataset.size();i++){
			sb.append(dataset.get(i)+System.getProperty("line.separator"));
		}
		FileWriter fw = new FileWriter("temp.txt");
		fw.write(sb.toString());
		fw.close();
	}catch(Exception e){
		e.printStackTrace();
	}
	java.util.Collections.sort(twu,new TWU());
	ViewTWU vt = new ViewTWU();
	vt.setVisible(true);
	vt.setSize(600,400);
	for(int i=twu.size()-1;i>=0;i--){
		TWU obj = twu.get(i);
		Object row[] = {obj.getItem(),obj.getTWU()};
		vt.dtm.addRow(row);
	}
}

public int getCount(String items[]){
	int count = 0;
	for(int i=0;i<items.length;i++){
		String s1[] = items[i].split(",");
		count = count + getCount(s1[0],Integer.parseInt(s1[1].trim()));
	}
	return count;
}
public int getCount(String item,int quantity){
	int count = 0;
	for(int i=0;i<dtm1.getRowCount();i++){
		String it = dtm1.getValueAt(i,0).toString().trim();
		int profit = Integer.parseInt(dtm1.getValueAt(i,1).toString().trim());
		if(item.equals(it)){
			count = quantity * profit;
		}
	}
	return count;
}
public static void main(String a[])throws Exception{
	 UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
	 MainScreen screen = new MainScreen();
	 screen.setExtendedState(JFrame.MAXIMIZED_BOTH);
	 screen.setLocationRelativeTo(null);
	 screen.setVisible(true);
}
public int getSupport(String item){
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


public int getProfit(String item){
	int profit = 0;
	for(int i=0;i<dtm1.getRowCount();i++){
		String it = dtm1.getValueAt(i,0).toString().trim();
		if(item.equals(it)){
			profit = Integer.parseInt(dtm1.getValueAt(i,1).toString().trim());
			break;
		}
	}
	return profit;
}
public String getNodeSupport(String parent,String root){
	int sup = 0;
	int util = 0;
	int count = 0;
	ArrayList<String> unmatch = new ArrayList<String>();
	for(int i=0;i<dtm.getRowCount();i++){
		String top = dtm.getValueAt(i,1).toString().trim();
		String items[] = dtm.getValueAt(i,1).toString().trim().split("\\s+");
		for(int j=0;j<items.length;j++){
			String it[] = items[j].trim().split(",");
			if(top.startsWith(root)){
				if(j < parent.length()){
					String ch = Character.toString(parent.charAt(j));
					if(ch.equals(it[0].trim())){
						count = count + 1;
					}else{
						unmatch.add(items[j].trim());
					}
				}else{
					unmatch.add(items[j].trim());
				}
			}
		}
		if(count == parent.length()){
			sup = sup + 1;
			util = util + Integer.parseInt(dtm.getValueAt(i,2).toString().trim());
			for(int m=0;m<unmatch.size();m++){
				String arr[] = unmatch.get(m).split(",");
				int profit = getProfit(arr[0].trim());
				int quantity = Integer.parseInt(arr[1].trim());
				int tot = profit*quantity;
				util = util-tot;
			}
		}
		count = 0;
		unmatch.clear();
	}
	return util+","+sup;
}
public String getSimilarNode(String root,int i,ArrayList<String> dup){
	double sim = 0;
	String child = "none";
	for(int j=0;j<dtm.getRowCount();j++){
		if(j != i){
			String items[] = dtm.getValueAt(j,1).toString().trim().split("\\s+");
			StringBuilder sb = new StringBuilder();
			for(int k=0;k<items.length;k++){
				String it[] = items[k].trim().split(",");
				sb.append(it[0]);
			}
			double si = Score.getScore(root,sb.toString());
			if(si > sim && !dup.contains(sb.toString())){
				sim = si;
				child = sb.toString();
				dup.add(sb.toString());
			}
		}
	}
	return child;
}
}