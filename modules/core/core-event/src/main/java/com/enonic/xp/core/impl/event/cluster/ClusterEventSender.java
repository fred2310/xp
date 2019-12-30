package com.enonic.xp.core.impl.event.cluster;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class ClusterEventSender
    implements EventListener
{
    private HazelcastInstance hazelcastInstance;

    private ITopic<SendEventRequest> topic;

    public static final String ACTION = "xp/event";

    @Activate
    public void activate()
    {
        topic = hazelcastInstance.getTopic( ACTION );
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event != null && event.isDistributed() )
        {
            topic.publish( new SendEventRequest( event ) );
        }
    }

    @Reference
    public void setHazelcastInstance( final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }
}
