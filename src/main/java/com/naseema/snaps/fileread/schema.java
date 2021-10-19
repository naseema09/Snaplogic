/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2021, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */
package com.naseema.snaps.fileread;


import com.google.common.collect.ImmutableMap;
import com.snaplogic.api.InputSchemaProvider;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.*;
import com.snaplogic.snap.schema.api.Schema;
import com.snaplogic.snap.schema.api.SchemaBuilder;
import com.snaplogic.snap.schema.api.SchemaProvider;



import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.naseema.snaps.fileread.Message.SCHEMA_DESCRI;
import static com.naseema.snaps.fileread.Message.SCHEMA_LABEL;
import static com.naseema.snaps.fileread.Message.SNAP_PUR;
import static com.naseema.snaps.fileread.Message.SNAP_AUTHOR;
import static com.naseema.snaps.fileread.Message.SNAP_TITLE;


/**
 * InputSchemaProvider is a snap that is capable of getting the sucess or failure
 * by taking input of field or fieldtype which was gaven in snap code
 *
 * @author smudassir
 */
@General(title = SNAP_TITLE,author = SNAP_AUTHOR,purpose = SNAP_PUR,docLink = "https://mylink.com")
@Inputs(min = 0,max = 1,accepts = {ViewType.DOCUMENT})
@Outputs(min=1,max = 1,offers = {ViewType.DOCUMENT})
@Errors(min = 1,max = 1,offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@Category(snap= SnapCategory.READ)
public class schema extends SimpleSnap implements InputSchemaProvider {
    private static final String SCHEMA_KEY = "key";

    private static final String TYPE_INT="int";
    private static final String TYPE_STRING="string";
    private static final String TYPE_BOOLEAN="boolean";
    private static final Map<String, SnapType> TYPES=ImmutableMap.of(
            TYPE_INT,SnapType.INTEGER,
            TYPE_STRING,SnapType.STRING,
            TYPE_BOOLEAN,SnapType.BOOLEAN
    );
    private String schemas;
    private Throwable error;


    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe(SCHEMA_KEY, SCHEMA_LABEL, SCHEMA_DESCRI)
                .uiRowCount(10)
                .required()
                .add();

    }

    @Override
    public void configure(PropertyValues propertyValues){
        schemas = propertyValues.get(SCHEMA_KEY);

    }

    @Override
    protected void process(Document document, String s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Result",error == null ? "Success" : "Failure ="+error.getMessage());
        outputViews.write(documentUtility.newDocument(map));


    }

    @Override
    public void defineInputSchema(SchemaProvider schemaProvider) {
        try {

            Collection<String> view = schemaProvider.getRegisteredInputViewNames();
            if (!view.isEmpty()) {
                String views = view.iterator().next();
                SchemaBuilder builder = schemaProvider.getSchemaBuilder(views);
                String[] fields = schemas.split(System.lineSeparator());
                for (String sy : fields) {
                    String[] parts = sy.split("\\s+");
                    Schema schema = schemaProvider.createSchema(TYPES.get(parts[1]), parts[0]);
                    builder.withChildSchema(schema);

                }
                builder.build();
            }

        } catch(Throwable t) {
            error = t;
        }
    }
}
