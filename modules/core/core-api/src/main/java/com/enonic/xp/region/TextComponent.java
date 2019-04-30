package com.enonic.xp.region;


import java.util.Objects;

import com.google.common.annotations.Beta;

@Beta
public class TextComponent
    extends Component
{
    private String text;

    protected TextComponent( final Builder builder )
    {
        super();
        this.text = builder.text != null ? builder.text : "";
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final TextComponent source )
    {
        return new Builder( source );
    }

    @Override
    public TextComponent copy()
    {
        return create( this ).build();
    }

    @Override
    public ComponentType getType()
    {
        return TextComponentType.INSTANCE;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        final TextComponent that = (TextComponent) o;

        if ( text != null ? !text.equals( that.text ) : that.text != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), text );
    }

    public static class Builder
        extends Component.Builder

    {
        private String text;

        Builder()
        {
            // Default
        }

        private Builder( final TextComponent source )
        {
            text = source.text;
        }

        public Builder text( String value )
        {
            this.text = value;
            return this;
        }

        public TextComponent build()
        {
            return new TextComponent( this );
        }
    }
}
