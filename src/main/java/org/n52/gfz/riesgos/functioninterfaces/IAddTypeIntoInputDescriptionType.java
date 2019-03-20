package org.n52.gfz.riesgos.functioninterfaces;

import net.opengis.wps.x100.InputDescriptionType;

@FunctionalInterface
public interface IAddTypeIntoInputDescriptionType {

    public void addType(final InputDescriptionType type);

}
