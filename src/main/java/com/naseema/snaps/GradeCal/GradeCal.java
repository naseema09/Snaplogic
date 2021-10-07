package com.naseema.snaps.GradeCal;

import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;



@General(title = "Grade Calculator",author = "naseema",purpose = "calculating grade",docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0,max  =1,accepts = {ViewType.DOCUMENT})
@Outputs(min = 1,max = 1,offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)

public class GradeCal extends SimpleSnap {
    private int id;
    private String name;
    private boolean senior;
    private int mark1,mark2,mark3;
    private int percentage;
    private String grade;


    @Override
    public void defineProperties(PropertyBuilder pb){
        pb.describe("id","ID","Student id");
        pb.type(SnapType.INTEGER);
        pb.add();
        pb.describe("name","Name","student name");
        pb.add();
        pb.describe("senior","Senior" ,"Senoir or junior for getting grades");
        pb.type(SnapType.BOOLEAN);
        pb.add();
        pb.describe("mark1","Marks sub1","1st subject marks out of 100");
        pb.type(SnapType.INTEGER);
        pb.add();
        pb.describe("mark2","Marks sub2","subject marks 2 out of 100");
        pb.type(SnapType.INTEGER);
        pb.add();
        pb.describe("mark3","Marks sub3","subject marks 3 out of 100");
        pb.type(SnapType.INTEGER);
        pb.add();

    }
    @Override
    public void configure(PropertyValues propertyValues){
        BigInteger bigInteger=propertyValues.get("id");
        if(bigInteger!=null){
            id=bigInteger.intValue();
        }
         name = propertyValues.get("name");
        senior = Boolean.TRUE.equals(propertyValues.get("senior"));
        BigInteger bigInteger1=propertyValues.get("mark1");
        if(bigInteger1!=null){
            mark1=bigInteger1.intValue();
        }
        BigInteger bigInteger2=propertyValues.get("mark2");
        if(bigInteger2!=null){
            mark2=bigInteger2.intValue();
        }
        BigInteger bigInteger3=propertyValues.get("mark3");
        if(bigInteger3!=null){
            mark3=bigInteger3.intValue();
        }



    }


    @Override
    protected void process(Document document, String s) {
        Map<String,Object> map=new LinkedHashMap<>();
        map.put("ID-NAME",+id+"-"+name);
        map.put("Percentage",getPercentage());
        if(senior)
        {
            map.put("Grade",getgrade());
        }

        outputViews.write(documentUtility.newDocument(map));
    }
    private int getPercentage()
    {
        percentage = (mark1+mark2+mark3) /3;
        return percentage;
    }
    private String getgrade() {

        if (percentage >= 90) {
            grade = "A";
        }
        else if (percentage > 70 && percentage < 90) {
            grade = "B";
        }
        else {
            grade = "C";
        }
        return grade;
    }
}
