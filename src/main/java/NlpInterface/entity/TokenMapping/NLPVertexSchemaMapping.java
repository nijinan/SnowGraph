package NlpInterface.entity.TokenMapping;

import NlpInterface.entity.NLPToken;
import NlpInterface.ir.LuceneSearchResult;
import NlpInterface.schema.GraphVertexType;

import java.util.List;

public class NLPVertexSchemaMapping extends NLPMapping{
    public GraphVertexType vertexType;
    public List<LuceneSearchResult> l;
    public boolean must = false;
    public NLPVertexSchemaMapping(GraphVertexType vertexType, NLPToken token, double similar){
        super(token, similar);
        this.vertexType = vertexType;
    }
    @Override
    public boolean equals(Object v) {
        return v instanceof NLPVertexSchemaMapping &&  vertexType.equals(((NLPVertexSchemaMapping) v).vertexType);
    }
}
