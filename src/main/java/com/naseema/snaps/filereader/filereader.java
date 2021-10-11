package com.naseema.snaps.filereader;



import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Immutable;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@General(title = "File Read",author = "naseema",purpose = "file reading either by file path or directory",
        docLink = "http://yourdocslinkhere.com")
@Inputs(min = 0,max = 1,accepts = {ViewType.DOCUMENT})
@Outputs(min=1,max = 1,offers = {ViewType.DOCUMENT})
@Errors(min = 1,max = 1,offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap= SnapCategory.READ)
public class filereader extends SimpleSnap {


    //private static final String FILE_BROWSER_PROP = "file_browser_prop";
    //private static final String PASSWORD_PROP_VALUE = "abc";

    //String fileBrowser;
    private String file1;
    private boolean attr;
    private String value;
    private String path1;


    /*public String getFileBrowser() {
        return fileBrowser;
    }*/
    static final String FILE = "File";
    static final String DIR = "Directory";
    static final String READ = "Read";
    static final String DLT = "Delete";
    static final Set<String> VALUES = ImmutableSet.of(FILE, DIR);
    static final Set<String> VALUES1 = ImmutableSet.of(READ, DLT);

    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe("file", "Object Type", "entering the file path");
        propertyBuilder.withAllowedValues(VALUES);
        propertyBuilder.defaultValue(DIR);
        propertyBuilder.add();

        propertyBuilder.describe("path", "Object path", "Pathe of the file ")
                .add();

        propertyBuilder.describe("operation", "Operation", "operation for read or dlt")
                .withAllowedValues(VALUES1)
                .defaultValue(READ)
                .add();

        propertyBuilder.describe("display", "Display Attributes", "display of attribute")
                .type(SnapType.BOOLEAN)
                .defaultValue(true)
                .add();



        /*propertyBuilder.describe(FILE_BROWSER_PROP, FILE_BROWSER_PROP)
                .required()
                .defaultValue(PASSWORD_PROP_VALUE)
                .fileBrowsing()
                .add();*/

    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {

        //fileBrowser = propertyValues.getAsExpression(FILE_BROWSER_PROP).eval(null);
        file1 = propertyValues.get("file");
        path1 = propertyValues.get("path");
        value = propertyValues.get("operation");
        attr = Boolean.TRUE.equals(propertyValues.get("display"));


    }

    @Override
    protected void process(Document document, String s) {


        Map<String, Object> map = new LinkedHashMap<>();
        File reads = new File(path1);


        BasicFileAttributes attrs;
        //map.put(FILE_BROWSER_PROP, getFileBrowser());
        if (file1.equals(FILE)) {
            if (value.equals(READ)) {
                if (reads.isFile()) {

                        Scanner sc = null;
                        StringBuffer sb = new StringBuffer();
                        String input;
                            sc = new Scanner(path1);
                            while (sc.hasNextLine()) {
                                input = sc.nextLine();
                                sb.append(input + " ");
                            }
                            map.put("Contents of the file: ", sb.toString());

                        if (attr) {


                            Date date = new Date(reads.lastModified());
                            map.put("Last Modified Date", date);
                            map.put("Size in bytes", reads.length());
                            try {
                            attrs = Files.readAttributes(reads.toPath(), BasicFileAttributes.class);
                            FileTime time = attrs.creationTime();

                            String pattern = "yyyy-MM-dd HH:mm:ss";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                            String formatted = simpleDateFormat.format(new Date(time.toMillis()));

                            map.put("The file creation date and time is: ", formatted);
                        }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }
                else {
                    map.put("Given File Path", reads + " is invalid");
                }

            } else if (value.equals(DLT)) {

                reads.delete();
                map.put("Sucessfully deleted given", reads + " file");

            }

        } else if (file1.equals(DIR)) {
            if (value.equals(READ)) {


                File directoryPath = new File(path1);
                //List of all files and directories
                File filesList[] = directoryPath.listFiles();

                Scanner sc = null;
                StringBuffer sb = new StringBuffer();
                String input;
                List<String> list=new ArrayList<>();
                for (File file : filesList) {

                    list.add(file.getName());
                    list.add(file.getAbsolutePath());
                    list.add(String.valueOf(file.getTotalSpace()));

                    //Instantiating the Scanner class
                    try {
                        sc = new Scanner(file);


                        while (sc.hasNextLine()) {
                            input = sc.nextLine();
                            sb.append(input + " ");
                        }
                        map.put("Contents of the file: ", sb.toString());

                        if (attr) {
                            Date date = new Date(directoryPath.lastModified());
                            map.put("Last Modified Date", date);

                            attrs = Files.readAttributes(reads.toPath(), BasicFileAttributes.class);

                            FileTime time = attrs.creationTime();

                            String pattern = "yyyy-MM-dd HH:mm:ss";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                            String formatted = simpleDateFormat.format(new Date(time.toMillis()));

                            map.put("The file creation date and time is: ", formatted);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                map.put("File attributes",list);
            }
            else if (value.equals(DLT)) {

                reads.delete();
                map.put("Sucessfully deleted given", reads + " directory");



            }


            outputViews.write(documentUtility.newDocument(map));
        }
    }
}
