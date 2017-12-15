package NlpInterface.wrapper;

import NlpInterface.config.StopWords;
import NlpInterface.entity.NLPToken;
import NlpInterface.entity.Query;

public class TokensGenerator {
    public static Query generator(String text){
        /*AST 匹配到*/
        Query query = new Query();
        query.text = text;
        long offset = -1;
        for (String word : text.split(" ")){
            if (!StopWords.isStopWord(word)) {
                NLPToken token = new NLPToken(word);
                offset++;
                token.offset = offset;
                query.tokens.add(token);
            }
        }

        return query;
    }
}
