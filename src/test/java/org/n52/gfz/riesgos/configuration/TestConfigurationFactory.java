package org.n52.gfz.riesgos.configuration;


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

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test class for the configuration factory
 */
public class TestConfigurationFactory {

    @Test
    public void testEquals() {
        final IConfiguration conf1 = ConfigurationFactory.createQuakeledger("123456");
        final IConfiguration conf2 = ConfigurationFactory.createQuakeledger("123456");

        assertEquals("The configurations are the same", conf1, conf2);

        final IConfiguration conf3 = ConfigurationFactory.createShakyground("123456");

        assertNotEquals("conf3 is different", conf1, conf3);
    }
}
