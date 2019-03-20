package org.n52.gfz.riesgos.functioninterfaces;

import net.opengis.wps.x100.OutputDescriptionType;

@FunctionalInterface
public interface IAddTypeIntoOutputDescriptionType {
    public void addType(final OutputDescriptionType type);
}
