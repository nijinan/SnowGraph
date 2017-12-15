package NlpInterface;

import NlpInterface.entity.*;
import NlpInterface.entity.TokenMapping.NLPVertexMapping;
import NlpInterface.wrapper.*;

public class NLPInterpreter {
    public static String pipeline(String plainText){
        Query query = generatorTokens(plainText);
        mapTokensToNodeAndRelation(query);
        //generatorTuples(query);
        //generatorTupleLinks(query);
        mapToSchema(query);
        generatorInferenceLinks(query);
        String cypherStr = generatorCyphers(query);
        System.out.println(query.tokens.size());
        System.out.println(cypherStr);
        return cypherStr;
        /*CypherSet cyphers = generatorCyphers(query);
        for (Cypher cypher : cyphers.sets){
            System.out.println(cypher.text);
        }*/
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

