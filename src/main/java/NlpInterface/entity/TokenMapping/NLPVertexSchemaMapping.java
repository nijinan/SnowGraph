package NlpInterface.entity.TokenMapping;

import NlpInterface.entity.NLPToken;
import NlpInterface.schema.GraphEdgeType;
import NlpInterface.schema.GraphVertexType;

public class NLPVertexSchemaMapping extends NLPMapping{
    public GraphVertexType vertexType;
    public NLPVertexSchemaMapping(GraphVertexType vertexType, NLPToken token, double similar){
        super(token, similar);
        this.vertexType = vertexType;
    }
    @Override
    public boolean equals(Object v) {
        return v instanceof NLPVertexSchemaMapping &&  vertexType.equals(((NLPVertexSchemaMapping) v).vertexType);
    }
}
