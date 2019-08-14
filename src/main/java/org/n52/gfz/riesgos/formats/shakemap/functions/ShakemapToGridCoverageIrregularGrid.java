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

package org.n52.gfz.riesgos.formats.shakemap.functions;

//import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
//import org.geotools.coverage.grid.GridCoverageFactory;
//import org.geotools.geometry.jts.ReferencedEnvelope;
//import org.geotools.referencing.CRS;
import org.n52.gfz.riesgos.formats.shakemap.IShakemap;
//import org.n52.gfz.riesgos.formats.shakemap.IShakemapData;
//import org.n52.gfz.riesgos.formats.shakemap.IShakemapField;
//import org.n52.gfz.riesgos.formats.shakemap.IShakemapSpecification;
//import org.opengis.geometry.Envelope;
//import org.opengis.referencing.FactoryException;
//import org.opengis.referencing.crs.CoordinateReferenceSystem;
//import smile.interpolation.KrigingInterpolation;

//import javax.media.jai.RasterFactory;
import java.awt.image.DataBuffer;
//import java.awt.image.WritableRaster;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.OptionalDouble;
import java.util.function.Function;
//import java.util.stream.Collectors;

/**
 * Class to interpolate the grid in case of
 * irregular points.
 *
 * This class is not used at the moment, because
 * the grid of the shakemaps are regular at the moment,
 * so there is no need to interpolate the points.
 */
