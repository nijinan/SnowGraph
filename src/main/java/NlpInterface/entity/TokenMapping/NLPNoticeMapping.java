package NlpInterface.entity.TokenMapping;


import NlpInterface.entity.NLPToken;
import NlpInterface.extractmodel.Vertex;
import NlpInterface.ir.LuceneSearchResult;
import NlpInterface.schema.GraphVertexType;

import java.util.ArrayList;
import java.util.List;

public class NLPNoticeMapping extends NLPMapping{
    public List<LuceneSearchResult> list;
    public NLPNoticeMapping(List<LuceneSearchResult>l) {
        list = list = new ArrayList<>();
        list.addAll(l);
    }
}
