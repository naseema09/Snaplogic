package com.naseema.snaps.filereader;

import com.google.common.collect.ImmutableSet;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.util.JsonPathBuilder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;



@General(title = "LookUp",author = "naseema",purpose = "look up ",
        docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0,max = 1,accepts = {ViewType.DOCUMENT})
@Outputs(min=1,max = 1,offers = {ViewType.DOCUMENT})
@Errors(min = 1,max = 1,offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap= SnapCategory.READ)
public class LookUp extends SimpleSnap {

    static final Set<Integer> VALUE= ImmutableSet.of(1,2,3,4,5,6,7,8,9,10);
    private ExpressionProperty addrExpr;
    private ExpressionProperty idExpr;
    private String name;
    private BigInteger cla;
    private BigInteger grad;

    private BigInteger id;
    private String add;







    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe("key1", "ID", "student id")
                .required()
                .type(SnapType.INTEGER)
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .add();
        SnapProperty nameprop = propertyBuilder.describe("key2", "Name", "Student name")
                .required()
                .build();
        SnapProperty addrprop = propertyBuilder.describe("key3", "Address", "address in more info")
                .required()
                .expression(SnapProperty.DecoratorType.ACCEPTS_SCHEMA)
                .uiRowCount(10)
                .build();
        SnapProperty classprop = propertyBuilder.describe("key4", "Class", "classes")
                .withAllowedValues(VALUE)
                .type(SnapType.INTEGER)
                .build();
        SnapProperty gradeprop = propertyBuilder.describe("key5", "Grade", "grade of student")
                .required()
                .type(SnapType.INTEGER)
                .build();
        List<SnapProperty> prop = new ArrayList<>();
        prop.add(addrprop);
        prop.add(nameprop);
        propertyBuilder.describe("key6", "More Info", "more information")
                .type(SnapType.COMPOSITE)
                .withEntries(prop)
                .add();
        propertyBuilder.describe("key7","Preformance Info","performance info of student")
                .type(SnapType.COMPOSITE)
                .withEntry(classprop)
                .withEntry(gradeprop)
                .add();

    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {

        idExpr =propertyValues.getAsExpression("key1");
        name = propertyValues.get("key6.value.key2");
        addrExpr=propertyValues.getAsExpression("key6.value.key3");
        String keypath1 = new JsonPathBuilder("key7")
                .appendValueElement().appendCurrentElement("key4")
                .build();
        cla =propertyValues.get(keypath1);
        String keypath2= new JsonPathBuilder("key7")
                .appendValueElement().appendCurrentElement("key5")
                .build();
        grad =propertyValues.get(keypath2);


    }
    @Override
    protected void process(Document document, String s) {

        String fileName = "/home/gaian/Training/files/csv.csv";

        id = idExpr.eval(document);
        add=addrExpr.eval(document);


        try {
            CSVParser csvParser =  new CSVParser(new FileReader(fileName), CSVFormat.DEFAULT.withHeader());

            Map<String,Object> map=new LinkedHashMap<>();
            for (CSVRecord csvRecord : csvParser) {

                //new BigInteger(csvRecord.get("ID")))==id&&
                //                        csvRecord.get("NAME").toLowerCase()==name.toLowerCase()&&
                //                        csvRecord.get("ADDRESS").toLowerCase()==add.toLowerCase()&&
                //                        new BigInteger(csvRecord.get("CLASS"))==cla&&
                //                        new BigInteger(csvRecord.get("GRADE"))==grad)
                String stdid = csvRecord.get("ID");
                String stdname = csvRecord.get("NAME");
                String stdaddr = csvRecord.get("ADDRESS").replaceAll("\\s","");
                String stdcls = csvRecord.get("CLASS");
                String stdgrade = csvRecord.get("GRADE");

                if (Integer.parseInt(stdid)==id.intValue() && stdaddr.equalsIgnoreCase(add.
                        replaceAll("\\s","")) &&
                        Integer.parseInt(stdcls)==cla.intValue() && stdname.equalsIgnoreCase(name) &&
                        stdgrade.equalsIgnoreCase(String.valueOf(grad))) {
                    map.put("Message", " Record Already Exists");
                    outputViews.write(documentUtility.newDocument(map));
                    return;
                }
            }
            map.put("Message", " Record Appended ");
            outputViews.write(documentUtility.newDocument(map));
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
