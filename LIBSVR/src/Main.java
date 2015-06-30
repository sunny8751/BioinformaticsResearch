import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	static String train = "train3.txt", test = "test3.txt";
	// features: k feature
	static int features = 3, mers = 36;
	private BufferedReader fp, nfp;
	private StringTokenizer st;
	// size: number of lines in the file
	private int size;
	private String core = "";
	// cores: "GCGC" or "GCGG" or "CCGC"
	private static double[] params;
	// form of -c, -p
	private static PrintWriter writer;
	private static int counter;

	// converts file to supported format
	public static void main(String[] args) throws IOException {
		if (!finalModel) {
			GridSearch.Start();
		}
		if (noTrain) {
			finalModel = false;
		}
		Main m = new Main();
		// select the subgroup of sequences to analyze and reformat
		m.select(train, true);
		// do the same with the test file
		m.select(test, false);
		
		//next scale the feature data
		svm_scale.main(new String[] { "converted-selected-train",
				"converted-selected-test" });
		// if final model
		if (finalModel) {
			for (counter = 0; counter < (int) params.length / 2; counter++) {
				svm_train.main(new String[] { "-q", "-c",
						Double.toString(params[2 * counter]), "-p",
						Double.toString(params[2 * counter + 1]), "-s", "3",
						"-t", "0", "scaled-converted-selected-train" });
				svm_predict
						.main(new String[] { "scaled-converted-selected-test",
								"scaled-converted-selected-train.model",
								"output.txt" });
			}
			// done
			writer.close();
		}
	}

	public Main() throws FileNotFoundException, UnsupportedEncodingException {
		params = new double[] { 0.06, 0.12

		};
		if (finalModel) {
			writer = new PrintWriter("models", "UTF-8");
			// print c
			writer.print("c ");
			// print p
			writer.print("p ");
			// print value
			writer.print("value");
			writer.println();
		}
	}

	public static void add(String s1, String s2) {
		// add this score to models from the prediction with the test set
		// print c
		writer.print(params[2 * counter] + " ");
		// print p
		writer.print(params[2 * counter + 1] + " ");
		// print score
		writer.print(s1);
		writer.println();
		writer.print(s2);
		writer.println();
	}

	private void select(String arg, boolean training) throws IOException {
		// make a selection of sequences to train/test
		PrintWriter writer;
		if (training) {
			writer = new PrintWriter("selected-train", "UTF-8");
		} else {
			writer = new PrintWriter("selected-test", "UTF-8");
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
			nfp = new BufferedReader(new FileReader(arg));
			int max = size;
			// calculate the new size of the seqs that have been selected
			size = 0;
			for (int i = 0; i < max; i++) {
				st = new StringTokenizer(fp.readLine());
				st.nextToken();
				// countTokens() method returns remaining number of characters
				// makes sure seq contains core seq that is in middle
				String seq = st.nextToken();
				if (seq.indexOf(core) == mers / 2 - 2) {
					// select this
					st = new StringTokenizer(nfp.readLine());
					writer.print(st.nextToken() + " ");
					writer.print(st.nextToken());
					writer.println();
					size++;
				} else {
					nfp.readLine();
				}
			}
		}
		fp.close();
		nfp.close();
		writer.close();
		if (training) {
			run("selected-train");
		} else {
			run("selected-test");
		}
	}

	private void run(String arg) throws IOException {
		// converts to right format
		fp = new BufferedReader(new FileReader(arg));
		nfp = new BufferedReader(new FileReader(arg));

		PrintWriter writer;
		writer = new PrintWriter("converted-" + arg, "UTF-8");

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
						valueShift = 4 * mers;
					} else if (features == 3) {
						valueShift = 4 * mers + 16 * (mers - 1);
					}
				} else if (feat13) {
					if (features == 2) {
						features = 3;
						valueShift = 4 * mers;
					}
				}
				for (int i = 0; i < seq.length() + 1 - features; i++) {
					// each position of sequence
					// find the value for each position with respect to the
					// feature number
					// print the feature value and its corresponding value
					writer.print(" "
							+ (int) (valueShift
									+ test(seq.substring(i, i + features),
											features) + i
									* Math.pow(4, features)) + ": 1");
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
	
	public static int countLines(BufferedReader bf) throws IOException{
		String l = bf.readLine();
		int counter = 0;
		while(l!=null){
			counter ++;
			l = bf.readLine();
		}
		return counter;
	}
}
