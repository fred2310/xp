package com.enonic.xp.impl.task.cluster;

import java.io.Serializable;

import com.enonic.xp.task.TaskId;

public final class TaskTransportRequest
    implements Serializable
{
    public enum Type
    {
        ALL, RUNNING, BY_ID
    }

    private final Type type;

    private final TaskId taskId;

    public TaskTransportRequest( final Type type, final TaskId taskId )
    {
        this.type = type;
        this.taskId = taskId;
    }

    public Type getType()
    {
        return type;
    }

    public TaskId getTaskId()
    {
        return taskId;
    }

    private Object writeReplace()
    {
        return new SerializedForm( this );
    }

    static class SerializedForm
        implements Serializable
    {
        private final Type type;

        private final String taskId;

        SerializedForm( TaskTransportRequest request )
        {
            type = request.getType();
            taskId = type == Type.BY_ID ? request.taskId.toString() : null;
        }

        Object readResolve()
        {
            TaskId taskIdResolved = type == Type.BY_ID ? TaskId.from( taskId ) : null;
            return new TaskTransportRequest( type, taskIdResolved );
        }

        private static final long serialVersionUID = 0;
    }
}
