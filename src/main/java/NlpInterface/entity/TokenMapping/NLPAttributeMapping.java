package NlpInterface.entity.TokenMapping;


import NlpInterface.entity.NLPToken;
import NlpInterface.extractmodel.Vertex;
import NlpInterface.schema.GraphVertexType;

public class NLPAttributeMapping extends NLPMapping{
    public Vertex vertex;
    public String attrValue;
    public Object value;
    public NLPAttributeSchemaMapping type;
    public NLPAttributeMapping(Vertex vertex, GraphVertexType vertexType, String attrType, String attrValue, NLPToken token, double similar){
        super(token,similar);
        type = new NLPAttributeSchemaMapping(vertexType,attrType,token,similar);
        this.vertex = vertex;
        this.attrValue = attrValue;
    }
}
