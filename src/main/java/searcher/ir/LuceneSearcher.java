package searcher.ir;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import cn.edu.pku.sei.SnowView.servlet.Config;
import graphdb.extractors.miners.text.TextExtractor;
import graphdb.extractors.parsers.stackoverflow.StackOverflowExtractor;
import graphdb.extractors.utils.TokenizationUtils;
import searcher.graph.GraphSearcher;
import searcher.graph.SearchResult;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class LuceneSearcher {

	QueryParser qp=new QueryParser("content", new EnglishAnalyzer());
	IndexSearcher indexSearcher=null;
	
	public static void main(String[] args){
		try {
			new LuceneSearcher().index(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void index(boolean test) throws IOException {

		GraphSearcher graphSearcher=Config.getGraphSearcher();
		
		Directory dir = FSDirectory.open(Paths.get(Config.getLucenePath()));
		Analyzer analyzer = new EnglishAnalyzer();
	    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
	    iwc.setOpenMode(OpenMode.CREATE);
	    IndexWriter writer = new IndexWriter(dir, iwc);
	    
		try (Statement statement = Config.getNeo4jBoltConnection().createStatement()) {
			String stat="match (n) where exists(n."+TextExtractor.TEXT+") return n."+TextExtractor.TITLE+" n."+TextExtractor.TEXT;
			if (test)
				stat="match (n:"+StackOverflowExtractor.ANSWER+") where exists(n."+TextExtractor.TEXT+") and n."+StackOverflowExtractor.ANSWER_ACCEPTED+"=TRUE "
						+"return n."+TextExtractor.TITLE+", n."+TextExtractor.TEXT+", id(n), labels(n)[0]";
			ResultSet rs=statement.executeQuery(stat);
			while (rs.next()) {
				String org_content = rs.getString("n."+TextExtractor.TEXT);
				String title = rs.getString("n."+TextExtractor.TITLE);
				String content = dealWithDocument("<html><title>" + title + "</title>" + org_content + "</html>");
				if (content.length() > 0) {
					Document document = new Document();
					document.add(new StringField("id", ""+rs.getLong("id(n)"), Field.Store.YES));
					document.add(new StringField("type", rs.getString("labels(n)[0]"), Field.Store.YES));
					document.add(new StringField("title", title, Field.Store.YES));
					document.add(new TextField("content", content, Field.Store.YES));
					document.add(new TextField("org_content", org_content, Field.Store.YES));
					if (test) {
						SearchResult subGraph = graphSearcher.query(content);
						String nodeSet = StringUtils.join(subGraph.nodes, " ").trim();
						document.add(new StringField("node_set", nodeSet, Field.Store.YES));
					}
					else
						document.add(new StringField("node_set", "", Field.Store.YES));
					writer.addDocument(document);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		writer.close();
	}

	public List<LuceneSearchResult> query(String q) {
		
		List<LuceneSearchResult> r=new ArrayList<>();
		
		if (indexSearcher==null){
			IndexReader reader=null;
			try {
				reader = DirectoryReader.open(FSDirectory.open(Paths.get(Config.getLucenePath())));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    indexSearcher = new IndexSearcher(reader);
		}
		
		q=dealWithDocument("<html>"+q+"</html>");
		
		if (q.trim().length()==0)
			return r;
		
		Query query=null;
		try {
			query=qp.parse(q);
		} catch (ParseException e) {
			return r;
		}
		TopDocs topDocs=null;
		try {
			topDocs=indexSearcher.search(query, 100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (ScoreDoc scoreDoc:topDocs.scoreDocs){
			Document document=null;
			try {
				document = indexSearcher.doc(scoreDoc.doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LuceneSearchResult result=new LuceneSearchResult(Long.parseLong(document.get("id")),
					document.get("type"), document.get("title"), document.get("org_content"),
					new Double(scoreDoc.score).doubleValue(), document.get("node_set"));
			r.add(result);
		}
		return r;
	}
	
	private String dealWithDocument(String content){
		String r="";
		content=Jsoup.parse(content).text();
		content=content.replaceAll("[^A-Za-z]+", " ");
		for (String token:content.split("\\s+")){
			List<String> eles=TokenizationUtils.camelSplit(token);
			if (eles.size()>1)
				r+=" "+StringUtils.join(eles," ");
		}
		return r.trim();
	}
	
}
