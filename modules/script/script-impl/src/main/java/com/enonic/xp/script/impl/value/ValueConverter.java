package com.enonic.xp.script.impl.value;

public interface ValueConverter
{
    Object toJs( Object value );

    Object fromJs( Object value );
}
