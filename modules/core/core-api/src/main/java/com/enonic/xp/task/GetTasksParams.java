package com.enonic.xp.task;

import java.util.Objects;

public class GetTasksParams
{
    private boolean running;

    private boolean local;

    private GetTasksParams( final Builder builder )
    {
        running = builder.running;
        local = builder.local;
    }

    public boolean isRunning()
    {
        return running;
    }

    public boolean isLocal()
    {
        return local;
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
        final GetTasksParams that = (GetTasksParams) o;
        return running == that.running && local == that.local;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( running, local );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private boolean running;

        private boolean local;

        private Builder()
        {
        }

        public Builder running( final boolean running )
        {
            this.running = running;
            return this;
        }

        public Builder local( final boolean local )
        {
            this.local = local;
            return this;
        }

        public GetTasksParams build()
        {
            return new GetTasksParams( this );
        }
    }
}
