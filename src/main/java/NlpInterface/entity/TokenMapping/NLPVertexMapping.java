package NlpInterface.entity.TokenMapping;

import NlpInterface.entity.NLPToken;
import NlpInterface.extractmodel.Vertex;
import NlpInterface.schema.GraphVertexType;

public class NLPVertexMapping extends NLPVertexSchemaMapping{
    public Vertex vertex;
    public NLPVertexMapping(Vertex vertex, GraphVertexType vertexType, NLPToken token, double similar){
        super(vertexType,token,similar);
        this.vertex = vertex;
    }
    public boolean equals(Object v) {
        return v instanceof NLPVertexMapping &&  vertexType.equals(((NLPVertexSchemaMapping) v).vertexType);
    }
}
