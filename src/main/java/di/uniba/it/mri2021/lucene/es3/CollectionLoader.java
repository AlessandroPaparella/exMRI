/**
 * @author AlessandroPaparella
 */

package di.uniba.it.mri2021.lucene.es3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import com.google.gson.Gson;

public class CollectionLoader {

	String queryFile, docFile;
	List<MyQuery> querySet = new ArrayList<CollectionLoader.MyQuery>();

	public CollectionLoader(String queryFile, String docFile) throws Exception {
		// TODO Auto-generated constructor stub
		this.docFile = docFile;
		this.queryFile = queryFile;
		querySet = loadQ();
	}


	private class Doc{
		String id, text, authors, title, biblio;
	}

	public class MyQuery{
		String id, query;
	}

	private List<MyQuery> loadQ() throws Exception {
		Gson gson = new Gson();
		BufferedReader cran = null;
		cran = new BufferedReader(new FileReader(queryFile));
		String l;
		ArrayList<MyQuery> ql = new ArrayList<MyQuery>();
		while((l=cran.readLine())!= null) {
			MyQuery q = gson.fromJson(l, MyQuery.class);
			ql.add(q);
		}
		cran.close();
		return ql;
	}

	public void index(String indexDir, Analyzer a) throws Exception{
		BufferedReader cran = null;
		Gson gson = new Gson();
		cran = new BufferedReader(new FileReader(docFile));
		String l;
		FieldType ft = new FieldType(TextField.TYPE_NOT_STORED);
		ft.setStoreTermVectors(true);
		FSDirectory fsdir = FSDirectory.open(new File(indexDir).toPath());
		IndexWriterConfig iwc = new IndexWriterConfig(a);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(fsdir, iwc);
		while ((l = cran.readLine()) != null) {
			Doc d = gson.fromJson(l, Doc.class);
			Document doc = new Document();
			doc.add(new StringField("id", d.id, Field.Store.YES));
			doc.add(new Field("text", d.text, ft));
			doc.add(new Field("authors", d.authors, ft));
			doc.add(new Field("title", d.title, ft));
			doc.add(new Field("biblio", d.biblio, ft));
			writer.addDocument(doc);
		}
		writer.close();
		cran.close();
	}

}
