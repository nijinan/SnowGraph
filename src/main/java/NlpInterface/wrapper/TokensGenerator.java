package NlpInterface.wrapper;

import NlpInterface.config.StopWords;
import NlpInterface.entity.NLPToken;
import NlpInterface.entity.Query;

import java.util.List;
import java.util.Set;

public class TokensGenerator {
    public static Query generator(String text){
        /*AST 匹配到*/
        Query query = new Query();
        query.text = text;
        long offset = -1;
        List<NLPToken> set = StanfordParser.getSingle().runAllAnnotators(text);
        for (NLPToken token : set){
            if (!StopWords.isStopWord(token.text)) {
                offset++;
                token.offset = offset;
                token.offsetVal = offset;
                query.tokens.add(token);
            }
        }
        return query;
    }
}
