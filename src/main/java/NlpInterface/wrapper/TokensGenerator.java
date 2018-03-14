package NlpInterface.wrapper;

import NlpInterface.config.StopWords;
import NlpInterface.entity.NLPToken;
import NlpInterface.entity.Query;
import NlpInterface.entity.TokenMapping.NLPNoticeMapping;
import NlpInterface.ir.LuceneIndex;
import NlpInterface.ir.LuceneSearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TokensGenerator {
    public static Query generator(String text){
        /*AST 匹配到*/
        Query query = new Query();
        boolean flag = false;
        String word = "";
        String tmptext = "";
        Map<String,String> key = new HashMap();
        for (int i = 0; i < text.length(); i++){
            if (text.charAt(i) == '"' && flag){
                String k = "TOKEN_"+key.size();
                key.put(k,word);
                tmptext += k;
                flag = !flag;
                continue;
            }else if (text.charAt(i) == '"' && !flag){
                flag = !flag;
                continue;
            }
            if (flag){
                word += text.charAt(i);
            }else{
                tmptext += text.charAt(i);
            }
        }
        text = tmptext;
        query.text = text;
        long offset = -1;
        List<NLPToken> set = StanfordParser.getSingle().runAllAnnotators(text);
        for (NLPToken token : set){
            if (!StopWords.isStopWord(token.text)) {
                offset++;
                token.offset = offset;
                token.offsetVal = offset;
                if (token.text.startsWith("TOKEN_")){
                    List<LuceneSearchResult> l = LuceneIndex.query(key.get(token.text));
                    token.mapping = new NLPNoticeMapping(l);
                    token.mappingList.add(token.mapping);
                }
                query.tokens.add(token);
            }
        }
        return query;
    }
}
