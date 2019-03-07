package com.enonic.xp.task;

import java.util.Objects;

import com.google.common.base.Preconditions;

public class GetTaskInfoParams
{
    private TaskId taskId;

    private boolean local;

    private GetTaskInfoParams( final Builder builder )
    {
        Preconditions.checkNotNull( builder.taskId, "Task ID cannot be null" );
        taskId = builder.taskId;
        local = builder.local;
    }

    public TaskId getTaskId()
    {
        return taskId;
    }

    public boolean isLocal()
    {
        return local;
    }

    public static GetTaskInfoParams from( final String taskId )
    {
        return create().
            taskId( TaskId.from( taskId ) ).
            build();
    }

    public static GetTaskInfoParams from( final TaskId taskId )
    {
        return create().
            taskId( taskId ).
            build();
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
        final GetTaskInfoParams that = (GetTaskInfoParams) o;
        return local == that.local && Objects.equals( taskId, that.taskId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( taskId, local );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private TaskId taskId;

        private boolean local;

        private Builder()
        {
        }

        public Builder taskId( final TaskId taskId )
        {
            this.taskId = taskId;
            return this;
        }

        public Builder local( final boolean local )
        {
            this.local = local;
            return this;
        }

        public GetTaskInfoParams build()
        {
            return new GetTaskInfoParams( this );
        }

    }
}
