package NlpInterface.entity;

import NlpInterface.entity.TokenMapping.NLPMapping;

public class NLPToken {
    public String text;
    public NLPMapping mapping;
    public long offset = -1;
    public NLPToken(String text){
        this.text = text;
    }
    public boolean isQWord(){
        return false;
    }
}
