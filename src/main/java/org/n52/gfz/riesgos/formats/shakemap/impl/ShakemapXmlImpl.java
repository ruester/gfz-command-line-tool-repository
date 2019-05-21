/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 *
 *
 */

package org.n52.gfz.riesgos.formats.shakemap.impl;

import org.apache.xmlbeans.XmlObject;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapData;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapField;
import org.n52.gfz.riesgos.formats.shakemap.IShakemapSpecification;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation for the shakemap using the xml data
 */
public class ShakemapXmlImpl implements IShakemap {

    private static final QName INDEX = new QName("index");
    private static final QName NAME = new QName("name");
    private static final QName UNITS = new QName("units");

    private static final QName LAT_MAX = new QName("lat_max");
    private static final QName LAT_MIN = new QName("lat_min");
    private static final QName LON_MAX = new QName("lon_max");
    private static final QName LON_MIN = new QName("lon_min");
    private static final QName N_LAT = new QName("nlat");
    private static final QName N_LON = new QName("nlon");
    private static final QName NOMINAL_LAT_SPACING = new QName("nominal_lat_spacing");
    private static final QName NOMINAL_LON_SPACING = new QName("nominal_lon_spacing");
    private static final QName REGULAR_GRID = new QName("regular_grid");

    private static final String SCHEMA = "http://earthquake.usgs.gov/eqcenter/shakemap";
    private static final String GRID_DATA = "grid_data";
    private static final String GRID_FIELD = "grid_field";
    private static final String GRID_SPECIFICATION = "grid_specification";
    private static final String SHAKEMAP_GRID = "shakemap_grid";

    private final XmlObject shakemap;

    private final IShakemapSpecification specification;
    private final List<IShakemapField> fields;
    private final IShakemapField latField;
    private final IShakemapField lonField;
    private final List<IShakemapData> data;

    public ShakemapXmlImpl(final XmlObject shakemap) {
        this.shakemap = shakemap;

        this.specification = readSpecification();
        this.fields = readFields();

        // both must be there
        this.latField = fields.stream().filter(IShakemapField::isLat).findFirst().get();
        this.lonField = fields.stream().filter(IShakemapField::isLon).findFirst().get();

        this.data = readData();
    }


    @Override
    public List<IShakemapField> getFields() {
        return fields;
    }

    @Override
    public List<IShakemapData> getData() {
        return data;
    }

    @Override
    public IShakemapSpecification getSpecification() {
        return specification;
    }

    private IShakemapSpecification readSpecification() {
        final XmlObject specification = shakemap
                .selectChildren(SCHEMA, SHAKEMAP_GRID)[0]
                .selectChildren(SCHEMA, GRID_SPECIFICATION)[0];

        final double latMax = Double.parseDouble(specification.selectAttribute(LAT_MAX).newCursor().getTextValue());
        final double latMin = Double.parseDouble(specification.selectAttribute(LAT_MIN).newCursor().getTextValue());
        final double lonMax = Double.parseDouble(specification.selectAttribute(LON_MAX).newCursor().getTextValue());
        final double lonMin = Double.parseDouble(specification.selectAttribute(LON_MIN).newCursor().getTextValue());
        final int nLat = Integer.parseInt(specification.selectAttribute(N_LAT).newCursor().getTextValue());
        final int nLon = Integer.parseInt(specification.selectAttribute(N_LON).newCursor().getTextValue());
        final double nominalLatSpacing = Double.parseDouble(specification.selectAttribute(NOMINAL_LAT_SPACING).newCursor().getTextValue());
        final double nominalLonSpacing = Double.parseDouble(specification.selectAttribute(NOMINAL_LON_SPACING).newCursor().getTextValue());

        final XmlObject xmlRegularGrid = specification.selectAttribute(REGULAR_GRID);
        final boolean regularGrid =  xmlRegularGrid == null || Boolean.parseBoolean(xmlRegularGrid.newCursor().getTextValue().toLowerCase());

        return new ShakemapSpecificationImpl(
                latMax, latMin, lonMax, lonMin, nLat, nLon, nominalLatSpacing, nominalLonSpacing, regularGrid);
    }

    private List<IShakemapField> readFields() {
        final XmlObject[] xmlfields = shakemap
                .selectChildren(SCHEMA, SHAKEMAP_GRID)[0]
                .selectChildren(SCHEMA, GRID_FIELD);
        return Stream.of(xmlfields).map(this::parseXmlGridField).collect(Collectors.toList());
    }

    private IShakemapField parseXmlGridField(final XmlObject gridField) {
        final int index = Integer.parseInt(gridField.selectAttribute(INDEX).newCursor().getTextValue());
        final String name = gridField.selectAttribute(NAME).newCursor().getTextValue();
        final String units = gridField.selectAttribute(UNITS).newCursor().getTextValue();

        return new ShakemapFieldImpl(index, name, units);
    }

    private List<IShakemapData> readData() {

        final String grid = shakemap
                .selectChildren(SCHEMA, SHAKEMAP_GRID)[0]
                .selectChildren(SCHEMA, GRID_DATA)[0].newCursor().getTextValue();
        final StringTokenizer tokenizer = new StringTokenizer(grid);

        int fieldIndex = 0;

        final DataBuilder builder = new DataBuilder();

        final List<IShakemapData> result = new ArrayList<>();

        while(tokenizer.hasMoreElements()) {
            final String data = tokenizer.nextElement().toString();

            fieldIndex = fieldIndex % fields.size();

            if(fieldIndex == lonField.getIndex() - 1) {
                final double lon = Double.parseDouble(data);
                builder.setLon(lon);
            } else if(fieldIndex == latField.getIndex() - 1) {
                final double lat = Double.parseDouble(data);
                builder.setLat(lat);
            } else {
                final IShakemapField field = fields.get(fieldIndex);
                final double val = Double.parseDouble(data);

                builder.setCustom(field.getName(), val);
            }
            if(fieldIndex == fields.size() - 1) {
                final IShakemapData singleRow = builder.buildData();
                result.add(singleRow);
                builder.startNext();
            }
            fieldIndex += 1;
        }

        return result;
    }

    private class DataBuilder {
        double lat;
        double lon;

        Map<String, Double> customValues;

        DataBuilder() {
            customValues = new HashMap<>();
        }

        void setLon(final double lon) {
            this.lon = lon;
        }

        void setLat(final double lat) {
            this.lat = lat;
        }

        void setCustom(final String name, final double value) {
            customValues.put(name, value);
        }

        IShakemapData buildData() {
            return new ShakemapDataImpl(lon, lat, customValues);
        }

        void startNext() {
            lat = 0.0;
            lon = 0.0;
            // must be new, otherwise it would change the values in the data entry
            customValues = new HashMap<>();
        }
    }
}
