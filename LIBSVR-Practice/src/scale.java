import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;


public class scale {
	
	PrintWriter writer;

	public static void main(String[] args) {
		try {
			new scale().run("newTest.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void run(String file) throws IOException{
		//find min and max of response variables in data
				BufferedReader fp = new BufferedReader(new FileReader(file));
				String line = fp.readLine();
				StringTokenizer st = new StringTokenizer(line);
				String token = st.nextToken();
				double max = Double.parseDouble(token), min = max;
				while(line!=null){
					st = new StringTokenizer(line);
					token = st.nextToken();
					double response = Double.parseDouble(token);
					if(response>max){
						max = response;
					}
					if(response<min){
						min = response;
					}
					line = fp.readLine();
				}
				//print the scaled data
				writer = new PrintWriter("normNewTest.txt", "UTF-8");
				fp.close();
				fp = new BufferedReader(new FileReader(file));
				line = fp.readLine();
				while(line!=null){
					st = new StringTokenizer(line);
					token = st.nextToken();
					//divide each variable by max
					writer.print(Double.toString((Double.parseDouble(token)-min)/(max-min)));
					writer.print(" "+st.nextToken());
					writer.println();
					line = fp.readLine();
				}
				fp.close();
	}
	
}
