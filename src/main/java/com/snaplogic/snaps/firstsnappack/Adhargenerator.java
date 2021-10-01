package com.snaplogic.snaps.firstsnappack;


import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.api.Snap;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.snap.view.InputView;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


@General(title = "Adhar Generator", purpose = "Generates Adhar",
        author = "naseema", docLink = "http://yourdocslinkhere.com")
@Inputs(min = 1, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap = SnapCategory.READ)
public class Adhargenerator implements Snap {
    private int id;
    private String name;
    private boolean nri;

    @Inject
    private OutputViews outputViews;
    @Inject
    private InputViews inputViews;
    @Inject
    private DocumentUtility documentUtility;

    @Override
    public void defineProperties(PropertyBuilder pb) {

        pb.describe("id","ID","id of indian national");
        pb.type(SnapType.INTEGER);
        pb.add();

        pb.describe("name","Name","Name of Indian");
        pb.add();

        pb.describe("nri","Nri","Is Not");
        pb.type(SnapType.BOOLEAN);
        pb.add();

    }
    @Override
    public void configure(PropertyValues propertyValues) {

        BigInteger bigInteger=propertyValues.get("id");
        if(bigInteger!=null){
            id=bigInteger.intValue();
        }
        name=propertyValues.get("name");
        nri=Boolean.TRUE.equals(propertyValues.get("nri"));
    }

    @Override
    public void execute() throws ExecutionException {
        UUID uuid=UUID.randomUUID();
        InputView inputView=inputViews.get();
        Iterator<Document> iterator=inputViews.getDocumentsFrom(inputView);
        while (iterator.hasNext()){
            Document document= iterator.next();

            Map<String,Object> record=new LinkedHashMap<>();
            if(nri){
                record.put("message",name+ "is not Indian");
            }
            else
            {
                record.put("message",name+ "an Indian" +id+ "has been generated adhar"+uuid.toString());
            }
            Document outputDocument=documentUtility.newDocument(record);
            outputViews.write(outputDocument);
        }

    }

    @Override
    public void cleanup() throws ExecutionException {

    }
}
