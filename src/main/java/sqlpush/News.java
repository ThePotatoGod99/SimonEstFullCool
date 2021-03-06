package sqlpush;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class News extends JSONObject{

    public News(String link) {
        super(getBody(link));
    }
    public static String getBody(String link){
        String body = "";
        try {
            HttpResponse<String> response = Unirest.get(link)
                    .header("Authorization", "Client-Key bf9ac6d8-9ad8-4124-a63c-7b7bdf22a2ee")
                    .header("Cache-Control", "no-cache")
                    .header("Postman-Token", "06ec1375-dab7-46f1-87b6-c3250c9acbe5")
                    .asString();
            body = response.getBody();
        } catch (UnirestException e) {
            System.out.println("USING TESTING BODY");
            body = "";
        }
        if(body.equals("")){
            System.out.println("BODY EMPTY");
            System.exit(0);
        }
        return body;

    }


//    public void pushToSQL(){
//
//        SharedCounts objClicker = new SharedCounts(this.getURL());
//        int nbClick = objClicker.getNbClick();
//
//
//        String titre = this.getTitle();
//        int articleID = Integer.parseInt(this.getID());
//        Documents kwDoc = new Documents();
//        String summary = this.getSummary();
//        kwDoc.add(articleID + "", "fr", summary );
//        String lien = this.getJSONObject("canonicalWebLink").getString("href");
//        String keywordsXD = Main.getCSVKeyWords(kwDoc);
//
//        Connection.queryInsert(new String[]{"titre","keywords","click","lien","idArticle"}, new Object[]{titre, keywordsXD, nbClick, lien, articleID});
//
//    }

    public String getSummary(){
        return this.getString("summary");
    }

    public String getTitle(){
        return this.getString("title");
    }

    public String getID(){
        return this.getString("id");
    }

    public JSONObject getBody(){
        return this.getJSONObject("body");
    }

    public String getHTML(){
        return getBody().getString("html");
    }
    public String getURL() {
        JSONObject jsonObj = this.getJSONObject("canonicalWebLink");

        return jsonObj.getString("href");
    }
}
