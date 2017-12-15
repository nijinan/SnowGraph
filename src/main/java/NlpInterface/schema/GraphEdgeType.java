package NlpInterface.schema;

import NlpInterface.extractmodel.Vertex;

public class GraphEdgeType {
    public GraphVertexType start;
    public GraphVertexType end;
    public String name;
    public GraphEdgeType(String name, GraphVertexType start, GraphVertexType end){
        this.name = name;
        this.start = start;
        this.end = end;
    }
    @Override
    public boolean equals(Object v) {
        return v instanceof GraphEdgeType &&  name.equals(((GraphEdgeType) v).name)
                && start.name.equals(((GraphEdgeType) v).start.name) && end.name.equals(((GraphEdgeType) v).end.name);
    }
}
