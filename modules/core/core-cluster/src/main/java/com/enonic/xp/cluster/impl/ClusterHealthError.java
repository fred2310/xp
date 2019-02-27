package com.enonic.xp.cluster.impl;

import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterValidationError;

class ClusterHealthError
    implements ClusterValidationError
{
    private final ClusterId providerId;

    private final String message;

    ClusterHealthError( final ClusterId providerId, final String message )
    {
        this.providerId = providerId;
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return "Cluster [" + providerId + "]  not healthy" + ( message == null ? "" : ": " + message );
    }
}
