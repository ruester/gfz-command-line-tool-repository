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

import org.n52.gfz.riesgos.commandlineparametertransformer.BoundingBoxDataToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.FileToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralBooleanBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralDoubleBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralIntBindingToStringCmd;
import org.n52.gfz.riesgos.commandlineparametertransformer.LiteralStringBindingToStringCmd;
import org.n52.gfz.riesgos.configuration.impl.InputParameterImpl;
import org.n52.gfz.riesgos.formats.IMimeTypeAndSchemaConstants;
import org.n52.gfz.riesgos.formats.json.binding.JsonDataBinding;
import org.n52.gfz.riesgos.formats.quakeml.binding.QuakeMLXmlDataBinding;
import org.n52.gfz.riesgos.formats.shakemap.binding.ShakemapXmlDataBinding;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGTVectorDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericFileDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericXMLDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGenericXMLDataBindingToBytesWithoutHeader;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertGeotiffBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertJsonDataBindingToBytes;
import org.n52.gfz.riesgos.idatatobyteconverter.ConvertLiteralStringToBytes;
import org.n52.gfz.riesgos.validators.LiteralStringBindingWithAllowedValues;
import org.n52.gfz.riesgos.validators.XmlBindingWithAllowedSchema;
import org.n52.gfz.riesgos.writeidatatofiles.WriteShapeFileToPath;
import org.n52.gfz.riesgos.writeidatatofiles.WriteSingleByteStreamToPath;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.data.binding.literal.LiteralBooleanBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.io.data.binding.literal.LiteralIntBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;

import java.util.List;
import java.util.UUID;

public enum InputParameterFactory {

    INSTANCE;

    /**
     * Creates a command line int argument.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the data
     * @param flag optional command line flag (--x for a parameter x)
     * @param defaultValue optional default value of the argument
     * @param allowedValues optional list with allowed values
     * @return object with information about how to use the value as a int
     * command line argument input parameter
     */
    public IInputParameter createCommandLineArgumentInt(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final boolean isOptional) {
        final InputParameterImpl.Builder builder =
                new InputParameterImpl.Builder(
                        identifier, LiteralIntBinding.class, isOptional, optionalAbstract)
                        .withFunctionToTransformToCmd(
                                new LiteralIntBindingToStringCmd(flag));

        if (defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }

        if (allowedValues != null && (!allowedValues.isEmpty())) {
            builder.withAllowedValues(allowedValues);
        }

        return builder.build();
    }

    /**
     * Creates a command line double argument.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional command line flag (--x for a parameter x)
     * @param defaultValue optional default value of the argument
     * @param allowedValues optional list with allowed values
     * @return object with information about how to use the value as a double
     * command line argument input parameter
     */
    public IInputParameter createCommandLineArgumentDouble(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final boolean isOptional) {
        final InputParameterImpl.Builder builder =
                new InputParameterImpl.Builder(
                        identifier, LiteralDoubleBinding.class, isOptional, optionalAbstract);
        builder.withFunctionToTransformToCmd(
                new LiteralDoubleBindingToStringCmd(flag));

        if (defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }

        if (allowedValues != null && (!allowedValues.isEmpty())) {
            builder.withAllowedValues(allowedValues);
        }

        return builder.build();
    }

    /**
     * Creates a command line string argument.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional command line flag (--x for a parameter x)
     * @param defaultValue optional default value of the argument
     * @param allowedValues optional list with allowed values
     * @return object with information about how to use the value as a string
     * command line argument input parameter
     */
    public IInputParameter createCommandLineArgumentString(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final List<String> allowedValues,
            final boolean isOptional) {
        final InputParameterImpl.Builder builder =
                new InputParameterImpl.Builder(
                        identifier, LiteralStringBinding.class, isOptional, optionalAbstract);
        builder.withFunctionToTransformToCmd(
                new LiteralStringBindingToStringCmd(flag));

        if (defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }

        if (allowedValues != null && (!allowedValues.isEmpty())) {
            builder.withAllowedValues(allowedValues);
            builder.withValidator(
                    new LiteralStringBindingWithAllowedValues(allowedValues));
        }

        return builder.build();
    }

