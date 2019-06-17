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

package org.n52.gfz.riesgos.configuration;

import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGTVectorDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericFileDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGenericXMLDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToGeotiffBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToJsonDataBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToLiteralStringBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToQuakeMLXmlBinding;
import org.n52.gfz.riesgos.bytetoidataconverter.ConvertBytesToShakemapXmlBinding;
import org.n52.gfz.riesgos.configuration.impl.OutputParameterImpl;
import org.n52.gfz.riesgos.exitvaluetoidataconverter.ConvertExitValueToLiteralIntBinding;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.gfz.riesgos.functioninterfaces.ICheckDataAndGetErrorMessage;
import org.n52.gfz.riesgos.readidatafromfiles.ReadShapeFileFromPath;
import org.n52.gfz.riesgos.readidatafromfiles.ReadSingleByteStreamFromPath;
import org.n52.gfz.riesgos.validators.XmlBindingWithAllowedSchema;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

public enum OutputParameterFactory {

    INSTANCE;

    /**
     * Creates a xml file (output) on a given path with an additional schema.
     * @param identifier identifier of the data
     * @param optionalAbstact optional description of the parameter
     * @param path path of the file to read after process termination
     * @param schema schema of the xml
     * @return output argument containing xml that will be read from a given
     * file
     */
    public IOutputParameter createFileOutXmlWithSchema(
            final String identifier,
            final String optionalAbstact,
            final String path,
            final String schema,
            final boolean isOptional) {

        final XmlBindingWithAllowedSchema validator;

        if (schema == null || schema.trim().length() == 0) {
            validator = null;
        } else {
            validator = new XmlBindingWithAllowedSchema(schema);
        }

        return new OutputParameterImpl.Builder(
                identifier, GenericXMLDataBinding.class, isOptional, optionalAbstact)
                .withPath(path)
                .withFunctionToReadFromFiles(
                        new ReadSingleByteStreamFromPath(
                                new ConvertBytesToGenericXMLDataBinding()))
                .withSchema(schema)
                .withValidator(validator)
                .build();
    }

