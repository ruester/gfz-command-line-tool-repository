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

/**
 * The enum follows the documentation on
 * https://usgs.github.io/shakemap/manual3_5/tg_intensity.html
 *
 *
 * The classification of the pga value into intensity values
 * is mostly straight forward,
 * however it starts with intensity ONE for all values below 0.05.
 * Because ZERO and ONE have the same color it is okay to not
 * care about the difference here.
 *
 * Also there is a classification for the values between
 * 0.05 up to 0.3 in level two or three.
 *
 * In order to use the full spectrum of the colors defined here,
 * we created a level border at 0.18.
 *
 * The rest is good documented.
 *
 */
public enum PgaIntensityLevel {

    /**
     * Level Zero.
     */
    ZERO(255, 255, 255, 0.0),
    /**
     * Level One.
     */
    ONE(255, 255, 255, 0.05),
    /**
     * Level Two.
     */
    TWO(191, 204, 255, 0.18),
    /**
     * Level Three.
     */
    THREE(160, 230, 255, 0.3),
    /**
     * Level Four.
     */
    FOUR(128, 255, 255, 2.8),
    /**
     * Level Five.
     */
    FIVE(122, 255, 147, 6.2),
    /**
     * Level Six.
     */
    SIX(255, 255, 0, 12),
    /**
     * Level Seven.
     */
    SEVEN(255, 200, 0, 22),
    /**
     * Level Eight.
     */
    EIGHT(255, 145, 0, 40),
    /**
     * Level Nine.
     */
    NINE(255, 0, 0, 139),
    /**
     * Level Ten.
     */
    TEN(200, 0, 0, Double.POSITIVE_INFINITY);

    /**
     * This variable stores the value for the red band
     * on a rgb visualization.
     */
    private final int red;

    /**
     * This variable stores the value for the green band
     * on a rgb visualization.
     */
    private final int green;
    /**
     * This variable stores the value for the blue band
     * on a rgb visualization.
     */
    private final int blue;
    /**
     * This is for storing the pga value that is the limit
     * up to this level is used for the pga.
     * The ordering is defined by the order in the enum.
     */
    private final double upperLimitForPga;

    /**
     * Constructor with red, green, blue and a limit for the pga.
     * @param aRed red value for rgb
     * @param aGreen green value for rgb
     * @param aBlue blue value of rgb
     * @param aUpperLimitForPga upper limit for the pga classification
     */
    PgaIntensityLevel(
            final int aRed,
            final int aGreen,
            final int aBlue,
            final double aUpperLimitForPga) {
        this.red = aRed;
        this.green = aGreen;
        this.blue = aBlue;
        this.upperLimitForPga = aUpperLimitForPga;
    }

    /**
     * Static method to classify the pga value in
     * IntensityLevel.
     * @param pga pga value of a shakemap
     * @return intensity level
     */
    public static PgaIntensityLevel classifyPga(final double pga) {

        for (final PgaIntensityLevel intensityLevel : values()) {
            if (pga < intensityLevel.upperLimitForPga) {
                return intensityLevel;
            }
        }

        return getLast();
    }

    /**
     *
     * @return the last element of the
     */
    private static PgaIntensityLevel getLast() {
        final PgaIntensityLevel[] vals = values();
        return vals[vals.length - 1];
    }

    /**
     *
     * @return red value for rgb
     */
    public int getRed() {
        return red;
    }

    /**
     *
     * @return green value for rgb
     */
    public int getGreen() {
        return green;
    }

    /**
     *
     * @return blue value for rgb
     */
    public int getBlue() {
        return blue;
    }

    /**
     *
     * @return the upper limit for this intensity class
     */
    public double getUpperLimitForPga() {
        return upperLimitForPga;
    }
}
