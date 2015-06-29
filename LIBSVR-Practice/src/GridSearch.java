import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class GridSearch {

	// P, C... Iterate through P first, then C

	private static int counterP = 4, counterC = 4;
	private static boolean course = true;
	private static long startTime;
	private static PrintWriter writer;

	private static int maxNumC = 8;

	private static double getC(int index) {
		// starts from 0
		if (course) {
			return Math.pow(2, index - 7);
		} else {
			return .005 + .01 * index;
		}
	}

	private static int maxNumP = 9;

	private static double getP(int index) {
		// starts from 0
		if (course) {
			return Math.pow(2, index - 9);
		} else {
			return .055 + index * .05;
		}
	}

	public static void Start() throws FileNotFoundException,
			UnsupportedEncodingException {
		writer = new PrintWriter("courseErrors", "UTF-8");
		writer.print(" ");
		for (int i = 0; i < maxNumP; i++) {
			writer.print(getP(i) + " ");
		}
		writer.println();
		writer.print(getC(0) + " ");
	}

	public static void go() throws IOException {
		// begin the training process
		//
		// course grid search
		course = true;
		maxNumC = 8;
		maxNumP = 9;
		train();
	}

	public static void main(String argv[]) throws IOException {
		// this method shouldn't run unless it is deliberately run
		// for easier testing purposes

		// argv = new String[] { "converted-selected-"+Main.train,
		// "converted-selected-test.txt"};
		// initialize
		Start();
		// start training the first pair of parameters
		train();
	}

	public static void add(double value) {
		// add this score to errors from the cross validation in grid search
		if (counterC == 0 && counterP == 0) {
			int secs = (int) ((System.currentTimeMillis() - startTime)
					* maxNumC * maxNumP / 1000);
			System.out.println("Estimated Wait: "
					+ ((int) ((secs - secs % 60) / 60)) + ":" + secs % 60);
		}
		System.out.println(counterP + ", " + counterC);
		System.out.println(value);
		writer.print(value + " ");
	}
	
	private static void fine() throws IOException{
		course = false;
		writer = new PrintWriter("fineErrors", "UTF-8");
		
	}

	public static void check() throws IOException {
		// check if the values are done being assigned
		// if done, then find the optimal parameters
		// if not, then continue training
		if (counterC == maxNumC - 1 && counterP == maxNumP - 1) {
			// done
			writer.close();
			//if course, do fine now
			if(course){
				fine();
			}else{
				return;
			}
		}
		// not done yet
		counterP++;
		if (counterP == maxNumP) {
			counterP = 0;
			counterC++;
			writer.println();
			writer.print(getC(counterC) + " ");
		}
		train();
	}

	private static void train() throws IOException {
		if (counterC == 0 && counterP == 0) {
			startTime = System.currentTimeMillis();
		}
		svm_train.main(new String[] { "-q", "-c",
				Double.toString(getC(counterC)), "-p",
				Double.toString(getP(counterP)), "-s", "3", "-t", "0", "-v",
				"5", "scaled-converted-selected-" + Main.train });
	}
}