    /**
     * Creates a xml file for quakeml on a given path with an additional
     * schema.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to read after process termination
     * @return output argument containing the quakeml xml that will be
     * read from a given file
     */
    public IOutputParameter createFileOutQuakeMLFile(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {

        final String schema = IMimeTypeAndSchemaConstants.SCHEMA_QUAKE_ML;

        return new OutputParameterImpl.Builder(
                identifier, QuakeMLXmlDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToReadFromFiles(
                        new ReadSingleByteStreamFromPath(
                                new ConvertBytesToQuakeMLXmlBinding()))
                .withSchema(schema)
                .withValidator(
                        new XmlBindingWithAllowedSchema(schema))
                .build();
    }

    /**
     * Creates a xml file for shakemap on a given path with an additional
     * schema.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to read after process termination
     * @return output argument containing the shakemap xml that will be
     * read from a given file
     */
    public IOutputParameter createFileOutShakemap(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {

        final String schema = IMimeTypeAndSchemaConstants.SCHEMA_SHAKEMAP;

        return new OutputParameterImpl.Builder(
                identifier, ShakemapXmlDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToReadFromFiles(
                        new ReadSingleByteStreamFromPath(
                                new ConvertBytesToShakemapXmlBinding()))
                .withSchema(schema)
                .withValidator(
                        new XmlBindingWithAllowedSchema(schema))
                .build();
    }

    /**
     * Creates a xml file for json on a given path.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to read after process termination
     * @return output argument containing the json that will be
     * read from a given file
     */
    public IOutputParameter createFileOutJson(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToReadFromFiles(
                        new ReadSingleByteStreamFromPath(
                                new ConvertBytesToJsonDataBinding()))
                .build();
    }



    /**
     * Creates a geotiff file (output) on a given path.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to read after process termination
     * @return output argument containing the geotiff data that will be
     * read from a given file
     */
    public IOutputParameter createFileOutGeotiff(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, GeotiffBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToReadFromFiles(
                        new ReadSingleByteStreamFromPath(
                                new ConvertBytesToGeotiffBinding()))
                .build();
    }

    /**
     * Creates a geojson file (output) on a given path.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to read after process termination
     * @return output argument containing the geojson data that will be read
     * from a given file
     */
    public IOutputParameter createFileOutGeojson(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, GTVectorDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToReadFromFiles(
                        new ReadSingleByteStreamFromPath(
                                new ConvertBytesToGTVectorDataBinding(
                                        ConvertBytesToGTVectorDataBinding.Format.JSON
                                )))
                .build();
    }

    /**
     * Creates a generic file (output) on a given path.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the data
     * @param path path of the file to read after process termination
     * @return output argument containing the data that will be read from a
     * given file
     */
    public IOutputParameter createFileOutGeneric(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, GenericFileDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToReadFromFiles(
                        new ReadSingleByteStreamFromPath(
                                new ConvertBytesToGenericFileDataBinding()))
                .build();
    }

    /**
     * Creates a shape file (output) on a given path.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the .shp file to read after process termination
     * @return output argument containing the data that will be read from the
     * given files
     */
    public IOutputParameter createFileOutShapeFile(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, GTVectorDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToReadFromFiles(
                        new ReadShapeFileFromPath())
                .build();
    }

    /**
     * Creates a xml output (via stdout) with an additional schema.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param schema schema of the xml
     * @return output argument containing xml that will be read from stdout
     */
    public IOutputParameter createStdoutXmlWithSchema(
            final String identifier,
            final String optionalAbstract,
            final String schema,
            final boolean isOptional) {

        final ICheckDataAndGetErrorMessage validator;

        if (schema == null || schema.trim().length() == 0) {
            validator = null;
        } else {
            validator = new XmlBindingWithAllowedSchema(schema);
        }

        return new OutputParameterImpl.Builder(
                identifier, GenericXMLDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStdout(
                        new ConvertBytesToGenericXMLDataBinding())
                .withSchema(schema)
                .withValidator(validator)
                .build();
    }

    /**
     * Creates a quakeml xml output (via stdout) with an additional schema.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return output argument containing quakeml xml that will be read from
     * stdout
     */
    public IOutputParameter createStdoutQuakeML(
            final String identifier,
            final String optionalAbstract,
            final boolean isOptional) {

        final String schema = IMimeTypeAndSchemaConstants.SCHEMA_QUAKE_ML;

        return new OutputParameterImpl.Builder(
                identifier, QuakeMLXmlDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStdout(
                        new ConvertBytesToQuakeMLXmlBinding())
                .withSchema(schema)
                .withValidator(
                        new XmlBindingWithAllowedSchema(schema))
                .build();
    }

    /**
     * Creates a shakemap xml output (via stdout) with an additional schema.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the data
     * @return output argument containing shakemap xml that will be read from
     * stdout
     */
    public IOutputParameter createStdoutShakemap(
            final String identifier,
            final String optionalAbstract,
            final boolean isOptional) {

        final String schema = IMimeTypeAndSchemaConstants.SCHEMA_SHAKEMAP;

        return new OutputParameterImpl.Builder(
                identifier, ShakemapXmlDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStdout(
                        new ConvertBytesToShakemapXmlBinding())
                .withSchema(schema)
                .withValidator(
                        new XmlBindingWithAllowedSchema(schema))
                .build();
    }

    /**
     * Creates a json output (via stdout).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return output argument containing json that will be read from stdout
     */
    public IOutputParameter createStdoutJson(
            final String identifier,
            final String optionalAbstract,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStdout(
                        new ConvertBytesToJsonDataBinding())
                .build();
    }

    /**
     * Creates a string output (via stdout).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return output argument containing the string that will be read from
     * stdout
     */
    public IOutputParameter createStdoutString(
            final String identifier,
            final String optionalAbstract,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, LiteralStringBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStdout(
                        new ConvertBytesToLiteralStringBinding())
                .build();
    }

    /**
     * Creates a string output (via stderr).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return output argument containing the string that will be read from
     * stderr
     */
    public IOutputParameter createStderrString(
            final String identifier,
            final String optionalAbstract,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, LiteralStringBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStderr(
                        new ConvertBytesToLiteralStringBinding())
                .build();
    }

    /**
     * Creates a json output (via stderr).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return output argument containing the json that will be read from
     * stderr
     */
    public IOutputParameter createStderrJson(
            final String identifier,
            final String optionalAbstract,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleStderr(
                        new ConvertBytesToJsonDataBinding())
                .build();
    }

    /**
     * Creates a int output (via exit value).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return output argument containing the integer that will be read from
     * exit value
     */
    public IOutputParameter createExitValueInt(
            final String identifier,
            final String optionalAbstract,
            final boolean isOptional) {
        return new OutputParameterImpl.Builder(
                identifier, LiteralIntBinding.class, isOptional, optionalAbstract)
                .withFunctionToHandleExitValue(
                        new ConvertExitValueToLiteralIntBinding())
                .build();
    }

}
