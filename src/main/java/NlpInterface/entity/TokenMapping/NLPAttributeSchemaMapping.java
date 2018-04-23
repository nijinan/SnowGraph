package NlpInterface.entity.TokenMapping;

import NlpInterface.entity.NLPToken;
import NlpInterface.extractmodel.Vertex;
import NlpInterface.ir.LuceneSearchResult;
import NlpInterface.schema.GraphVertexType;

import java.util.List;
import java.util.Set;

public class NLPAttributeSchemaMapping extends NLPMapping{
    public GraphVertexType vertexType;
    public String attrType;
    public List<LuceneSearchResult> l;
    public Set<Long> s;
    public boolean must = false;
    public boolean isbool = false;
    public boolean boolval = false;
    public NLPAttributeSchemaMapping(GraphVertexType vertexType, String attrType, NLPToken token, double similar){
        super(token,similar);
        this.vertexType = vertexType;
        this.attrType = attrType;
    }
    public static boolean isBoolAttr(String s){
        s = s.toLowerCase();
        if (s.contains("static") || s.contains("abstract") || s.contains("final")) return true;
        return false;
    }
}
