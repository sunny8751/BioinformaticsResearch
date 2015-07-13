import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChangeData {

	public static void main(String[] args) throws IOException {
		for(int i = 1; i<=2; i++){
			new ChangeData().run(Integer.toString(i));
		}
	}

	void run(String num) throws IOException {
		Random random = new Random();
		BufferedReader allData = new BufferedReader(new FileReader("allData.txt"));
		int size = Main.countLines(allData);
		allData.close();
		//shuffle lines
		allData = new BufferedReader(new FileReader("allData.txt"));
		List <String> newAllData = new ArrayList<String>(size);
		String line = allData.readLine();
		while(line!=null){
				newAllData.add(line);
				line = allData.readLine();
		}
		Collections.shuffle(newAllData);
		allData.close();
		//partition
		PrintWriter trainWriter = new PrintWriter("train"+num+".txt", "UTF-8");
		PrintWriter testWriter = new PrintWriter("test"+num+".txt", "UTF-8");
		double percent = .70;// percent of total data that is train set
		int trainSize = 0;
		//for the entire combined data, randomly partition into train and test
		for(int i = 0; i <size; i++) {
			if(random.nextInt(2)==0&&trainSize<(int) (size * percent)){
			//train
				trainSize ++;
				trainWriter.println(newAllData.get(i));
			}else{
			//test
				testWriter.println(newAllData.get(i));
			
			}
		}
		trainWriter.close();
		testWriter.close();
		allData.close();
	}
}
