import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class analysis {

	static String tf;
	Test1 t;

	public static void main(String[] args) throws IOException {
		Test1 t = new Test1();
		analysis a = new analysis();
		// tf = "E2F1";
		a.rank("E2F1_GCGC", "E2F1_GCGG");
		// a.graphRankings("E2F1_GCGC", "E2F4_GCGC");
		// a.graphWeights();

	}

	void graphRankings(String s1, String s2) throws IOException {
		// graphs ranking differences on clustered column graph for each feature
		// input: tf_core ex. E1F1_GCGC
		// output: prints ranking differences for all features to console
		BufferedReader br1 = new BufferedReader(new FileReader("averages_" + s1
				+ ".txt"));
		List<Double> weights1 = new ArrayList<Double>();
		String line = br1.readLine();
		while (line != null) {
			weights1.add(Double.parseDouble((new StringTokenizer(line))
					.nextToken()));
			line = br1.readLine();
		}
		br1.close();
		// do the same to the 2nd dataset
		BufferedReader br2 = new BufferedReader(new FileReader("averages_" + s2
				+ ".txt"));
		List<Double> weights2 = new ArrayList<Double>();
		line = br2.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			weights2.add(Double.parseDouble(st.nextToken()));
			line = br2.readLine();
		}
		br2.close();
		// get rankings (the indices in descending order)
		List<Integer> ranking1 = getRanking(weights1, false), ranking2 = getRanking(
				weights2, false);
		// get ranking differences
		for (int i = 0; i < ranking1.size(); i++) {
			System.out.println(Math.abs(ranking1.get(i) - ranking2.get(i)));
		}
	}

	void rank(String s1, String s2) throws IOException {
		// uses feature avereages to find the biggest ranking differences
		// input: tf_core ex. E1F1_GCGC
		// output: prints everything and the features to console
		// find rank diff between 2 sets of data
		// features are in filename = "averages_tf_core.txt"

		// assign values from 1st dataset to list
		BufferedReader br1 = new BufferedReader(new FileReader("averages_" + s1
				+ ".txt"));
		List<Double> weights1 = new ArrayList<Double>();
		String line = br1.readLine();
		while (line != null) {
			weights1.add(Double.parseDouble((new StringTokenizer(line))
					.nextToken()));
			line = br1.readLine();
		}
		br1.close();
		// do the same to the 2nd dataset
		BufferedReader br2 = new BufferedReader(new FileReader("averages_" + s2
				+ ".txt"));
		List<Double> weights2 = new ArrayList<Double>();
		line = br2.readLine();
		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			weights2.add(Double.parseDouble(st.nextToken()));
			line = br2.readLine();
		}
		br2.close();
		// get rankings (the indices in descending order)
		List<Integer> ranking1 = getRanking(weights1, false), ranking2 = getRanking(
				weights2, false), feats = new ArrayList<Integer>(), diffs = new ArrayList<Integer>();
		// which side is greater? false=s1, true = s2
		List<Boolean> sides = new ArrayList<Boolean>();
		// get ranking differences
		for (int i = 0; i < ranking1.size(); i++) {
			int diff = Math.abs(ranking1.get(i) - ranking2.get(i));
			// process the diff
			// both weights can't be less than weightCutoff
			// .05 for E2F1 GCGC vs GCGG
			// .02 for E2F4 GCGC vs GCGG
			double weightCutoff1 = 0, weightCutoff2 = 0;
			// set weight cutoff for s1
			if (s1.substring(0, 4).equals("E2F1")) {
				weightCutoff1 = .1;
			} else if (s1.substring(0, 4).equals("E2F4")) {
				weightCutoff1 = .04;
			} else {
				System.err.println("INVALID TF");
			}
			// set weight cutoff for s2
			if (s2.substring(0, 4).equals("E2F1")) {
				weightCutoff2 = .1;
			} else if (s2.substring(0, 4).equals("E2F4")) {
				weightCutoff2 = .04;
			} else {
				System.err.println("INVALID TF");
			}
			// make sure both weights are above cutoff
			if (Math.abs(weights1.get(i)) <= weightCutoff1
					&& Math.abs(weights2.get(i)) <= weightCutoff2) {
				continue;
			}
			// set ranking cutoff
			int rankingCutoff = 0;
			// ranking diff can't be less than rankingCutoff
			if (diff <= rankingCutoff) {
				continue;
			}
			// see which side the weights are on
			if (weights1.get(i) > weights2.get(i)) {
				sides.add(false);
			} else {
				sides.add(true);
			}
			// add them to feats and diffs
			feats.add(i + 1);
			diffs.add(diff);
		}
		// sort the processed values so biggest diff comes first
		List<Integer> orderedDiffs = new ArrayList(diffs);
		Collections.sort(orderedDiffs, Collections.reverseOrder());
		int[] finalDiffs = new int[diffs.size()], finalFeats = new int[feats
				.size()];
		boolean[] finalSides = new boolean[sides.size()];
		for (int i = 0; i < orderedDiffs.size(); i++) {
			for (int j = 0; j < diffs.size(); j++) {
				if (orderedDiffs.get(i) == diffs.get(j)) {
					finalFeats[i] = feats.get(j);
					finalDiffs[i] = diffs.get(j);
					finalSides[i] = sides.get(j);
					diffs.remove(j);
					feats.remove(j);
					sides.remove(j);
					break;
				}
			}
		}
		// print out the final output
		List<String> visualPos = getPositions(finalFeats);
		System.out.println("Feat#\t"+s1+"\t"+s2+"\tRankDiff\tName\tSeq\tPos"
				+ visualPos.get(0));
		for (int i = 0; i < finalFeats.length; i++) {
			// print feat#, rankdiff, seq, pos, visual version of pos
			System.out.println(finalFeats[i] + "\t"
					+ ranking1.get(finalFeats[i] - 1) + "\t"
					+ ranking2.get(finalFeats[i] - 1) + "\t" + finalDiffs[i]
					+ "\t" + (!finalSides[i] ? s1 : s2) + "\t"
					+ t.getSeqPos(finalFeats[i])[0] + "\t"
					+ t.getSeqPos(finalFeats[i])[1] + visualPos.get(i + 1));
		}
	}

	void graphWeights() throws IOException {
		// 1&3mer E2F1 feat weights vs 1&3mer E2F4 feat weights
		// gets outliers from y=x graph and puts it in ascending order
		// input: paste into averages.txt data in format: avg, sd, avg, sd
		// found in csv
		// cutoff for min distance
		double cutoff = .25;
		BufferedReader br = new BufferedReader(new FileReader("averages.txt"));
		int size = Main.countLines(br) - 1;
		br.close();
		br = new BufferedReader(new FileReader("averages.txt"));
		double[][] points = new double[2][size], sd = new double[2][size];
		// set the points to an array
		// indices of points where stand dev intersects y=x line
		List<Double> distances = new ArrayList<Double>(), orderedDistances = new ArrayList<Double>();
		// headers
		StringTokenizer st = new StringTokenizer(br.readLine());
		String name0 = st.nextToken();
		st.nextToken();
		String name1 = st.nextToken();
		// set variables
		for (int i = 0; i < size; i++) {
			String line = br.readLine();
			st = new StringTokenizer(line);
			// E2F1 average
			points[0][i] = Double.parseDouble(st.nextToken());
			// E2F1 sd
			sd[0][i] = Double.parseDouble(st.nextToken());
			// E2F4 average
			points[1][i] = Double.parseDouble(st.nextToken());
			// E2F4 sd
			sd[1][i] = Double.parseDouble(st.nextToken());
			// double x0 = points[0][i] - sd[0][i], x1 = points[0][i] +
			// sd[0][i], y0 = points[1][i]
			// - sd[1][i], y1 = points[1][i] + sd[1][i];
			// if (testIntersect(x0, y1, x1, y1) || testIntersect(x1, y1, x1,
			// y0)
			// || testIntersect(x0, y0, x1, y0)
			// || testIntersect(x0, y1, x0, y0)) {
			// // collision
			// indices.add(i);
			// System.out.println(i);
			// }
			distances
					.add(Math.abs(-points[0][i] + points[1][i]) / Math.sqrt(2));
			orderedDistances.add(distances.get(i));
		}
		br.close();
		// order distances in descending order
		Collections.sort(orderedDistances, Collections.reverseOrder());

		// to not repeat lines due to the nested for loop
		List<Integer> usedFeats = new ArrayList<Integer>();
		// for the print stuff
		List<Integer> finalFeats = new ArrayList<Integer>();
		List<Double> finalDistances = new ArrayList<Double>();
		for (int i = 0; i < size; i++) {
			if (orderedDistances.get(i) > cutoff) {
				for (int j = 0; j < size; j++) {
					if (distances.get(j) == orderedDistances.get(i)
							&& !usedFeats.contains(j + 1)) {
						usedFeats.add(j + 1);
						finalFeats.add(j + 1);
						finalDistances.add(distances.get(j));
					}
				}
			}
		}

		// print out stuff
		List<String> visualPos = getPositions(finalFeats);
		// print header
		System.out.println("Feat#\tDist(y=x)\tName\tSeq\tPos"
				+ visualPos.get(0));
		// print for each feature
		for (int i = 0; i < finalFeats.size(); i++) {
			System.out.print(finalFeats.get(i) + "\t" + finalDistances.get(i)
					+ "\t");
			// find which side of the y=x line the point is on
			if (points[0][finalFeats.get(i) - 1] > points[1][finalFeats.get(i) - 1]) {
				System.out.print(name0);
			} else {
				System.out.print(name1);
			}
			System.out.print("\t" + t.getSeqPos(finalFeats.get(i))[0] + "\t"
					+ t.getSeqPos(finalFeats.get(i))[1] + visualPos.get(i + 1));
			System.out.println();
		}
	}

	public analysis() {
		t = new Test1();
	}

	void weights_() throws IOException {
		int[] feats = new int[] {

		};
		System.out.print("Seq\tPos\t");
		// print seq positions
		for (int i = 1; i <= 34; i++) {
			System.out.print(i + "\t");
		}
		System.out.println();
		for (int i = 0; i < feats.length; i++) {
			String s = t.getSeqPos(feats[i])[0] + "\t"
					+ t.getSeqPos(feats[i])[1];
			String seq = t.getSeqPos(feats[i])[0];
			int pos = Integer.parseInt(t.getSeqPos(feats[i])[1]);
			// show the position here
			// put appropriate number of spaces to get to the right pos
			for (int j = 0; j < pos; j++) {
				s = s + "\t";
			}
			// iterate through each character and print it
			for (int j = 0; j < seq.length(); j++) {
				s = s + seq.substring(j, j + 1) + "\t";
			}
			System.out.println(s);
		}
	}

	public List<String> getPositions(int[] feats) throws IOException {
		int lowestPos = Integer.parseInt(t.getSeqPos(feats[0])[1]), highestPos = lowestPos;
		for (int i = 0; i < feats.length; i++) {
			int pos = Integer.parseInt(t.getSeqPos(feats[i])[1]);
			if (pos < lowestPos) {
				lowestPos = pos;
			}
			pos += t.getSeqPos(feats[i])[0].length() - 1;
			if (pos > highestPos) {
				highestPos = pos;
			}
		}
		List<String> list = new ArrayList<String>();
		// add labeling
		String s = "";
		for (int i = lowestPos; i <= highestPos; i++) {
			s = s + "\t" + i;
		}
		list.add(s);
		// each feat's position string
		for (int i = 0; i < feats.length; i++) {
			s = "";
			String seq = t.getSeqPos(feats[i])[0];
			int pos = Integer.parseInt(t.getSeqPos(feats[i])[1]);
			// show the position here
			// put appropriate number of spaces to get to the right pos
			for (int j = 0; j < pos - lowestPos; j++) {
				s = s + "\t";
			}
			// iterate through each character and print it
			for (int j = 0; j < seq.length(); j++) {
				s = s + "\t" + seq.substring(j, j + 1);
			}
			list.add(s);
		}
		return list;
	}

	public List<String> getPositions(List<Integer> feats) throws IOException {
		int lowestPos = Integer.parseInt(t.getSeqPos(feats.get(0))[1]), highestPos = lowestPos;
		for (int i = 0; i < feats.size(); i++) {
			int pos = Integer.parseInt(t.getSeqPos(feats.get(i))[1]);
			if (pos < lowestPos) {
				lowestPos = pos;
			}
			pos += t.getSeqPos(feats.get(i))[0].length() - 1;
			if (pos > highestPos) {
				highestPos = pos;
			}
		}
		List<String> list = new ArrayList<String>();
		// add labeling
		String s = "";
		for (int i = lowestPos; i <= highestPos; i++) {
			s = s + "\t" + i;
		}
		list.add(s);
		// each feat's position string
		for (int i = 0; i < feats.size(); i++) {
			s = "";
			String seq = t.getSeqPos(feats.get(i))[0];
			int pos = Integer.parseInt(t.getSeqPos(feats.get(i))[1]);
			// show the position here
			// put appropriate number of spaces to get to the right pos
			for (int j = 0; j < pos - lowestPos; j++) {
				s += "\t";
			}
			// iterate through each character and print it
			for (int j = 0; j < seq.length(); j++) {
				s = s + "\t" + seq.substring(j, j + 1);
			}
			list.add(s);
		}
		return list;
	}

	public List<Integer> getRanking(List values, boolean ascending) {
		// returns ranked list of indices in order of the elements in values
		// descending: 0-largest, ascending: 0-lowest
		List<Integer> indices = new ArrayList<Integer>();
		List valuesCopy = new ArrayList(values);
		if (ascending) {
			Collections.sort(valuesCopy);
		} else {
			Collections.sort(valuesCopy, Collections.reverseOrder());
		}
		for (int i = 0; i < values.size(); i++) {
			indices.add(valuesCopy.indexOf(values.get(i)));
		}
		return indices;
	}

}
