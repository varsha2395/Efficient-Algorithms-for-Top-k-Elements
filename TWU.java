package hui;
import java.util.Comparator;
public class TWU implements Comparator
{
	String item;
	int twu;
public void setItem(String item){
	this.item = item;
}
public String getItem(){
	return item;
}
public void setTWU(int twu){
	this.twu = twu;
}
public int getTWU(){
	return twu;
}
public int compare(Object sr1, Object sr2){
	TWU p1 =(TWU)sr1;
	TWU p2 =(TWU)sr2;
	int s1 = p1.getTWU();
    int s2 = p2.getTWU();
	if (s1 == s2)
		return 0;
    else if (s1 > s2)
    	return 1;
    else
		return -1;
}
}