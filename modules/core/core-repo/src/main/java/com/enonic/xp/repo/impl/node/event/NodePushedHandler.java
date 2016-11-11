package com.enonic.xp.repo.impl.node.event;

import java.util.List;
import java.util.Map;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StorageService;

public class NodePushedHandler
    extends AbstractNodeEventHandler
{

    @Override
    public void handleEvent( StorageService storageService, final Event event, final InternalContext context )
    {
        final List<Map<Object, Object>> valueMapList = getValueMapList( event );

        for ( final Map<Object, Object> map : valueMapList )
        {
            final InternalContext nodeContext = createNodeContext( map, context );
            final NodePath previousPath = NodePath.create( map.get( PREVIOUS_PATH ).toString() ).build();
            storageService.handleNodePushed( getId( map ), getPath( map ), previousPath, nodeContext );
        }
    }
}
