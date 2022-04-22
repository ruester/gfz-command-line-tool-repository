#!/usr/bin/env python3

import time
import collections
import requests
import lxml.etree as le

ResultEncoding = collections.namedtuple(
    "ResultEncoding", ["mime_type", "encoding", "schema"]
)

Bbox = collections.namedtuple(
    "Bbox",
    [
        "lonmin",
        "lonmax",
        "latmin",
        "latmax",
    ],
)


class ExecutionNotAcceptedException(Exception):
    pass


class ExecutionNotSuccessfulException(Exception):
    pass


class WpsServer:
    def __init__(self, endpoint):
        self.endpoint = endpoint

    @classmethod
    def dev(cls):
        return cls("http://localhost:8080/wps/WebProcessingService")

    @classmethod
    def staging(cls):
        return cls("https://rz-vm140.gfz-potsdam.de:8443/wps/WebProcessingService")

    @classmethod
    def prod(cls):
        return cls("https://rz-vm140.gfz-potsdam.de/wps/WebProcessingService")

    def execute_async(self, payload):
        resp = requests.post(
            self.endpoint, payload, headers={"Content-Type": "text/xml"}
        )
        resp.raise_for_status()
        status_info = le.fromstring(resp.content)
        status = status_info.find("{http://www.opengis.net/wps/2.0}Status")
        if not status.text == "Accepted":
            raise ExecutionNotAcceptedException(status.text)
        job_id = status_info.find("{http://www.opengis.net/wps/2.0}JobID")
        return job_id.text

    def block_until_done(self, job_id):
        is_running = True
        while is_running:
            resp = requests.get(
                self.endpoint,
                {
                    "service": "WPS",
                    "version": "2.0.0",
                    "request": "GetStatus",
                    "jobId": job_id,
                    "language": "en-US",
                },
            )
            resp.raise_for_status()
            status_report = le.fromstring(resp.content)
            status = status_report.find("{http://www.opengis.net/wps/2.0}Status")
            if status.text not in ["Running", "Accepted"]:
                is_running = False
            time.sleep(1)
        if status.text != "Succeeded":
            raise ExecutionNotSuccessfulException(status.text)

    def get_result_links(self, job_id):
        resp = requests.get(
            self.endpoint,
            {
                "service": "WPS",
                "version": "2.0.0",
                "request": "GetResult",
                "jobId": job_id,
                "language": "en-US",
            },
        )
        resp.raise_for_status()
        outputs = {}
        result = le.fromstring(resp.content)
        for output in result.findall("{http://www.opengis.net/wps/2.0}Output"):
            id = output.attrib["id"]
            for reference in output.findall(
                "{http://www.opengis.net/wps/2.0}Reference"
            ):
                schema = reference.attrib.get("schema")
                encoding = reference.attrib.get("encoding")
                mime_type = reference.attrib.get("mimeType")
                key = ResultEncoding(
                    schema=schema, encoding=encoding, mime_type=mime_type
                )
                href = reference.attrib.get("{http://www.w3.org/1999/xlink}href")

                outputs.setdefault(id, {})
                outputs[id][key] = href
        return outputs


class QuakeledgerProcess:
    def __init__(self, server):
        self.server = server

    def send_request(self, bbox):
        template = """
            <wps:Execute service="WPS" version="2.0.0" xmlns:wps="http://www.opengis.net/wps/2.0"
                xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wpsExecute.xsd"
                response="document" mode="async">
                <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.QuakeledgerProcess</ows:Identifier>
                <wps:Input id="mmin">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>6.6</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="mmax">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>8.5</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="zmin">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>5</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="zmax">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>140</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="p">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>0.1</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="etype">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>observed</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="tlon">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>-71.5730623712764</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="tlat">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>-33.1299174879672</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="input-boundingbox">
                    <wps:Data>
                        <ows:BoundingBox crs="EPSG:4326" dimensions="2">
                            <ows:LowerCorner>:LATMIN: :LONMIN:</ows:LowerCorner>
                            <ows:UpperCorner>:LATMAX: :LONMAX:</ows:UpperCorner>
                        </ows:BoundingBox>
                    </wps:Data>
                </wps:Input>
                <wps:Output id="selectedRows" transmission="reference" schema="http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd"
                    mimeType="text/xml" encoding="UTF-8"> </wps:Output>
            </wps:Execute>
        """
        filled_template = template
        for repl_text, repl_value in {
            ":LONMIN:": bbox.lonmin,
            ":LONMAX:": bbox.lonmax,
            ":LATMIN:": bbox.latmin,
            ":LATMAX:": bbox.latmax,
        }.items():
            filled_template = filled_template.replace(repl_text, str(repl_value))

        payload = filled_template
        job_id = self.server.execute_async(payload)
        self.server.block_until_done(job_id)
        outputs = self.server.get_result_links(job_id)
        result_link = outputs["selectedRows"][
            ResultEncoding(
                encoding="UTF-8",
                mime_type="text/xml",
                schema="http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd",
            )
        ]

        return result_link


