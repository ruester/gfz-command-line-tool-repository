package org.n52.gfz.riesgos.writeidatatofiles;

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
import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.exceptions.ConvertToBytesException;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.gfz.riesgos.util.FileEndingReplacer;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Implementation to write all the files of a shapefile to files
 */
public class WriteShapeFileToPath implements IWriteIDataToFiles {

    @Override
    public void writeToFiles(IData iData, IExecutionContext context, String workingDirectory, String path) throws ConvertToBytesException, IOException {
        if(iData instanceof GTVectorDataBinding) {
            final GTVectorDataBinding bindingClass = (GTVectorDataBinding) iData;

            final File shpFile = bindingClass.getPayloadAsShpFile();

            for(final SingleFile singleFile : SingleFile.values()) {
                final File specificFile = singleFile.getSpecificFileByShapeFile(shpFile);
                final byte[] content = readFile(specificFile);
                final String outPath = singleFile.getSpecificPathByShapeFilePath(path);

                context.writeToFile(content, workingDirectory, outPath);
            }
        } else {
            throw new ConvertToBytesException("Wrong binding class");
        }
    }

    private byte[] readFile(final File file) throws IOException {
        try(final FileInputStream inputStream = new FileInputStream(file)) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    private interface IGetSpecificPath {
        File getSpecificFileByShapeFile(final File shpFile);
        String getSpecificPathByShapeFilePath(final String path);
    }



    private static class TakeShapeFile implements IGetSpecificPath {
        @Override
        public File getSpecificFileByShapeFile(final File shpFile) {
            return shpFile;
        }

        @Override
        public String getSpecificPathByShapeFilePath(final String path) {
            return path;
        }
    }

    private static class GetFileWithOtherEnding implements IGetSpecificPath {

        private final String ending;

        private GetFileWithOtherEnding(final String ending) {
            this.ending = ending;
        }

        @Override
        public File getSpecificFileByShapeFile(final File shpFile) {
            return new File(getSpecificPathByShapeFilePath(shpFile.getPath()));

        }

        @Override
        public String getSpecificPathByShapeFilePath(final String path) {
            return FileEndingReplacer.replaceFileEnding(path, ".shp", ending);
        }
    }

    public enum SingleFile implements IGetSpecificPath {
        SHP(new TakeShapeFile(), ".shp"),
        SHX(new GetFileWithOtherEnding(".shx"), ".shx"),
        PRJ(new GetFileWithOtherEnding(".prj"), ".prj"),
        DBF(new GetFileWithOtherEnding(".dbf"), ".dbf");

        private final IGetSpecificPath getFile;
        private final String ending;

        SingleFile(final IGetSpecificPath getFile, final String ending) {
            this.getFile = getFile;
            this.ending = ending;
        }

        @Override
        public File getSpecificFileByShapeFile(final File shpFile) {
            return getFile.getSpecificFileByShapeFile(shpFile);
        }

        @Override
        public String getSpecificPathByShapeFilePath(final String path) {
            return getFile.getSpecificPathByShapeFilePath(path);
        }

        public String getEnding() {
            return ending;
        }
    }
}
