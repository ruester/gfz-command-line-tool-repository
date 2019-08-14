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
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation for the shakemap using the xml data.
 */
public class ShakemapXmlImpl implements IShakemap {

    /**
     * QName for the index attribute.
     */
    private static final QName INDEX = new QName("index");
    /**
     * QName for the name attribute.
     */
    private static final QName NAME = new QName("name");
    /**
     * QName for the units attribute.
     */
    private static final QName UNITS = new QName("units");

    /**
     * QName of the lat_max attribute.
     */
    private static final QName LAT_MAX = new QName("lat_max");
    /**
     * QName of the lat_min attribute.
     */
    private static final QName LAT_MIN = new QName("lat_min");
    /**
     * QName of the lon_max attribute.
     */
    private static final QName LON_MAX = new QName("lon_max");
    /**
     * QName of the lon_min attribute.
     */
    private static final QName LON_MIN = new QName("lon_min");
    /**
     * QName of the nlat attribute.
     */
    private static final QName N_LAT = new QName("nlat");
    /**
     * QName of the nlon attribute.
     */
    private static final QName N_LON = new QName("nlon");
    /**
     * QName of the nominal_lat_spacing attribute.
     */
    private static final QName NOMINAL_LAT_SPACING =
            new QName("nominal_lat_spacing");
    /**
     * QName of the nominal_lon_spacing attribute.
     */
    private static final QName NOMINAL_LON_SPACING =
            new QName("nominal_lon_spacing");
    /**
     * QName of the regular_grid attribute.
     */
    private static final QName REGULAR_GRID =
            new QName("regular_grid");

    /**
     * Schema / XML-Namespace for the shakemap.
     */
    private static final String SCHEMA =
            "http://earthquake.usgs.gov/eqcenter/shakemap";
    /**
     * Name of the grid_data tag.
     */
    private static final String GRID_DATA = "grid_data";
    /**
     * Name of the grid_field tag.
     */
    private static final String GRID_FIELD = "grid_field";
    /**
     * Name of the grid_specification tag.
     */
    private static final String GRID_SPECIFICATION = "grid_specification";

    /**
     * Name of the shakemap_grid tag.
     */
    private static final String SHAKEMAP_GRID = "shakemap_grid";

    /**
     * Xml object with the actual shakemap.
     */
    private final XmlObject shakemap;

    /**
     * Field with the specification of the shakemap.
     */
    private final IShakemapSpecification specification;
    /**
     * Fields of the shakemap.
     */
    private final List<IShakemapField> fields;
    /**
     * Field with the latitude column of the shakemap.
     */
    private final IShakemapField latField;

    /**
     * Field with the longitude column of the shakemap.
     */
    private final IShakemapField lonField;
    /**
     * List with all the points with the data in
     * the shakemap.
     */
    private final List<IShakemapData> data;

    /**
     * Default constructor.
     * @param aShakemap xml element with the shakemap
     */
    public ShakemapXmlImpl(final XmlObject aShakemap) {
        this.shakemap = aShakemap;

        this.specification = readSpecification();
        this.fields = readFields();

        // both must be there
        final Optional<IShakemapField> optionalLatField =
                fields.stream().filter(IShakemapField::isLat).findFirst();
        final Optional<IShakemapField> optionalLonField =
                fields.stream().filter(IShakemapField::isLon).findFirst();
        if (!optionalLatField.isPresent() || !optionalLonField.isPresent()) {
            throw new IllegalArgumentException(
                    "There must be fields for lat and lon");
        }
        this.latField = optionalLatField.get();
        this.lonField = optionalLonField.get();

        this.data = readData();
    }


    /**
     *
     * @return fields (columns) of the shakemap
     */
    @Override
    public List<IShakemapField> getFields() {
        return fields;
    }

    /**
     *
     * @return data (points) of the shakemap
     */
    @Override
    public List<IShakemapData> getData() {
        return data;
    }

    /**
     *
     * @return specification of the shakemap grid
     */
    @Override
    public IShakemapSpecification getSpecification() {
        return specification;
    }

