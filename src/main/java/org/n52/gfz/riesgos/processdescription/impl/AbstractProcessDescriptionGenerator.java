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

package org.n52.gfz.riesgos.processdescription.impl;

import net.opengis.wps.x100.ComplexDataCombinationType;
import net.opengis.wps.x100.ComplexDataCombinationsType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataType;
import org.n52.gfz.riesgos.processdescription.IProcessDescriptionGenerator;
import org.n52.wps.io.IGenerator;
import org.n52.wps.io.IParser;
import org.n52.wps.webapp.api.FormatEntry;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract class for IProcessDescriptionGenerator implementations.
 */
public abstract class AbstractProcessDescriptionGenerator
        implements IProcessDescriptionGenerator {

    /**
     * Empty string.
     */
    private static final String EMPTY = "";

    /**
     * Takes all the constructors and searches for one constructor
     * getting a single element the class of this element is the
     * name to search for.
     *
     * Example:
     * I search in the constructors of LiteralStringBinding
     * There is a constructor using a single string argument
     * --> the result is string
     *
     * Same for LiteralDoubleBinding and so on
     *
     * @param constructors constructors of a class
     * @return optional simple name of a class
     */
    protected Optional<String> findSimpleNameOfFirstConstructorParameter(
            final Constructor<?>[] constructors) {
        Optional<String> result = Optional.empty();
        for (final Constructor<?> constructor : constructors) {
            final Class<?>[] supportedClasses = constructor.getParameterTypes();
            if (supportedClasses.length == 1) {
                result = Optional.of(supportedClasses[0].getSimpleName());
            }
        }
        return result;
    }

    /**
     * Searches for all the interfaces a class implements.
     * It is used to search for IData implementations that are using
     * the classes for LiteralData and ComplexData.
     * If the current class has no interfaces the search will be extended
     * to the super class.
     *
     * @param clazz class to search the interfaces for
     * @return list with interfaces
     */
    protected List<Class<?>> findInterfaces(final Class<?> clazz) {
        final List<Class<?>> result = Arrays.asList(clazz.getInterfaces());
        final Class<?> superClass = clazz.getSuperclass();
        if (result.isEmpty() && superClass != null) {
            return findInterfaces(superClass);
        }
        return result;
    }

    /**
     * Searches for a parser that supports a specific binding class.
     * @param allParsers list with all parsers
     * @param clazz class to search if the parser supports it
     * @return list with parsers that support the class
     */
    protected List<IParser> findParser(
            final List<IParser> allParsers, final Class<?> clazz) {
        return allParsers.stream().filter(
                new ParserSupportsClass(clazz)).collect(Collectors.toList());
    }

    /**
     * Searches for a generator that supports a specific binding clas.
     * @param allGenerators list with all generators
     * @param clazz class that must be supported
     * @return list with the generators that support the class
     */
    protected List<IGenerator> findGenerators(
            final List<IGenerator> allGenerators, final Class<?> clazz) {
        return allGenerators.stream().filter(
                new GeneratorSupportsClass(clazz)).collect(Collectors.toList());
    }

    /**
     * Extracts the formats for the parsers.
     * @param foundParsers list with parsers
     * @return list with formats
     */
    protected List<FormatEntry> extractFormatsFromParsers(
            final List<IParser> foundParsers) {
        return foundParsers.stream()
                .map(IParser::getSupportedFullFormats)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    /**
     * Extracts the formats for the generators.
     * @param foundGenerators list with generators
     * @return list with formats
     */
    protected List<FormatEntry> extractFormatsFromGenerators(
            final List<IGenerator> foundGenerators) {
        return foundGenerators.stream()
                .map(IGenerator::getSupportedFullFormats)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Adds the complex output formats from the given defaultFormat
     * and the format entries.
     * @param complexData supported complex data type to add the formats
     * @param defaultFormat default format (must also be included in the the
     *                      list with format entries
     * @param formatEntries iterable with formats
     */
    protected void addFormats(
            final SupportedComplexDataType complexData,
            final FormatEntry defaultFormat,
            final Iterable<FormatEntry> formatEntries) {
        final ComplexDataCombinationsType complexDataCombinationsType =
                complexData.addNewSupported();

        if (complexData.getDefault() == null) {
            final ComplexDataCombinationType defaultInputFormat =
                    complexData.addNewDefault();
            final ComplexDataDescriptionType supportedFormat =
                    defaultInputFormat.addNewFormat();
            supportedFormat.setMimeType(defaultFormat.getMimeType());
            final String encoding = defaultFormat.getEncoding();
            if (encoding != null && !encoding.equals(EMPTY)) {
                supportedFormat.setEncoding(encoding);
            }

            final String schema = defaultFormat.getSchema();
            if (schema != null && !schema.equals(EMPTY)) {
                supportedFormat.setSchema(schema);
            }
        }

        for (final FormatEntry formatEntry : formatEntries) {

            final ComplexDataDescriptionType supportedFormat =
                    complexDataCombinationsType.addNewFormat();
            supportedFormat.setMimeType(formatEntry.getMimeType());
            final String encoding = formatEntry.getEncoding();
            if (encoding != null && !encoding.equals(EMPTY)) {
                supportedFormat.setEncoding(encoding);
            }
            final String schema = formatEntry.getSchema();
            if (schema != null && !schema.equals(EMPTY)) {
                supportedFormat.setSchema(schema);
            }
        }

    }

    /**
     * Predicate to filter parser that support the class.
     */
    private static class ParserSupportsClass implements Predicate<IParser> {
        /**
         * Clazz that must be supported.
         */
        private final Class<?> clazz;

        /**
         * Default constructor.
         * @param aClazz binding class to test
         */
        ParserSupportsClass(final Class<?> aClazz) {
            this.clazz = aClazz;
        }

        /**
         * True if the parser supports this binding class.
         * @param parser parser to test
         * @return true if the parser supports the class
         */
        @Override
        public boolean test(final IParser parser) {
            return Arrays.asList(
                    parser.getSupportedDataBindings()).contains(clazz);
        }
    }

    /**
     * Predicate to filter generators that support the class.
     */
    private static class GeneratorSupportsClass
            implements Predicate<IGenerator> {

        /**
         * Clazz that must be supported.
         */
        private final Class<?> clazz;

        /**
         * Default constructor.
         * @param aClazz binding class to test
         */
        GeneratorSupportsClass(final Class<?> aClazz) {
            this.clazz = aClazz;
        }

        /**
         * Tests the generator if it support the clazz.
         * @param generator generator to test
         * @return true if the generator supports this clazz
         */
        @Override
        public boolean test(final IGenerator generator) {
            return Arrays.asList(
                    generator.getSupportedDataBindings()).contains(clazz);
        }
    }
}
