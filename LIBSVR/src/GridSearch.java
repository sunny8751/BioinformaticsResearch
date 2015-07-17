import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

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
			if (index == 3) {
				index++;
				counterC++;
			}
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
			if (index == 3) {
				index++;
				counterP++;
			}
			return yP + index * mP;
		}
	}

	public static void Start() throws FileNotFoundException,
			UnsupportedEncodingException {
		maxNumC = 8;
		maxNumP = 8;
		values = new double[maxNumP][maxNumC];
		writer = new PrintWriter("courseErrors" + Main.iteration, "UTF-8");
		for (int i = 0; i < maxNumP; i++) {
			writer.print("\t" + getP(i));
		}
		writer.println();
		writer.print(getC(0) + "\t");
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
		// Start();
		// start training the first pair of parameters
		// train();
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
		if (course) {
			writer.print(value + "\t");
			values[counterP][counterC] = value;
		} else {
			for (int i = 0; i < 7; i++) {
				int cIndex = cValues.indexOf(getC(i));
				for (int j = 0; j < 7; j++) {
					int pIndex = pValues.indexOf(getP(j));
					values[pIndex][cIndex] = value;
					return;
				}
			}
		}
		// PUT NOTHING HERE CUZ OF THE RETURN
	}

	private static List<Double> pValues;
	private static List<Double> cValues;

	private static void fine() throws IOException {
		counterP = 0;
		counterC = 0;
		//
		// get min and max values of p and c of top 3 R^2 values
		int pValue, cValue;
		double topValue = topValues(1)[0];
		// allocate to pValues and cValues
		// find the p and c values
		pValue = findIndices(topValue, values)[0];
		cValue = findIndices(topValue, values)[1];
		// set the getP() and getC() variables now
		mC = getC(cValue) * 1.5 / 7d;
		yC = getC(cValue) - 3 * mC;
		mP = getP(pValue) * 1.5 / 7d;
		yP = getP(pValue) - 3 * mP;
		System.out.println("yP" + yP + ", mP" + mP + ", yC" + yC + ", mC" + mC);
		maxNumC = 7;
		maxNumP = 7;
		values = new double[maxNumP][maxNumC];
		// get the values from courseErrors in arraylist
		pValues = new ArrayList<Double>();
		cValues = new ArrayList<Double>();
		values = new double[8][8];
		BufferedReader bf = new BufferedReader(new FileReader("courseErrors"
				+ Main.iteration));
		for (int i = 0; i < 9; i++) {
			// c
			StringTokenizer st = new StringTokenizer(bf.readLine());
			if (i == 0) {
				// first line
				while (st.hasMoreTokens()) {
					pValues.add(Double.parseDouble(st.nextToken()));
				}
			} else {
				// beginning of each line, add to cValues
				cValues.add(Double.parseDouble(st.nextToken()));
				// p,c
				// counter for p
				for (int j = 0; j < 8; j++) {
					// p
					values[j][i - 1] = Double.parseDouble(st.nextToken());
				}
			}
		}
		bf.close();
		// add spaces for the fine grid search values
		double[][] courseValues = values;
		values = new double[14][14];
		for (int i = 0; i < 7; i++) {
			if (i == 3) {
				continue;
			}
			// c values
			cValues.add(yC + i * mC);
			// p values
			pValues.add(yP + i * mP);
		}
		Collections.sort(cValues);
		Collections.sort(pValues);
		// add the course values into values
		// at the right positions
		for (int i = 0; i < 8; i++) {
			int cIndex = cValues.indexOf(getC(i));
			for (int j = 0; j < 8; j++) {
				int pIndex = pValues.indexOf(getP(j));
				// System.out.println(getP(j) + ", " + pIndex);
				values[pIndex][cIndex] = courseValues[j][i];
			}
		}
		writer = new PrintWriter("analysis" + Main.iteration, "UTF-8");
		for (int i = 0; i < 14; i++) {
			for (int j = 0; j < 14; j++) {
				writer.print(values[j][i] + "\t");
			}
			writer.println();
		}
		writer.close();
		course = false;
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
			// if course, do fine now
			if (course) {
				writer.close();
				// counterP = 0;
				// counterC = 0;
				started = false;
				// System.out.println(topValues(1)[0]);
				fine();
				// System.out.println("STARTING FINE SEARCH.............");
				/*
				 * dont need to set counter to 0... already done in fine()
				 * fine();
				 */
				return;
			} else {
				// write to errors file
				// setup the writer
				writer = new PrintWriter("errors" + Main.iteration, "UTF-8");
				writer.print("P, C");
				for (int i = 0; i < 14; i++) {
					writer.print("\t" + cValues.get(i));
				}
				for (int i = 0; i < 14; i++) {
					// each line
					writer.println();
					writer.print(pValues.get(i));
					// print the error values now
					for (int j = 0; j < 14; j++) {
						writer.print("\t" + values[i][j]);
					}
				}
				writer.close();

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
			writer.print(getC(counterC) + "\t");
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