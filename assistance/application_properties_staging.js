var applicationProperties = {
    wpsServices: [
        // "http://geoprocessing.demo.52north.org:8080/wps/WebProcessingService",
        // "https://riesgos.52north.org/wps/WebProcessingService",
        // "http://tsunami-riesgos.awi.de:8080/wps/WebProcessingService"
        "__WPS_URL__"
    ],
    serviceVersion: "2.0.0",         // "1.0.0", "2.0.0"
    selectedServiceUrl: "__WPS_URL__",
    skipWpsSetup: true,              // true, false,
    reuseGeoJSONOutput: true,
    mapStartCenter: [ - 33.2551, -70.8676 ],
    mapStartZoom: 7,
    defaultLanguage: "en",           // "en", "de"
    complexInputDataSetup: {
        defaultMimetypeIfAvailable: "application/vnd.geo+json",
        defaultSchemaIfAvailable: "",
        defaultEncodingIfAvailable: ""
    },
    complexOutputDataSetup: {
        defaultMimetypeIfAvailable: "application/vnd.geo+json",
        defaultSchemaIfAvailable: "",
        defaultEncodingIfAvailable: "",
        defaultTransmissionMode: "value"
    }
};
