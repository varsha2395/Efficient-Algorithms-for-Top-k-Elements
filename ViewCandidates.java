package hui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
public class ViewCandidates extends JFrame{
	Font f1;
	JPanel p1;
	DefaultTableModel dtm;
	JScrollPane jsp;
	JTable table;
public ViewCandidates(){
	setTitle("items");
	getContentPane().setLayout(new BorderLayout());
	
    f1 = new Font("Courier New",Font.BOLD,14);
    p1 = new JPanel();
	p1.setLayout(new BorderLayout());
    dtm = new DefaultTableModel(){
		public boolean isCellEditable(){
			return false;
		}
	};
	dtm.addColumn("Item Name");
	dtm.addColumn("support count");
	dtm.addColumn("utility value");
	table = new JTable(dtm);
	table.setFont(f1);
	table.setRowHeight(30);
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	TableColumn col = table.getColumnModel().getColumn(0);
	col.setPreferredWidth(390);
	col = table.getColumnModel().getColumn(1);
	col.setPreferredWidth(100);
	jsp = new JScrollPane(table);
	p1.add(jsp,BorderLayout.CENTER);
	
	getContentPane().add(p1, BorderLayout.CENTER);
}
public void clearTable(){
	for(int i=dtm.getRowCount()-1;i>=0;i--){
		dtm.removeRow(i);
	}
}

}