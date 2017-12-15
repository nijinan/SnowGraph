package NlpInterface.entity.TokenMapping;

import NlpInterface.entity.NLPToken;
import NlpInterface.extractmodel.Vertex;
import NlpInterface.schema.GraphVertexType;

public class NLPAttributeSchemaMapping extends NLPMapping{
    public GraphVertexType vertexType;
    public String attrType;
    public NLPAttributeSchemaMapping(GraphVertexType vertexType, String attrType, NLPToken token, double similar){
        super(token,similar);
        this.vertexType = vertexType;
        this.attrType = attrType;
    }
}
