package com.enonic.xp.impl.task.cluster;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgress;
import com.enonic.xp.task.TaskState;

public final class TaskTransportResponse
    implements Serializable
{
    private final ImmutableList<TaskInfo> taskInfos;

    public TaskTransportResponse( final List<TaskInfo> taskInfos )
    {
        this.taskInfos = ImmutableList.copyOf( taskInfos );
    }

    public List<TaskInfo> getTaskInfos()
    {
        return taskInfos;
    }

    private Object writeReplace()
    {
        return new SerializedForm( this );
    }

    static class SerializedForm
        implements Serializable
    {
        private final SerializedFormTaskInfo[] taskInfos;

        SerializedForm( TaskTransportResponse response )
        {
            taskInfos = response.taskInfos.stream().map( SerializedFormTaskInfo::new ).toArray( SerializedFormTaskInfo[]::new );
        }

        private Object readResolve()
        {
            ImmutableList<TaskInfo> taskInfoList =
                Arrays.stream( taskInfos ).map( SerializedForm::readResolveTaskInfo ).collect( ImmutableList.toImmutableList() );
            return new TaskTransportResponse( taskInfoList );
        }

        private static TaskInfo readResolveTaskInfo( SerializedFormTaskInfo serializedFormTaskInfo )
        {
            final TaskProgress taskProgress = TaskProgress.create().
                current( serializedFormTaskInfo.progressCurrent ).
                total( serializedFormTaskInfo.progressTotal ).
                info( serializedFormTaskInfo.progressInfo ).
                build();

            return TaskInfo.create().
                id( TaskId.from( serializedFormTaskInfo.taskId ) ).
                name( serializedFormTaskInfo.name ).
                description( serializedFormTaskInfo.description ).
                state( serializedFormTaskInfo.state ).
                application( ApplicationKey.from( serializedFormTaskInfo.application ) ).
                user( PrincipalKey.from( serializedFormTaskInfo.user ) ).
                startTime( serializedFormTaskInfo.startTime ).
                progress( taskProgress ).
                build();
        }

        private static final long serialVersionUID = 0;
    }

    static class SerializedFormTaskInfo
        implements Serializable
    {
        private final String taskId;

        private final String name;

        private final String description;

        private final TaskState state;

        private final int progressCurrent;

        private final int progressTotal;

        private final String progressInfo;

        private final String application;

        private final String user;

        private final Instant startTime;

        SerializedFormTaskInfo( TaskInfo taskInfo )
        {

            this.taskId = taskInfo.getId().toString();
            this.name = taskInfo.getName();
            this.description = taskInfo.getDescription();
            this.state = taskInfo.getState();
            this.application = taskInfo.getApplication().getName();
            this.user = taskInfo.getUser().toString();
            this.startTime = taskInfo.getStartTime();

            TaskProgress taskProgress = taskInfo.getProgress();
            this.progressCurrent = taskProgress.getCurrent();
            this.progressTotal = taskProgress.getTotal();
            this.progressInfo = taskProgress.getInfo();
        }

        private static final long serialVersionUID = 0;
    }
}
