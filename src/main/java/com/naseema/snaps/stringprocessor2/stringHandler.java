package com.naseema.snaps.stringprocessor2;

import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;

import java.util.LinkedHashMap;
import java.util.Map;

@General(title = "String Processor2",author = "naseema",purpose = "snap extends simpesnap",docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0,max  =1,accepts = {ViewType.DOCUMENT})
@Outputs(min = 1,max = 1,offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)

public class stringHandler extends SimpleSnap {
    private String input;
    private boolean palindrome;

    @Override
    public void defineProperties(PropertyBuilder pb){
        pb.describe("key1","Input String " ," input to be processed");
        pb.add();
        pb.describe("key2","Palindrome","given input is palindrome or not");
        pb.type(SnapType.BOOLEAN);
        pb.add();
    }
    @Override
    public void configure(PropertyValues propertyValues){
        input = propertyValues.get("key1");
        palindrome = Boolean.TRUE.equals(propertyValues.get("key2"));
    }
    @Override
    protected void process(Document document, String s) {
        Map<String, Object> n = new LinkedHashMap<>();
        n.put("Reverse",new StringBuffer(input).reverse());
        n.put("Upper case" , input.toUpperCase());
        n.put("Lower Case", input.toLowerCase() );
        n.put("Length", input.length() );
        if (palindrome){
            n.put("isPalindrome",verifyIfPalindrome(input));

        }
        outputViews.write(documentUtility.newDocument(n));
    }
    private boolean verifyIfPalindrome(String input){
        String reverse=new StringBuffer(input).reverse().toString();
        return reverse.equals(input);
    }
}
