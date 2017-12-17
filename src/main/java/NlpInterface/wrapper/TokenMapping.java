package NlpInterface.wrapper;

import NlpInterface.entity.NLPToken;
import NlpInterface.entity.Query;
import NlpInterface.entity.TokenMapping.*;
import NlpInterface.extractmodel.ExtractModel;
import NlpInterface.extractmodel.Graph;
import NlpInterface.extractmodel.Vertex;
import NlpInterface.schema.GraphEdgeType;
import NlpInterface.schema.GraphSchema;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class TokenMapping {
    public static double threshold = 0.5;
    public static void process(Query query){
        /* 全文匹配，疑问词匹配 ，之后改成模糊匹配，*
        需要记录下AST/
         */
        GraphSchema graphSchema = ExtractModel.getSingle().graphSchema;
        Graph graph = ExtractModel.getSingle().graph;
        for (NLPToken token : query.tokens){
            /*先schema匹配，后实体匹配*/
            if (token.mapping != null) continue;
            for (String edgeTypeName : graphSchema.edgeTypes.keySet()){
                double similar = isSimilar(token.text, edgeTypeName);
                if (similar > threshold  && ((token.mapping == null)||(token.mapping != null && similar > token.mapping.score) )){
                    NLPMapping mapping = new NLPEdgeSchemaMapping(edgeTypeName,null, token, similar);
                    if (token.mapping == null){
                        token.mappingList.add(mapping);
                        token.mapping = mapping;
                        continue;
                    }
                    if (similar < token.mapping.score + 0.01){
                        token.mappingList.add(mapping);
                        continue;
                    }
                    token.mappingList.clear();
                    token.mapping = mapping;
                    token.mappingList.add(mapping);
                    //break;
                }
            }
            if (token.mapping != null) continue;
            for (String vertexTypeName : graphSchema.vertexTypes.keySet()){
                double similar = isSimilar(token.text, vertexTypeName);
                if (similar > threshold  && ((token.mapping == null)||(token.mapping != null && similar > token.mapping.score) )){
                    NLPMapping mapping = new NLPVertexSchemaMapping(graphSchema.vertexTypes.get(vertexTypeName),token, similar);
                    if (token.mapping == null){
                        token.mappingList.add(mapping);
                        token.mapping = mapping;
                        continue;
                    }
                    if (similar < token.mapping.score + 0.01){
                        token.mappingList.add(mapping);
                        continue;
                    }
                    token.mappingList.clear();
                    token.mapping = mapping;
                    token.mappingList.add(mapping);
                    //break;
                }
            }
        }
        for (NLPToken token : query.tokens){
            if (token.mapping != null) continue;
            for (Vertex vertex : graph.getAllVertexes()){
                double similar = isSimilar(token.text, vertex.name);
                if (similar > threshold  && ((token.mapping == null)||(token.mapping != null && similar > token.mapping.score) )){
                    boolean flag = false;
                    for (NLPToken tok : query.tokens){
                        if (tok.mapping != null && tok.mapping instanceof NLPEdgeSchemaMapping){
                            for (GraphEdgeType edgeType : ExtractModel.getSingle().graphSchema.edgeTypes.get(((NLPEdgeSchemaMapping) tok.mapping).type)){
                                if  (edgeType.start.name.equals(vertex.labels) || edgeType.end.name.equals(vertex.labels) ){
                                    flag = true; break;
                                }
                            }
                            if (flag) break;
                        }
                    }
                    if (!flag) continue;
                    NLPMapping mapping = new NLPVertexMapping(vertex,graphSchema.vertexTypes.get(vertex.labels),token, similar);
                    //token.mapping = mapping;
                    if (token.mapping == null){
                        token.mappingList.add(mapping);
                        token.mapping = mapping;
                        continue;
                    }
                    if (similar < token.mapping.score + 0.01){
                        token.mappingList.add(mapping);
                        continue;
                    }
                    token.mappingList.clear();
                    token.mapping = mapping;
                    token.mappingList.add(mapping);
                    //break;
                }
            }
        }
        for (NLPToken token : query.tokens){
            if (token.mapping != null) continue;
            for (Vertex vertex : graph.getAllVertexes()){
                double similar = isSimilar(token.text, vertex.name);
                if (similar > threshold  && ((token.mapping == null)||(token.mapping != null && similar > token.mapping.score - 0.01) )){
                    NLPMapping mapping = new NLPVertexMapping(vertex,graphSchema.vertexTypes.get(vertex.labels),token, similar);
                    if (token.mapping == null){
                        token.mappingList.add(mapping);
                        token.mapping = mapping;
                        continue;
                    }
                    if (similar < token.mapping.score + 0.01){
                        token.mappingList.add(mapping);
                        continue;
                    }
                    token.mappingList.clear();
                    token.mapping = mapping;
                    token.mappingList.add(mapping);
                    //break;
                }
            }
        }
        for (NLPToken token : query.tokens){
            if (token.mapping != null) continue;
            for (String vertexTypeName : graphSchema.vertexTypes.keySet()){
                boolean flag = false;
                for (NLPToken tok : query.tokens){
                    if (tok.mapping != null){
                        if (tok.mapping instanceof NLPVertexSchemaMapping){ flag = true; break;}
                        if (tok.mapping instanceof NLPVertexMapping && ((NLPVertexMapping) tok.mapping).vertexType.name.equals(vertexTypeName)){ flag = true; break;}
                    }
                }
                if (!flag) continue;
                for (String attrName : graphSchema.vertexTypes.get(vertexTypeName).attrs.keySet()){
                    double similar = isSimilar(token.text, attrName);
                    if (similar > threshold  && ((token.mapping == null)||(token.mapping != null && similar > token.mapping.score) )){
                        NLPMapping mapping = new NLPAttributeSchemaMapping(graphSchema.vertexTypes.get(vertexTypeName),attrName,token, similar);
                        //token.mapping = mapping;
                        if (token.mapping == null){
                            token.mappingList.add(mapping);
                            token.mapping = mapping;
                            continue;
                        }
                        if (similar < token.mapping.score + 0.01){
                            token.mappingList.add(mapping);
                            continue;
                        }
                        token.mappingList.clear();
                        token.mapping = mapping;
                        token.mappingList.add(mapping);
                        //break;
                    }
                }
            }
        }
        GraphDatabaseService db = ExtractModel.getSingle().db;
        try (Transaction tx = db.beginTx()) {
            for (NLPToken token : query.tokens) {
                if (token.mapping != null) continue;
                for (Vertex vertex : graph.getAllVertexes()) {
                    boolean flag = false;
                    for (NLPToken tok : query.tokens) {
                        if (tok.mapping != null) {
                            if (tok.mapping instanceof NLPVertexMapping && ((NLPVertexMapping) tok.mapping).vertex.equals(vertex)) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (!flag) continue;
                    Node node = db.getNodeById(vertex.id);
                    for (String attrTypeName : graphSchema.vertexTypes.get(vertex.labels).attrs.keySet()) {

                        Object obj = node.getAllProperties().get(attrTypeName);
                        if (!(obj instanceof String)) continue;
                        String attrValue = (String)obj;
                        double similar = isSimilar(token.text, attrValue);
                        if (similar > threshold  && ((token.mapping == null)||(token.mapping != null && similar > token.mapping.score) )){
                            NLPMapping mapping = new NLPAttributeMapping(vertex, graphSchema.vertexTypes.get(vertex.labels), attrTypeName, attrValue, token, similar);
                            if (token.mapping == null){
                                token.mappingList.add(mapping);
                                token.mapping = mapping;
                                continue;
                            }
                            if (similar < token.mapping.score + 0.01){
                                token.mappingList.add(mapping);
                                continue;
                            }
                            token.mappingList.clear();
                            token.mapping = mapping;
                            token.mappingList.add(mapping);
                        }
                    }
                }
            }
            tx.success();
        }
    }
    public static double isSimilar(String str1, String str2){
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        if (str1.equals(str2)) return 1;
        if (str1.contains(str2) && (2 * str2.length() >  str1.length())) return ((double)str2.length()) / str1.length();
        if (str2.contains(str1) && (2 * str1.length() >  str2.length())) return ((double)str1.length()) / str2.length();
        return 0;
    }
    public static boolean isUnDirect(String str){
        if (str.endsWith("ed")){
            return true;
        }
        return false;
    }
}
