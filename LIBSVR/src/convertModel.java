import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class convertModel {

	public static void main(String[] args) throws IOException {
		// new convertModel().run();
		new convertModel().convert();
	}

	void convert() throws IOException {
		// get the feat. contribution weight matrix (1xP)
		// R = SV*D

		BufferedReader fp = new BufferedReader(new FileReader(
				"13FeatNoCore(Data3).model"));

		// get # of SVs
		fp.readLine();
		fp.readLine();
		fp.readLine();
		// 4th line
		StringTokenizer st = new StringTokenizer(fp.readLine());
		st.nextToken();// text of "total_sv"
		// total # of SVs
		int N = Integer.parseInt(st.nextToken());// parse the numb
		// total # of features
		int P = (int) Math.pow(4, Main.features)
				* (Main.mers + 1 - Main.features);
		if (Main.allFeat) {
			// 1, 2, and 3mer features
			P = 4 * Main.mers + 16 * (Main.mers + 1 - 2) + 64
					* (Main.mers + 1 - 3);
		}
		// create SV (1xN)
		double[] sv = new double[N];
		// create D (NxP)
		int[][] d = new int[N][P];
		fp.readLine();
		fp.readLine();
		String line = fp.readLine();
		int lineNumber = 0;
		while (line != null) {
			// for each line
			st = new StringTokenizer(line);
			// weight
			sv[lineNumber] = Double.parseDouble(st.nextToken());
			String token = st.nextToken();
			// feature that has a one, keeps track of when to move to next token
			int currentFeature = Integer.parseInt(token.substring(0,
					token.indexOf(":")));
			for (int i = 0; i < P; i++) {
				// feature
				if (i == currentFeature - 1) {
					d[lineNumber][i] = 1;
					if (st.hasMoreTokens()) {
						token = st.nextToken();
						currentFeature = Integer.parseInt(token.substring(0,
								token.indexOf(":")));
					}
				} else {
					// replace with a 0
					d[lineNumber][i] = 0;
				}
			}
			line = fp.readLine();
			lineNumber++;
		}
		// multiply the two matrices
		double[] r = new double[P];
		PrintWriter writer = new PrintWriter("r", "UTF-8");
		for (int i = 0; i < P; i++) {
			// ignore the features that have the core
			if (ignoreFeat(i)) {
				continue;
			}
			double sum = 0;
			for (int j = 0; j < N; j++) {
				sum += sv[i] * d[j][i];
			}
			r[i] = sum;
			writer.print(sum);
			writer.println();
		}
		writer.close();
		fp.close();
	}

	boolean ignoreFeat(int index) {
		index++;
		int feature = Main.features;
		int minPos = 17;
		int maxPos = 20;
		// minPos-1 to account for all ranges
		minPos--;
		if (Main.allFeat) {
			// test 1mer feat
			if (index >= 4 * minPos && index < 4 * maxPos) {
				return true;
			}
			index -= 4 * Main.mers;
			// test 2mer feat
			if (index >= 16 * minPos && index < 16 * maxPos) {
				return true;
			}
			index -= 16 * Main.mers;
			// test 3mer feat
			if (index >= 64 * minPos && index < 64 * maxPos) {
				return true;
			}
		} else if (Main.feat13) {
			// test 1mer feat
			if (index >= 4 * minPos && index < 4 * maxPos) {
				return true;
			}
			index -= 4 * Main.mers;
			// test 3mer feat
			if (index >= 64 * minPos && index < 64 * maxPos) {
				return true;
			}
		} else if (feature == 1) {
			if (index >= 4 * minPos && index < 4 * maxPos) {
				return true;
			}
		} else if (feature == 2) {
			if (index >= 16 * minPos && index < 16 * maxPos) {
				return true;
			}
		} else if (feature == 3) {
			if (index >= 64 * minPos && index < 64 * maxPos) {
				return true;
			}
		}
		// not any of these
		return false;
	}

	void run() throws IOException {
		// find seq from features
		PrintWriter writer = new PrintWriter("converted-model", "UTF-8");

		BufferedReader fp = new BufferedReader(new FileReader("model.model"));
		// get rid of the header stuff
		for (int i = 0; i < 6; i++) {
			fp.readLine();
		}
		String line = fp.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			// print weight as is
			writer.print(st.nextToken() + " ");
			if (Main.allFeat) {
				writer.print(findSeq(st, 1));
			} else {
				writer.print(findSeq(st, Main.features));
				// System.out.println(findSeq(st, Main.features));
			}
			writer.println();
			line = fp.readLine();
		}
		fp.close();
		writer.close();
	}

	String findSeq(StringTokenizer st, int feature) {
		// find seq given the features
		String seq = "", token;
		token = st.nextToken();
		int counter = 0, maxCounter = st.countTokens();
		while (counter < maxCounter + 1) {
			counter++;
			System.out.println("ww");
			// extract the feature number
			int featureValue = Integer.parseInt(token.substring(0,
					token.indexOf(":")));
			if (featureValue <= Math.pow(4, feature) * Main.mers) {
				// now interpret the feature numbers
				if (feature > 1
						&& featureValue > Math.pow(4, feature)
								* (Main.mers - 1)) {
					for (int i = feature; i > 0; i--) {
						if (i == 1) {
							if (featureValue == 1) {
								seq += "A";
							} else if (featureValue == 2) {
								seq += "C";
							} else if (featureValue == 3) {
								seq += "G";
							} else if (featureValue == 0) {
								seq += "T";
							}
							break;
						}
						if (Math.ceil(featureValue / Math.pow(4, i - 1)) == 1) {
							seq += "A";
						} else if (Math.ceil(featureValue / Math.pow(4, i - 1)) == 2) {
							seq += "C";
						} else if (Math.ceil(featureValue / Math.pow(4, i - 1)) == 3) {
							seq += "G";
						} else if (Math.ceil(featureValue / Math.pow(4, i - 1)) == 4) {
							seq += "T";
						}
						featureValue %= Math.pow(4, i - 1);
						if (featureValue == 0) {
							featureValue = 1;
						}
					}
				} else if (feature == 1) {
					if (featureValue % 4 == 1) {
						seq += "A";
					} else if (featureValue % 4 == 2) {
						seq += "C";
					} else if (featureValue % 4 == 3) {
						seq += "G";
					} else if (featureValue % 4 == 0) {
						seq += "T";
					}
				} else {
					if (Math.ceil(featureValue / Math.pow(4, feature - 1)) == 1) {
						seq += "A";
					} else if (Math.ceil(featureValue
							/ Math.pow(4, feature - 1)) == 2) {
						seq += "C";
					} else if (Math.ceil(featureValue
							/ Math.pow(4, feature - 1)) == 3) {
						seq += "G";
					} else if (Math.ceil(featureValue
							/ Math.pow(4, feature - 1)) == 4) {
						seq += "T";
					}
				}
			}
			if (st.hasMoreTokens()) {
				break;
			}
			token = st.nextToken();
		}
		return seq;
	}

}
