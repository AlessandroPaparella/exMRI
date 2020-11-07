package di.uniba.it.mri2021.lucene.es3;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class testStandard {

	public testStandard() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		CollectionLoader loader = new CollectionLoader("./resources/cran/cran.qry.json", "./resources/cran/cran.all.1400.json");
		loader.index("./resources/cran/", new StandardAnalyzer());

		//test
		System.out.println("Testing...");
		FSDirectory fsdir = FSDirectory.open(new File("./resources/cran/").toPath());
		IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(fsdir));
		QueryParser qp = new QueryParser("text", new StandardAnalyzer());
		PrintWriter outFile = null;
		outFile = new PrintWriter(new FileWriter("./resources/cran/results.out"));
		Integer rank = 1, id_q=1;
		for(CollectionLoader.MyQuery q : loader.querySet) {
			Query qLucene = qp.parse(QueryParserBase.escape(q.query));
			TopDocs topdocs = searcher.search(qLucene, 100);
			for (ScoreDoc sdoc : topdocs.scoreDocs) {
				//append to results file
				outFile.println(id_q+" 0 "+searcher.doc(sdoc.doc).get("id")+" "+rank+" "+sdoc.score+" StandardAnalyzer");
				rank++;
            }
			id_q++;
		}
		outFile.close();
		System.out.println("Done!");
	}

}
