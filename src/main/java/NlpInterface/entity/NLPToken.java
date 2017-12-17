package NlpInterface.entity;

import NlpInterface.entity.TokenMapping.NLPMapping;

import java.util.ArrayList;
import java.util.List;

public class NLPToken {
    public String text;
    public NLPMapping mapping;
    public List<NLPMapping> mappingList = new ArrayList<>();
    public String POS;
    public String NE;

    public long offset = -1;
    public NLPToken(String text){
        this.text = text;
    }
    public NLPToken(String text, String POS, String NE){
        this.text = text;
        this.POS = POS;
        this.NE = NE;
    }
    public boolean isQWord(){
        return false;
    }
}
