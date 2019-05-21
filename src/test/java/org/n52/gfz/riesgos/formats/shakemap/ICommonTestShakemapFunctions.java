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

package org.n52.gfz.riesgos.formats.shakemap;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import static junit.framework.TestCase.fail;

public interface ICommonTestShakemapFunctions {

    default XmlObject createExampleShakemap() {
        // this is basically the content of shakemap.xml
        // but I changed the values for lat, lon and the values themselves
        final String veryBasicShakemapRaw = "<shakemap_grid \n" +
                "    xmlns=\"http://earthquake.usgs.gov/eqcenter/shakemap\" \n" +
                "    event_id=\"84945\" \n" +
                "    map_status=\"RELEASED\" \n" +
                "    process_timestamp=\"2019-04-09T08:59:36.927766Z\" \n" +
                "    shakemap_event_type=\"stochastic\" \n" +
                "    shakemap_id=\"84945\" \n" +
                "    shakemap_originator=\"GFZ\" \n" +
                "    shakemap_tool=\"shakyground\" \n" +
                "    tool_version=\"0.1\" \n" +
                "    xsi:schemaLocation=\"http://earthquake.usgs.gov http://earthquake.usgs.gov/eqcenter/shakemap/xml/schemas/shakemap.xsd\" \n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <event \n" +
                "        depth=\"34.75117\" \n" +
                "        event_description=\"\" \n" +
                "        event_id=\"84945\" \n" +
                "        event_network=\"nan\" \n" +
                "        event_timestamp=\"16773-01-01T00:00:00.000000Z\" \n" +
                "        lat=\"-30.9227\" \n" +
                "        lon=\"-71.49875\" \n" +
                "        magnitude=\"8.35\"/>\n" +
                "    <event_specific_uncertainty name=\"pga\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"pgv\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"mi\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"psa03\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"psa10\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"psa30\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <grid_specification\n" +
                "        lat_max=\"8.0\" \n" +
                "        lat_min=\"-8.0\" \n" +
                "        lon_max=\"8.0\" \n" +
                "        lon_min=\"-8.0\" \n" +
                "        nlat=\"5\" \n" +
                "        nlon=\"5\" \n" +
                "        nominal_lat_spacing=\"4.0\" \n" +
                "        nominal_lon_spacing=\"4.0\" \n" +
                "        regular_grid=\"True\"/>\n" +
                "    <grid_field index=\"1\" name=\"LON\" units=\"dd\"/>\n" +
                "    <grid_field index=\"2\" name=\"LAT\" units=\"dd\"/>\n" +
                "    <grid_field index=\"3\" name=\"VAL\" units=\"m\"/>\n" +
                "    <grid_data>" +
                "        -8.0 -8.0 0.0\n" +
                "        -8.0 -4.0 0.0\n" +
                "        -8.0 0.0 0.0\n" +
                "        -8.0 4.0 0.0\n" +
                "        -8.0 8.0 0.0\n" +
                "        -4.0 -8.0 0.0\n" +
                "        -4.0 -4.0 0.0\n" +
                "        -4.0 0.0 0.0\n" +
                "        -4.0 4.0 0.0\n" +
                "        -4.0 8.0 0.0\n" +
                "         0.0 -8.0 0.0\n" +
                "         0.0 -4.0 0.0\n" +
                "         0.0 0.0 0.0\n" +
                "         0.0 4.0 0.0\n" +
                "         0.0 8.0 0.0\n" +
                "         4.0 -8.0 0.0\n" +
                "         4.0 -4.0 0.0\n" +
                "         4.0 0.0 0.0\n" +
                "         4.0 4.0 0.0\n" +
                "         4.0 8.0 0.0\n" +
                "         8.0 -8.0 0.0\n" +
                "         8.0 -4.0 0.0\n" +
                "         8.0 0.0 0.0\n" +
                "         8.0 4.0 0.0\n" +
                "         8.0 8.0 0.0\n" +
                "    </grid_data>" +
                "</shakemap_grid>";

        try {
            final XmlObject veryBasicShakemap = XmlObject.Factory.parse(veryBasicShakemapRaw);
            return veryBasicShakemap;
        } catch(final XmlException xmlException) {
            fail("There should be no xml exception on parsing the shakemap");
        }
        return null;
    }

