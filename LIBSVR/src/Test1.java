import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Test1 {

	public static void main(String[] args) throws IOException {
		Test1 t = new Test1();
		Main.mers = 34;
		// t.selectionCompare();
		// t.cutoff = 2;
		// t.process("E2F4");
		// t.compare();
		String tf = "E2F1";
		t.cutoff = .125;
		t.process(tf); // split into 10 versions
		for (int i = 1; i <= 0; i++) {
			new ChangeData().run(Integer.toString(i), tf);
		}
		// partition
		// t.cutoff(tf);
		Main.main(new String[] { tf });

	}

	void partition(String tf) throws IOException {
		BufferedReader data = new BufferedReader(new FileReader("processed"
				+ tf + ".txt"));
		PrintWriter writer = new PrintWriter("train" + tf + ".txt", "UTF-8");
		data.readLine();
		int count = (int) data.lines().count();
		data.close();
		data = new BufferedReader(new FileReader("processed" + tf + ".txt"));
		for (int i = 0; i < (int) (.8d * count); i++) {
			writer.println(data.readLine());
		}
		writer.close();
		writer = new PrintWriter("test" + tf + ".txt", "UTF-8");
		String line = data.readLine();
		System.out.println(line);
		while (line != null) {
			writer.println(line);
			data.readLine();
		}
		writer.close();
		data.close();
	}

	void process(String tf) throws IOException {
		BufferedReader data = new BufferedReader(new FileReader(tf + ".txt"));
		PrintWriter writer = new PrintWriter("processed" + tf + ".txt", "UTF-8");
		data.readLine();
		String line = data.readLine();
		while (line != null) {
			// specific cores
			StringTokenizer st = new StringTokenizer(line);
			// select the right sequences
			// contains one of these names
			String name = st.nextToken();
			if (name.contains("Bound") || name.contains("Neg")
					|| name.contains("Flank") || name.contains("PosCtrl2")) {
				st.nextToken();
				// get seq and seq core
				//cutoff the first and last bases
				String seq = st.nextToken().substring(1, 35), core = seq
						.substring(Main.mers / 2 - 2, Main.mers / 2 + 2);
				// if has core
				if (containsCore(core)) {
					st.nextToken();
					st.nextToken();
					double score = Double.parseDouble(st.nextToken());
					double diff = Double.parseDouble(st.nextToken());
					// if the diff in orientation is within cutoff
					if (diff > -cutoff && diff < cutoff) {
						// make sure core isn't in flanks of 11 bases long
						if (!containsCore(seq.substring(0, 11))
								&& !containsCore(seq.substring(Main.mers - 11,
										Main.mers))) {
							// DO STUFF
							writer.println(score + "\t" + seq);
							// writer.println(line);
						}
					}
				}
			}
			line = data.readLine();
		}
		writer.close();
		data.close();
		data = new BufferedReader(new FileReader("processed" + tf + ".txt"));
		System.out.println("Total Seq: "+data.lines().count());
		data.close();
	}

	void compare() throws IOException {

		List<String> E2F4 = new ArrayList<String>(), E2F1 = new ArrayList<String>();

		BufferedReader data2 = new BufferedReader(new FileReader(
				"processedE2F1.txt"));
		String line = data2.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			E2F1.add(st.nextToken());
			line = data2.readLine();
		}
		data2.close();

		BufferedReader data1 = new BufferedReader(new FileReader(
				"processedE2F4.txt"));
		line = data1.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			E2F4.add(st.nextToken());
			line = data1.readLine();
		}
		data1.close();

		// compare
		Collections.sort(E2F1);
		PrintWriter writer = new PrintWriter("alphaE2F1.txt", "UTF-8");
		for (int i = 0; i < E2F1.size(); i++) {
			writer.println(E2F1.get(i));
		}
		System.out.println(E2F1.size());
		writer.close();
		Collections.sort(E2F4);
		writer = new PrintWriter("alphaE2F4.txt", "UTF-8");
		for (int i = 0; i < E2F4.size(); i++) {
			writer.println(E2F4.get(i));
		}
		System.out.println(E2F4.size());
		/*
		 * boolean done = false; for (int i = 0; i < 1000; i++) { if
		 * (!E2F1.get(i).equals(E2F4.get(i))) { if (done) { break; }
		 * System.out.println(i + 1); System.out.println(E2F1.get(i));
		 * System.out.println(E2F4.get(i)); if (E2F4.get(i +
		 * 1).equals(E2F1.get(i))) { System.out.println("YES"); } done = true; }
		 * }
		 */
		writer.close();
	}

	double cutoff;

	private boolean containsCore(String s) {
		if (s.contains("GCGC") || s.contains("GCGG") || s.contains("CGCC")) {
			return true;
		} else {
			return false;
		}
	}

	void analyzee2f4() throws IOException {
		BufferedReader data1 = new BufferedReader(new FileReader("allData.txt")), data2 = new BufferedReader(
				new FileReader("allData.txt"));
		List<String> dataA = new ArrayList<String>(), dataB = new ArrayList<String>();
		String line = data1.readLine();
		while (line != null) {
			dataA.add(line);
			line = data1.readLine();
		}
		line = data2.readLine();
		while (line != null) {
			dataB.add(line);
			line = data2.readLine();
		}
		StringTokenizer st;
		for (int i = 0; i < dataA.size(); i++) {
			st = new StringTokenizer(dataA.get(i));
			st.nextToken();
			String a = st.nextToken();
			for (int j = 0; j < dataB.size(); j++) {
				if (i == j) {
					continue;
				}
				st = new StringTokenizer(dataB.get(j));
				st.nextToken();
				if (st.nextToken().equals(a)) {
					System.out.println(a);
				}
			}
		}

		data1.close();
		data2.close();
	}

	void selectionCompare() throws IOException {
		// compare e2f1 and e2f4 data and create new e2f4 data with same
		// sequences
		BufferedReader data1 = new BufferedReader(new FileReader("allData.txt")), data2 = new BufferedReader(
				new FileReader("E2F1.txt"));
		PrintWriter writer = new PrintWriter("newE2F1.txt", "UTF-8");
		List<String> dataA = new ArrayList<String>(), dataB = new ArrayList<String>();
		String line = data1.readLine();
		while (line != null) {
			dataA.add(line);
			line = data1.readLine();
		}
		data2.readLine();
		line = data2.readLine();
		while (line != null) {
			dataB.add(line);
			line = data2.readLine();
		}
		StringTokenizer st;
		double NF = 0;
		for (int i = 0; i < dataA.size(); i++) {
			st = new StringTokenizer(dataA.get(i));
			st.nextToken();
			String a = st.nextToken();
			boolean f = false;
			List<String> duplicates = new ArrayList<String>();
			for (int j = 0; j < dataB.size(); j++) {
				st = new StringTokenizer(dataB.get(j));
				st.nextToken();
				st.nextToken();
				if (st.nextToken().equals(a)) {
					duplicates.add(dataB.get(j));
				}
			}
			double least = 100;
			int index = -1;
			for (int j = 0; j < duplicates.size(); j++) {
				st = new StringTokenizer(dataB.get(j));
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				st.nextToken();
				double d = Double.parseDouble(st.nextToken());
				if (d < least) {
					least = d;
					index = j;
					if (Math.abs(d) > NF) {
						NF = Math.abs(d);
					}
				}
			}
			if (index != -1) {
				writer.println(duplicates.get(index));
			}
		}
		System.out.println(NF);
		writer.close();
		data1.close();
		data2.close();
	}

	void featureMeaning() throws IOException {
		int counter = 1;
		PrintWriter writer = new PrintWriter("featureMeaning.txt", "UTF-8");
		writer.print("feature seq pos");
		writer.println();
		for (int i = 0; i < 4 * Main.mers + 64 * (Main.mers - 2); i++) {
			// only for feat13
			String seq;
			int pos, j = i;
			if (j < 4 * (Main.mers)) {
				Main.features = 1;
				pos = (int) Math.ceil((j + 1) / 4f);
				seq = test(j % 4 + 1, 1);
			} else {
				j -= 4 * (Main.mers);
				Main.features = 3;
				pos = (int) Math.ceil((j + 1) / 64f);
				seq = test(j % 64 + 1, 3);
			}
			if (pos - 1 < Main.mers / 2 - 2
					|| pos - 1 >= Main.mers / 2 + 2 + (1 - Main.features)) {
				writer.print(counter + " " + seq + " " + pos);
				writer.println();
				counter++;
			}
		}
		writer.close();
	}

	private String cutoffIteration = "train";

	void cutoff(String tf) throws IOException {
		for (int i = 1; i <= 10; i++) {
			BufferedReader data = new BufferedReader(new FileReader(
					cutoffIteration + i + tf + ".txt"));
			PrintWriter writer = new PrintWriter("_" + cutoffIteration + i + tf
					+ ".txt", "UTF-8");
			String line = data.readLine();
			while (line != null) {
				StringTokenizer st = new StringTokenizer(line);
				writer.print(st.nextToken() + " ");
				String seq = st.nextToken();
				seq = seq.substring(1, seq.length() - 1);
				writer.print(seq);
				writer.println();
				line = data.readLine();
			}
			writer.close();
			data.close();
		}
		if (cutoffIteration.equals("train")) {
			cutoffIteration = "test";
			cutoff(tf);
		}
	}

	private static String test(int value, int feature) {
		// returns the single/pair/triplet value of the input seq at that
		// position
		String seq = "";
		if (feature == 1) {
			switch (value) {
			case 1:
				seq = "A";
				break;
			case 2:
				seq = "C";
				break;
			case 3:
				seq = "G";
				break;
			case 4:
				seq = "T";
				break;
			default:
				seq = "NO";
			}
		} else if (feature == 2) {
			switch (value) {
			case 1:
				seq = "AA";
				break;
			case 2:
				seq = "AC";
				break;
			case 3:
				seq = "AG";
				break;
			case 4:
				seq = "AT";
				break;
			case 5:
				seq = "CA";
				break;
			case 6:
				seq = "CC";
				break;
			case 7:
				seq = "CG";
				break;
			case 8:
				seq = "CT";
				break;
			case 9:
				seq = "GA";
				break;
			case 10:
				seq = "GC";
				break;
			case 11:
				seq = "GG";
				break;
			case 12:
				seq = "GT";
				break;
			case 13:
				seq = "TA";
				break;
			case 14:
				seq = "TC";
				break;
			case 15:
				seq = "TG";
				break;
			case 16:
				seq = "TT";
				break;
			default:
				seq = "NO";
			}
		} else if (feature == 3) {
			// first nucleotide
			if (value <= 16) {
				seq = "A";
			} else if (value <= 32) {
				seq = "C";
				value -= 16;
			} else if (value <= 48) {
				seq = "G";
				value -= 32;
			} else if (value <= 64) {
				seq = "T";
				value -= 48;
			}
			// second nucleotide
			if (value <= 4) {
				seq += "A";
			} else if (value <= 8) {
				seq += "C";
				value -= 4;
			} else if (value <= 12) {
				seq += "G";
				value -= 8;
			} else if (value <= 16) {
				seq += "T";
				value -= 12;
			}
			// third nucleotide
			if (value == 1) {
				seq += "A";
			} else if (value == 2) {
				seq += "C";
			} else if (value == 3) {
				seq += "G";
			} else if (value == 4) {
				seq += "T";
			}
		}
		return seq;
	}
}