package org.n52.gfz.riesgos.idatatobyteconverter;

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

import org.geotools.coverage.grid.io.imageio.geotiff.codes.GeoTiffGCSCodes;
import org.geotools.image.crop.GTCropCRIF;
import org.junit.Test;
import org.n52.gfz.riesgos.functioninterfaces.IConvertIDataToByteArray;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import static junit.framework.TestCase.assertEquals;

/**
 * Test class for ConvertGTVectorDataBindingToBytes
 */
public class TestConvertGTVectorDataBindingToBytes {

    /**
     * Tests equality
     */
    @Test
    public void testEquals() {
        final IConvertIDataToByteArray<GTVectorDataBinding> converter1 = new ConvertGTVectorDataBindingToBytes(ConvertGTVectorDataBindingToBytes.Format.JSON);
        final IConvertIDataToByteArray<GTVectorDataBinding> converter2 = new ConvertGTVectorDataBindingToBytes(ConvertGTVectorDataBindingToBytes.Format.JSON);

        assertEquals("Both are equal", converter1, converter2);
    }
}
