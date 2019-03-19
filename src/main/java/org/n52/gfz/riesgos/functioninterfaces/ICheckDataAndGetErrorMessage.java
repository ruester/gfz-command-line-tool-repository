package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.wps.io.data.IData;

import java.util.Optional;

/**
 * Interface to check an idata element
 */
@FunctionalInterface
public interface ICheckDataAndGetErrorMessage {

    /**
     * Checks a IData and (maybe) gives back the text of the problem
     * @param data element to check
     * @return empty if there is no problem with the value; else the text of the problem description
     */
    Optional<String> check(final IData data);
}
