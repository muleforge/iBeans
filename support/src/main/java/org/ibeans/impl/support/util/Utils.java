/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.support.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class Utils
{
    // The maximum number of Collection and Array elements used for messages
    public static final int MAX_ELEMENTS = 50;

// @GuardedBy(itself)
    private static final List maskedProperties = new CopyOnWriteArrayList();

    static
    {
        // When printing property lists mask password fields
        // Users can register their own fields to mask
        registerMaskedPropertyName("password");
    }

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /** logger used by this class */
    protected static final Log logger = LogFactory.getLog(Utils.class);


    /**
     * Register a property name for masking. This will prevent certain values from
     * leaking e.g. into debugging output or logfiles.
     *
     * @param name the key of the property to be masked.
     * @throws IllegalArgumentException is name is null or empty.
     */
    public static void registerMaskedPropertyName(String name)
    {
        if (!isEmpty(name))
        {
            maskedProperties.add(name);
        }
        else
        {
            throw new IllegalArgumentException("Cannot mask empty property name.");
        }
    }

    /**
     * Gets a String from a value in a null-safe manner.
     * <p/>
     *
     * @param answer       the object value
     * @param defaultValue the default to use if null or of incorrect type
     * @return the value as a String, or the defaultValue
     */
    public static String getString(final Object answer, String defaultValue)
    {
        if (answer != null)
        {
            return answer.toString();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Gets a boolean from a value in a null-safe manner.
     * <p/>
     *
     * @param answer       the object value
     * @param defaultValue the default to use if null or of incorrect type
     * @return the value as a boolean, or the defaultValue
     */
    public static boolean getBoolean(final Object answer, boolean defaultValue)
    {
        if (answer != null)
        {
            if (answer instanceof Boolean)
            {
                return ((Boolean) answer).booleanValue();

            }
            else if (answer instanceof String)
            {
                return Boolean.valueOf((String) answer).booleanValue();

            }
            else if (answer instanceof Number)
            {
                Number n = (Number) answer;
                return ((n.intValue() > 0) ? Boolean.TRUE : Boolean.FALSE).booleanValue();
            }
            else
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn("Value exists but cannot be converted to boolean: "
                            + answer + ", returning default value: " + defaultValue);
                }
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Gets a byte from a value in a null-safe manner.
     * <p/>
     *
     * @param answer       the object value
     * @param defaultValue the default to use if null or of incorrect type
     * @return the value as a byte, or the defaultValue
     */
    public static byte getByte(final Object answer, byte defaultValue)
    {
        if (answer == null)
        {
            return defaultValue;
        }
        else if (answer instanceof Number)
        {
            return ((Number) answer).byteValue();
        }
        else if (answer instanceof String)
        {
            try
            {
                return Byte.valueOf((String) answer).byteValue();
            }
            catch (NumberFormatException e)
            {
                //handled below
            }
        }
        if (logger.isWarnEnabled())
        {
            logger.warn("Value exists but cannot be converted to byte: "
                    + answer + ", returning default value: " + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Gets a short from a value in a null-safe manner.
     * <p/>
     *
     * @param answer       the object value
     * @param defaultValue the default to use if null or of incorrect type
     * @return the value as a short, or the defaultValue
     */
    public static short getShort(final Object answer, short defaultValue)
    {
        if (answer == null)
        {
            return defaultValue;
        }
        else if (answer instanceof Number)
        {
            return ((Number) answer).shortValue();
        }
        else if (answer instanceof String)
        {
            try
            {
                return Short.valueOf((String) answer).shortValue();
            }
            catch (NumberFormatException e)
            {
                //handled below
            }
        }
        if (logger.isWarnEnabled())
        {
            logger.warn("Value exists but cannot be converted to short: "
                    + answer + ", returning default value: " + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Gets a int from a value in a null-safe manner.
     * <p/>
     *
     * @param answer       the object value
     * @param defaultValue the default to use if null or of incorrect type
     * @return the value as a int, or the defaultValue
     */
    public static int getInt(final Object answer, int defaultValue)
    {
        if (answer == null)
        {
            return defaultValue;
        }
        else if (answer instanceof Number)
        {
            return ((Number) answer).intValue();
        }
        else if (answer instanceof String)
        {
            try
            {
                return Integer.valueOf((String) answer).intValue();
            }
            catch (NumberFormatException e)
            {
                //handled below
            }
        }
        if (logger.isWarnEnabled())
        {
            logger.warn("Value exists but cannot be converted to int: "
                    + answer + ", returning default value: " + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Gets a long from a value in a null-safe manner.
     * <p/>
     *
     * @param answer       the object value
     * @param defaultValue the default to use if null or of incorrect type
     * @return the value as a long, or the defaultValue
     */
    public static long getLong(final Object answer, long defaultValue)
    {
        if (answer == null)
        {
            return defaultValue;
        }
        else if (answer instanceof Number)
        {
            return ((Number) answer).longValue();
        }
        else if (answer instanceof String)
        {
            try
            {
                return Long.valueOf((String) answer).longValue();
            }
            catch (NumberFormatException e)
            {
                //handled below

            }
        }
        if (logger.isWarnEnabled())
        {
            logger.warn("Value exists but cannot be converted to long: "
                    + answer + ", returning default value: " + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Gets a float from a value in a null-safe manner.
     * <p/>
     *
     * @param answer       the object value
     * @param defaultValue the default to use if null or of incorrect type
     * @return the value as a float, or the defaultValue
     */
    public static float getFloat(final Object answer, float defaultValue)
    {
        if (answer == null)
        {
            return defaultValue;
        }
        else if (answer instanceof Number)
        {
            return ((Number) answer).floatValue();
        }
        else if (answer instanceof String)
        {
            try
            {
                return Float.valueOf((String) answer).floatValue();
            }
            catch (NumberFormatException e)
            {
                //handled below

            }
        }
        if (logger.isWarnEnabled())
        {
            logger.warn("Value exists but cannot be converted to float: "
                    + answer + ", returning default value: " + defaultValue);
        }
        return defaultValue;
    }

    /**
     * Gets a double from a value in a null-safe manner.
     * <p/>
     *
     * @param answer       the object value
     * @param defaultValue the default to use if null or of incorrect type
     * @return the value as a double, or the defaultValue
     */
    public static double getDouble(final Object answer, double defaultValue)
    {
        if (answer == null)
        {
            return defaultValue;
        }
        else if (answer instanceof Number)
        {
            return ((Number) answer).doubleValue();
        }
        else if (answer instanceof String)
        {
            try
            {
                return Double.valueOf((String) answer).doubleValue();
            }
            catch (NumberFormatException e)
            {
                //handled below
            }
        }
        if (logger.isWarnEnabled())
        {
            logger.warn("Value exists but cannot be converted to double: "
                    + answer + ", returning default value: " + defaultValue);
        }
        return defaultValue;
    }

    public static Properties getPropertiesFromQueryString(String query)
    {
        Properties props = new Properties();

        if (query == null)
        {
            return props;
        }

        query = new StringBuffer(query.length() + 1).append('&').append(query).toString();

        int x = 0;
        while ((x = addProperty(query, x, '&', props)) != -1)
        {
            // run
        }

        return props;
    }

    public static Properties getPropertiesFromString(String query, char separator)
    {
        Properties props = new Properties();

        if (query == null)
        {
            return props;
        }

        query = new StringBuffer(query.length() + 1).append(separator).append(query).toString();

        int x = 0;
        while ((x = addProperty(query, x, separator, props)) != -1)
        {
            // run
        }

        return props;
    }

    private static int addProperty(String query, int start, char separator, Properties properties)
    {
        int i = query.indexOf(separator, start);
        int i2 = query.indexOf(separator, i + 1);
        String pair;
        if (i > -1 && i2 > -1)
        {
            pair = query.substring(i + 1, i2);
        }
        else if (i > -1)
        {
            pair = query.substring(i + 1);
        }
        else
        {
            return -1;
        }
        int eq = pair.indexOf('=');

        if (eq <= 0)
        {
            String key = pair;
            String value = "";
            properties.setProperty(key, value);
        }
        else
        {
            String key = pair.substring(0, eq);
            String value = (eq == pair.length() ? "" : pair.substring(eq + 1));
            properties.setProperty(key, value);
        }
        return i2;
    }

    public static Map<String, Object> convertKeyValuePairsToMap(String[] properties)
    {
        if (properties.length > 0)
        {
             Map<String, Object> props = new HashMap<String, Object>(properties.length);
            for (int i = 0; i < properties.length; i++)
            {
                String property = properties[i];
                if (property.length() == 0)
                {
                    continue;
                }
                int x = property.indexOf("=");
                if (x < 1)
                {
                    throw new IllegalArgumentException("Property string is malformed: " + property);
                }
                String value = property.substring(x + 1);
                property = property.substring(0, x);
                props.put(property, value);

            }
            return props;
        }
        return null;
    }


    /**
     * Like {@link org.mule.util.StringUtils#split(String, String)}, but
     * additionally trims whitespace from the result tokens.
     */
    public static String[] splitAndTrim(String string, String delim)
    {
        if (string == null)
        {
            return null;
        }

        if (isEmpty(string))
        {
            return new String[]{};
        }

        String[] rawTokens =string.split(delim);
        List<String> tokens = new ArrayList<String>();
        String token;
        if (rawTokens != null)
        {
            for (int i = 0; i < rawTokens.length; i++)
            {
                token = rawTokens[i];
                if (token !=null && token.length() > 0)
                {
                    tokens.add(token.trim());
                }
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }

/**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>Checks if the String contains only unicode digits.
     * A decimal point is not a unicode digit and returns false.</p>
     *
     * <p><code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.</p>
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }


    /**
     * @see ArrayUtils#toString(Object, int)
     * @see org.apache.commons.collections.CollectionUtils#toString(java.util.Collection , int)
     * @see org.apache.commons.collections.MapUtils#toString(java.util.Map, boolean)
     */
    public static String toString(Object o)
    {
        if (o == null)
        {
            return "null";
        }
        else if (o instanceof Class)
        {
            return ((Class) o).getName();
        }
        else if (o instanceof Map)
        {
            return toString((Map) o, false);
        }
        else if (o.getClass().isArray())
        {
            return o.toString();
        }
        else if (o instanceof Collection)
        {
            return toString((Collection) o, MAX_ELEMENTS);
        }
        else
        {
            return o.toString();
        }
    }


    /**
     * Creates a String representation of the given Map, with optional newlines
     * between elements.
     *
     * @param props the map to format
     * @param newline indicates whether elements are to be split across lines
     * @return the formatted String
     */
    public static String toString(Map props, boolean newline)
    {
        if (props == null || props.isEmpty())
        {
            return "{}";
        }

        StringBuffer buf = new StringBuffer(props.size() * 32);
        buf.append('{');

        if (newline)
        {
            buf.append(LINE_SEPARATOR);
        }

        Object[] entries = props.entrySet().toArray();
        int i;

        for (i = 0; i < entries.length - 1; i++)
        {
            Map.Entry property = (Map.Entry) entries[i];
            buf.append(property.getKey());
            buf.append('=');
            buf.append(maskedPropertyValue(property));

            if (newline)
            {
                buf.append(LINE_SEPARATOR);
            }
            else
            {
                buf.append(',').append(' ');
            }
        }

        // don't forget the last one
        Map.Entry lastProperty = (Map.Entry) entries[i];
        buf.append(lastProperty.getKey().toString());
        buf.append('=');
        buf.append(maskedPropertyValue(lastProperty));

        if (newline)
        {
            buf.append(LINE_SEPARATOR);
        }

        buf.append('}');
        return buf.toString();
    }


    /**
     * Returns the String representation of the property value or a masked String if
     * the property key has been registered previously via
     * {@link #registerMaskedPropertyName(String)}.
     *
     * @param property a key/value pair
     * @return String of the property value or a "masked" String that hides the
     *         contents, or <code>null</code> if the property, its key or its value
     *         is <code>null</code>.
     */
    public static String maskedPropertyValue(Map.Entry property)
    {
        if (property == null)
        {
            return null;
        }

        Object key = property.getKey();
        Object value = property.getValue();

        if (key == null || value == null)
        {
            return null;
        }

        if (maskedProperties.contains(key))
        {
            return ("*****");
        }
        else
        {
            return value.toString();
        }
    }


    /**
     * Calls {@link #toString(java.util.Collection, int, boolean)} with <code>false</code>
     * for newline.
     */
    public static String toString(Collection c, int maxElements)
    {
        return toString(c, maxElements, false);
    }

    /**
     * Creates a String representation of the given Collection, with optional
     * newlines between elements. Class objects are represented by their full names.
     * Considers at most <code>maxElements</code> values; overflow is indicated by
     * an appended "[..]" ellipsis.
     *
     * @param c the Collection to format
     * @param maxElements the maximum number of elements to take into account
     * @param newline indicates whether elements are to be split across lines
     * @return the formatted String
     */
    public static String toString(Collection c, int maxElements, boolean newline)
    {
        if (c == null || c.isEmpty())
        {
            return "[]";
        }

        int origNumElements = c.size();
        int numElements = Math.min(origNumElements, maxElements);
        boolean tooManyElements = (origNumElements > maxElements);

        StringBuffer buf = new StringBuffer(numElements * 32);
        buf.append('[');

        if (newline)
        {
            buf.append(LINE_SEPARATOR);
        }

        Iterator items = c.iterator();
        for (int i = 0; i < numElements - 1; i++)
        {
            Object item = items.next();

            if (item instanceof Class)
            {
                buf.append(((Class) item).getName());
            }
            else
            {
                buf.append(item);
            }

            if (newline)
            {
                buf.append(LINE_SEPARATOR);
            }
            else
            {
                buf.append(',').append(' ');
            }
        }

        // don't forget the last one
        Object lastItem = items.next();
        if (lastItem instanceof Class)
        {
            buf.append(((Class) lastItem).getName());
        }
        else
        {
            buf.append(lastItem);
        }

        if (newline)
        {
            buf.append(LINE_SEPARATOR);
        }

        if (tooManyElements)
        {
            buf.append(" [..]");
        }

        buf.append(']');
        return buf.toString();
    }

}