import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Test1 {

	double cutoff;

	public static void main(String[] args) throws IOException {
		Test1 t = new Test1();

		String tf = "E2F1";
		if (tf.equals("E2F1")) {
			t.cutoff = .4; // - E2F1 GCGC
			
		} else {
			t.cutoff = 0.135;// - E2F4 GCGC
		}
		t.process(tf);
		// split into 10 versions
		 for (int i = 1; i <= 10; i++) {
		 new ChangeData().run(Integer.toString(i), tf);
		 }
		// partition
		// t.cutoff(tf);
		 Main.main(new String[] { tf });

		// t.interpretAverages();

	}

	List<String> revCompl(String tf, String _core) throws IOException {
		// reverse complement
		BufferedReader br = new BufferedReader(new FileReader(tf + ".txt"));
		br.readLine();
		// list of all the corrected seqs with the _core
		List<String> list = new ArrayList<String>();
		String line = br.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			String s = "";
			s += st.nextToken() + "\t";
			s += st.nextToken() + "\t";
			String seq = st.nextToken();
			if (seq.substring(Main.mers / 2 - 2, Main.mers / 2 + 2).equals(
					_core)) {
				// reverse complement
				for (int i = seq.length() - 1; i >= 0; i--) {
					char c = seq.charAt(i);
					switch (c) {
					case 'A':
						s += "T";
						break;
					case 'C':
						s += "G";
						break;
					case 'G':
						s += "C";
						break;
					case 'T':
						s += "A";
						break;
					}
				}
				// add rest of the line
				s += "\t" + st.nextToken() + "\t" + st.nextToken() + "\t"
						+ st.nextToken() + "\t" + st.nextToken();
				list.add(s);
			}
			line = br.readLine();
		}
		br.close();
		return list;
	}

	void interpretAverages() throws IOException {
		// get the significant averages and interpret it
		String file = "averages.txt";
		String line;
		// feat 1
		int count = 120;
		double largest = 0;
		BufferedReader br = new BufferedReader(new FileReader(file));
		for (int i = 0; i < count; i++) {
			line = br.readLine();
			if (Math.abs(Double.parseDouble(line)) > largest) {
				largest = Math.abs(Double.parseDouble(line));
			}
		}
		br.close();
		double cutoff = largest / 2;
		List<Double> sigFeatValues = new ArrayList<Double>();
		// 1,2,3,...
		List<Integer> sigFeats = new ArrayList<Integer>();
		// add the sig features to the list
		br = new BufferedReader(new FileReader(file));
		for (int i = 0; i < count; i++) {
			line = br.readLine();
			if (Math.abs(Double.parseDouble(line)) > cutoff) {
				sigFeatValues.add(Double.parseDouble(line));
				sigFeats.add(i + 1);
			}
		}
		br.close();
		System.out.print("\tFeature#\tValue\tSeq\tPos\t");
		// print seq positions
		for (int i = Integer.parseInt(getSeqPos(sigFeats.get(0))[1]); i <= Integer
				.parseInt(getSeqPos(sigFeats.get(sigFeats.size() - 1))[1]); i++) {
			System.out.print(i + "\t");
		}
		System.out.println();
		// each feature
		for (int i = 0; i < sigFeats.size(); i++) {
			String seq = getSeqPos(sigFeats.get(i))[0];
			int pos = Integer.parseInt(getSeqPos(sigFeats.get(i))[1]);
			System.out.print("\t" + sigFeats.get(i) + "\t"
					+ sigFeatValues.get(i) + "\t" + seq + "\t" + pos + "\t");
			// show the position here
			// put appropriate number of spaces to get to the right pos
			for (int j = 0; j < pos
					- Integer.parseInt(getSeqPos(sigFeats.get(0))[1]); j++) {
				System.out.print("\t");
			}
			// iterate through each character and print it
			for (int j = 0; j < seq.length(); j++) {
				System.out.print(seq.substring(j, j + 1) + "\t");
			}
			System.out.println();
		}
		System.out.print("\tcutoff\t" + cutoff);
		// ________________________________________________________________________________
		System.out.println();
		System.out.println();
		// feat 3
		br = new BufferedReader(new FileReader(file));
		count = (int) (br.lines().count() - 120);
		br.close();
		largest = 0;
		br = new BufferedReader(new FileReader(file));
		for (int i = 0; i < count; i++) {
			line = br.readLine();
			if (Math.abs(Double.parseDouble(line)) > largest) {
				largest = Math.abs(Double.parseDouble(line));
			}
		}
		br.close();
		cutoff = largest / 2;
		sigFeatValues = new ArrayList<Double>();
		// 1,2,3,...
		sigFeats = new ArrayList<Integer>();
		// add the sig features to the list
		br = new BufferedReader(new FileReader(file));
		for (int i = 0; i < count; i++) {
			line = br.readLine();
			if (Math.abs(Double.parseDouble(line)) > cutoff) {
				sigFeatValues.add(Double.parseDouble(line));
				sigFeats.add(i + 1);
			}
		}
		br.close();
		System.out.print("Graph\tFeature#\tValue\tSeq\tPos\t");
		// print seq positions
		for (int i = Integer.parseInt(getSeqPos(sigFeats.get(0))[1]); i <= Integer
				.parseInt(getSeqPos(sigFeats.get(sigFeats.size() - 1))[1]) + 2; i++) {
			System.out.print(i + "\t");
		}
		System.out.println();
		// each feature
		for (int i = 0; i < sigFeats.size(); i++) {
			String seq = getSeqPos(sigFeats.get(i))[0];
			int pos = Integer.parseInt(getSeqPos(sigFeats.get(i))[1]);
			System.out.print((sigFeats.get(i) - 120) + "\t" + sigFeats.get(i)
					+ "\t" + sigFeatValues.get(i) + "\t" + seq + "\t" + pos
					+ "\t");
			// show the position here
			// put appropriate number of spaces to get to the right pos
			for (int j = 0; j < pos
					- Integer.parseInt(getSeqPos(sigFeats.get(0))[1]); j++) {
				System.out.print("\t");
			}
			// iterate through each character and print it
			for (int j = 0; j < seq.length(); j++) {
				System.out.print(seq.substring(j, j + 1) + "\t");
			}
			System.out.println();
		}
		System.out.print("\tcutoff\t" + cutoff);
	}

	private String[] getSeqPos(int feat) throws IOException {
		// returns 0-seq, 1-pos
		String[] re = new String[2];
		BufferedReader br = new BufferedReader(new FileReader(
				"featureMeaning.txt"));
		int count = (int) br.lines().count();
		br.close();
		br = new BufferedReader(new FileReader("featureMeaning.txt"));
		br.readLine();
		for (int j = 0; j < count - 1; j++) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			if (Integer.parseInt(st.nextToken()) == feat) {
				// found the right feature
				re[0] = st.nextToken();
				re[1] = st.nextToken();
				break;
			}
		}
		br.close();
		return re;
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
		Main.mers = 34;
		BufferedReader data = new BufferedReader(new FileReader(tf + ".txt"));
		PrintWriter writer = new PrintWriter("processed" + tf + ".txt", "UTF-8");
		data.readLine();
		String line = data.readLine();
		while (line != null) {
			String s = processLine(line);
			if (!s.equals("")) {
				writer.println(s);
			}
			line = data.readLine();
		}
		if (Main.core.equals("GCGG")) {
			// add the reverse complemented seqs of "CCGC"
			Main.core = "CCGC";
			Main.mers = 36;
			List<String> list = revCompl(tf, "CCGC");
			Main.mers = 34;
			for (int i = 0; i < list.size(); i++) {
				String s = processLine(list.get(i));
				if (!s.equals("")) {
					writer.println(s);
				}
			}
		}
		writer.close();
		data.close();
		//get rid of the duplicate sequences and take the median of the intensity scores
		selectDiffSeq("processed" + tf + ".txt");
	}

	void selectDiffSeq(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		List<String> list = new ArrayList<String>();
		String line = br.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			list.add(st.nextToken());
			line = br.readLine();
		}
		br.close();
		Collections.sort(list);
		List<String> sameSeq = new ArrayList<String>();
		// get the same sequences
		for (int i = 0; i < list.size() - 1; i++) {
			if (sameSeq.size() > 0
					&& sameSeq.get(sameSeq.size() - 1).equals(list.get(i))) {
				continue;
			}
			if (list.get(i).equals(list.get(i + 1))) {
				// same seq as the one next
				sameSeq.add(list.get(i));
			}
		}
		// new list of the corrected stuff
		list.clear();
		for (int i = 0; i < sameSeq.size(); i++) {
			List<Double> values = new ArrayList<Double>();
			br = new BufferedReader(new FileReader(file));
			// get same values from each sequence
			line = br.readLine();
			while (line != null) {
				StringTokenizer st = new StringTokenizer(line);
				String value = st.nextToken();
				if (st.nextToken().equals(sameSeq.get(i))) {
					// if there is a match
					values.add(Double.parseDouble(value));
				}
				line = br.readLine();
			}
			br.close();
			Collections.sort(values);
			// take median and add one new seq for all the duplicates
			if (values.size() % 2 == 0) {
				// even
				list.add(((values.get(values.size() / 2) + values.get((values
						.size()) / 2 - 1)) / 2) + "\t" + sameSeq.get(i));
			} else {
				// odd
				list.add(values.get((values.size() - 1) / 2) + "\t"
						+ sameSeq.get(i));
			}
		}

		// add the non duplicate sequences to list
		br = new BufferedReader(new FileReader(file));
		line = br.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			st.nextToken();
			if (sameSeq.indexOf(st.nextToken()) == -1) {
				//non duplicates
				list.add(line);
			}
			line = br.readLine();
		}
		// print list
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		for (int i = 0; i < list.size(); i++) {
			writer.println(list.get(i));
		}
		writer.close();
		System.out.println("Total Seq: " + list.size());
	}

	String processLine(String line) {
		// returns the processed line from the raw data line

		// specific cores
		StringTokenizer st = new StringTokenizer(line);
		// select the right sequences
		// contains one of these names
		String name = st.nextToken();
		if (name.contains("Bound") || name.contains("Neg")
				|| name.contains("Flank") || name.contains("PosCtrl2")) {
			st.nextToken();
			// get seq and seq core
			// cutoff the first and last bases
			String seq = st.nextToken().substring(1, 35), core = seq.substring(
					Main.mers / 2 - 2, Main.mers / 2 + 2);
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
						return score + "\t" + seq;
						// writer.println(line);
					}
				}
			}
		}
		return "";
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