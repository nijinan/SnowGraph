package NlpInterface.config;

import apps.Config;
import org.apache.commons.io.FileUtils;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopWords {
    private static EnglishStemmer stemmer = new EnglishStemmer();
    public static Set<String> englishStopWords = new HashSet<>();
    static {
        List<String> lines=new ArrayList<>();
        try {
            lines= FileUtils.readLines(new File(Config.class.getResource("/").getPath()+"stopwords_lcy.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        englishStopWords.addAll(lines);
        lines.forEach(n->{
            stemmer.setCurrent(n);
            stemmer.stem();
            englishStopWords.add(stemmer.getCurrent());
        });
    }
    public static boolean isStopWord(String word){
        stemmer.setCurrent(word);
        stemmer.stem();
        String s = stemmer.getCurrent();
        return englishStopWords.contains(word) || englishStopWords.contains(s);
    }
}
