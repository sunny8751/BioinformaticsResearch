import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChangeData {

	public static void main(String[] args) throws IOException {
		for (int i = 1; i <= 10; i++) {
			new ChangeData().run(Integer.toString(i), "E2F1");
		}
	}

	void run(String num, String tf) throws IOException {
		BufferedReader allData = new BufferedReader(new FileReader("processed"
				+ tf + ".txt"));
		int size = Main.countLines(allData);
		allData.close();
		// shuffle lines
		allData = new BufferedReader(new FileReader("processed" + tf + ".txt"));
		List<String> newAllData = new ArrayList<String>(size);
		String line = allData.readLine();
		while (line != null) {
			newAllData.add(line);
			line = allData.readLine();
		}
		Collections.shuffle(newAllData);
		allData.close();
		// partition
		PrintWriter trainWriter = new PrintWriter("train" + num + tf + ".txt",
				"UTF-8");
		PrintWriter testWriter = new PrintWriter("test" + num + tf + ".txt",
				"UTF-8");
		double percent = .80;// percent of total data that is train set
		int trainSize = 0;
		// for the entire combined data, randomly partition into train and test
		for (int i = 0; i < size; i++) {
			if (trainSize < (int) (size * percent)) {
				// train;
				trainWriter.println(newAllData.get(i));
				trainSize++;
			} else {
				// test
				testWriter.println(newAllData.get(i));
			}
		}
		trainWriter.close();
		testWriter.close();
		allData.close();
	}
}
