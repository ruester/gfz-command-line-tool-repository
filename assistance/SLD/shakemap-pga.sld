<?xml version="1.0" encoding="UTF-8"?>
<!-- Created by SLD Editor 0.7.7 -->
<!-- Copied from https://github.com/riesgos/riesgos-frontend/issues/22#issuecomment-523379744 -->
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name/>
    <sld:UserStyle>
      <sld:Name>shakemap-pga</sld:Name>
      <sld:FeatureTypeStyle>
        <sld:Name>name</sld:Name>
        <sld:Rule>
          <sld:RasterSymbolizer>
            <sld:ColorMap>
              <sld:ColorMapEntry color="#6699FF" opacity="0" quantity="0" label=""/>
              <sld:ColorMapEntry color="#6699FF" opacity="1.0" quantity="0.09" label=""/>
              <sld:ColorMapEntry color="#009999" opacity="1.0" quantity="0.16" label=""/>
              <sld:ColorMapEntry color="#009966" opacity="1.0" quantity="0.25" label=""/>
              <sld:ColorMapEntry color="#00CC33" opacity="1.0" quantity="0.36" label=""/>
              <sld:ColorMapEntry color="#71C627" opacity="1.0" quantity="0.5"/>
              <sld:ColorMapEntry color="#CCFF00" opacity="1.0" quantity="0.62" label=""/>
              <sld:ColorMapEntry color="#FFFF00" opacity="1.0" quantity="0.7" label=""/>
              <sld:ColorMapEntry color="#FFCC00" opacity="1.0" quantity="0.81" label=""/>
              <sld:ColorMapEntry color="#FF9900" opacity="1.0" quantity="0.96" label=""/>
              <sld:ColorMapEntry color="#FF0000" opacity="1.0" quantity="1" label=""/>
            </sld:ColorMap>
            <sld:ContrastEnhancement>
              <sld:GammaValue>1.0</sld:GammaValue>
            </sld:ContrastEnhancement>
          </sld:RasterSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>
