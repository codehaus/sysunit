package org.sysunit.util;

public class SimpleBean
{
    private String stringProp;
    private int intProp;
    private long longProp;
    private float floatProp;
    private double doubleProp;
    private boolean booleanProp;

    public void setString(String value)
    {
        this.stringProp = value;
    }

    public String getString()
    {
        return this.stringProp;
    }

    public void setInt(int value)
    {
        this.intProp = value;
    }

    public void setIntObject(Integer value)
    {
        this.intProp = value.intValue();
    }

    public int getInt()
    {
        return this.intProp;
    }

    public void setLong(long value)
    {
        this.longProp = value;
    }

    public void setLongObject(Long value)
    {
        this.longProp = value.longValue();
    }

    public long getLong()
    {
        return this.longProp;
    }

    public void setFloat(float value)
    {
        this.floatProp = value;
    }

    public void setFloatObject(Float value)
    {
        this.floatProp = value.floatValue();
    }

    public float getFloat()
    {
        return this.floatProp;
    }

    public void setDouble(double value)
    {
        this.doubleProp = value;
    }

    public void setDoubleObject(Double value)
    {
        this.doubleProp = value.doubleValue();
    }

    public double getDouble()
    {
        return this.doubleProp;
    }

    public void setBoolean(boolean value)
    {
        this.booleanProp = value;
    }

    public void setBooleanObject(Boolean value)
    {
        this.booleanProp = value.booleanValue();
    }

    public boolean getBoolean()
    {
        return this.booleanProp;
    }
}
