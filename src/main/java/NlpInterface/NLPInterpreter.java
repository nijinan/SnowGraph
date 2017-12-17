package NlpInterface;

import NlpInterface.entity.*;
import NlpInterface.entity.TokenMapping.NLPVertexMapping;
import NlpInterface.wrapper.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NLPInterpreter {
    public static String cypherStr = "";
    private static int offsetMax;
    public static JSONObject pipeline(String plainText){
        Query query = generatorTokens(plainText);
        mapTokensToNodeAndRelation(query);
        Set<NLPToken> tmp = new HashSet<>();
        offsetMax = query.tokens.size();
        for (NLPToken token : query.tokens){
            if (token.mapping != null){
                tmp.add(token);
            }
        }
        query.tokens = tmp;
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < offsetMax; i++) list.add(0);
        cypherStr  = "";
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();

        DFS(query,0,list,arr);
        //generatorTuples(query);
        //generatorTupleLinks(query);
        obj.put("rankedResults",arr);
        return obj;
        /*CypherSet cyphers = generatorCyphers(query);
        for (Cypher cypher : cyphers.sets){
            System.out.println(cypher.text);
        }*/
    }

    public static void DFS(Query query, int offset, List<Integer> list, JSONArray arr){
        if (offset == offsetMax){
            Query newquery = query.copy();
            for (NLPToken token : newquery.tokens){
                token.mapping = token.mappingList.get(list.get((int)token.offset));

            }
            mapToSchema(newquery);
            generatorInferenceLinks(newquery);
            String s = generatorCyphers(newquery);
            if (!s.equals("")) {
                cypherStr += s + "</br>";
                arr.put(s);
            }
            return;
        }
        boolean flag = false;
        for (NLPToken token : query.tokens){
            if (token.offset == offset){
                flag = true;
                for (int i = 0; i < token.mappingList.size(); i++){
                    list.set(offset,i);
                    DFS(query,offset+1, list,arr);
                }
            }
        }
        if (!flag) DFS(query,offset+1, list,arr);
    }

    public static Query generatorTokens(String plainText){
        return TokensGenerator.generator(plainText);
    }


    public static void mapTokensToNodeAndRelation(Query query){
        TokenMapping.process(query);
    }

    public static void generatorInferenceLinks(Query query){
        InferenceLinksGenerator.generate(query);
    }

    public static void mapToSchema(Query query){
        SchemaMapping.mapping(query);
    }
    public static String generatorCyphers(Query query){
        return CyphersGenerator.generate(query);
    }
}