class ShakygroundProcess:
    def __init__(self, server):
        self.server = server

    def send_request(self, ql_result_link):
        template = """
            <wps:Execute service="WPS" version="2.0.0" xmlns:wps="http://www.opengis.net/wps/2.0"
                xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.opengis.net/wps/2.0 			  http://schemas.opengis.net/wps/2.0/wpsExecute.xsd"
                response="document" mode="async">
                <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess</ows:Identifier>
                <wps:Input id="gmpe">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>MontalvaEtAl2016SInter</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="vsgrid">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>USGSSlopeBasedTopographyProxy</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="quakeMLFile">
                    <wps:Reference schema="http://quakeml.org/xmlns/quakeml/1.2/QuakeML-1.2.xsd" mimeType="text/xml"
                        encoding="UTF-8"
                        xlink:href=":LINK:" />
                        xlink:href="http://localhost:8080/wps/RetrieveResultServlet?id=9454c691-c11d-47c1-bb48-5ec97829ec35selectedRows.ee7bdec8-e098-40b2-b2f6-bf5599870a61" />
                </wps:Input>
                <wps:Output id="shakeMapFile" transmission="reference" schema="http://earthquake.usgs.gov/eqcenter/shakemap"
                    mimeType="text/xml" encoding="UTF-8"> </wps:Output>
            </wps:Execute>
        """
        payload = template.replace(":LINK:", ql_result_link)
        job_id = self.server.execute_async(payload)
        self.server.block_until_done(job_id)
        outputs = self.server.get_result_links(job_id)
        result_link = outputs["shakeMapFile"][
            ResultEncoding(
                mime_type="text/xml",
                encoding="UTF-8",
                schema="http://earthquake.usgs.gov/eqcenter/shakemap",
            )
        ]
        return result_link


class ModelpropProcess:
    def __init__(self, server):
        self.server = server

    def send_request(self):
        template = """
            <wps:Execute service="WPS" version="2.0.0" xmlns:wps="http://www.opengis.net/wps/2.0"
                xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.opengis.net/wps/2.0 			  http://schemas.opengis.net/wps/2.0/wpsExecute.xsd"
                response="document" mode="async">
                <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess</ows:Identifier>
                <wps:Input id="schema">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>SARA_v1.0</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="assetcategory">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>buildings</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="losscategory">
                    <wps:Data mimeType="text/xml">
                        <wps:LiteralValue>structural</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Input id="taxonomies">
                    <wps:Data>
                        <wps:LiteralValue>none</wps:LiteralValue>
                    </wps:Data>
                </wps:Input>
                <wps:Output id="selectedRows" transmission="reference" mimeType="application/json" encoding="UTF-8"> </wps:Output>
            </wps:Execute>
        """
        payload = template
        job_id = self.server.execute_async(payload)
        self.server.block_until_done(job_id)
        outputs = self.server.get_result_links(job_id)
        result_link = outputs["selectedRows"][
            ResultEncoding(mime_type="application/json", encoding="UTF-8", schema=None)
        ]
        return result_link


