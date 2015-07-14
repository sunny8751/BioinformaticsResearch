import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class GridSearch {

	// P, C... Iterate through P first, then C

	private static int counterP = 0, counterC = 0;
	private static boolean course = true;
	private static double[][] values;
	private static long startTime;
	private static PrintWriter writer;
	private static boolean started = false;

	private static int maxNumC;
	// y-intercept and slope?
	private static double yC, mC;

	private static double getC(int index) {
		// starts from 0
		if (course) {
			return Math.pow(2, index - 7);
		} else {
			return yC + mC * index;
		}
	}

	private static int maxNumP;
	// y-intercept and slope?
	private static double yP, mP;

	private static double getP(int index) {
		// starts from 0
		if (course) {
			return Math.pow(2, index - 9);
		} else {
			return yP + index * mP;
		}
	}

	public static void Start() throws FileNotFoundException,
			UnsupportedEncodingException {
		maxNumC = 8;
		maxNumP = 8;
		values = new double[maxNumP][maxNumC];
		writer = new PrintWriter("courseErrors" + Main.iteration, "UTF-8");
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
		if (!started) {
			int secs = (int) ((System.currentTimeMillis() - startTime)
					* maxNumC * maxNumP / 1000);
			System.out.println("Estimated Wait: "
					+ ((int) ((secs - secs % 60) / 60)) + ":" + secs % 60);
			System.out.println(counterP + ", " + counterC);
			started = true;
		} else if (counterP == 0) {
			System.out.println(counterP + ", " + counterC);
		}
		System.out.println(value);
		writer.print(value + " ");
		values[counterP][counterC] = value;
	}

	private static void fine() throws IOException {
		counterP = 0;
		counterC = 0;
		//
		// get min and max values of p and c of top 3 R^2 values
		int[] pValues = new int[3], cValues = new int[3];
		double[] top3 = topValues(3);
		// allocate to pValues and cValues
		for (int i = 0; i < 3; i++) {
			// find the value in ascendingValues
			// p and c values no longer in ascending order
			pValues[i] = findIndices(top3[i], values)[0];
			cValues[i] = findIndices(top3[i], values)[1];
		}
		Arrays.sort(pValues);
		Arrays.sort(cValues);
		// set the getP() and getC() variables now
		if (top3[2] >= top3[1] + .02D) {
			System.out.println("Method 1");
			// if top value is way above the other 2
			// just consider the top value
			int middleP = findIndices(top3[2], values)[0];
			int middleC = findIndices(top3[2], values)[1];
			mP = getP(middleP) * .2f;
			mC = getC(middleC) * .2f;
			yP = getP(middleP) - mP * 3;
			yC = getC(middleC) - mC * 3;
			course = false;
		} else if (pValues[2] == maxNumP - 1 || cValues[2] == maxNumC - 1
				|| pValues[2] == 0 || cValues[2] == 0) {
			System.out.println("Method 2");
			// if the highest value is on the edge of course grid search
			int middleP = findIndices(top3[2], values)[0];
			int middleC = findIndices(top3[2], values)[1];
			mP = getP(middleP) * .2f;
			mC = getC(middleC) * .2f;
			yP = getP(middleP) - mP * 3;
			yC = getC(middleC) - mC * 3;
		} else {
			System.out.println("Method 3");
			yP = getP(pValues[0]);
			yC = getC(cValues[0]);
			mP = (getP(pValues[2]) - getP(pValues[0])) / 6f;
			mC = (getC(cValues[2]) - getC(cValues[0])) / 6f;
		}
		System.out.println("yP" + yP + ", mP" + mP + ", yC" + yC + ", mC" + mC);
		System.out.println("TOP: "+top3[2]);
		maxNumC = 7;
		maxNumP = 7;
		course = false;
		values = new double[maxNumP][maxNumC];
		// setup the writer
		writer = new PrintWriter("fineErrors" + Main.iteration, "UTF-8");
		writer.print(" ");
		for (int i = 0; i < maxNumP; i++) {
			writer.print(getP(i) + " ");
		}
		writer.println();
		writer.print(getC(0) + " ");
		train();
	}

	static double[] topValues(int length) {
		double[] ascendingValues = new double[maxNumP * maxNumC];
		for (int c = 0; c < maxNumC; c++) {
			for (int p = 0; p < maxNumP; p++) {
				ascendingValues[p + c * maxNumP] = values[p][c];
			}
		}
		// sort in ascending order
		Arrays.sort(ascendingValues);
		// get last elements, aka biggest values
		// it is still in increasing order
		double[] top = new double[length];
		for (int i = 0; i < length; i++) {
			top[i] = ascendingValues[ascendingValues.length - length + i];
		}
		return top;
	}

	// this is for the fine() method
	static int[] findIndices(double d, double[][] values) {
		// d is the value to look for in values[0][0]
		int valueP = -1, valueC = -1;
		for (int c = 0; c < maxNumC; c++) {
			for (int p = 0; p < maxNumP; p++) {
				if (values[p][c] == d) {
					valueP = p;
					valueC = c;
					break;
				}
			}
			if (valueP != -1) {
				break;
			}
		}
		return new int[] { valueP, valueC };
	}

	public static void check() throws IOException {
		// check if the values are done being assigned
		// if done, then find the optimal parameters
		// if not, then continue training
		if (counterC == maxNumC - 1 && counterP == maxNumP - 1) {
			// done
			writer.close();
			// if course, do fine now
			if (course) {
				counterP = 0;
				counterC = 0;
				started = false;
				//System.out.println("STARTING FINE SEARCH.............");
				/*
				 * dont need to set counter to 0... already done in fine()
				 * fine();
				 */
				return;
			} else {
				// try top 5 and find best model
				counterP = 0;
				counterC = 0;
				started = false;
				// return to main and do next iteration
				System.out.println("DONE WITH ITERATION.............");
				// highest score
				double top = topValues(1)[0];
				System.out.println(top + " at " + findIndices(top, values)[0]
						+ ", " + findIndices(top, values)[1]);
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
		/*
		 * when you do scale svm_train.main(new String[] { "-q", "-c",
		 * Double.toString(getC(counterC)), "-p",
		 * Double.toString(getP(counterP)), "-s", "3", "-t", "0", "-v", "5",
		 * "scaled-converted-selected-train" });
		 */
		svm_train.main(new String[] { "-q", "-c",
				Double.toString(getC(counterC)), "-p",
				Double.toString(getP(counterP)), "-s", "3", "-t", "0", "-v",
				"5", "converted-selected-train" });
	}
}