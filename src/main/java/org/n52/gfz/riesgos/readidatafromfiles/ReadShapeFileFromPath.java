package org.n52.gfz.riesgos.readidatafromfiles;

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

import org.apache.commons.io.IOUtils;
import org.geotools.data.DataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.functioninterfaces.IReadIDataFromFiles;
import org.n52.gfz.riesgos.writeidatatofiles.WriteShapeFileToPath;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Implementation to read multiple files for a shapefile
 */
public class ReadShapeFileFromPath implements IReadIDataFromFiles<GTVectorDataBinding> {

    @Override
    public GTVectorDataBinding readFromFiles(
            final IExecutionContext context,
            final String workingDirectory,
            final String path) throws IOException {

        final Path tempDir = Files.createTempDirectory("tempshapefile");
        final File tempDirAsFile = new File(path);
        tempDirAsFile.deleteOnExit();

        final String outputFileTemplate = "output";
        final String outputFileTemplatePath = Paths.get(tempDir.toString(), outputFileTemplate).toString();
        final String outputFilePathShp = outputFileTemplatePath + ".shp";

        for(final WriteShapeFileToPath.SingleFile singleFile : WriteShapeFileToPath.SingleFile.values()) {
            final String pathToRead = singleFile.getSpecificPathByShapeFilePath(path);

            final byte[] content = context.readFromFile(Paths.get(workingDirectory, pathToRead).toString());

            final File tempOutFile = new File(outputFileTemplatePath + singleFile.getEnding());
            writeFile(tempOutFile, content);
            tempOutFile.deleteOnExit();
        }

        // that code is reused from GTBinZippedSHPParser
        final DataStore store = new ShapefileDataStore(new File(outputFilePathShp).toURI().toURL());
        final SimpleFeatureCollection features = store.getFeatureSource(store.getTypeNames()[0]).getFeatures();
        return new GTVectorDataBinding(features);
    }

    private void writeFile(final File file, final byte[] content) throws IOException {
        try(final FileOutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.write(content, outputStream);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }

}
