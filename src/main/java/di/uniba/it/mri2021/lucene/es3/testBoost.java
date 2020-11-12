package di.uniba.it.mri2021.lucene.es3;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class testBoost {

	public testBoost() {
		// TODO Auto-generated constructor stub
	}


	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		CollectionLoader loader = new CollectionLoader("./resources/cran/cran.qry.json", "./resources/cran/cran.all.1400.json");
		loader.index("./resources/cran/", new EnglishAnalyzer());

		//test
		System.out.println("Testing...");
		FSDirectory fsdir = FSDirectory.open(new File("./resources/cran/").toPath());
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(fsdir));
		QueryParser qp = new QueryParser("text", new EnglishAnalyzer());
		QueryParser qpTitle = new QueryParser("title", new EnglishAnalyzer());
		QueryParser qpAuth = new QueryParser("authors", new EnglishAnalyzer());
		PrintWriter outFile = null;
		outFile = new PrintWriter(new FileWriter("./resources/cran/resultsBoost.out"));
		Integer rank = 1, id_q=1;
		for(CollectionLoader.MyQuery q : loader.querySet) {
			BooleanQuery.Builder qb = new BooleanQuery.Builder();
			Query qLucene = qp.parse(QueryParserBase.escape(q.query));
			qLucene = new BoostQuery(qLucene, 1.4f);
			Query qLuceneT = qpTitle.parse(QueryParserBase.escape(q.query));
			qb.add(qLucene, Occur.MUST);
			qLuceneT = new BoostQuery(qLuceneT, 0.8f);
			qb.add(qLuceneT, Occur.SHOULD);
			Query qLuceneA = qpAuth.parse(QueryParserBase.escape(q.query));
			qLuceneA = new BoostQuery(qLuceneA, 0.3f);
			qb.add(qLuceneA, Occur.SHOULD);
			TopDocs topdocs = searcher.search(qb.build(), 100);
			for (ScoreDoc sdoc : topdocs.scoreDocs) {
				//append to results file
				outFile.println(id_q+" 0 "+searcher.doc(sdoc.doc).get("id")+" "+rank+" "+sdoc.score+" QueryBoost");
				rank++;
            }
			id_q++;
		}
		outFile.close();
		System.out.println("Done!");
	}

}