class AssetmasterProcess:
    def __init__(self, server):
        self.server = server

    def send_request(self, bbox, model):
        template = """
            <wps:Execute service="WPS" version="2.0.0" xmlns:wps="http://www.opengis.net/wps/2.0"
            xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.opengis.net/wps/2.0 			  http://schemas.opengis.net/wps/2.0/wpsExecute.xsd"
            response="document" mode="async">
            <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess</ows:Identifier>
            <wps:Input id="lonmin">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>:LONMIN:</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Input id="lonmax">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>:LONMAX:</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Input id="latmin">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>:LATMIN:</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Input id="latmax">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>:LATMAX:</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Input id="schema">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>SARA_v1.0</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Input id="assettype">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>res</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Input id="querymode">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>intersects</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Input id="model">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>:MODEL:</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Output id="selectedRowsGeoJson" transmission="reference" mimeType="application/json" encoding="UTF-8">
            </wps:Output>
        </wps:Execute>
        """
        filled_template = template
        for repl_text, repl_value in {
            ":MODEL:": model,
            ":LONMIN:": bbox.lonmin,
            ":LONMAX:": bbox.lonmax,
            ":LATMIN:": bbox.latmin,
            ":LATMAX:": bbox.latmax,
        }.items():
            filled_template = filled_template.replace(repl_text, str(repl_value))

        payload = filled_template
        job_id = self.server.execute_async(payload)
        self.server.block_until_done(job_id)
        outputs = self.server.get_result_links(job_id)
        result_link = outputs["selectedRowsGeoJson"][
            ResultEncoding(mime_type="application/json", encoding="UTF-8", schema=None)
        ]
        return result_link


class DeusProcess:
    def __init__(self, server):
        self.server = server

    def send_request(self, sk_result_link, am_result_link, md_result_link):
        template = """
            <wps:Execute service="WPS" version="2.0.0" xmlns:wps="http://www.opengis.net/wps/2.0"
            xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.opengis.net/wps/2.0 			  http://schemas.opengis.net/wps/2.0/wpsExecute.xsd"
            response="document" mode="async">
            <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.DeusProcess</ows:Identifier>
            <wps:Input id="intensity">
                <wps:Reference mimeType="text/xml"
                    xlink:href=":SHAKYGROUNDLINK:" />
            </wps:Input>
            <wps:Input id="exposure">
                <wps:Reference mimeType="application/json" encoding="UTF-8"
                    xlink:href=":ASSETMASTERLINK:" />
            </wps:Input>
            <wps:Input id="schema">
                <wps:Data mimeType="text/xml">
                    <wps:LiteralValue>SARA_v1.0</wps:LiteralValue>
                </wps:Data>
            </wps:Input>
            <wps:Input id="fragility">
                <wps:Reference mimeType="application/json" encoding="UTF-8"
                    xlink:href=":MODELPROPLINK:" />
            </wps:Input>
            <wps:Output id="merged_output" transmission="reference" mimeType="application/json" encoding="UTF-8"> </wps:Output>
        </wps:Execute>
        """
        filled_template = template
        for repl_text, repl_value in {
            ":ASSETMASTERLINK:": am_result_link,
            ":MODELPROPLINK:": md_result_link,
            ":SHAKYGROUNDLINK:": sk_result_link,
        }.items():
            filled_template = filled_template.replace(repl_text, str(repl_value))

        payload = filled_template
        job_id = self.server.execute_async(payload)
        self.server.block_until_done(job_id)
        outputs = self.server.get_result_links(job_id)
        result_link = outputs["merged_output"][
            ResultEncoding(mime_type="application/json", encoding="UTF-8", schema=None)
        ]
        return result_link


def main():
    wps = WpsServer.dev()
    model_bbox = Bbox(lonmin=-88, lonmax=-66, latmin=-21, latmax=0)
    eq_bbox = Bbox(lonmin=-86.5, lonmax=-68.5, latmin=-20.5, latmax=-0.6)
    am = AssetmasterProcess(wps)
    am_result_link = am.send_request(model_bbox, model="LimaBlocks")
    print(f"Assetmaster: {am_result_link}")
    md = ModelpropProcess(wps)
    md_result_link = md.send_request()
    print(f"Modelprop: {md_result_link}")
    ql = QuakeledgerProcess(wps)
    ql_result_link = ql.send_request(eq_bbox)
    sk = ShakygroundProcess(wps)
    sk_result_link = sk.send_request(ql_result_link)
    print(f"Shakyground: {sk_result_link}")

    ds = DeusProcess(wps)
    print("Started deus...")
    ds_result_link = ds.send_request(
        sk_result_link=sk_result_link,
        am_result_link=am_result_link,
        md_result_link=md_result_link,
    )
    print(f"Deus: {ds_result_link}")


if __name__ == "__main__":
    main()
