package NlpInterface.ir;

import java.util.HashSet;
import java.util.Set;

public class LuceneSearchResult {

	public LuceneSearchResult(long id, String vertex_type, String attr_type, String attr_val){
		this.id = id;
		this.vertex_type = vertex_type;
		this.attr_type = attr_type;
		this.attr_val = attr_val;
	}
	
	public long id;
	public String vertex_type;
	public String attr_type;
	public String attr_val;
}
