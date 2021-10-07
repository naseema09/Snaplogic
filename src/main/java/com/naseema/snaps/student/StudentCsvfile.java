package com.naseema.snaps.student;

import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.snap.schema.api.ObjectSchema;
import com.snaplogic.snap.schema.api.SchemaProvider;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@General(title = "CSV FILE READER", purpose = "Generates documents based on the configuration",
        author = "Your Company Name", docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class StudentCsvfile extends SimpleSnap {
    //private static final String FILE_BROWSER_PROP = "file_browser_prop";
    //private static final String PASSWORD_PROP_VALUE = "abc";

    //String fileBrowser;


    private String file;
    private Integer year1;
    private Integer percentage1;





    /*public String getFileBrowser() {
        return fileBrowser;
    }*/

    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe("file","File Path","entering the file path");
        propertyBuilder.required();
        propertyBuilder.add();
        propertyBuilder.describe("year1","YEAR","entering the year of student");
        propertyBuilder.required();
        propertyBuilder.type(SnapType.INTEGER);
        propertyBuilder.add();
        propertyBuilder.describe("percentage1","PERCENTAGE","entering the percentage of student");
        propertyBuilder.required();
        propertyBuilder.type(SnapType.INTEGER);
        propertyBuilder.add();

        /*propertyBuilder.describe(FILE_BROWSER_PROP, FILE_BROWSER_PROP)
                .required()
                .defaultValue(PASSWORD_PROP_VALUE)
                .fileBrowsing()
                .add();*/
    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        //fileBrowser = propertyValues.getAsExpression(FILE_BROWSER_PROP).eval(null);

            file=propertyValues.get("file");
        BigInteger bigInteger=propertyValues.get("year1");
        if(bigInteger!=null){
            year1=bigInteger.intValue();
        }
        BigInteger bigInteger1=propertyValues.get("percentage1");
        if(bigInteger!=null){
            percentage1=bigInteger1.intValue();
        }



    }
    @Override
    protected void process(Document document, String s) {

        //map.put(FILE_BROWSER_PROP, getFileBrowser());
        String path=file;

        try {
            BufferedReader br =Files.newBufferedReader(Paths.get(path));
            CSVParser csvParser=new CSVParser(br, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());


            Map<String, List<String>> map=new LinkedHashMap<>();
            map.put("ID",new ArrayList<String>());
            map.put("Name",new ArrayList<String>());
            map.put("Year",new ArrayList<String>());
            map.put("Percentage",new ArrayList<String>());
            for(CSVRecord csvRecord: csvParser){
                String id=csvRecord.get("ID");
                String name=csvRecord.get("Name");
                String year=csvRecord.get("Year");
                String percentage=csvRecord.get("Percentage");
                if(Integer.parseInt(percentage)> percentage1.intValue()&& Integer.parseInt(year) == year1.intValue() ){
                    map.get("ID").add(id);
                    map.get("Name").add(name);
                    map.get("Year").add(year);
                    map.get("Percentage").add(percentage);

                }
            }
            outputViews.write(documentUtility.newDocument(map));
        }
        catch (IOException e){
            e.printStackTrace();

        }


    }
}
