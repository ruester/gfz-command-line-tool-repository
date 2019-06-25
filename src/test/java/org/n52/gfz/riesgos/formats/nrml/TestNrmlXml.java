/*
 * Copyright (C) 2019 GFZ German Research Centre for Geosciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.n52.gfz.riesgos.formats.nrml;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.gfz.riesgos.util.StringUtils;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * This is the test class for the xml implementation
 * for the INrml interfaces
 */
public class TestNrmlXml {

    @Test
    public void testFile() {

        try {
            final XmlObject xml = readFile();

            // here are some problems

            final INrml nrml = Nrml.fromOriginalXml(xml);
            final INrmlExposureModel exposureModel = nrml.getExposureModel();

            assertEquals("the id is as expected", "SARA_v1.0", exposureModel.getId());
            assertEquals("the category is as expected", "buildings", exposureModel.getCategory());
            assertEquals("The taxonomySource is as expceted", "GEM", exposureModel.getTaxonomySource());

            final INrmlDescription description = exposureModel.getDescription();

            assertEquals("The description is as expected", "GEM-SARA Model, project RIESGOS", description.getText());

            final INrmlConversions conversions = exposureModel.getConversions();
            final INrmlCostTypes costTypes = conversions.getCostTypes();
            final INrmlCostType costType = costTypes.getCostType();

            assertEquals("The name is as expected", "structural", costType.getName());
            assertEquals("The type is as expected", "per_asset", costType.getType());
            assertEquals("THe unit is as expected", "USD", costType.getUnit());

            final INrmlAssets assets = exposureModel.getAssets();

            final List<INrmlAsset> assetList = assets.getAssetList();

            assertEquals("There are some entries", 68, assetList.size());

            final INrmlAsset firstAsset = assetList.get(0);

            assertEquals("The id is as expected", "CHL.16.7.3_1", firstAsset.getId());
            assertEquals("The number is as expected", 91, firstAsset.getNumber());
            assertEquals("The taxonomy is as expected", "CR+PC/LWAL", firstAsset.getTaxonomy());

            final INrmlLocation firstLocation = firstAsset.getLocation();

            assertTrue("The lon is as expected", Math.abs(firstLocation.getLon() - (-71.2888956)) < 0.0001);
            assertTrue("The lat is as expected", Math.abs(firstLocation.getLat() - (-33.0532539)) < 0.0001);

            final INrmlCosts firstCosts = firstAsset.getCosts();
            final INrmlCost firstCost = firstCosts.getCost();

            assertEquals("The type is as expected", "structural", firstCost.getType());
            assertTrue("The value is as expected", Math.abs(firstCost.getValue() - 985250.0) < 0.0001);

            final INrmlOccupancies firstOccupancies = firstAsset.getOccupancies();
            final List<INrmlOccupancy> firstOccupancyList = firstOccupancies.getOccupancyList();

            assertEquals("There are 2 entries",  2, firstOccupancyList.size());

            final INrmlOccupancy firstOccupancyListFirstEntry = firstOccupancyList.get(0);

            assertEquals("The period is as expected", "day", firstOccupancyListFirstEntry.getPeriod());
            assertEquals("The occupants are as expected", 3, firstOccupancyListFirstEntry.getOccupants());

            final INrmlOccupancy firstOccupancyListSecondEntry = firstOccupancyList.get(1);

            assertEquals("The period is as expected", "night", firstOccupancyListSecondEntry.getPeriod());
            assertEquals("THe occupants are as expected", 6, firstOccupancyListSecondEntry.getOccupants());

            final INrmlAsset lastAsset = assetList.get(assetList.size() - 1);

            assertEquals("The id is as expected", "CHL.16.7.7_1", lastAsset.getId());
            assertEquals("The number is as expected", 2384, lastAsset.getNumber());
            assertEquals("The taxonomy is as expected", "W+WS/H_1,2", lastAsset.getTaxonomy());

            final INrmlLocation lastLocation = lastAsset.getLocation();

            assertTrue("The lon is as expected", Math.abs(lastLocation.getLon() - (-71.4886588)) < 0.0001);
            assertTrue("The lat is as expected", Math.abs(lastLocation.getLat() - (-32.9192134)) < 0.0001);

            final INrmlCosts lastCosts = lastAsset.getCosts();
            final INrmlCost lastCost = lastCosts.getCost();

            assertEquals("The type is as expected", "structural", lastCost.getType());
            assertTrue("The value is as expected", Math.abs(lastCost.getValue() - 10740.0) < 0.0001);

            final INrmlOccupancies lastOccupancies = lastAsset.getOccupancies();
            final List<INrmlOccupancy> lastOccupancyList = lastOccupancies.getOccupancyList();

            assertEquals("There are 2 entries",  2, lastOccupancyList.size());

            final INrmlOccupancy lastOccupancyListFirstEntry = lastOccupancyList.get(0);

            assertEquals("The period is as expected", "day", lastOccupancyListFirstEntry.getPeriod());
            assertEquals("The occupants are as expected", 3, lastOccupancyListFirstEntry.getOccupants());

            final INrmlOccupancy lastOccupancyListSecondEntry = lastOccupancyList.get(1);

            assertEquals("The period is as expected", "night", lastOccupancyListSecondEntry.getPeriod());
            assertEquals("THe occupants are as expected", 6, lastOccupancyListSecondEntry.getOccupants());





        } catch (final IOException | XmlException exception) {
            fail("There should be no exception on reading the content");
        }
    }

    private XmlObject readFile() throws IOException, XmlException {
        final String content = StringUtils.readFromResourceFile("org/n52/gfz/riesgos/formats/nrml.xml");
        return XmlObject.Factory.parse(content);
    }
}
