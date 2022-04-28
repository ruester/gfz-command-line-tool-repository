<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" version="1.0.0">
  <sld:NamedLayer>
    <sld:Name>Accumulated economic damage</sld:Name>
    <sld:UserStyle>
      <sld:Name>style-cum-loss</sld:Name>
      <sld:FeatureTypeStyle>
        <sld:Name>name</sld:Name>

        <sld:Rule>
          <sld:Name>0 - 124,999</sld:Name>
          <ogc:Filter>
            <ogc:PropertyIsLessThan>
                <ogc:PropertyName>cum_loss</ogc:PropertyName>
                <ogc:Literal>125000</ogc:Literal>
              </ogc:PropertyIsLessThan>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:CssParameter name="fill">#8cbaa7</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#232323</sld:CssParameter>
              <sld:CssParameter name="stroke-linejoin">bevel</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>

        <sld:Rule>
          <sld:Name>125,000 - 249,999</sld:Name>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsLessThanOrEqualTo>
                  <ogc:Literal>125000</ogc:Literal>
                  <ogc:PropertyName>cum_loss</ogc:PropertyName>
                </ogc:PropertyIsLessThanOrEqualTo>
              <ogc:PropertyIsLessThan>
                  <ogc:PropertyName>cum_loss</ogc:PropertyName>
                  <ogc:Literal>250000</ogc:Literal>
                </ogc:PropertyIsLessThan>
            </ogc:And>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:CssParameter name="fill">#e8e9ab</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#232323</sld:CssParameter>
              <sld:CssParameter name="stroke-linejoin">bevel</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>

        <sld:Rule>
          <sld:Name>250,000 - 499,999</sld:Name>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsLessThanOrEqualTo>
                  <ogc:Literal>250000</ogc:Literal>
                  <ogc:PropertyName>cum_loss</ogc:PropertyName>
                </ogc:PropertyIsLessThanOrEqualTo>
              <ogc:PropertyIsLessThan>
                  <ogc:PropertyName>cum_loss</ogc:PropertyName>
                  <ogc:Literal>500000</ogc:Literal>
                </ogc:PropertyIsLessThan>
            </ogc:And>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:CssParameter name="fill">#fed7aa</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#232323</sld:CssParameter>
              <sld:CssParameter name="stroke-linejoin">bevel</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>

        <sld:Rule>
          <sld:Name>>= 500,000</sld:Name>
          <ogc:Filter>
            <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:Literal>500000</ogc:Literal>
                <ogc:PropertyName>cum_loss</ogc:PropertyName>
              </ogc:PropertyIsLessThanOrEqualTo>
          </ogc:Filter>
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:CssParameter name="fill">#d78b8b</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">#232323</sld:CssParameter>
              <sld:CssParameter name="stroke-linejoin">bevel</sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>
    </sld:UserStyle>
  </sld:NamedLayer>
</sld:StyledLayerDescriptor>
