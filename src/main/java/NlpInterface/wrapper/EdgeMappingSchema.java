package NlpInterface.wrapper;

import NlpInterface.entity.NLPNode;
import NlpInterface.entity.NLPRelation;
import NlpInterface.entity.NLPToken;
import NlpInterface.entity.Query;
import NlpInterface.entity.TokenMapping.NLPEdgeSchemaMapping;
import NlpInterface.entity.TokenMapping.NLPMapping;
import NlpInterface.entity.TokenMapping.NLPPathSchemaMapping;
import NlpInterface.entity.TokenMapping.NLPVertexSchemaMapping;
import NlpInterface.extractmodel.ExtractModel;
import NlpInterface.schema.GraphEdgeType;
import NlpInterface.schema.GraphPath;
import NlpInterface.schema.GraphVertexType;

import java.util.ArrayList;
import java.util.List;

public class EdgeMappingSchema {
    public static Query query;
    public static  List<Query> queries;
    public static int[] mapEnd = new int[100];
    public static int[] mapStart = new int[100];
    public static List<NLPNode> nodes;
    public static List<Query> process(Query query){
        EdgeMappingSchema.query = query;
        nodes = query.nodes;
        queries = new ArrayList<>();
        DFS(0);
        return queries;
    }
    public static void DFS(int t){
        if (t == query.tokens.size()){
            List<NLPNode> tmp = query.nodes;
            query.nodes = nodes;
            Query newquery = query.copy();
            query.nodes = tmp;
            for (int i = 0; i < t; i++){
                NLPToken token = query.tokens.get(i);
                if (token.mapping instanceof  NLPEdgeSchemaMapping){
                    GraphEdgeType edgeType = ((NLPEdgeSchemaMapping) token.mapping).edgeType;
                    NLPRelation relation1 = new NLPRelation(edgeType);
                    NLPRelation relation2 = new NLPRelation(edgeType);
                    relation1.mirror = relation2;
                    relation2.mirror = relation1;
                    NLPNode nodeStartFinal = newquery.nodes.get(mapStart[i]);
                    NLPNode nodeEndFinal = newquery.nodes.get(mapEnd[i]);
                    nodeStartFinal.addNext(nodeEndFinal, relation1);
                    nodeEndFinal.addLast(nodeStartFinal, relation2);
                }
                if (token.mapping instanceof  NLPPathSchemaMapping){
                    GraphPath path = ((NLPPathSchemaMapping) token.mapping).path;
                    NLPNode nodeStartFinal = newquery.nodes.get(mapStart[i]);
                    NLPNode nodeEndFinal = newquery.nodes.get(mapEnd[i]);
                    NLPNode last = nodeStartFinal;
                    for (GraphVertexType vertexType : path.nodes){
                        NLPRelation relation1 = new NLPRelation("hidden");
                        NLPRelation relation2 = new NLPRelation("hidden");
                        relation1.mirror = relation2;
                        relation2.mirror = relation1;
                        NLPNode newNode = new NLPNode(new NLPToken("what"));
                        NLPMapping mapping = new NLPVertexSchemaMapping(vertexType,newNode.token,1);
                        newNode.token.mapping = mapping;
                        newNode.id = newquery.nodes.size();
                        newquery.nodes.add(newNode);
                        last.addNext(newNode,relation1);
                        newNode.addNext(last,relation2);
                        last = newNode;
                    }
                    NLPRelation relation1 = new NLPRelation("hidden");
                    NLPRelation relation2 = new NLPRelation("hidden");
                    relation1.mirror = relation2;
                    relation2.mirror = relation1;
                    last.addNext(nodeEndFinal, relation1);
                    nodeEndFinal.addLast(last, relation2);

                }
            }
            queries.add(newquery);
            return;
        }
        if (!(query.tokens.get(t).mapping instanceof  NLPEdgeSchemaMapping || query.tokens.get(t).mapping instanceof NLPPathSchemaMapping)) {
            DFS(t+1);
            return;
        }
        double direct = 1;
        NLPToken token = query.tokens.get(t);
        if (token.POS.equals("VBD")) direct = -1;

        String startname;
        String endname;

        if (query.tokens.get(t).mapping instanceof  NLPEdgeSchemaMapping){
            GraphEdgeType edgeType = ((NLPEdgeSchemaMapping) token.mapping).edgeType;
            startname = edgeType.start.name;
            endname  =edgeType.end.name;
        }else{
            GraphPath path = ((NLPPathSchemaMapping) token.mapping).path;
            startname = path.start.name;
            endname  = path.end.name;
        }

        List<NLPNode> tmpnodes = new ArrayList<>();
        tmpnodes.addAll(nodes);
        for (NLPNode nodeStart : tmpnodes) if (nodeStart.token.mapping instanceof NLPVertexSchemaMapping){
            if (((NLPVertexSchemaMapping) nodeStart.token.mapping).vertexType.name.equals(startname))
                for (NLPNode nodeEnd : tmpnodes) if (nodeEnd.token.mapping instanceof  NLPVertexSchemaMapping){
                    if (nodeStart == nodeEnd) continue;
                    if (((NLPVertexSchemaMapping) nodeEnd.token.mapping).vertexType.name.equals(endname)){
                        mapStart[t] = nodeStart.id;
                        mapEnd[t] = nodeEnd.id;
                        DFS(t+1);
                    }
                }
        }

        for (NLPNode node : tmpnodes) if (node.token.mapping instanceof  NLPVertexSchemaMapping) {
            if (((NLPVertexSchemaMapping) node.token.mapping).vertexType.name.equals(startname)){
                NLPNode newNode = new NLPNode(new NLPToken("what"));
                NLPMapping mapping = new NLPVertexSchemaMapping(ExtractModel.getSingle().
                        graphSchema.vertexTypes.get(endname),newNode.token,1);
                newNode.token.mapping = mapping;
                newNode.id = nodes.size();
                nodes.add(newNode);
                mapStart[t] = node.id;
                mapEnd[t] = newNode.id;
                DFS(t+1);
                nodes.remove(newNode);
            }
            if (((NLPVertexSchemaMapping) node.token.mapping).vertexType.name.equals(endname)){
                NLPNode newNode = new NLPNode(new NLPToken("what"));
                NLPMapping mapping = new NLPVertexSchemaMapping(ExtractModel.getSingle().
                        graphSchema.vertexTypes.get(startname),newNode.token,1);
                newNode.token.mapping = mapping;
                newNode.id = nodes.size();
                nodes.add(newNode);
                mapStart[t] = newNode.id;
                mapEnd[t] = node.id;
                DFS(t+1);
                nodes.remove(newNode);
            }
        }
        NLPNode newNodeStart = new NLPNode(new NLPToken("what"));
        NLPMapping mappingStart = new NLPVertexSchemaMapping(ExtractModel.getSingle().
                graphSchema.vertexTypes.get(startname),newNodeStart.token,1);
        newNodeStart.token.mapping = mappingStart;
        newNodeStart.id = nodes.size();
        mapStart[t] = newNodeStart.id;
        nodes.add(newNodeStart);

        NLPNode newNodeEnd = new NLPNode(new NLPToken("what"));
        NLPMapping mappingEnd = new NLPVertexSchemaMapping(ExtractModel.getSingle().
                graphSchema.vertexTypes.get(endname),newNodeEnd.token,1);
        newNodeEnd.token.mapping = mappingEnd;
        newNodeEnd.id = nodes.size();
        mapEnd[t] = newNodeEnd.id;
        nodes.add(newNodeEnd);
        DFS(t+1);
        nodes.remove(newNodeStart);
        nodes.remove(newNodeEnd);
    }
    public static void mappingEdgeSchema(){
        for (NLPToken token : query.tokens) if (token.mapping instanceof NLPEdgeSchemaMapping){
            double direct = 1;
            if (token.POS.equals("VBD")) direct = -1;
            boolean flagFind = false;
            String edgeTypeName = ((NLPEdgeSchemaMapping) token.mapping).type;
            NLPNode nodeStartFinal = null;
            NLPNode nodeEndFinal = null;
            double score = 10000;
            for (NLPNode nodeStart : query.nodes) if (nodeStart.token.mapping instanceof NLPVertexSchemaMapping){
                if (((NLPVertexSchemaMapping) nodeStart.token.mapping).vertexType.outcomings.keySet().contains(edgeTypeName))
                    for (NLPNode nodeEnd : query.nodes) if (nodeEnd.token.mapping instanceof  NLPVertexSchemaMapping){
                        if (nodeStart == nodeEnd) continue;
                        if (((NLPVertexSchemaMapping) nodeEnd.token.mapping).vertexType.incomings.keySet().contains(edgeTypeName)){
                            flagFind = true;
                            double newscore = Math.abs(token.offset + direct*1 -nodeEnd.token.offset)+Math.abs(token.offset - direct*1 -nodeStart.token.offset);
                            if (newscore < score){
                                score = newscore ;
                                nodeStartFinal = nodeStart;
                                nodeEndFinal = nodeEnd;
                            }
                            //break;
                        }
                    }
                //if (flagFind) break;

            }
            if (flagFind) {
                GraphEdgeType edgeType = ExtractModel.getSingle().graphSchema.findGraphEdgeTypeByNameAndVertex(edgeTypeName,
                        ((NLPVertexSchemaMapping) nodeStartFinal.token.mapping).vertexType, ((NLPVertexSchemaMapping) nodeEndFinal.token.mapping).vertexType);
                NLPRelation relation1 = new NLPRelation(edgeType);
                NLPRelation relation2 = new NLPRelation(edgeType);
                relation1.mirror = relation2;
                relation2.mirror = relation1;
                nodeStartFinal.addNext(nodeEndFinal, relation1);
                nodeEndFinal.addLast(nodeStartFinal, relation2);
                continue;
            }

            NLPNode newNodeLast = null;
            for (NLPNode node : query.nodes) if (node.token.mapping instanceof  NLPVertexSchemaMapping){
                if (((NLPVertexSchemaMapping) node.token.mapping).vertexType.outcomings.keySet().contains(edgeTypeName)){
                    GraphEdgeType edgeType = ExtractModel.getSingle().graphSchema.findGraphEdgeTypeByNameAndVertex(edgeTypeName,
                            ((NLPVertexSchemaMapping) node.token.mapping).vertexType,null);
                    NLPNode newNode = new NLPNode(new NLPToken("what"));
                    NLPMapping mapping = new NLPVertexSchemaMapping(ExtractModel.getSingle().
                            graphSchema.vertexTypes.get(edgeType.end.name),newNode.token,1);
                    newNode.token.mapping = mapping;
                    newNode.focus = true;
//                    NLPRelation relation1 = new NLPRelation(edgeType);
//                    NLPRelation relation2 = new NLPRelation(edgeType);
//                    relation1.mirror = relation2;
//                    relation2.mirror = relation1;
//                    newNode.addLast(node,relation1);
//                    node.addNext(newNode,relation2);
//                    query.nodes.add(newNode);
                    double newscore = Math.abs(token.offset - direct*1 -node.token.offset);
                    if (newscore < score){
                        flagFind = true;
                        score = newscore ;
                        nodeStartFinal = node;
                        nodeEndFinal = newNode;
                        newNodeLast = newNode;
                    }
                }
                if (((NLPVertexSchemaMapping) node.token.mapping).vertexType.incomings.keySet().contains(edgeTypeName)){
                    GraphEdgeType edgeType = ExtractModel.getSingle().graphSchema.findGraphEdgeTypeByNameAndVertex(edgeTypeName,
                            null,((NLPVertexSchemaMapping) node.token.mapping).vertexType);
                    NLPNode newNode = new NLPNode(new NLPToken("what"));
                    NLPMapping mapping = new NLPVertexSchemaMapping(ExtractModel.getSingle().
                            graphSchema.vertexTypes.get(edgeType.start.name),newNode.token,1);
                    newNode.token.mapping = mapping;
                    newNode.focus = true;
//                    NLPRelation relation1 = new NLPRelation(edgeType);
//                    NLPRelation relation2 = new NLPRelation(edgeType);
//                    relation1.mirror = relation2;
//                    relation2.mirror = relation1;
//                    newNode.addNext(node,relation1);
//                    node.addLast(newNode,relation2);
//                    query.nodes.add(newNode);
                    double newscore = Math.abs(token.offset + direct*1 -node.token.offset);
                    if (newscore < score){
                        flagFind = true;
                        score = newscore ;
                        nodeStartFinal = newNode;
                        nodeEndFinal = node;
                        newNodeLast = newNode;
                    }
                }
            }
            if (flagFind) {
                GraphEdgeType edgeType = ExtractModel.getSingle().graphSchema.findGraphEdgeTypeByNameAndVertex(edgeTypeName,
                        ((NLPVertexSchemaMapping) nodeStartFinal.token.mapping).vertexType, ((NLPVertexSchemaMapping) nodeEndFinal.token.mapping).vertexType);
                NLPRelation relation1 = new NLPRelation(edgeType);
                NLPRelation relation2 = new NLPRelation(edgeType);
                relation1.mirror = relation2;
                relation2.mirror = relation1;
                nodeStartFinal.addNext(nodeEndFinal, relation1);
                nodeEndFinal.addLast(nodeStartFinal, relation2);
                query.nodes.add(newNodeLast);
            }
        }
    }

}

