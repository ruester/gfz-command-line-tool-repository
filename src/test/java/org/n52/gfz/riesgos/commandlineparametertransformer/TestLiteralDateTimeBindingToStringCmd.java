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

package org.n52.gfz.riesgos.commandlineparametertransformer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.ConvertToStringCmdException;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToCommandLineParameter;
import org.n52.wps.io.data.binding.literal.LiteralDateTimeBinding;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test case for the LiteralDateTimeBindingToStringCmd class.
 */
public class TestLiteralDateTimeBindingToStringCmd {

    /**
     * Test the equals method.
     */
    @Test
    public void testEquals() {
        final IConvertIDataToCommandLineParameter<LiteralDateTimeBinding> converter1 = new LiteralDateTimeBindingToStringCmd();
        final IConvertIDataToCommandLineParameter<LiteralDateTimeBinding> converter2 = new LiteralDateTimeBindingToStringCmd();

        assertEquals("The converter are equal", converter1, converter2);

        final IConvertIDataToCommandLineParameter<LiteralDateTimeBinding> converter3 = new LiteralDateTimeBindingToStringCmd("--flag");
        assertNotEquals("The third is different", converter1, converter3);

        final IConvertIDataToCommandLineParameter<LiteralDateTimeBinding> converter4  = new LiteralDateTimeBindingToStringCmd("--flag");
        assertEquals("Third & fourth are equal", converter3, converter4);
    }

    /**
     * Test the conversion of a datetime to a command line string.
     */
    @Test
    public void testConversion() {
        final LiteralDateTimeBinding iData = createTestDataUtc(
                        2022,
                        2,
                        4,
                        8,
                        19,
                        26
        );

        final IConvertIDataToCommandLineParameter<LiteralDateTimeBinding> converter = new LiteralDateTimeBindingToStringCmd();

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);
            assertEquals("There is one element", 1, result.size());
            assertEquals("It should be an ISO string", "2022-02-04T08:19:26.000Z", result.get(0));
        } catch (final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Test the conversion of a datetime to a command line string with a default
     * flag.
     */
    @Test
    public void testConversionWithDefaultFlag() {
        final LiteralDateTimeBinding iData = createTestDataUtc(
                2021,
                12,
                31,
                23,
                59,
                59
        );

        final String flag = "--starttime";
        final IConvertIDataToCommandLineParameter<LiteralDateTimeBinding> converter = new LiteralDateTimeBindingToStringCmd(flag);

        try {
            final List<String> result = converter.convertToCommandLineParameter(iData);
            assertEquals("There are two elements", 2, result.size());
            assertEquals("First one should be the flag", flag, result.get(0));
            assertEquals("Second one should be an ISO string", "2021-12-31T23:59:59.000Z", result.get(1));
        } catch (final ConvertToStringCmdException exception) {
            fail("There should be no exception");
        }
    }

    /**
     * Create some test datetime objects.
     * @param year for example 2022
     * @param month for example 2 (=> February)
     * @param day for example 12 (day of the month)
     * @param hour for example 23 (hour of the day)
     * @param minute for example 45 (minute of the hour)
     * @param second for example 11 (second of the minute).
     * @return literal date time binding
     */
    private LiteralDateTimeBinding createTestDataUtc(int year, int month, int day, int hour, int minute, int second) {
        return new LiteralDateTimeBinding(
                new DateTime(
                        year,
                        month,
                        day,
                        hour,
                        minute,
                        second,
                        DateTimeZone.UTC
                ).toDate());
    }
}