    /**
     * Creates a command line boolean argument.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag command line flag to insert if the value is true
     * @param defaultValue optional default value
     * @return object with information about how to use the value as a
     * boolean command line argument input parameter
     */
    public IInputParameter createCommandLineArgumentBoolean(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final String defaultValue,
            final boolean isOptional) {
        final InputParameterImpl.Builder builder =
                new InputParameterImpl.Builder(
                        identifier, LiteralBooleanBinding.class, isOptional, optionalAbstract);
        builder.withFunctionToTransformToCmd(
                new LiteralBooleanBindingToStringCmd(flag));

        if (defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }

        return builder.build();
    }


    /**
     * Creates a command line argument bounding box.
     * (This will add four single command line arguments in that order:
     *  lonmin, lonmax, latmin, latmax)
     *
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param supportedCRSForBBox list with the supported CRS
     *                            for the bounding box
     * @return bounding box command line argument
     */
    public IInputParameter createCommandLineArgumentBBox(
            final String identifier,
            final String optionalAbstract,
            final List<String> supportedCRSForBBox,
            final boolean isOptional) {

        return new InputParameterImpl.Builder(
                identifier, BoundingBoxData.class, isOptional, optionalAbstract)
                .withFunctionToTransformToCmd(
                        new BoundingBoxDataToStringCmd())
                .withSupportedCRSForBBox(supportedCRSForBBox)
                .build();
    }

