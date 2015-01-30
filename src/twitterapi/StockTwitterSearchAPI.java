package twitterapi;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;



import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class StockTwitterSearchAPI {
	
	public static void main(String args[]) throws InterruptedException, Exception {

		// The factory instance is re-usable and thread safe.
	    TwitterFactory factory = new TwitterFactory();
	    AccessToken accessToken = loadAccessToken();
	    Twitter twitter = factory.getInstance();
	    twitter.setOAuthConsumer("Bho3FYEVtbiNmNSWxZHWadLlY", "TugKQSam1itcJeWjdtBIhLmSUi9SZLVEtvY4fRwTyGEM92weuc");
	    twitter.setOAuthAccessToken(accessToken);
	    
	    //Status status = twitter.updateStatus("Hello");
	    //System.out.println("Successfully updated the status to [ " + status.getText() + " ].");
	    
	    ArrayList<String> stockList = new ArrayList<String>(Arrays.asList(
	    													//"ABB","ACN","ATVI","ADBE","NFLX","AAPL","AMZN"
	    													"MSFT","ADBE","NFLX","AAPL","AMZN","YHOO","GOOG","HPQ","CSCO","IBM","INTU","EBAY"
	    													//,"AKAM","ADP","BIDU","BRCM","CA","CAJ","CSCO","CTSH","CTXS","DELL"
	    													//,"EBAY","EMC","EMR","ERIC","EXPE ","GOOG","HPQ","HIT","FB","INTC"
	    													//,"IBM","INTU","JNPR","LNKD","MA","MSFT","MOT","NTAP","TSLA","ORCL"
	    													//,"PAYX","QCOM","RIMM","SAP","SYMC","TSM","TXN","TEL","TRIP","VMW"
	    													//,"WU","WIT","YHOO"
	    													));
	    
	    Date date = Calendar.getInstance().getTime();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String dateToday = sdf.format(date);
        String tweet = null;
        
	    int count = 0;
	    int getCount = 0;
	    int retweets = 0;
	    int maxRetweets = 0;
	    int tweetCount = 0;
	    int totalTweetCount = 0;
        Long sinceId = null;
        Long maxVal = null;
	    
        try {
            //Twitter twitterTag = TwitterFactory.getSingleton();
            PrintWriter idWriter = new PrintWriter("LastIdData" + dateToday + ".csv");
            
            for(String stock : stockList){
                PrintWriter writer = new PrintWriter("StockData" + dateToday + stock + ".csv");
            	totalTweetCount = 0;
            	maxVal = Long.valueOf("999999999999999999");
            	Query query = new Query("$" + stock);
            	query.setCount(100);
            	maxRetweets = 0;
            	
            	do{
                	if (getCount > 175){
                		System.out.println("+++++ Too tired! Sleeping for 15 minutes +++++");
                		java.lang.Thread.sleep(990000);
                		System.out.println("+++++            Back to work!           +++++");
                		getCount = 0;
                	}
                 	System.out.println("Writing $" + stock + "'s search Data: " + query.getCount());
	            	query.setMaxId(maxVal);
	            	QueryResult result = twitter.search(query);
	            	getCount++;
	            	for (Status status : result.getTweets()) {
		            	tweet = status.getText();
		            	tweet = tweet.replaceAll("\n", " ");
		            	sinceId = status.getId();
		            	retweets = status.getRetweetCount();
		 	            writer.write("\"" + stock + "\"" + "\t,\t" + "\"" + sinceId + "\"" + "\t,\t" + "\"" + retweets + "\"" 
		 	            					+ "\t,\t" + "\"" + status.getCreatedAt() + "\""	+ "\t,\t" + "\"" 
		 	            					+ "$" + status.getUser().getScreenName() + "\"" + "\t,\t" + "\"" + tweet + "\"" 
		 	            			+ "\n");	            	
		            	if (retweets > maxRetweets){
		            		maxRetweets = retweets;
		            	}
		            	tweetCount++;
	            	}
	            	maxVal = sinceId;
	            	totalTweetCount = totalTweetCount + tweetCount;
	            	count++;
	            	writer.flush();
            	}while(tweetCount >99);
            	writer.write("\n");
            	idWriter.write("\"" + "$" + stock + "\"" + "\t,\t" + "\"" + maxVal + "\"" + "\t,\t" + "\"" 
            								+ maxRetweets + "\"" + "\t,\t" + "\"" + tweetCount + "\"" + "\n" );
            	idWriter.flush();
            	writer.close();
            }
            System.out.println("+++++ Writing Complete for " + stockList.size() + " stocks +++++" + count);
            idWriter.close();
         } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
	    System.exit(0);
	  }
	
	  private static AccessToken loadAccessToken(){
		    String token = "563136910-RtY9KwkYQD3w53xbxXo00WGvTlgeqnM7Sm3weLyW";		// load from a persistent store
		    String tokenSecret = "nl7UQADl2cXbo5tV1pMRzCVV7KfSiZczVvkfApF6pUOao";		// load from a persistent store
	    return new AccessToken(token, tokenSecret);
	  }
	
}
