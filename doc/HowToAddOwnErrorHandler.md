# Add your own error handler

Sometimes the included handlers and java classes in this repository
are not enough to support proper error handling in your specific 
cases.

## pythonTraceback as example

Most of the supported processes here are python scripts so it was
important to include a error handler for python tracebacks.

```python
1/0
```

```
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
ZeroDivisionError: division by zero
```

This handling only focuses on the shell side of the output. From our
java code we have to possibility to access the python stacktrace from
within python, so we only parse the stderr text and search for
something like
``` 
Traceback [...]
```

All the text from this on is given back as an error message.

The code itself for this is pretty stupid:

```java
package org.n52.gfz.riesgos.stderrhandler;

public class PythonTracebackStderrHandler implements IStderrHandler  {

    private static final String TRACEBACK = "Traceback (most recent call";

    @Override
    public void handleStderr(String stderr, ILogger logger) throws NonEmptyStderrException {
        final int index = stderr.indexOf(TRACEBACK);

        if(index >= 0) {
            final String subString = stderr.substring(index);
            throw new NonEmptyStderrException(subString);
        }
    }
}
```

## R error handler

Lets say we think the python traceback handler is a good first step and
we want to have something similar for our R scripts.

(We added this error handler while writing this guide. The necessary
steps remain the same, but the code may change according to
new insights and demands).

### Check Text on stderr for R errors

The very first step is to look how R errors are printed on stderr.

```R
read.table("non_existing_file.txt")
```

```
Fehler in file(file, "rt") : kann Verbindung nicht öffnen
Zusätzlich: Warnmeldung:
In file(file, "rt") :
  kann Datei 'non_existing_file.txt' nicht öffnen: Datei oder Verzeichnis nicht gefunden
```

In this very first try the error message is in german - which is no
surprise once my system uses german local settings.

However to write a error handler this is unhelpful for implementing
a proper error handler.
In most cases - as our wps server and all the docker images does -
the locale settings are english, so we have to take a look at the
english error message:

```
Error in file(file, "rt") : cannot open the connection
In addition: Warning message:
In file(file, "rt") :
  cannot open file 'non_existing_file.txt': No such file or directory
```

So, we see a text with Error, a location of the error a colon and+
a error description in the first line.
In this case also the additional warning is interesting to give it
back to the client.

What is the error message in case of an undefined function?

```R
non_existing_function("do something useful")
```

```
Error: could not find function "non_existing_function"
```

This shows us that we can't trust on having a location for the error
or having additional warnings.

So from what we learned we have to take a look at the word "Error" at
the beginning of the line.

### Write a test class

To follow good development practice we can write the
following test class:

```java
package org.n52.gfz.riesgos.stderrhandler;

import org.junit.Test;
import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Test class for the RErrorMessageStderrHandler
 */
public class TestRErrorMessageStderrHandler {

    /**
     * Test with empty stderr text
     */
    @Test
    public void testEmpty() {
        final String stderrText = "";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
        } catch (final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            fail("There should be no exception on empty stderr text");
        }
    }

    /**
     * Test with the error message of a non existing function
     */
    @Test
    public void testNonExistingFunction() {
        final String stderrText = "Error: could not find function \"non_existing_function\"";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            fail("There must be an exception");
        } catch (final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            assertNotNull("There is a exception because of the error", exception);
        }
    }

    /**
     * Test the error message of reading a non existing file
     */
    @Test
    public void testNonExistingFile() {
        final String stderrText = "Error in file(file, \"rt\") : cannot open the connection\n" +
                "In addition: Warning message:\n" +
                "In file(file, \"rt\") :\n" +
                "  cannot open file 'non_existing_file.txt': No such file or directory";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            fail("There mjust be an exception");
        } catch (final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            assertNotNull("There is a exception because of the error", exception);
        }
    }

    /**
     * This is a test with a warning that contains just a warning and no error message.
     * Done via warning("This may can cause an Error: Be careful! It is NO error!")
     */
    @Test
    public void testWarningText() {
        final String stderrText = "Warning message:\n" +
                "This may can cause an Error: Be careful! It is NO error!";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
        } catch(final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            fail("There should be no exception on a warning on stderr text");
        }
    }

    /**
     * This text has the warning message before a real error message.
     * It should find the Error: could not find function part.
     */
    @Test
    public void testWarningTextBeforeErrorMessage() {
        final String stderrText = "Warning message:\n" +
                "This may can cause an Error: Be careful! It is NO error!\n" +
                "Error: could not find function \"non_existing_function\"";

        final IStderrHandler stderrHandler = new RErrorMessageStderrHandler();

        final StringBuilder stringBuilder = new StringBuilder();

        try {
            stderrHandler.handleStderr(stderrText, stringBuilder::append);
            fail("There must be an exception");
        } catch (final NonEmptyStderrException exception) {
            assertTrue("The logging is still empty", stringBuilder.toString().isEmpty());
            assertNotNull("There is a exception because of the error", exception);
        }
    }
}
```

### Write the implementation


And this implementation will do the job:

```java
package org.n52.gfz.riesgos.stderrhandler;

import org.n52.gfz.riesgos.exceptions.NonEmptyStderrException;
import org.n52.gfz.riesgos.functioninterfaces.ILogger;
import org.n52.gfz.riesgos.functioninterfaces.IStderrHandler;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is an implementation that handles the error texts
 * of R scripts.
 */
public class RErrorMessageStderrHandler implements IStderrHandler {

    /**
     * Pattern that will search for the term "Error" on the beginning of a line.
     * Takes the Multiline flag to test the beginning (^) at every line.
     */
    private static final Pattern ERROR_PATTERN =
            Pattern.compile("^Error", Pattern.MULTILINE);

    /**
     * Handles stderr text for output of a R script.
     * @param stderr text to handle
     * @param logger logger of the algorithm - this implementation will
     *               not use the logger at all
     * @throws NonEmptyStderrException there may be an exception
     *                                 on an error error text
     */
    @Override
    public void handleStderr(
            final String stderr,
            final ILogger logger)
            throws NonEmptyStderrException {
        final Matcher matcher = ERROR_PATTERN.matcher(stderr);
        if (matcher.find()) {
            final int startIndex = matcher.start();
            final String errorMessage = stderr.substring(startIndex);
            throw new NonEmptyStderrException(errorMessage);
        }
    }

    /**
     * Tests equality.
     * @param o other object
     * @return true of this and o are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return o != null && getClass() == o.getClass();
    }

    /**
     * Computes the hash code of the object.
     * @return hashcode of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getClass().getName());
    }
}
```

Now we have the logic to handle the error handling for R scripts.

### Register the class to the parser

The next step is to let the parser know about this option on
parsing the json configuration files.

Just add the following line to the StderrHandlerOption enum in
the org.n52.gfz.riesgos.configuration.parse.stderrhandler package:

```
R_ERROR("rError", RErrorMessageStderrHandler::new)
``` 

Now it is possible to change the stderr handler to "rError" for any
json configuration files.