import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TvDbGen {
	
	stopWords stops ;
	HashSet<String> stopwords;
	
	public TvDbGen()
	{
		stops = new stopWords();
		stopwords = new HashSet<String>();
	}
	
	public String getSeriesId(String tv) throws Exception
	{
		Connection.Response response = Jsoup.connect("http://thetvdb.com/api/GetSeries.php")
				.data("seriesname",tv)
                .ignoreContentType(false)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .followRedirects(false)
                .method(Connection.Method.GET)
                .execute();
        Document doc = response.parse();
        //System.out.println(doc.toString());
        Element id = doc.getElementsByTag("series").get(0);
        return id.getElementsByTag("seriesid").get(0).text();
        
        
	}

	
	public void storeCast(String id) throws Exception
	{
		Connection.Response response = Jsoup.connect("http://thetvdb.com/api/E63B88DCFFCADC7D/series/"+ id +"/actors.xml")
                .ignoreContentType(false)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .followRedirects(false)
                .method(Connection.Method.GET)
                .execute();
        Document doc = response.parse();
        Elements actors = doc.getElementsByTag("actor");
        for(Element actor : actors)
        {
        	String Name = actor.getElementsByTag("Name").get(0).text();
        	ArrayList<String> names = stops.removeStopwords(Name);
        	for(String over : names)
        	{
        		//System.out.println(over);
        		stopwords.add(over);
        	}
        	String Role = actor.getElementsByTag("Role").get(0).text();
        	ArrayList<String> roles = stops.removeStopwords(Role);
        	for(String over : roles)
        	{
        		//System.out.println(over);
        		stopwords.add(over);
        	}
        }
		
	}
	
	public void storeOverview(String id) throws Exception
	{
		Connection.Response response = Jsoup.connect("http://thetvdb.com/api/E63B88DCFFCADC7D/series/"+ id +"/en.xml")
                .ignoreContentType(false)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .followRedirects(false)
                .method(Connection.Method.GET)
                .execute();
        Document doc = response.parse();
        Elements actors = doc.getElementsByTag("Series");
        for(Element actor : actors)
        {
        	String overview = actor.getElementsByTag("Overview").get(0).text();
        	ArrayList<String> overviews = stops.removeStopwords(overview);
        	for(String over : overviews)
        	{
        		//System.out.println(over);
        		stopwords.add(over);
        	}
        }
	}
	
	public HashSet<String> GenerateStopwords(String tvShow) throws Exception
	{
		String id = getSeriesId(tvShow);
		storeOverview(id);
		storeCast(id);
		String[] splits = tvShow.split(" ");
		String combined = "";
		for(String letter:splits)
		{
			combined = combined + letter;
		}
		String[] breakout = tvShow.split("(?<=[\\S])[\\S]*(\\s*)?");
		String result = "";
		for (String letter : breakout)
		{
			result = result + letter;
		}
		
		String hashed = "#" +result;
		String hashedcomb = "#" + combined;
		stopwords.add(result);
		stopwords.add(hashed);
		stopwords.add(combined);
		stopwords.add(hashedcomb);
        return stopwords;
	}
	
	

}
