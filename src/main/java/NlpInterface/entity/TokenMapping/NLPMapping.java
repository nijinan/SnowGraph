package NlpInterface.entity.TokenMapping;

import NlpInterface.entity.NLPToken;

public class NLPMapping {
    public NLPToken belong;
    public double score = 0;
    public double rank = -1; // -1 代表是之后添加的结点
    public NLPMapping(NLPToken token, double similar){
        this.belong = token;
        this.score = similar;
    }
}
