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
 */

import org.apache.commons.io.IOUtils;
import org.n52.gfz.riesgos.cmdexecution.IExecutionContext;
import org.n52.gfz.riesgos.functioninterfaces.IWriteIDataToFiles;
import org.n52.gfz.riesgos.util.FileEndingReplacer;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Implementation to write all the files of a shapefile to files.
 */
public class WriteShapeFileToPath
        implements IWriteIDataToFiles<GTVectorDataBinding> {

    /**
     * Writes the content of the idata to the filesystem (maybe multiple files)
     * and maybe in a container.
     * @param bindingClass Binding class to write
     * @param context context (maybe a container)
     * @param workingDirectory directory to copy the f
     * @param path destination of the iData
     * @throws IOException a normal IOException that may happen
     * on writing the files
     */
    @Override
    public void writeToFiles(
            final GTVectorDataBinding bindingClass,
            final IExecutionContext context,
            final String workingDirectory,
            final String path) throws IOException {

        final File shpFile = bindingClass.getPayloadAsShpFile();

        for (final SingleFile singleFile : SingleFile.values()) {
            final File specificFile =
                    singleFile.getSpecificFileByShapeFile(shpFile);
            final byte[] content = readFile(specificFile);
            final String outPath =
                    singleFile.getSpecificPathByShapeFilePath(path);

            context.writeToFile(content, workingDirectory, outPath);
        }
    }

    /**
     * Tests equality.
     * @param o other object
     * @return true if both are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    /**
     *
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }

    /**
     *
     * @param file file to read
     * @return byte array with the content of that file
     * @throws IOException exception on reading the file
     */
    private byte[] readFile(final File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    /**
     * Intterface to get the path to a file.
     */
    private interface IGetSpecificPath {
        /**
         *
         * @param shpFile file with the shp ending
         * @return file that belongs to the shapefile ensemble
         */
        File getSpecificFileByShapeFile(File shpFile);

        /**
         *
         * @param path directory of the shapefile
         * @return filename for the specific file of the shapefile
         * ensemble
         */
        String getSpecificPathByShapeFilePath(String path);
    }

    /**
     * Implementation for the shp file itself.
     */
    private static class TakeShapeFile implements IGetSpecificPath {
        /**
         *
         * @param shpFile file with the shp ending
         * @return file that belongs to the shapefile ensemble
         */
        @Override
        public File getSpecificFileByShapeFile(final File shpFile) {
            return shpFile;
        }

        /**
         *
         * @param path directory of the shapefile
         * @return filename for the specific file of the shapefile
         * ensemble
         */
        @Override
        public String getSpecificPathByShapeFilePath(final String path) {
            return path;
        }
    }

    /**
     * Implementation for the other files that belong
     * to a shapefile.
     */
    private static final class GetFileWithOtherEnding
            implements IGetSpecificPath {

        /**
         * Variable to save the file ending.
         */
        private final String ending;

        /**
         * Constructor with a giving ending.
         * @param aEnding file ending (".shx", ".dbf" or similar)
         */
        private GetFileWithOtherEnding(final String aEnding) {
            this.ending = aEnding;
        }

        /**
         *
         * @param shpFile file with the shp ending
         * @return file that belongs to the shapefile ensemble
         */
        @Override
        public File getSpecificFileByShapeFile(final File shpFile) {
            return new File(getSpecificPathByShapeFilePath(shpFile.getPath()));

        }

        /**
         *
         * @param path directory of the shapefile
         * @return filename for the specific file of the shapefile
         * ensemble
         */
        @Override
        public String getSpecificPathByShapeFilePath(final String path) {
            return FileEndingReplacer.INSTANCE.replaceFileEnding(
                    path, ".shp", ending);
        }
    }

    /**
     * Enum with the files to care about.
     */
    public enum SingleFile implements IGetSpecificPath {
        /**
         * The shp file itself.
         */
        SHP(new TakeShapeFile(), ".shp"),
        /**
         * The shx file.
         */
        SHX(new GetFileWithOtherEnding(".shx"), ".shx"),
        /**
         * The prj file.
         */
        PRJ(new GetFileWithOtherEnding(".prj"), ".prj"),
        /**
         * The dbf file.
         */
        DBF(new GetFileWithOtherEnding(".dbf"), ".dbf");

        /**
         * Wrapped implementation.
         */
        private final IGetSpecificPath getFile;
        /**
         * Ending for the file.
         */
        private final String ending;


        /**
         * Constructor with a sub implementation and a file ending.
         * @param aGetFile sub implementation to use
         * @param aEnding file ending
         */
        SingleFile(final IGetSpecificPath aGetFile, final String aEnding) {
            this.getFile = aGetFile;
            this.ending = aEnding;
        }

        /**
         *
         * @param shpFile file with the shp ending
         * @return file that belongs to the shapefile ensemble
         */
        @Override
        public File getSpecificFileByShapeFile(final File shpFile) {
            return getFile.getSpecificFileByShapeFile(shpFile);
        }

        /**
         *
         * @param path directory of the shapefile
         * @return filename for the specific file of the shapefile
         * ensemble
         */
        @Override
        public String getSpecificPathByShapeFilePath(final String path) {
            return getFile.getSpecificPathByShapeFilePath(path);
        }

        /**
         *
         * @return file ending of the single file
         */
        public String getEnding() {
            return ending;
        }
    }
}
