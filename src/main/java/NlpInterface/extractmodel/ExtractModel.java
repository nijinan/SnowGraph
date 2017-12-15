package NlpInterface.extractmodel;

import NlpInterface.schema.GraphAttribute;
import NlpInterface.schema.GraphEdgeType;
import NlpInterface.schema.GraphSchema;
import NlpInterface.schema.GraphVertexType;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.*;

public class ExtractModel {
	public static ExtractModel single = null;
	public GraphDatabaseService db = null;
	public Graph graph = null;
	public GraphSchema graphSchema = null;
	public static ExtractModel getSingle(){
		if (single != null) return single;
		single = new ExtractModel("C:\\Users\\dell\\Documents\\graphdb-more");
		return single;
	}
	private ExtractModel(String srcPath){
		db = new GraphDatabaseFactory().newEmbeddedDatabase(new File(srcPath));
		graphSchema = new GraphSchema();
		graph = pipeline();

		//for (Vertex vertex:graph.getAllVertexes()) System.out.println(vertex.name);
	}

	private Graph pipeline(){
		Graph graph = extractProgramAbstract();
		return graph;
	}

	private Graph extractProgramAbstract(){

		Graph graph=new Graph();

		try (Transaction tx = db.beginTx()){
			for (Node node : db.getAllNodes()){
				for (Iterator<Label> o_iterator = node.getLabels().iterator(); o_iterator.hasNext(); ) {
					Label label = o_iterator.next();
					String node_type = label.name();
					if (!GraphSchemaKeywords.getSingle().types.containsKey(node_type)) continue;
					Object leftObj = node.getProperty(GraphSchemaKeywords.getSingle().types.get(node_type).getLeft());
					Object rightObj = node.getProperty(GraphSchemaKeywords.getSingle().types.get(node_type).getRight());
					String name;
					String longName;
					if (leftObj instanceof  Integer) name = ""+((Integer)leftObj).intValue(); else  name = (String) leftObj;
					if (rightObj instanceof  Integer) longName = ""+((Integer)rightObj).intValue(); else  longName = (String) rightObj;
					Vertex vertex = new Vertex(node.getId(), name, longName, node_type);
					graph.add(vertex);
					break;
				}
			}
			for (Relationship rel : db.getAllRelationships()){
				long srcId = rel.getStartNodeId();
				long dstId = rel.getEndNodeId();
				String type = rel.getType().name();
				if (graph.contains(srcId) && graph.contains(dstId)){
					graph.addEdge(graph.get(srcId), graph.get(dstId), type);
					String src_type = graph.vertexes.get(srcId).labels;
					String dst_type = graph.vertexes.get(dstId).labels;
					if (!graphSchema.vertexTypes.containsKey(src_type)){
						graphSchema.vertexTypes.put(src_type,new GraphVertexType(src_type));
					}
					if (!graphSchema.vertexTypes.containsKey(dst_type)){
						graphSchema.vertexTypes.put(dst_type,new GraphVertexType(dst_type));
					}
					if (!graphSchema.vertexTypes.get(dst_type).incomings.containsKey(type)) graphSchema.vertexTypes.get(dst_type).incomings.put(type,new HashSet<>());
					graphSchema.vertexTypes.get(dst_type).incomings.get(type).add(graphSchema.vertexTypes.get(src_type));
					if (!graphSchema.vertexTypes.get(src_type).outcomings.containsKey(type)) graphSchema.vertexTypes.get(src_type).outcomings.put(type,new HashSet<>());
					graphSchema.vertexTypes.get(src_type).outcomings.get(type).add(graphSchema.vertexTypes.get(dst_type));
				}
			}
			for (GraphVertexType vertexType : graphSchema.vertexTypes.values()){
				for (String type : vertexType.outcomings.keySet()) {
					for (GraphVertexType dstVertex : vertexType.outcomings.get(type)){
						if (!graphSchema.edgeTypes.containsKey(type)) graphSchema.edgeTypes.put(type, new HashSet<>());
						graphSchema.edgeTypes.get(type).add(new GraphEdgeType(type,vertexType,dstVertex));

					}
				}
			}
			for (Set<GraphEdgeType> edgeTypes : graphSchema.edgeTypes.values()){
				for (GraphEdgeType edgeType : edgeTypes){
					edgeType.start.outcomingsEdges.add(edgeType);
					edgeType.end.incomingsEdges.add(edgeType);
				}
			}
			for (Node node : db.getAllNodes()){
				for (Iterator<Label> o_iterator = node.getLabels().iterator(); o_iterator.hasNext(); ) {
					Label label = o_iterator.next();
					String node_type = label.name();
					GraphVertexType vertexType = graphSchema.vertexTypes.get(node_type);
					for (String propertiesName : node.getAllProperties().keySet()){
						if (!vertexType.attrs.keySet().contains(propertiesName)){
							vertexType.attrs.put(propertiesName,new GraphAttribute(propertiesName,vertexType));
						}
					}
					break;
				}
			}
			tx.success();
		}
		return graph;
	}

	private Graph longNameFilter(Graph graph){
		for (Vertex vertex:graph.getAllVertexes()){
			String longName = vertex.longName.toLowerCase();
			if (longName.contains("codec")||longName.contains("util")||longName.contains("test")||longName.contains("exception")
					||longName.contains("comparator")||longName.contains("attribute"))
				graph.remove(vertex);
		}
		return graph;
	}

}
