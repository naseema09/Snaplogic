package com.naseema.snaps.filereader;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@General(title = "Expression Property",author = "naseema",purpose = "Expression property view",
        docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0,max = 1,accepts = {ViewType.DOCUMENT})
@Outputs(min=1,max = 1,offers = {ViewType.DOCUMENT})
@Errors(min = 1,max = 1,offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap= SnapCategory.READ)
public class ExpressionProp  extends SimpleSnap {
    private static final  String DEV = "Dev";
    private static final String QA="Qa";
    private static final String DOC="Doc";
    private static final String SUPPORT="Support";
    private static final String PS="Ps";
    private static final String ACTIVE="active";

    static final Set<String> SET= ImmutableSet.of(DEV,QA,DOC,SUPPORT,PS);

    //upstream expression declare
    private ExpressionProperty addrExpr;
    private ExpressionProperty empsExpr;
    private String id;
    private String name;
    private Boolean active;
    private String addr;
    private BigInteger emps;
    private String rate;


    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe("id","Org ID","id with dropdown value")
                .withAllowedValues(SET)
                .defaultValue(DEV)
                .add();
        propertyBuilder.describe("name","Org Name","name of the organisation")
                .expression()
                .add();
        propertyBuilder.describe("addr","Address","address of the organisation")
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .add();
        propertyBuilder.describe("active","Active","boolean active emplys or not")
                .expression()
                .type(SnapType.BOOLEAN)
                .defaultValue(true)
                .add();
        propertyBuilder.describe("emps","Num of Emp","Number of employees in organisation")
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .type(SnapType.INTEGER)
                .add();

    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        id=propertyValues.get("id");
        name=propertyValues.get("name");
        addrExpr=propertyValues.getAsExpression("addr");
        active=propertyValues.getBoolean(ACTIVE,true);
        empsExpr=propertyValues.getAsExpression("emps");



    }
    @Override
    protected void process(Document document, String s) {
        Map<String,Object> map=new LinkedHashMap<>();
        map.put("ORG ID",id);
        map.put("ORG Name",name);
        addr=addrExpr.eval(document);
        map.put("ORG Address",addr);
        emps=empsExpr.eval(document);
        map.put("Num of Employees",emps);
        if (active && emps!=null){
            map.put("Ratings",getRatings());
        }
        outputViews.write(documentUtility.newDocument(map));



    }
    private String getRatings()
    {
        if (emps.intValue()>=100){
            rate = "A";
        }
        else if (emps.intValue()>=50){
            rate ="B";
        }
        else {
            rate ="C";
        }
        return rate;
    }

}
