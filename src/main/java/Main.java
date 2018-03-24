import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.util.ArrayList;

class Document {
    public String id, language, text;

    public Document(String id, String language, String text){
        this.id = id;
        this.language = language;
        this.text = text;
    }
}

class Documents {
    public List<Document> documents;

    public Documents() {
        this.documents = new ArrayList<Document> ();
    }
    public void add(String id, String language, String text) {
        this.documents.add (new Document (id, language, text));
    }
}

public class Main {
  
    final static String API_KEY = "feb29daae1dc4f2eb888862eb560400d";
    final static String HOST = "https://westcentralus.api.cognitive.microsoft" +
                               ".com";
    final static String PATH = "/text/analytics/v2.0/keyPhrases";

    public static  String getKeyPhrases (Documents documents) throws Exception {
        String text = new Gson().toJson(documents);
        byte[] encodedText = text.getBytes ("UTF-8");
        URL                url        = new URL(HOST + PATH);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/json");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", API_KEY);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(encodedText, 0, encodedText.length);
        wr.flush();
        wr.close();

        StringBuilder response = new StringBuilder ();
        BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();
    }
  
    public static String getCSVKeyWords(Documents documents) {
        String response = "";
        try {
            response = getKeyPhrases (documents);
        } catch (Exception e) {
            System.err.println ("Error: Couldn't get keywords from APi.");
            StringBuilder sb = new StringBuilder ();
            for (int i = 0; i < documents.documents.size (); ++i) {
                sb.append (documents.documents.get (i).id + '\n');
            }
            System.err.print ("IDs: " + sb);
            return "";
        }

        JSONObject jsonResponse = new JSONObject (response);
        JSONObject docs = jsonResponse.getJSONArray ("documents")
            .optJSONObject (0);
        JSONArray keyWords = docs.getJSONArray ("keyPhrases");

        StringBuilder sb = new StringBuilder ();
        for (int i = 0; i < keyWords.length (); ++i) {
            sb.append (keyWords.getString (i) + ',');
        }

        // remove last comma
        sb.deleteCharAt (sb.length () - 1);
        return sb.toString ();
    }
  
    public static void writeCSVToFile(String csv, File file) {
        file.delete();

        try {
            PrintWriter out = new PrintWriter(file.getPath());
            out.println(csv);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static void writeToFile(String filePath, String content) {
        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            writer.println(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getCSVString(JSONArray object) {
        return CDL.toString(object);
    }

    public static String getLink(JSONArray items, int id){
        return items.getJSONObject(id).getJSONObject("selfLink").getString("href");
    }


    public static void main(String[] args) {
        String link = "https://services.radio-canada.ca/hackathon/neuro/v1/future/lineups/475289?pageNumber=1";
        Lineup lineup = new Lineup(link);

        JSONArray items = lineup.getItems();

        link = getLink(items, 3);
        News news = new News(link);

        writeToFile("XD.html", news.getHTML());

        System.out.println(news);
        System.out.println(news.names());

        System.out.println(news.getSummary() + "");
        System.out.println(news.getTitle());
        String keywords = " tseting testing TEST HACKATHON";
        Connection.queryInsert(keywords,  news.getTitle(), 2);
        // Connection.queryInsert(keywords,  news.getTitle(), 2);
      
        // keyword query use example
        Documents documents = new Documents ();
        documents.add ("1", "en", "I really enjoy the new XBox One S. It has a clean look, it has 4K/HDR resolution and it is affordable.");
        System.out.println (getCSVKeyWords (documents));
    }
}
