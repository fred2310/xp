package com.enonic.wem.portal.internal.rendering;

import com.enonic.wem.api.exception.BaseException;

public final class RenderException
    extends BaseException
{
    public RenderException( final String message )
    {
        super( message );
    }

    public RenderException( final String message, final Object... args )
    {
        super( message, args );
    }
}
