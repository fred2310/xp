package com.enonic.xp.core.impl.event.cluster;

import java.io.Serializable;
import java.util.Map;

import com.enonic.xp.event.Event;

public final class SendEventRequest
    implements Serializable
{
    private final Event event;

    public SendEventRequest( final Event event )
    {
        this.event = event;
    }

    public Event getEvent()
    {
        return this.event;
    }


    private Object writeReplace()
    {
        return new SerializedForm( this );
    }

    static class SerializedForm
        implements Serializable
    {
        final String type;

        final long timestamp;

        final boolean distributed;

        final Map<String, Object> data;

        SerializedForm( SendEventRequest request )
        {
            Event event = request.event;
            type = event.getType();
            timestamp = event.getTimestamp();
            distributed = event.isDistributed();
            data = event.getData();
        }

        Object readResolve()
        {
            final Event.Builder eventBuilder = Event.create( type ).
                timestamp( timestamp ).
                distributed( distributed );

            data.forEach( eventBuilder::value );

            return new SendEventRequest( eventBuilder.build() );
        }

        private static final long serialVersionUID = 0;
    }
}
