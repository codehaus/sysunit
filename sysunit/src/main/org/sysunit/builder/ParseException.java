package org.sysunit.builder;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class ParseException
    extends SAXParseException
{
    public ParseException(String message,
                          Locator locator)
    {
        super( message,
               locator );
    }

    public ParseException(String message,
                          Locator locator,
                          Exception e)
    {
        super( message,
               locator,
               e );
    }

    public String getMessage()
    {
        return getSystemId() + ":" + getLineNumber() + ":" + getColumnNumber() + " " + super.getMessage();
    }
}
