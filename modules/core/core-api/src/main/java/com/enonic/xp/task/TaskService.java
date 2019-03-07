package com.enonic.xp.task;

import java.util.List;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;

@Beta
public interface TaskService
{
    TaskId submitTask( RunnableTask runnable, String description );

    TaskId submitTask( DescriptorKey key, PropertyTree config );

    @Deprecated
    TaskInfo getTaskInfo( TaskId taskId );

    TaskInfo getTaskInfo( final GetTaskInfoParams params );

    @Deprecated
    List<TaskInfo> getAllTasks();

    @Deprecated
    List<TaskInfo> getRunningTasks();

    List<TaskInfo> getTasks( final GetTasksParams params );
}
