package NlpInterface.schema;

import NlpInterface.extractmodel.ExtractModel;
import NlpInterface.extractmodel.Graph;
import NlpInterface.extractmodel.Vertex;
import org.neo4j.graphdb.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GraphSchema {
    public Map<String,GraphVertexType> vertexTypes = new HashMap<>() ;
    public Map<String,Set<GraphEdgeType>> edgeTypes = new HashMap<>();
    public GraphEdgeType findGraphEdgeTypeByNameAndVertex(String name, GraphVertexType vertex1, GraphVertexType vertex2){
        for (GraphEdgeType edgeType : edgeTypes.get(name)){
            if ((vertex1 == null  || edgeType.start.equals(vertex1))&& (vertex2 == null || edgeType.end.equals(vertex2) )) return edgeType;
        }
        return null;
    }
}
