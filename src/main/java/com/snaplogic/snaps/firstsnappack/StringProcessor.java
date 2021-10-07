package com.snaplogic.snaps.firstsnappack;


import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.api.Snap;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.snap.view.InputView;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@General(title = "String Processor", purpose = "Generates uppercase",
        author = "naseema", docLink = "http://yourdocslinkhere.com")
// This Snap does not permit input views for demonstration purposes, however, in practice, there
// should almost always be an input view
@Inputs(min = 1, max = 1, accepts = {ViewType.DOCUMENT})
// This snap has exactly one document output view (min = 1, max = 1).
// Snaps can also have binary output i.e., offers={ViewType.BINARY}
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
// This snap has an optional document error view(min = 1, max = 1).
// Snaps can also have binary error view i.e., offers={ViewType.BINARY}
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
// Version number of the snap
@Version(snap = 1)
// This snap belongs to SnapCategory.READ as it is idempotent.
@Category(snap = SnapCategory.READ)

public class StringProcessor implements Snap {
    private String string;

    @Inject
    private OutputViews outputViews;
    @Inject
    private InputViews inputViews;
    @Inject
    private DocumentUtility documentUtility;



    @Override
    public void defineProperties(PropertyBuilder pb) {
        pb.describe("string" ,"STRING","ENTER STRING");
        pb.add();

    }

    @Override
    public void configure(PropertyValues propertyValues) throws ConfigurationException {
        string= propertyValues.get("string");
    }

    @Override
    public void execute() throws ExecutionException {
        InputView inputView = inputViews.get();
        Iterator<Document> iterator = inputViews.getDocumentsFrom(inputView);

        Map<String, Object> record = null;
        while (iterator.hasNext()) {
            Document document = iterator.next();
            record = new LinkedHashMap<>();
            record.put("UPPERCASE", " "+ string.toUpperCase());
            record.put("LOWERCASE"," " +string.toLowerCase());
            record.put("LENGTH",  + string.length());
            StringBuilder string1= new StringBuilder();
            string1.append(string);
            record.put("REVERSE", " "+string1.reverse());
        }
        Document outputdocument = documentUtility.newDocument(record);
        outputViews.write(outputdocument);

    }

    @Override
    public void cleanup() throws ExecutionException {

    }
}
