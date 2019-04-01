package org.n52.gfz.riesgos.convertformats;

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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.apache.xmlbeans.XmlObject;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.n52.gfz.riesgos.exceptions.ConvertFormatException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertFormat;
import org.n52.wps.io.GTHelper;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class to convert QuakeML XML to a FeatureCollection.
 * This class mostly resamples to code from
 * QuakeMLParser and QuakeMLGenerator by Benjamin Pross
 */
public class QuakeMLToFeatureCollectionConverter implements IConvertFormat<XmlObject, FeatureCollection<SimpleFeatureType, SimpleFeature>> {

    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> convert(final XmlObject xmlObject) throws ConvertFormatException {

        final DefaultFeatureCollection featureCollection = new DefaultFeatureCollection();

        final XmlObject eventParameters = findEventParameters(xmlObject);

        if (eventParameters == null) {
            throw new ConvertFormatException("eventParameters are null");
        }
        final XmlObject[] events = eventParameters.selectChildren(new QName("event"));

        final SimpleFeatureType sft = createFeatureType();

        // iterate events:
        for(final XmlObject event : events) {

            final SimpleFeature feature = getFeatureFromEvent(event, sft);

            setFeatureProperties(feature, event);

            if (feature != null) {
                featureCollection.add(feature);
            }
        }

        return featureCollection;
    }

    private XmlObject findEventParameters(final XmlObject root) throws ConvertFormatException {
        final XmlObject[] result = root.selectChildren(new QName("eventParameters"));
        if(result.length != 0) {
            return result[0];
        }
        final XmlObject[] quakeml = root.selectChildren(new QName("q", "quakeml"));
        if(quakeml.length == 1) {
            final XmlObject quakemlRoot = quakeml[0];
            return quakemlRoot.selectChildren(new QName("eventParameters"))[0];
        }
        throw new ConvertFormatException("eventParameters could not be found");
    }

    private void setIfElementIsPresent(final SimpleFeature feature, final XmlObject childElem, final String elementToCheck, final String prefixToSet) {
        final String origPubTime = getChildNodeValueById(childElem, elementToCheck);
        if (origPubTime != null) {
            feature.setAttribute(prefixToSet + "." + elementToCheck, origPubTime);
        }
    }

    private void addOrigin(final SimpleFeature feature, final String element, final XmlObject childElem) {
        final String origPubID = childElem.selectAttribute(new QName("publicID")).newCursor().getTextValue();
        feature.setAttribute("origin.publicID", origPubID);
        for(final String elementToCheck : Arrays.asList(
                "time.value",
                "time.uncertainty",
                "depth.value",
                "depth.uncertainty",
                "depthType",
                "timeFixed",
                "epicenterFixed",
                "referenceSystemID",
                "type",
                "creationInfo.value",
                "quality.azimuthalGap",
                "quality.minimumDistance",
                "quality.maximumDistance",
                "quality.usedPhaseCount",
                "quality.usedStationCount",
                "quality.standardError",
                "evaluationMode",
                "evaluationStatus"

        )) {
            setIfElementIsPresent(feature, childElem, elementToCheck, "origin");
        }
    }

    private void addOriginUncertainty(final SimpleFeature feature, final String element, final XmlObject childElem) {
        for(final String elementToCheck: Arrays.asList(
                "horizontalUncertainty",
                "minHorizontalUncertainty",
                "maxHorizontalUncertainty",
                "azimuthMaxHorizontalUncertainty"
        )) {
            setIfElementIsPresent(feature, childElem, elementToCheck, "originUncertainty");
        }
    }

    private void addAttributeByName(final SimpleFeature feature, final String element, final XmlObject childElem) {
        feature.setAttribute(element, childElem.newCursor().getTextValue());
    }

    private void addDescription(final SimpleFeature feature, final String element, final XmlObject childElem) {
        setIfElementIsPresent(feature, childElem, "text", "description");
    }

    private void addMagnitude(final SimpleFeature feature, final String element, final XmlObject childElem) {
        final String magPubID = childElem.selectAttribute(new QName("publicID")).newCursor().getTextValue();
        feature.setAttribute("magnitude.publicID", magPubID);
        for(final String elementToCheck : Arrays.asList(
                "mag.value",
                "mag.uncertainty",
                "type",
                "evaluationStatus",
                "originID",
                "stationCount",
                "creationInfo.value"
        )) {
            setIfElementIsPresent(feature, childElem, elementToCheck, "magnitude");
        }
    }

    private void addFocalMechanism(final SimpleFeature feature, final String element, final XmlObject childElem) {
        String focMechID = childElem.selectAttribute(new QName("publicID")).newCursor().getTextValue();
        feature.setAttribute("focalMechanism.publicID", focMechID);
        for(final String elementToCheck : Arrays.asList(
                "nodalPlanes.nodalPlane1.strike.value",
                "nodalPlanes.nodalPlane1.strike.uncertainty",
                "nodalPlanes.nodalPlane1.dip.value",
                "nodalPlanes.nodalPlane1.dip.uncertainty",
                "nodalPlanes.nodalPlane1.rake.value",
                "nodalPlanes.nodalPlane1.rake.uncertainty",
                "nodalPlanes.preferredPlane"
        )) {
            setIfElementIsPresent(feature, childElem, elementToCheck, "focalMechanism");
        }
    }

    private void addAmplitude(final SimpleFeature feature, final String element, final XmlObject childElem) {
        final String ampPubID = childElem.selectAttribute(new QName("publicID")).newCursor().getTextValue();
        feature.setAttribute("amplitude.publicID", ampPubID);
        for(final String elementToCheck : Arrays.asList(
                "type",
                "genericAmplitude.value"
        )) {
            setIfElementIsPresent(feature, childElem, elementToCheck, "amplitude");

        }
    }

