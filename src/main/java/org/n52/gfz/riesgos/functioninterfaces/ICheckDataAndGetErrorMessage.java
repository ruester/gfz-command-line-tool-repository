package org.n52.gfz.riesgos.functioninterfaces;

import org.n52.wps.io.data.IData;

import java.util.Optional;

@FunctionalInterface
public interface ICheckDataAndGetErrorMessage {

    public Optional<String> check(final IData data);
}
