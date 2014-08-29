package com.enonic.wem.api.content;

import com.enonic.wem.api.entity.EntityVersionId;
import com.enonic.wem.api.support.AbstractId;

public class ContentVersionId
    extends AbstractId
{

    private ContentVersionId( final String value )
    {
        super( value );
    }

    public static ContentVersionId from( final String id )
    {
        return new ContentVersionId( id );
    }

    public static ContentVersionId from( final EntityVersionId id )
    {
        return new ContentVersionId( id.toString() );
    }


}


