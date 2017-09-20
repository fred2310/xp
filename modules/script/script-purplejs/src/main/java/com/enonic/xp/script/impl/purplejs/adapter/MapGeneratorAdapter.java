package com.enonic.xp.script.impl.purplejs.adapter;

import io.purplejs.core.json.JsonGenerator;

import com.enonic.xp.script.serializer.MapGenerator;

public final class MapGeneratorAdapter
    implements MapGenerator
{
    private final JsonGenerator generator;

    public MapGeneratorAdapter( final JsonGenerator generator )
    {
        this.generator = generator;
    }

    @Override
    public MapGenerator map()
    {
        this.generator.map();
        return this;
    }

    @Override
    public MapGenerator map( final String key )
    {
        this.generator.map( key );
        return this;
    }

    @Override
    public MapGenerator array()
    {
        this.generator.array();
        return this;
    }

    @Override
    public MapGenerator array( final String key )
    {
        this.generator.array( key );
        return this;
    }

    @Override
    public MapGenerator value( final Object value )
    {
        this.generator.value( value );
        return this;
    }

    @Override
    public MapGenerator value( final String key, final Object value )
    {
        this.generator.value( key, value );
        return this;
    }

    @Override
    public MapGenerator rawValue( final Object value )
    {
        this.generator.value( value );
        return this;
    }

    @Override
    public MapGenerator rawValue( final String key, final Object value )
    {
        this.generator.value( key, value );
        return this;
    }

    @Override
    public MapGenerator end()
    {
        this.generator.end();
        return this;
    }
}
