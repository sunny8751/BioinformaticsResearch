import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class ChangeData {

	public static void main(String[] args) throws IOException {
		for(int i = 3; i<=10; i++){
			new ChangeData().run(Integer.toString(i));
		}
	}

	void run(String num) throws IOException {
		Random random = new Random();
		BufferedReader allData = new BufferedReader(new FileReader("allData.txt"));
		int size = (int) (allData.lines().count());
		allData.close();
		allData = new BufferedReader(new FileReader("allData.txt"));
		
		PrintWriter trainWriter = new PrintWriter("train"+num+".txt", "UTF-8");
		PrintWriter testWriter = new PrintWriter("test"+num+".txt", "UTF-8");
		double percent = .70;// percent of total data that is train set

		String line = allData.readLine();
		int trainSize = 0;
		//for the entire combined data, randomly partition into train and test
		while (line != null) {
			if(random.nextInt(2)==0&&trainSize<(int) (size * percent)){
			//train
				trainSize ++;
				trainWriter.println(line);
				line = allData.readLine();
			}else{
			//test
				testWriter.println(line);
				line = allData.readLine();
			
			}
		}
		trainWriter.close();
		testWriter.close();
		allData.close();
	}
}