    default XmlObject createExampleShakemapExtended() {
        // this is basically the content of shakemap.xml
        // but I changed the values for lat, lon and the values themselves
        final String veryBasicShakemapRaw = "<shakemap_grid \n" +
                "    xmlns=\"http://earthquake.usgs.gov/eqcenter/shakemap\" \n" +
                "    event_id=\"84945\" \n" +
                "    map_status=\"RELEASED\" \n" +
                "    process_timestamp=\"2019-04-09T08:59:36.927766Z\" \n" +
                "    shakemap_event_type=\"stochastic\" \n" +
                "    shakemap_id=\"84945\" \n" +
                "    shakemap_originator=\"GFZ\" \n" +
                "    shakemap_tool=\"shakyground\" \n" +
                "    tool_version=\"0.1\" \n" +
                "    xsi:schemaLocation=\"http://earthquake.usgs.gov http://earthquake.usgs.gov/eqcenter/shakemap/xml/schemas/shakemap.xsd\" \n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <event \n" +
                "        depth=\"34.75117\" \n" +
                "        event_description=\"\" \n" +
                "        event_id=\"84945\" \n" +
                "        event_network=\"nan\" \n" +
                "        event_timestamp=\"16773-01-01T00:00:00.000000Z\" \n" +
                "        lat=\"-30.9227\" \n" +
                "        lon=\"-71.49875\" \n" +
                "        magnitude=\"8.35\"/>\n" +
                "    <event_specific_uncertainty name=\"pga\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"pgv\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"mi\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"psa03\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"psa10\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <event_specific_uncertainty name=\"psa30\" numsta=\"\" value=\"0.0\"/>\n" +
                "    <grid_specification\n" +
                "        lat_max=\"8.0\" \n" +
                "        lat_min=\"-8.0\" \n" +
                "        lon_max=\"0.0\" \n" +
                "        lon_min=\"-16.0\" \n" +
                "        nlat=\"5\" \n" +
                "        nlon=\"5\" \n" +
                "        nominal_lat_spacing=\"4.0\" \n" +
                "        nominal_lon_spacing=\"4.0\" \n" +
                "        regular_grid=\"True\"/>\n" +
                "    <grid_field index=\"1\" name=\"LON\" units=\"dd\"/>\n" +
                "    <grid_field index=\"2\" name=\"LAT\" units=\"dd\"/>\n" +
                "    <grid_field index=\"3\" name=\"VAL\" units=\"m\"/>\n" +
                "    <grid_field index=\"3\" name=\"VAL2\" units=\"m\"/>\n" +
                "    <grid_data>" +
                "        -16.0 -8.0 1.0 7.0\n" +
                "        -16.0 -4.0 2.0 7.0\n" +
                "        -16.0 0.0 3.0 7.0\n" +
                "        -16.0 4.0 4.0 7.0\n" +
                "        -16.0 8.0 5.0 7.0\n" +
                "        -12.0 -8.0 6.0 6.0\n" +
                "        -12.0 -4.0 7.0 6.0\n" +
                "        -12.0 0.0 8.0 6.0\n" +
                "        -12.0 4.0 9.0 6.0\n" +
                "        -12.0 8.0 10.0 6.0\n" +
                "         -8.0 -8.0 11.0 5.0\n" +
                "         -8.0 -4.0 12.0 5.0\n" +
                "         -8.0 0.0 13.0 5.0\n" +
                "         -8.0 4.0 14.0 5.0\n" +
                "         -8.0 8.0 15.0 5.0\n" +
                "         -4.0 -8.0 16.0 4.0\n" +
                "         -4.0 -4.0 17.0 4.0\n" +
                "         -4.0 0.0 18.0 4.0\n" +
                "         -4.0 4.0 19.0 4.0\n" +
                "         -4.0 8.0 20.0 4.0\n" +
                "         0.0 -8.0 21.0 3.0\n" +
                "         0.0 -4.0 22.0 3.0\n" +
                "         0.0 0.0 23.0 3.0\n" +
                "         0.0 4.0 24.0 3.0\n" +
                "         0.0 8.0 25.0 3.0\n" +
                "    </grid_data>" +
                "</shakemap_grid>";

        try {
            final XmlObject veryBasicShakemap = XmlObject.Factory.parse(veryBasicShakemapRaw);
            return veryBasicShakemap;
        } catch(final XmlException xmlException) {
            fail("There should be no xml exception on parsing the shakemap");
        }
        return null;
    }
}
