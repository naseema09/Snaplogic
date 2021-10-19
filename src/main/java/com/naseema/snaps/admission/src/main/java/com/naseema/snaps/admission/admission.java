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
package com.naseema.snaps.admission;


import com.google.common.collect.ImmutableSet;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.Suggestions;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.common.properties.builders.SuggestionBuilder;
import com.snaplogic.snap.api.*;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.ViewType;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.naseema.snaps.admission.Messages.ADM_YEAR;
import static com.naseema.snaps.admission.Messages.ADM_DESC;
import static com.naseema.snaps.admission.Messages.KEY2_DESC;
import static com.naseema.snaps.admission.Messages.KEY2_SP;
import static com.naseema.snaps.admission.Messages.KEY3_DESC;
import static com.naseema.snaps.admission.Messages.KEY3_GOAL;
import static com.naseema.snaps.admission.Messages.SNAP_TITLE;
import static com.naseema.snaps.admission.Messages.SNAP_PURPOSE;

/**
 * Snap to read the given input by use of Suggestions
 *
 * @author naseema
 */
@General(title = SNAP_TITLE,author = "naseema",purpose = SNAP_PURPOSE,docLink = "http://doclink.com")
@Inputs(min = 0,max = 1,accepts = ViewType.DOCUMENT)
@Outputs(min=1,max = 1,offers = ViewType.DOCUMENT)
@Category(snap = SnapCategory.READ)
@Version(snap = 1)

public class admission extends SimpleSnap {
    public static String ADM_KEY="key1";
    public static String ADM_KEY2="key2";
    public static String ADM_KEY3="key3";
    private static final Integer ONE=1;
    private static final Integer TWO=2;
    private static final Integer THREE=3;
    private static final Integer FOUR=4;

    private static final String CSE="CSE";
    private static final String ECE="ECE";
    private static final String CIVIL="CIVIL";
    private static final String MECH="MECHANICAL";
    private static final String IT="INFORMATION TECHNOLOGY";
    private static final String EEE="EEE";

    static final Set<Integer> VALUES= ImmutableSet.of(ONE,TWO,THREE,FOUR);

    private BigInteger key1;
    private String key2;
    private String key3;


    @Override
    public void defineProperties(PropertyBuilder propertyBuilder) {
        propertyBuilder.describe(ADM_KEY,ADM_YEAR,ADM_DESC)
                .withAllowedValues(VALUES)
                .type(SnapType.INTEGER)
                .add();
        propertyBuilder.describe(ADM_KEY2,KEY2_SP,KEY2_DESC)
                .withSuggestions(new Suggestions() {
                    @Override
                    public void suggest(SuggestionBuilder suggestionBuilder, PropertyValues propertyValues) {
                        suggestionBuilder
                                .node(ADM_KEY2)
                                .suggestions(new String[] {CSE,ECE,CIVIL,MECH,IT,EEE});
                    }
                })
                .add();
        propertyBuilder.describe(ADM_KEY3,KEY3_GOAL,KEY3_DESC)
                .withSuggestions(new Suggestions() {
                    @Override
                    public void suggest(SuggestionBuilder suggestionBuilder, PropertyValues propertyValues) {
                            String name = propertyValues.get(ADM_KEY2);
                            if(name.equals(CSE)) {
                                suggestionBuilder.node(ADM_KEY3).suggestions(new String[]{"DEV","QA"});
                            }
                            else if(name.equals(ECE)) {
                                suggestionBuilder.node(ADM_KEY3).suggestions(new String[]{"DESIGNER","PCB"});
                            }
                            else if(name.equals(EEE)) {
                                suggestionBuilder.node(ADM_KEY3).suggestions(new String[]{
                                        "Electrical","SMART DEVICES"});
                            }
                            else if(name.equals(CIVIL)) {
                                suggestionBuilder.node(ADM_KEY3).suggestions(new String[]{"Designer","Build"});
                            }
                        }
                })
                .add();

    }

    @Override
    public void configure(PropertyValues propertyValues) {
        key1=propertyValues.get(ADM_KEY);
        key2=propertyValues.get(ADM_KEY2);
        key3=propertyValues.get(ADM_KEY3);


    }

    @Override
    protected void process(Document document, String s) {
        Map<String,Object> map=new LinkedHashMap<>();
        map.put("ADMISSION YEAR",key1);
        map.put("SPECIALISATION",key2);
        map.put("GOAL",key3);
        outputViews.write(documentUtility.newDocument(map));


    }
}
