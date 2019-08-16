To run the integration tests following steps are needed:

- download JMeter from https://jmeter.apache.org/download_jmeter.cgi and unpack it

```
wget http://ftp-stud.hs-esslingen.de/pub/Mirrors/ftp.apache.org/dist/jmeter/binaries/apache-jmeter-5.1.1.tgz
tar xf apache-jmeter-5.1.1.tgz
```

- change to the directory where the `.jmx` file is located

```
cd integration-tests
```

- start JMeter and open the `.jmx` file

```
/path/to/apache-jmeter/bin/jmeter.sh -t RIESGOS-integration-tests.jmx
```