public class ShakemapToGridCoverageIrregularGrid
        implements Function<IShakemap, GridCoverage2D> {

    /**
     * Name for the coverage.
     */
    private static final String COVERAGE_NAME = "Shakemap";
    /**
     * Data type that should be used for the grids.
     */
    private static final int DATA_TYPE = DataBuffer.TYPE_DOUBLE;

    /**
     *
     * @param shakemap
     * @return interpolated grid
     */
    @Override
    public GridCoverage2D apply(final IShakemap shakemap) {

        throw new UnsupportedOperationException(
                "Please delete the comments that hide the code if you want"
                        + "to use the interpolation function");
    }
        /*
        final IInterpolator interp = new InterpolateViaKriging(shakemap);


        final IShakemapSpecification specification =
                new EstimateSpecification(shakemap);

        // I have the bounding box here
        final double minX = specification.getLonMin();
        final double maxX = specification.getLonMax();
        final double minY = specification.getLatMin();
        final double maxY = specification.getLatMax();

        // and I want to have 1000 pixels in width
        final int width = 1000;
        // -> compute the height by using the aspect ratio of the
        // bounding box
        final int height = (int) (width * (Math.abs(maxY - minY) /
        Math.abs(maxX - minX)));



        final double diffX = (maxX - minX) / width;
        final double diffY = (maxY - minY) / height;

        final List<IShakemapField> fields = shakemap.getFields();

        final List<IShakemapField> customFields = fields.stream()
                .filter(IShakemapField::isCustom)
                .collect(Collectors.toList());
        final WritableRaster raster = RasterFactory.createBandedRaster(
                DATA_TYPE,
                width,
                height,
                customFields.size(),
                null
        );

        for (
                int bandIndex = 0;
                bandIndex < customFields.size();
                bandIndex += 1
        ) {

            for(int indexWidth = 0; indexWidth < width; indexWidth += 1) {
                final double lon = transformWidthToLon(
                indexWidth, width, minX, maxX);

                for(int indexHeight = 0;
                indexHeight < height;
                indexHeight += 1) {
                    final double lat = transformHeightToLat(
                    indexHeight, height, minY, maxY);

                    final double value =
                    interp.interpolate(lon, lat, bandIndex);
                    raster.setSample(indexWidth, indexHeight,
                    bandIndex, value);
                }
            }
        }

        // long (x), lat (y)
        final CoordinateReferenceSystem crs = findWgs84();

        // making the envelope bigger means that the
        // locations are in the middle of the points
        final double minXExtended = minX - diffX / 2;
        final double maxXExtended = maxX + diffX / 2;
        final double minYExtended = minY - diffY / 2;
        final double maxYExtended = maxY + diffY / 2;

        final Envelope envelope = new ReferencedEnvelope(
                minXExtended, maxXExtended,
                minYExtended, maxYExtended,
                crs);

        final GridCoverageFactory factory =
                CoverageFactoryFinder.getGridCoverageFactory(null);

        return factory.create(COVERAGE_NAME, raster, envelope);
    }

    private interface IInterpolator {
        double interpolate(final double lon, final double lat, final int band);
    }


    private static class InterpolateViaKriging implements IInterpolator {

        private final List<KrigingInterpolation> interp;

        InterpolateViaKriging(
                final IShakemap shakemap) {

            final List<IShakemapData> shakemapData = shakemap.getData();
            final List<IShakemapField> customFields = shakemap.getFields()
            .stream()
            .filter(IShakemapField::isCustom).collect(Collectors.toList());

            final double[][] pointCoordinates =
            new double[shakemapData.size()][2];
            final List<double[]> valuesOnThatPoints = new ArrayList<>();

            for(final IShakemapField customField : customFields) {
                valuesOnThatPoints.add(new double[shakemapData.size()]);
            }

            for(int pointIndex = 0;
            pointIndex < shakemapData.size();
            pointIndex += 1) {

                final IShakemapData singlePoint = shakemapData.get(pointIndex);

                pointCoordinates[pointIndex][0] = singlePoint.getLon();
                pointCoordinates[pointIndex][1] = singlePoint.getLat();

                final Map<String, Double> customValues =
                singlePoint.getCustomValues();

                for(int customIndex = 0;
                customIndex < customFields.size();
                customIndex += 1) {
                    final IShakemapField customField =
                    customFields.get(customIndex);
                    final double customValue =
                    customValues.get(customField.getName());
                    valuesOnThatPoints.get(customIndex)[pointIndex] =
                    customValue;
                }
            }

            interp = new ArrayList<>();

            for(final double[] values : valuesOnThatPoints) {
                interp.add(
                new KrigingInterpolation(pointCoordinates, values));
            }
        }

        @Override
        public double interpolate(
        final double lon, final double lat, final int band) {
            return interp.get(band).interpolate(lon, lat);
        }
    }

    private double transformWidthToLon(
            final int widthPosition, final int overallWidth,
            final double lonMin, final double lonMax) {

        // this is a number between zero and one
        final double pos = 1.0 * widthPosition / overallWidth;
        final double overallDist = lonMax - lonMin;
        final double part = pos * overallDist;
        // lonMin is start
        return part + lonMin;
    }

    private double transformHeightToLat(
            final int heightPosition, final int overallHeight,
            final double latMin, final double latMax) {
        // this is a number between zero and one
        final double pos = 1.0 * heightPosition / overallHeight;
        final double overallDist = latMax - latMin;
        final double part = pos * overallDist;
        // latMax is start
        // the coordinates for the y part is inverted (upper left is 0,0)
        return latMax - part;
    }


    private CoordinateReferenceSystem findWgs84() {

        try {
            return CRS.decode("EPSG:4326");
        } catch (FactoryException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static class EstimateSpecification
    implements IShakemapSpecification {

        private final double latMax;
        private final double latMin;
        private final double lonMax;
        private final double lonMin;

        EstimateSpecification(final IShakemap shakemap) {
            OptionalDouble optionalLatMax = OptionalDouble.empty();
            OptionalDouble optionalLatMin = OptionalDouble.empty();
            OptionalDouble optionalLonMax = OptionalDouble.empty();
            OptionalDouble optionalLonMin = OptionalDouble.empty();

            for(final IShakemapData shakemapData : shakemap.getData()) {
                // one block for lat
                {
                    final double pointLat = shakemapData.getLat();
                    if ((!optionalLatMax.isPresent()) ||
                    optionalLatMax.getAsDouble() < pointLat) {
                        optionalLatMax = OptionalDouble.of(pointLat);
                    }
                    if ((!optionalLatMin.isPresent()) ||
                    optionalLatMin.getAsDouble() > pointLat) {
                        optionalLatMin = OptionalDouble.of(pointLat);
                    }
                }
                // one block for lon
                {
                    final double pointLon = shakemapData.getLon();
                    if ((!optionalLonMax.isPresent()) ||
                    optionalLonMax.getAsDouble() < pointLon) {
                        optionalLonMax = OptionalDouble.of(pointLon);
                    }
                    if ((!optionalLonMin.isPresent()) ||
                    optionalLonMin.getAsDouble() > pointLon) {
                        optionalLonMin = OptionalDouble.of(pointLon);
                    }
                }
            }

            latMax = optionalLatMax.getAsDouble();
            latMin = optionalLatMin.getAsDouble();
            lonMax = optionalLonMax.getAsDouble();
            lonMin = optionalLonMin.getAsDouble();
        }

        @Override
        public double getLatMax() {
            return latMax;
        }

        @Override
        public double getLatMin() {
            return latMin;
        }

        @Override
        public double getLonMax() {
            return lonMax;
        }

        @Override
        public double getLonMin() {
            return lonMin;
        }

        @Override
        public int getNLat() {
            throw new UnsupportedOperationException(
            "getNLat is not supported here");
        }

        @Override
        public int getNLon() {
            throw new UnsupportedOperationException(
            "getNLon is not supported here");
        }

        @Override
        public double getNominalLatSpacing() {
            throw new UnsupportedOperationException(
            "getNominalLatSpacing is not supported here");
        }

        @Override
        public double getNominalLonSpacing() {
            throw new UnsupportedOperationException(
            "getNominalLonSpacing is not supported here");
        }

        @Override
        public boolean isRegular() {
            return false;
        }*/
}