    @FunctionalInterface
    interface IAddDataToFeature {
        void addFeature(final SimpleFeature feature, final String elementToExtract, final XmlObject childElem);
    }

    private void setFeatureProperties(final SimpleFeature feature, final XmlObject event) {

        final Map<String, IAddDataToFeature> elementsToExtract = new HashMap<>();
        elementsToExtract.put("preferredOriginID", this::addAttributeByName);
        elementsToExtract.put("preferredMagnitudeID", this::addAttributeByName);
        elementsToExtract.put("type", this::addAttributeByName);
        elementsToExtract.put("description", this::addDescription);
        elementsToExtract.put("origin", this::addOrigin);
        elementsToExtract.put("originUncertainty", this::addOriginUncertainty);
        elementsToExtract.put("magnitude", this::addMagnitude);
        elementsToExtract.put("focalMechanism", this::addFocalMechanism);
        elementsToExtract.put("amplitude", this::addAmplitude);


        for(final String elementToExtract : elementsToExtract.keySet()) {
            final XmlObject[] selected = event.selectChildren(new QName(elementToExtract));
            if(selected.length > 0) {
                final XmlObject childElem = selected[0];
                final IAddDataToFeature add = elementsToExtract.get(elementToExtract);
                add.addFeature(feature, elementToExtract, childElem);
            }
        }
    }

    private String getChildNodeValueById(XmlObject child, String id) {
        String[] nestedChilds = id.split("\\.");
        if (nestedChilds.length == 1) {
            XmlObject[] nList = child.selectChildren(new QName(nestedChilds[0]));
            if (nList.length > 0) {
                XmlObject nChild = nList[0];
                return nChild.newCursor().getTextValue();
            }
        } else {
            final XmlObject[] nList = child.selectChildren(new QName(nestedChilds[0]));
            String restID = id.substring(id.indexOf(".")+1);
            if (nList.length > 0) {
                XmlObject nChild = nList[0];
                return getChildNodeValueById(nChild, restID);
            }
        }
        return null;
    }

    private Coordinate getCoordinate(XmlObject eventElem) {
        // get origin:
        final XmlObject origin = eventElem.selectChildren(new QName("origin"))[0];
        // get latitude:
        final XmlObject latitude = origin.selectChildren(new QName("latitude"))[0];
        final String lat = latitude.selectChildren(new QName("value"))[0].newCursor().getTextValue();
        // get longitude:
        final XmlObject longitude = origin.selectChildren(new QName("longitude"))[0];
        final String lng = longitude.selectChildren(new QName("value"))[0].newCursor().getTextValue();

        return new Coordinate(
                Double.parseDouble(lng),
                Double.parseDouble(lat)
        );
    }

    private SimpleFeature getFeatureFromEvent(XmlObject event, SimpleFeatureType sft) {
        final String id = event.selectAttribute(new QName("publicID")).newCursor().getTextValue();
        final Point point = new GeometryFactory().createPoint(getCoordinate(event));
        return GTHelper.createFeature(id, point, sft);
    }

    private SimpleFeatureType createFeatureType() {
        final SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        final String id = UUID.randomUUID().toString().substring(0, 5);
        final String namespace = "http://www.52north.org/" + id;
        final Name name = new NameImpl(namespace, "Feature-" + id);
        builder.setName(name);
        builder.setCRS(DefaultGeographicCRS.WGS84);

        builder.add("the_geom", Point.class);

        for(final String featureAttribute : Arrays.asList(
                "preferredOriginID",
                "preferredMagnitudeID",
                "type",
                "description.text",
                "origin.publicID",
                "origin.time.value",
                "origin.time.uncertainty",
                "origin.depth.value",
                "origin.depth.uncertainty",
                "origin.depthType",
                "origin.timeFixed",
                "origin.epicenterFixed",
                "origin.referenceSystemID",
                "origin.type",
                "origin.creationInfo.value",
                "origin.quality.azimuthalGap",
                "origin.quality.minimumDistance",
                "origin.quality.maximumDistance",
                "origin.quality.usedPhaseCount",
                "origin.quality.usedStationCount",
                "origin.quality.standardError",
                "origin.evaluationMode",
                "origin.evaluationStatus",
                "originUncertainty.horizontalUncertainty",
                "originUncertainty.minHorizontalUncertainty",
                "originUncertainty.maxHorizontalUncertainty",
                "originUncertainty.azimuthMaxHorizontalUncertainty",
                "magnitude.publicID",
                "magnitude.mag.value",
                "magnitude.mag.uncertainty",
                "magnitude.type",
                "magnitude.evaluationStatus",
                "magnitude.originID",
                "magnitude.stationCount",
                "magnitude.creationInfo.value",
                "focalMechanism.publicID",
                "focalMechanism.nodalPlanes.nodalPlane1.strike.value",
                "focalMechanism.nodalPlanes.nodalPlane1.strike.uncertainty",
                "focalMechanism.nodalPlanes.nodalPlane1.dip.value",
                "focalMechanism.nodalPlanes.nodalPlane1.dip.uncertainty",
                "focalMechanism.nodalPlanes.nodalPlane1.rake.value",
                "focalMechanism.nodalPlanes.nodalPlane1.rake.uncertainty",
                "focalMechanism.nodalPlanes.preferredPlane",
                "amplitude.publicID",
                "amplitude.type",
                "amplitude.genericAmplitude.value"
        )) {
            builder.add(featureAttribute, String.class);
        }

        return builder.buildFeatureType();
    }
}
