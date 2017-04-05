import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 *
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";


		try {
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){//if unable to encode character return null
			return null;
		}

		//initializing variables
		HttpURLConnection urlConnection = null;//for request to server
		BufferedReader reader = null;//to read in the data

		String[] movies = null;//save the movies in here


		try {
			//creating url from string
			URL url = new URL(urlString);
			//trying to open connection with url
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");//the request method we only want to get data
			urlConnection.connect();//connecting

			InputStream inputStream = urlConnection.getInputStream();//getting the stream of data
			StringBuffer buffer = new StringBuffer();// for saving the resuts of request
			if (inputStream == null) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));//creatingobject to read in all the data from

			String line = reader.readLine();//reading individual line on the BufferedReader
			while (line != null) {//as long as there is a line to read
				buffer.append(line + "\n");//adding the line to the StringBuffer
				line = reader.readLine();//going to the next line
			}
			//if you got no data from the request return null
			if (buffer.length() == 0) {
				return null;
			}


			String results = buffer.toString();
			//modifying the string output of the results
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");
			//separating the string into separate elements for the movie array
			movies = results.split("\n");
		}
		catch (IOException e) {//catching the exception and returning null if there are any thrown
			return null;
		}
		finally {//after try block finishes executing
			if (urlConnection != null) {
				urlConnection.disconnect();//if there is still a connection disconnect
			}
			if (reader != null) {
				try {
					reader.close();//closing the BufferedReader if it's not null
				}
				catch (IOException e) {//catching anyexceptions being thrown
				}
			}
		}

		return movies;
	}


	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
