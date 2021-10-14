
package com.naseema.snaps.Counter;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;



@General(title = "Counter",author = "naseema",purpose = "Counting of characters and words",docLink
        = "http://yourdocslinkhere.com")
@Inputs(min = 0,max = 0,accepts = {ViewType.DOCUMENT})
@Outputs(min = 1,max = 1,offers = {ViewType.DOCUMENT})
@Errors(min = 0,max = 1,offers = {ViewType.DOCUMENT})
@Category(snap = SnapCategory.READ)
@Version(snap = 1)
public class Counter extends SimpleSnap {
    private String sentence;
    private Boolean reverse;
    private String str;

    static final String CHAR = "Characters";
    static final String WORD = "Words";
    static final String BOTH = "Characters and Words";
    static final Set<String> VALUES = ImmutableSet.of(CHAR, WORD, BOTH);


    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe("sentence", "Sentence", "Write a String of Characters or Words");
        propertyBuilder.defaultValue("abcdefghijklmnopqrstuvwxyz");
        propertyBuilder.add();

        propertyBuilder.describe("reverse", "Reverse", "Want to Reverse The Given String")
                .type(SnapType.BOOLEAN)
                .defaultValue("true")
                .add();

        propertyBuilder.describe("count", "Count", "Count of what Characters or words")
                .withAllowedValues(VALUES)
                .defaultValue(BOTH)
                .add();

    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        sentence = propertyValues.get("sentence");
        reverse = Boolean.TRUE.equals(propertyValues.get("reverse"));
        str = propertyValues.get("count");


    }

    @Override
    protected void process(Document document, String s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("SENTENCE", sentence);
        if (reverse) {


            map.put("REVERSE ", new StringBuffer(sentence).reverse());
        }

        if (str == null || str.isEmpty()) {
            map.put("please enter the string",sentence);
        }
        else if (str.equals(BOTH)) {
            map.put("Number of Characters ", characterCount(sentence));
            map.put(" Number of Words", wordCountUsingSplit(sentence));



        } else if (str.equals(WORD)) {

            map.put("Number of Words count", wordCountUsingSplit(sentence));

        } else {
            map.put("Number of Characters", characterCount(sentence));


        }
        outputViews.write(documentUtility.newDocument(map));


    }

    private int wordCountUsingSplit(String sentence) {

        String[] words = sentence.split("\\s+");
        return words.length;


    }
    private int characterCount(String sentence){
        return sentence.replace(" ","").length();
    }
}
