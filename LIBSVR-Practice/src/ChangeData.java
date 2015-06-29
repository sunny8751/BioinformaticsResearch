import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class ChangeData {

	public static void main(String[] args) throws IOException {
		new ChangeData().run();
	}
	
	void run() throws IOException{

		Random random = new Random();

		BufferedReader train = new BufferedReader(new FileReader("train.txt"));
		BufferedReader test = new BufferedReader(new FileReader("test.txt"));
		int size = (int) (train.lines().count() + test.lines().count());
		train.close();
		test.close();
		train = new BufferedReader(new FileReader("train.txt"));
		test = new BufferedReader(new FileReader("test.txt"));

		PrintWriter trainWriter = new PrintWriter("nTrain", "UTF-8");
		PrintWriter testWriter = new PrintWriter("nTest", "UTF-8");
		double percent = .70;//percent of total data that is train set

		// new train file
		String trainLine = train.readLine(), testLine = test.readLine();
		for (int i = 0; i < (int) (size * percent); i++) {
			if (random.nextInt(2) == 0) {
				// take from original train dataset
				// make sure enough lines
				if (trainLine == null) {
					trainWriter.println(testLine);
					testLine = test.readLine();
				} else {
					// can write
					trainWriter.println(trainLine);
					trainLine = train.readLine();
				}
			} else {
				// take from original test dataset
				// make sure enough lines
				if (testLine == null) {
					trainWriter.println(trainLine);
					trainLine = train.readLine();
				} else {
					// can write
					trainWriter.println(testLine);
					testLine = test.readLine();
				}
			}
		}

		// print the rest in the new test file
		while (trainLine != null) {
			testWriter.println(trainLine);
			trainLine = train.readLine();
		}
		while (testLine != null) {
			testWriter.println(testLine);
			testLine = test.readLine();
		}
		trainWriter.close();
		testWriter.close();
		train.close();
		test.close();
	}
}
