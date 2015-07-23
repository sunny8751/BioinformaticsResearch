import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Main {

	// process: select, convert*, scale, train
	// convert may or may not take place

	// select the subset of the data file

	// no train- terminate program after creating files? false=keep doing stuff
	// finalModel- stop grid search and start creating model against test data?
	// allFeat- features 1, 2, and 3
	static boolean noTrain = false, finalModel = false, feat13 = true,
			allFeat = false;
	// features: k feature
	static int features = 1, mers = 34;
	private BufferedReader fp;
	private static BufferedReader bf;
	private StringTokenizer st;
	// size: number of lines in the file
	private int size;
	public static String core = "GCGC";
	// cores: "GCGC" or "GCGG" or "CCGC"
	private static double[] params;
	// form of -c, -p
	private static PrintWriter writer;
	private static int modelCounter = 0;
	static double[] R2Scores;
	// changes from "", selected-, converted-selected-
	public static String currentState = "";

	public static int iteration;

	// converts file to supported format
	public static void main(String[] args) throws IOException {
		args = new String[] { "E2F1" };
		Main m = new Main();
		// in order to do all of them at once
		/*
		 * for(int j = 0; j<2; j++){ String tf = "E2F1"; if(j==1){ tf = "E2F4";
		 * }
		 */
		if (!finalModel) {
			for (int i = 1; i <= 10; i++) {
				iteration = i;
				if (!finalModel) {
					GridSearch.Start();
				}
				if (noTrain) {
					finalModel = false;
				}
				train = "train" + i + args[0] + ".txt";
				test = "test" + i + args[0] + ".txt";
				// select the subgroup of sequences to analyze and reformat
				m.select(train, true);
				// do the same with the test file
				m.select(test, false);
				/*
				 * // next scale the feature data svm_scale.main(new String[] {
				 * "converted-selected-train", "converted-selected-test" });
				 */
				// start training
				if (!finalModel && !noTrain) {
					System.out.println("ITERATION DATA " + i);
					GridSearch.go();
					// get top 5 values
					double[] ascendingValues = new double[14 * 14];
					int index = 0;
					for (int a = 0; a < 14; a++) {
						for (int b = 0; b < 14; b++) {
							ascendingValues[index] = GridSearch.values[a][b];
							index++;
						}
					}
					Arrays.sort(ascendingValues);
					params = new double[2 * 5];
					for (int a = 0; a < 5; a++) {
						// c, p
						// find the c & p indices of the top value
						int pIndex = -1, cIndex = -1;
						for (int c = 0; c < 14; c++) {
							for (int p = 0; p < 14; p++) {
								if (GridSearch.values[p][c] == ascendingValues[ascendingValues.length
										- 1 - a]) {
									pIndex = p;
									cIndex = c;
									System.out.println(p + ", " + c);
									break;
								}
							}
							if (pIndex != -1) {
								break;
							}
						}
						params[2 * a] = GridSearch.cValues.get(cIndex);
						params[2 * a + 1] = GridSearch.pValues.get(pIndex);
						// System.out.println("indices "+cIndex+", "+pIndex);
						// System.out.println("top: "+ascendingValues[modelCounter]);
					}
					// get models for these values
					finalModel = true;
					bf = new BufferedReader(new FileReader("errors" + iteration
							+ ".txt"));
					writer = new PrintWriter("errors_" + iteration + ".txt",
							"UTF-8");
					// add model title stuff to writer
					writer.print(bf.readLine() + "\t-c\t-p\tR^2");
					writer.println();
					R2Scores = new double[5];
					for (modelCounter = 0; modelCounter < 5; modelCounter++) {
						svm_train.main(new String[] { "-q", "-c",
								Double.toString(params[2 * modelCounter]),
								"-p",
								Double.toString(params[2 * modelCounter + 1]),
								"-s", "3", "-t", "0", currentState + "train" });
						svm_predict.main(new String[] { currentState + "test",
								currentState + "train.model", "output.txt" });
					}
					// finish printing the rest of the data
					String line = bf.readLine();
					while (line != null) {
						writer.print(line);
						writer.println();
						line = bf.readLine();
					}
					// done with error file
					writer.close();
					bf.close();
					// create the best model from R2Scores
					double largestScore = R2Scores[0];
					for (int a = 1; a < 5; a++) {
						if (R2Scores[a] > largestScore) {
							largestScore = R2Scores[a];
						}
					}
					index = -1;
					for (int a = 0; a < 5; a++) {
						if (R2Scores[a] == largestScore) {
							index = a;
						}
					}
					modelCounter = -1;
					svm_train.main(new String[] {
							"-q",
							"-c",
							Double.toString(params[2 * index]),
							"-p",
							Double.toString(params[2 * index + 1]),
							"-s",
							"3",
							"-t",
							"0",
							currentState + "train",
							"data" + iteration + "_" + args[0] + "_Core" + core
									+ "_Feat13.model" });
					svm_predict.main(new String[] {
							currentState + "test",
							"data" + iteration + "_" + args[0] + "_Core" + core
									+ "_Feat13.model", "output.txt" });
					convertModel.convert("data" + iteration + "_" + args[0]
							+ "_Core" + core + "_Feat13");
					// done
					finalModel = false;
					// ok next dataset now
				}
			}
		} else {
			// if final model
			// select and convert
			m.select(train, true);
			// do the same with the test file
			m.select(test, false);
			for (modelCounter = 0; modelCounter < (int) params.length / 2; modelCounter++) {
				svm_train.main(new String[] { "-q", "-c",
						Double.toString(params[2 * modelCounter]), "-p",
						Double.toString(params[2 * modelCounter + 1]), "-s",
						"3", "-t", "0", currentState + "train" });
				svm_predict.main(new String[] { currentState + "test",
						currentState + "train.model", "output.txt" });
			}
			// done
			writer.close();
		}
	}

	static String train = "train0E2F4.txt", test = "test0E2F4.txt";

	public Main() throws FileNotFoundException, UnsupportedEncodingException {
		int i = 9;
		train = "train" + i + "E2F4.txt";
		test = "test" + i + "E2F4.txt";
		params = new double[] { 0.012835, 0.102679, };
		if (finalModel) {
			writer = new PrintWriter("models", "UTF-8");
			// print c
			writer.print("c\t");
			// print p
			writer.print("p\t");
			// print value
			writer.print("value");
			writer.println();
		}
	}

	public static void add(Double score) throws IOException {
		if (modelCounter == -1) {
			return;
		}
		// add this score to models from the prediction with the test set
		// print c
		System.out.print(params[2 * modelCounter] + "\t");
		// print p
		System.out.print(params[2 * modelCounter + 1] + "\t");
		// print score
		System.out.print(score.toString());
		System.out.println();

		if (iteration != 0) {
			// add these to the right of the data values
			// print data before it
			writer.print(bf.readLine() + "\t");
			// print c
			writer.print(params[2 * modelCounter] + "\t");
			// print p
			writer.print(params[2 * modelCounter + 1] + "\t");
			// print score
			writer.print(score.toString());
			writer.println();
			R2Scores[modelCounter] = score;
		}
	}

	private void select(String arg, boolean training) throws IOException {
		// make a selection of sequences to train/test
		PrintWriter writer;
		currentState = "selected-";
		if (training) {
			writer = new PrintWriter(currentState + "train", "UTF-8");
		} else {
			writer = new PrintWriter(currentState + "test", "UTF-8");
		}
		fp = new BufferedReader(new FileReader(arg));
		// calculate the number of seqs and lines in data
		size = countLines(fp);
		fp.close();
		fp = new BufferedReader(new FileReader(arg));
		if (core.equals("")) {
			// no specified core
			String line = fp.readLine();
			while (line != null) {
				writer.println(line);
				line = fp.readLine();
			}
		} else {
			// selects subset with specified core
			// this is only to count initial seq amount and see if seq contains
			// core
			// this is to print selected seqs back to a new file
			int max = size;
			// calculate the new size of the seqs that have been selected
			size = 0;
			for (int i = 0; i < max; i++) {
				st = new StringTokenizer(fp.readLine());
				String score = st.nextToken();
				// makes sure seq contains core seq that is in middle
				String seq = st.nextToken();
				if (seq.indexOf(core) == mers / 2 - 2) {
					// select this
					writer.println(score + "\t" + seq);
					size++;
				}
			}
		}
		fp.close();
		writer.close();
		if (training) {
			run("train");
		} else {
			run("test");
		}
	}

	private void run(String arg) throws IOException {
		// converts to right format
		fp = new BufferedReader(new FileReader(currentState + arg));
		PrintWriter writer;
		currentState = "converted-" + currentState;
		writer = new PrintWriter(currentState + arg, "UTF-8");

		for (int c = 0; c < size; c++) {
			// each sequence
			st = new StringTokenizer(fp.readLine());
			writer.print(st.nextToken()); // prints the response
			// look at each position in the sequence
			String seq = st.nextToken();
			boolean repeat = false;
			if (allFeat || feat13) {
				features = 1;
				repeat = true;
			}
			do {
				// need to shift features values over for allFeat
				// because 2mers after 1mers and 3mers after 2mers
				int valueShift = 0;
				if (allFeat) {
					if (features == 2) {
						valueShift = 4 * (mers - 4);
					} else if (features == 3) {
						valueShift = 4 * (mers - 4) + 16 * (mers - 1 - 3);
					}
				} else if (feat13) {
					if (features == 2) {
						features = 3;
						valueShift = 4 * (mers - 4);
					}
				}
				int counter = 0;
				for (int i = 0; i < seq.length() + 1 - features; i++) {
					// each position of sequence
					// find the value for each position with respect to the
					// feature number
					// print the feature value and its corresponding value
					// only include features that aren't only core
					if (i < mers / 2 - 2 || i >= mers / 2 + 2 + (1 - features)) {
						// feature location+position+base pos
						writer.print(" "
								+ (int) (valueShift
										+ test(seq.substring(i, i + features),
												features) + counter
										* Math.pow(4, features)) + ": 1");
						counter++;
					}
				}
				if (allFeat || feat13) {
					if (features == 3) {
						repeat = false;
					} else {
						features++;
					}
				}
			} while (repeat);
			writer.println();
		}

		writer.close();
		fp.close();
		// created the converted file in the right format
	}

	private int test(String _seq, int _features) {
		// returns the single/pair/triplet value of the input seq at that
		// position
		int value = -1;
		if (features == 1) {
			switch (_seq) {
			case "A":
				value = 1;
				break;
			case "C":
				value = 2;
				break;
			case "G":
				value = 3;
				break;
			case "T":
				value = 4;
				break;
			default:
				value = -1;
			}
		} else if (features == 2) {
			switch (_seq) {
			case "AA":
				value = 1;
				break;
			case "AC":
				value = 2;
				break;
			case "AG":
				value = 3;
				break;
			case "AT":
				value = 4;
				break;
			case "CA":
				value = 5;
				break;
			case "CC":
				value = 6;
				break;
			case "CG":
				value = 7;
				break;
			case "CT":
				value = 8;
				break;
			case "GA":
				value = 9;
				break;
			case "GC":
				value = 10;
				break;
			case "GG":
				value = 11;
				break;
			case "GT":
				value = 12;
				break;
			case "TA":
				value = 13;
				break;
			case "TC":
				value = 14;
				break;
			case "TG":
				value = 15;
				break;
			case "TT":
				value = 16;
				break;
			default:
				System.err.print("unknown nucleotide\n");
			}
		} else if (features == 3) {
			// first nucleotide
			if (_seq.charAt(0) == 'A') {
				value = 0;
			} else if (_seq.charAt(0) == 'C') {
				value = 16;
			} else if (_seq.charAt(0) == 'G') {
				value = 32;
			} else if (_seq.charAt(0) == 'T') {
				value = 48;
			}
			// second nucleotide
			if (_seq.charAt(1) == 'A') {
				value += 0;
			} else if (_seq.charAt(1) == 'C') {
				value += 4;
			} else if (_seq.charAt(1) == 'G') {
				value += 8;
			} else if (_seq.charAt(1) == 'T') {
				value += 12;
			}
			// third nucleotide
			if (_seq.charAt(2) == 'A') {
				value += 1;
			} else if (_seq.charAt(2) == 'C') {
				value += 2;
			} else if (_seq.charAt(2) == 'G') {
				value += 3;
			} else if (_seq.charAt(2) == 'T') {
				value += 4;
			}
		}
		return value;
	}

	public static int countLines(BufferedReader bf) throws IOException {
		String l = bf.readLine();
		int counter = 0;
		while (l != null) {
			counter++;
			l = bf.readLine();
		}
		return counter;
	}
}