    /**
     * Creates a command line argument (xml file) with a file path that will
     * be written down as a temporary file.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param schema schema of the xml
     * @param defaultFlag default flag for the command line argument
     *                    (for example --file)
     * @return xml file command line argument
     */
    public IInputParameter
    createCommandLineArgumentXmlFileWithSchema(
            final String identifier,
            final String optionalAbstract,
            final String schema,
            final String defaultFlag,
            final boolean isOptional) {

        final String filename = createUUIDFilename(".xml");
        final XmlBindingWithAllowedSchema validator;

        if (schema == null || schema.trim().length() == 0) {
            validator = null;
        } else {
            validator = new XmlBindingWithAllowedSchema(schema);
        }

        return new InputParameterImpl.Builder(
                identifier, GenericXMLDataBinding.class, isOptional, optionalAbstract)
                .withValidator(validator)
                .withFunctionToTransformToCmd(
                        new FileToStringCmd(filename, defaultFlag))
                .withPath(filename)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGenericXMLDataBindingToBytes()))
                .withSchema(schema)
                .build();
    }

    /**
     * Same as createCommandLineArgumentXmlFileWithSchema,
     * but it removes the xml header before
     * writing it to a file.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param schema schema of the xml
     * @param flag optional flag for the command line argument
     * @return xml file command line argument
     */
    public IInputParameter
    createCommandLineArgumentXmlFileWithSchemaWithoutHeader(
            final String identifier,
            final String optionalAbstract,
            final String schema,
            final String flag,
            final boolean isOptional) {

        final String filename = createUUIDFilename(".xml");
        final XmlBindingWithAllowedSchema validator;

        if (schema == null || schema.trim().length() == 0) {
            validator = null;
        } else {
            validator = new XmlBindingWithAllowedSchema(schema);
        }

        return new InputParameterImpl.Builder(
                identifier, GenericXMLDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToTransformToCmd(
                        new FileToStringCmd(filename, flag))
                .withPath(filename)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGenericXMLDataBindingToBytesWithoutHeader()))
                .withSchema(schema)
                .withValidator(validator)
                .build();
    }

    /**
     * Same as  createCommandLineArgumentXmlFileWithSchema but with QuakeML.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional flag for the command line argument
     * @return quakeml xml file command line argument
     */
    public IInputParameter createCommandLineArgumentQuakeML(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final boolean isOptional) {
        final String filename = createUUIDFilename(".xml");

        final String schema = IMimeTypeAndSchemaConstants.SCHEMA_QUAKE_ML;

        return new InputParameterImpl.Builder(
                identifier, QuakeMLXmlDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToTransformToCmd(
                        new FileToStringCmd(filename, flag))
                .withPath(filename)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGenericXMLDataBindingToBytes()))
                .withSchema(schema)
                .withValidator(new XmlBindingWithAllowedSchema(schema))
                .build();
    }

    /**
     * Creates a command line argument (geotiff file) with a file path that
     * will be written down as a temporary file.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional command line flag
     * @return geotiff file command line argument
     */
    public IInputParameter createCommandLineArgumentGeotiff(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final boolean isOptional) {
        final String filename = createUUIDFilename(".tiff");

        return new InputParameterImpl.Builder(
                identifier, GeotiffBinding.class, isOptional, optionalAbstract)
                .withFunctionToTransformToCmd(
                        new FileToStringCmd(filename, flag))
                .withPath(filename)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGeotiffBindingToBytes()))
                .build();
    }

    /**
     * Creates a command line argument (geojson) with a file path that will
     * be written down as a temporary file.
     * @param identifier identifier of the data
     * @param optionalDescription optional description of the parameter
     * @param flag optional command line flag
     * @return geojson file command line argument
     */
    public IInputParameter createCommandLineArgumentGeojson(
            final String identifier,
            final String optionalDescription,
            final String flag,
            final boolean isOptional) {
        final String filename = createUUIDFilename(".json");
        return new InputParameterImpl.Builder(
                identifier, GTVectorDataBinding.class, isOptional, optionalDescription)
                .withFunctionToTransformToCmd(
                        new FileToStringCmd(filename, flag))
                .withPath(filename)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGTVectorDataBindingToBytes(
                                        ConvertGTVectorDataBindingToBytes.Format.JSON
                                )))
                .build();
    }

    /**
     * Creates a command line argument (shapefile) with a file path that will
     * be written down as a temporary file
     * (or multiple files, because one shapefile contains multiple files).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional command line flag
     * @return shapefile command line argument
     */
    public IInputParameter createCommandLineArgumentShapeFile(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final boolean isOptional) {

        final String filename = createUUIDFilename(".shp");

        return new InputParameterImpl.Builder(
                identifier, GTVectorDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToTransformToCmd(
                        new FileToStringCmd(filename, flag))
                .withPath(filename)
                .withFunctionToWriteToFiles(
                        new WriteShapeFileToPath())
                .build();
    }


    /**
     * Creates a command line argument (generic file) with a file path that
     * will be written down as a temporary file.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional command line flag
     * @return file command line argument
     */
    public IInputParameter createCommandLineArgumentFile(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final boolean isOptional) {
        final String filename = createUUIDFilename(".dat");

        return new InputParameterImpl.Builder(
                identifier, GenericFileDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToTransformToCmd(
                        new FileToStringCmd(filename, flag))
                .withPath(filename)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGenericFileDataBindingToBytes()))
                .build();
    }

    /**
     * Creates a command line argument (json file) with a file path that will
     * be written down as a temporary file.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param flag optional command line flag
     * @return json command line argument
     */
    public IInputParameter createCommandLineArgumentJson(
            final String identifier,
            final String optionalAbstract,
            final String flag,
            final boolean isOptional) {
        final String filename = createUUIDFilename(".json");

        return new InputParameterImpl.Builder(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToTransformToCmd(
                        new FileToStringCmd(filename, flag))
                .withPath(filename)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertJsonDataBindingToBytes()))
                .build();
    }

    /**
     * Creates a stdin input with a string.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param defaultValue optional default value of the argument
     * @param allowedValues optional list with allowed values
     * @return object with information about how to use the value as
     * a string stdin input parameter
     */
    public IInputParameter createStdinString(
            final String identifier,
            final String optionalAbstract,
            final String defaultValue,
            final List<String> allowedValues,
            final boolean isOptional) {

        final InputParameterImpl.Builder builder =
                new InputParameterImpl.Builder(
                        identifier, LiteralStringBinding.class, isOptional, optionalAbstract);
        builder.withFunctionToWriteToStdin(
                new ConvertLiteralStringToBytes());

        if (defaultValue != null) {
            builder.withDefaultValue(defaultValue);
        }
        if (allowedValues != null && (!allowedValues.isEmpty())) {
            builder.withAllowedValues(allowedValues);
            builder.withValidator(
                    new LiteralStringBindingWithAllowedValues(
                            allowedValues));
        }
        return builder.build();
    }

    /**
     * Creates a stdin input with json.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @return object with information about how to use the value
     * as a json stdin input parameter
     */
    public IInputParameter createStdinJson(
            final String identifier,
            final String optionalAbstract,
            final boolean isOptional) {
        return new InputParameterImpl.Builder(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withFunctionToWriteToStdin(
                        new ConvertJsonDataBindingToBytes())
                .build();
    }

    /**
     * Creates a input file argument (geotiff file).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to write before starting the process
     * @return geotiff input file
     */
    public IInputParameter createFileInGeotiff(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new InputParameterImpl.Builder(
                identifier, GeotiffBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGeotiffBindingToBytes()))
                .build();
    }

    /**
     * Creates a input file argument (geojson file).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to write before staring the process
     * @return geojson input file
     */
    public IInputParameter createFileInGeojson(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new InputParameterImpl.Builder(
                identifier, GTVectorDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGTVectorDataBindingToBytes(
                                        ConvertGTVectorDataBindingToBytes.Format.JSON
                                )))
                .build();
    }

    /**
     * Creates a input file argument (shapefile - with all the other files to
     * care about).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to write before starting the process
     *             (just the .shp file)
     * @return shapefile input file
     */
    public IInputParameter createFileInShapeFile(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new InputParameterImpl.Builder(
                identifier, GTVectorDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToWriteToFiles(
                        new WriteShapeFileToPath())
                .build();
    }

    /**
     * Creates an input file argument with quakeml.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to write before starting the process
     * @return quakeml input file
     */
    public IInputParameter createFileInQuakeML(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {

        final String schema = IMimeTypeAndSchemaConstants.SCHEMA_QUAKE_ML;

        return new InputParameterImpl.Builder(
                identifier, QuakeMLXmlDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGenericXMLDataBindingToBytes()))
                .withSchema(schema)
                .withValidator(
                        new XmlBindingWithAllowedSchema(schema))
                .build();
    }

    /**
     * Creates an input file argument with shakemap.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to write before starting the process
     * @return shakemap input file
     */
    public IInputParameter createFileInShakemap(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {

        final String schema = IMimeTypeAndSchemaConstants.SCHEMA_SHAKEMAP;

        return new InputParameterImpl.Builder(
                identifier, ShakemapXmlDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGenericXMLDataBindingToBytes()))
                .withSchema(schema)
                .withValidator(
                        new XmlBindingWithAllowedSchema(schema))
                .build();
    }

    /**
     * Creates an input file argument with json.
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of file to write before starting the process
     * @return json input file
     */
    public IInputParameter createFileInJson(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {

        return new InputParameterImpl.Builder(
                identifier, JsonDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertJsonDataBindingToBytes()))
                .build();
    }


    /**
     * Creates a input file argument (generic).
     * @param identifier identifier of the data
     * @param optionalAbstract optional description of the parameter
     * @param path path of the file to write before staring the process
     * @return generic input file
     */
    public IInputParameter createFileInGeneric(
            final String identifier,
            final String optionalAbstract,
            final String path,
            final boolean isOptional) {
        return new InputParameterImpl.Builder(
                identifier, GenericFileDataBinding.class, isOptional, optionalAbstract)
                .withPath(path)
                .withFunctionToWriteToFiles(
                        new WriteSingleByteStreamToPath(
                                new ConvertGenericFileDataBindingToBytes()))
                .build();
    }

    /**
     *
     * @param ending file ending
     * @return a unique file name
     */
    private String createUUIDFilename(final String ending) {
        final String prefix = "inputfile";
        return prefix + UUID.randomUUID() + ending;
    }
}
