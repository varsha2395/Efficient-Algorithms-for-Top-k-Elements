package hui;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
public class DBCon{
public static ArrayList<String> readProfit(){
    ArrayList<String> profit = new ArrayList<String>();
	try{
		BufferedReader br = new BufferedReader(new FileReader("dataset/profit.txt"));
		String line = null;
		while((line = br.readLine())!=null){
			line = line.trim();
			if(line.length() > 0)
				profit.add(line);
		}
		br.close();
	}catch(Exception e){
		e.printStackTrace();
	}
    return profit;
}
public static ArrayList<String> readTransaction(){
    ArrayList<String> transaction = new ArrayList<String>();
	try{
		int j = 1;
		BufferedReader br = new BufferedReader(new FileReader("dataset/transaction.txt"));
		String line = null;
		while((line = br.readLine())!=null){
			line = line.trim();
			if(line.length() > 0){
				transaction.add("T"+j+"#"+line);
				j = j + 1;
			}
		}
		br.close();
	}catch(Exception e){
		e.printStackTrace();
	}
	return transaction;
}
}
