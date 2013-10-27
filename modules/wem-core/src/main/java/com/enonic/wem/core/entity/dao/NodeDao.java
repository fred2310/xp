package com.enonic.wem.core.entity.dao;


import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NoEntityFoundException;
import com.enonic.wem.api.entity.NoEntityWithIdFound;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;

public interface NodeDao
{
    public Node getNodeById( EntityId id )
        throws NoEntityWithIdFound;

    public Node getNodeByPath( NodePath path )
        throws NoEntityFoundException;

    public Nodes getNodesByParentPath( NodePath parent )
        throws NoEntityFoundException;

    public Node createNode( CreateNodeArguments createNodeArguments );

    public Node updateNode( final UpdateNodeArgs updateNodeArgs );

    public void deleteNodeById( final EntityId id );

    public void deleteNodeByPath( final NodePath path );
}
