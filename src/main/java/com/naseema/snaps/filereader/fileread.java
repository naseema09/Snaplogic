package com.naseema.snaps.fileread;

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
public class fileread extends SimpleSnap {


    //private static final String FILE_BROWSER_PROP = "file_browser_prop";
    //private static final String PASSWORD_PROP_VALUE = "abc";

    //String fileBrowser;
    private String file;
    private boolean attr;
    private String value;
    private String path;


    /*public String getFileBrowser() {
        return fileBrowser;
    }*/
    static final String FILE = "file";
    static final String DIR = "directory";
    static final String READ = "Read";
    static final String DLT = "Delete";
    static final Set<String> VALUES = ImmutableSet.of(FILE, DIR);
    static final Set<String> VALUES1 = ImmutableSet.of(READ, DLT);

    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe("file", "File Path Dropdown", "entering the file path");
        propertyBuilder.withAllowedValues(VALUES);
        propertyBuilder.defaultValue(DIR);
        propertyBuilder.add();

        propertyBuilder.describe("path", "File path", "Pathe of the file ")
                .fileBrowsing()
                .add();

        propertyBuilder.describe("operation", "Operation", "operation for read or dlt")
                .withAllowedValues(VALUES1)
                .defaultValue(READ)
                .add();

        propertyBuilder.describe("display", "Display", "display of attribute")
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
        file = propertyValues.get("file");
        path = propertyValues.get("path");
        value = propertyValues.get("operation");
        attr = Boolean.TRUE.equals(propertyValues.get("display"));


    }

    @Override
    protected void process(Document document, String s) {


        Map<String, Object> map = new LinkedHashMap<>();
        File reads = new File(file);
        Path paths = Paths.get(file);
        BasicFileAttributes attrs;
        //map.put(FILE_BROWSER_PROP, getFileBrowser());
        if (file.equals(FILE)) {
            if (value.equals(READ)) {

                if (reads.isFile()) {

                    try {
                        Stream<String> lines = Files.lines(paths);
                        lines.forEach(s1 -> map.put("Content of File", s1));
                        if (attr) {
                            map.put("Last Modified Date", reads.lastModified());
                            map.put("Size in bytes", reads.length());
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
                } else {
                    map.put("Given File Path", reads + " is in valid");
                }

            } else if (value.equals(DLT)) {
                reads.deleteOnExit();
                map.put("Sucessfully deleted given", reads + " file");
                try {
                    if (attr) {
                        map.put("Last Modified Date", reads.lastModified());
                        map.put("Size in bytes", reads.length());

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

        } else if (file.equals(DIR)) {
            File dir = new File(file);
            //List of all files and directories
            File filesList[] = dir.listFiles();
            map.put("List of files and directories in the specified directory:", " ");
            for (File file : filesList) {
                map.put("File name: ", file.getName());
                map.put("File path: ", file.getAbsolutePath());
                map.put(" ", "");
                try {
                    if (attr) {
                        map.put("Last Modified Date", reads.lastModified());


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

        }
        outputViews.write(documentUtility.newDocument(map));
    }
}
