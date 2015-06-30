import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class GridSearch {

	// P, C... Iterate through P first, then C

	private static int counterP = 0, counterC = 0;
	private static boolean course = true;
	private static double[][] values;
	private static long startTime;
	private static PrintWriter writer;

	private static int maxNumC = 8;
	//y-intercept and slope?
	private static double yC, mC;

	private static double getC(int index) {
		// starts from 0
		if (course) {
			return Math.pow(2, index - 7);
		} else {
			return yC + mC * index;
		}
	}

	private static int maxNumP = 9;
	//y-intercept and slope?
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
		values = new double[maxNumP][maxNumC];
		writer = new PrintWriter("courseErrors"+Main.iteration, "UTF-8");
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
		if (course) {
			values[counterP][counterC] = value;
		}
	}

	private static void fine() throws IOException {
		course = false;
		writer = new PrintWriter("fineErrors", "UTF-8");
		counterP = 0;
		counterC = 0;
		//
		//get min and max values of p and c of top 3 R^2 values
		int [] pValues = new int[3], cValues = new int[3];
		double [] top3 = new double[3];
		for (int p = 0; p < maxNumP; p++) {
			for (int c = 0; c < maxNumC; c++) {
				double value = values[p][c];
				//counter- see if this value is higher than all of top3
				//happens when counter==3
				int counter = 0, indexOfLowestValue = 0;
				//find the lowest value to replace later on
				double lowestValue = top3[0];
				for(int i = 0; i <top3.length; i++){
					//value is higher than top 3
					if(value>top3[i]){
						counter++;
					}
					//find the lowest value that "value" will replace
					if(top3[i]<lowestValue){
						lowestValue = top3[i];
						indexOfLowestValue = i;
					}
				}
				if(counter==3){
					//now switch for the lowest value
					top3[indexOfLowestValue] = value;
					pValues[indexOfLowestValue] = p;
					cValues[indexOfLowestValue] = c;
				}
			}
		}
		//determine how to divide fine grid search
		//find range of p and c values
		int indexMinP = 0, indexMinC = 0, indexMaxP = 0, indexMaxC = 0;
		for(int i = 0; i < pValues.length; i ++){
			//min
			if(pValues[i]<pValues[indexMinP]){
				indexMinP = i;
			}
			//max
			if(pValues[i]>pValues[indexMaxP]){
				indexMaxP = i;
			}
		}
		for(int i = 0; i < cValues.length; i ++){
			//min
			if(cValues[i]<cValues[indexMinC]){
				indexMinC = i;
			}
			//max
			if(cValues[i]>cValues[indexMaxC]){
				indexMaxC = i;
			}
		}
		yP = pValues[indexMinP];
		yC = cValues[indexMinC];
		mP = (pValues[indexMaxP]-pValues[indexMinP])/6;
		mC = (cValues[indexMaxC]-cValues[indexMinC])/6;
		maxNumP = 6;
		maxNumC = 6;
		//
		writer.print(" ");
		for (int i = 0; i < maxNumP; i++) {
			writer.print(getP(i) + " ");
		}
		writer.println();
		writer.print(getC(0) + " ");
		train();
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
				System.out.println("Done with iteration.");
				return;
			} else {
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
				"5", "scaled-converted-selected-train" });
	}
}