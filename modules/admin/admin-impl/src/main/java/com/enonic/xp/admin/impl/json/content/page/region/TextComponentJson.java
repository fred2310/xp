package com.enonic.xp.admin.impl.json.content.page.region;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.TextComponent;

@SuppressWarnings("UnusedDeclaration")
public class TextComponentJson
    extends ComponentJson<TextComponent>
{
    private final TextComponent text;

    public TextComponentJson( final TextComponent component )
    {
        super( component );
        this.text = component;
    }

    @JsonCreator
    public TextComponentJson( @JsonProperty("text") final String text )
    {
        super( TextComponent.create().
            text( text ).
            build() );

        this.text = getComponent();
    }

    public String getText()
    {
        return this.text.getText();
    }
}
