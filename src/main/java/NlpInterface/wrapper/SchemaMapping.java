package NlpInterface.wrapper;

import NlpInterface.entity.NLPNode;
import NlpInterface.entity.NLPRelation;
import NlpInterface.entity.NLPToken;
import NlpInterface.entity.Query;
import NlpInterface.entity.TokenMapping.*;
import NlpInterface.extractmodel.ExtractModel;
import NlpInterface.schema.GraphEdgeType;

public class SchemaMapping {
    public static Query query = null;
    public static void  mapping(Query query){
        SchemaMapping.query = query;
        /**
         * Case 1 : AttributeSchema without Attribute : return a attribute of a vertex
         * Case 2 : Attribute pair()
         */
        init();
        linkAttributeAndAttributeSchema();
        linkAttributeAndVertexSchema();
        linkAttributeSchemaAndVectexSchemaAndVertex();
        mappingEdgeSchema();
    }
    public static void init(){
        for (NLPToken token : query.tokens)
            if (token.mapping != null && !(token.mapping instanceof NLPEdgeSchemaMapping))
                query.nodes.add(new NLPNode(token));
    }
    public static void linkAttributeAndAttributeSchema(){
        /*make pair attribute with attrbuteschema*/
        for (NLPNode node : query.nodes){
            if (node.token.mapping instanceof NLPAttributeMapping){
                for (long offset = 1; offset < 20; offset++) {
                    boolean find = false;
                    for (NLPNode faNode : query.nodes) {
                        if (Math.abs(faNode.token.offset - node.token.offset) != offset) continue;
                        if (faNode.token.mapping instanceof NLPAttributeSchemaMapping &&
                                faNode.token.mapping.equals(((NLPAttributeMapping) node.token.mapping).type)) {
                            if (!faNode.nextNode.isEmpty()) continue;
                            NLPRelation relation1 = new NLPRelation("is");
                            NLPRelation relation2 = new NLPRelation("is");
                            relation1.mirror = relation2;
                            relation2.mirror = relation1;
                            faNode.addNext(node, relation1);
                            node.addLast(faNode, relation2);
                            find = true;
                            break;
                        }
                    }
                    if (find) break;
                }
            }
        }
    }

    public static void linkAttributeAndVertexSchema(){
        for (NLPNode node : query.nodes){
            if (node.token.mapping instanceof NLPAttributeSchemaMapping && !node.nextNode.isEmpty()){
                for (long offset = 1; offset < 20; offset++) {
                    boolean find = false;
                    for (NLPNode faNode : query.nodes) {
                        if (Math.abs(faNode.token.offset - node.token.offset) != offset) continue;
                        if (faNode.token.mapping instanceof NLPVertexSchemaMapping && !(faNode.token.mapping instanceof NLPVertexMapping) &&
                                ((NLPVertexSchemaMapping) faNode.token.mapping).vertexType.equals(((NLPAttributeSchemaMapping) node.token.mapping).vertexType)) {
                            NLPRelation relation1 = new NLPRelation("has");
                            NLPRelation relation2 = new NLPRelation("has");
                            relation1.mirror = relation2;
                            relation2.mirror = relation1;
                            faNode.addNext(node, relation1);
                            node.addLast(faNode, relation2);
                            find = true;
                            break;
                        }
                    }
                    if (find) break;
                }
            }
        }
    }
    public static void linkAttributeSchemaAndVectexSchemaAndVertex(){
        for (NLPNode node : query.nodes) {
            if (node.token.mapping instanceof NLPAttributeSchemaMapping && node.nextNode.isEmpty()) { //has no value
                boolean flag = false;
                for (long offset = 1; offset < 20; offset++) {
                    for (NLPNode faNode : query.nodes) {
                        if (Math.abs(faNode.token.offset - node.token.offset) != offset) continue;
                        if (faNode.token.mapping instanceof NLPVertexSchemaMapping && !(faNode.token.mapping instanceof NLPVertexMapping) &&
                                ((NLPVertexSchemaMapping) faNode.token.mapping).vertexType.equals(((NLPAttributeSchemaMapping) node.token.mapping).vertexType)) {
                            NLPRelation relation1 = new NLPRelation("has");
                            NLPRelation relation2 = new NLPRelation("has");
                            relation1.mirror = relation2;
                            relation2.mirror = relation1;
                            faNode.addNext(node, relation1);
                            node.addLast(faNode, relation2);
                            faNode.focus = true;
                            flag = true;
                            break;
                        }
                    }
                    if (flag) break;
                }
                if (flag) continue;
                for (long offset = 1; offset < 20; offset++) {
                    for (NLPNode faNode : query.nodes) {
                        if (Math.abs(faNode.token.offset - node.token.offset) != offset) continue;
                        if (faNode.token.mapping instanceof NLPVertexMapping &&
                                ((NLPVertexSchemaMapping) faNode.token.mapping).vertexType.equals(((NLPAttributeSchemaMapping) node.token.mapping).vertexType)) {
                            NLPRelation relation1 = new NLPRelation("has");
                            NLPRelation relation2 = new NLPRelation("has");
                            relation1.mirror = relation2;
                            relation2.mirror = relation1;
                            faNode.addNext(node, relation1);
                            node.addLast(faNode, relation2);
                            faNode.focus = true;
                            if (flag) break;
                            break;
                        }
                    }
                    if (flag) break;
                }
            }
        }
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
            for (NLPNode nodeStart : query.nodes) if (nodeStart.token.mapping instanceof  NLPVertexSchemaMapping){
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