    /**
     * Method to read the specification of the shakemap grid.
     * @return IShakemapSpecification
     */
    private IShakemapSpecification readSpecification() {
        final XmlObject localSpecification = shakemap
                .selectChildren(SCHEMA, SHAKEMAP_GRID)[0]
                .selectChildren(SCHEMA, GRID_SPECIFICATION)[0];

        final double latMax = Double.parseDouble(
                localSpecification.selectAttribute(LAT_MAX)
                        .newCursor().getTextValue());
        final double latMin = Double.parseDouble(
                localSpecification.selectAttribute(LAT_MIN)
                        .newCursor().getTextValue());
        final double lonMax = Double.parseDouble(
                localSpecification.selectAttribute(LON_MAX)
                        .newCursor().getTextValue());
        final double lonMin = Double.parseDouble(
                localSpecification.selectAttribute(LON_MIN)
                        .newCursor().getTextValue());
        final int nLat = Integer.parseInt(
                localSpecification.selectAttribute(N_LAT)
                        .newCursor().getTextValue());
        final int nLon = Integer.parseInt(
                localSpecification.selectAttribute(N_LON)
                        .newCursor().getTextValue());
        final double nominalLatSpacing = Double.parseDouble(
                localSpecification.selectAttribute(NOMINAL_LAT_SPACING)
                        .newCursor().getTextValue());
        final double nominalLonSpacing = Double.parseDouble(
                localSpecification.selectAttribute(NOMINAL_LON_SPACING)
                        .newCursor().getTextValue());

        final XmlObject xmlRegularGrid =
                localSpecification.selectAttribute(REGULAR_GRID);
        final boolean regularGrid =
                xmlRegularGrid == null
                        || Boolean.parseBoolean(
                                xmlRegularGrid
                                        .newCursor()
                                        .getTextValue()
                                        .toLowerCase());

        return new ShakemapSpecificationImpl(
                new LatLonRange(latMin, latMax),
                new LatLonRange(lonMin, lonMax),
                nLat,
                nLon,
                nominalLatSpacing,
                nominalLonSpacing,
                regularGrid);
    }

    /**
     * Function to read the fields of the shakemap.
     * @return list with IShakemapFields
     */
    private List<IShakemapField> readFields() {
        final XmlObject[] xmlfields = shakemap
                .selectChildren(SCHEMA, SHAKEMAP_GRID)[0]
                .selectChildren(SCHEMA, GRID_FIELD);
        return Stream.of(xmlfields)
                .map(this::parseXmlGridField)
                .collect(Collectors.toList());
    }

    /**
     * Function to read the fields from the shakemaps.
     * @param gridField xml element with the grid field tag
     * @return IShakempField
     */
    private IShakemapField parseXmlGridField(final XmlObject gridField) {
        final int index = Integer.parseInt(
                gridField.selectAttribute(INDEX)
                        .newCursor().getTextValue());
        final String name = gridField.selectAttribute(NAME)
                .newCursor().getTextValue();
        final String units = gridField.selectAttribute(UNITS)
                .newCursor().getTextValue();

        return new ShakemapFieldImpl(index, name, units);
    }

    /**
     * Function to read all the data (points) from the shakemap.
     * @return list with IShakemapData
     */
    private List<IShakemapData> readData() {

        final String grid = shakemap
                .selectChildren(SCHEMA, SHAKEMAP_GRID)[0]
                .selectChildren(SCHEMA, GRID_DATA)[0]
                .newCursor().getTextValue();
        final StringTokenizer tokenizer = new StringTokenizer(grid);

        int fieldIndex = 0;

        final DataBuilder builder = new DataBuilder();

        final List<IShakemapData> result = new ArrayList<>();

        while (tokenizer.hasMoreElements()) {
            final String value = tokenizer.nextElement().toString();

            fieldIndex = fieldIndex % fields.size();

            if (fieldIndex == lonField.getIndex() - 1) {
                final double lon = Double.parseDouble(value);
                builder.setLon(lon);
            } else if (fieldIndex == latField.getIndex() - 1) {
                final double lat = Double.parseDouble(value);
                builder.setLat(lat);
            } else {
                final IShakemapField field = fields.get(fieldIndex);
                final double val = Double.parseDouble(value);

                builder.setCustom(field.getName(), val);
            }
            if (fieldIndex == fields.size() - 1) {
                final IShakemapData singleRow = builder.buildData();
                result.add(singleRow);
                builder.startNext();
            }
            fieldIndex += 1;
        }

        return result;
    }

    /**
     * Builder class for a shakemap point.
     */
    private class DataBuilder {
        /**
         * Double value of the latitude.
         */
        private double lat;
        /**
         * Double value of the longitude.
         */
        private double lon;

        /**
         * Map with all the custom values.
         */
        private Map<String, Double> customValues;

        /**
         * Default constructor.
         */
        DataBuilder() {
            customValues = new HashMap<>();
        }


        /**
         * Method to set the longitude value.
         * @param aLon longitude value
         */
        void setLon(final double aLon) {
            this.lon = aLon;
        }

        /**
         * Method to set the latitude value.
         * @param aLat latitude value
         */
        void setLat(final double aLat) {
            this.lat = aLat;
        }

        /**
         * Method to set a custom value.
         * @param name name of the value
         * @param value the value itself
         */
        void setCustom(final String name, final double value) {
            customValues.put(name, value);
        }

        /**
         * Method to build a new IShakemapData entry (a point).
         * @return IShakemapData
         */
        IShakemapData buildData() {
            return new ShakemapDataImpl(lon, lat, customValues);
        }

        /**
         * Clears the lat/lon values. Assigns a new Map, so that
         * the existing custom values can remain in the ShapemapData
         * implementation.
         */
        void startNext() {
            lat = 0.0;
            lon = 0.0;
            // must be new,
            // otherwise it would change the values in the data entry
            customValues = new HashMap<>();
        }
    }
}
