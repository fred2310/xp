package com.enonic.xp.lib.task;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.GetTaskInfoParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

public final class GetTaskHandler
    implements ScriptBean
{
    private Supplier<TaskService> taskServiceSupplier;

    private String taskId;

    private Boolean local;

    public void setTaskId( final String taskId )
    {
        this.taskId = taskId;
    }

    public void setLocal( final Boolean local )
    {
        this.local = local;
    }

    public TaskMapper getTask()
    {
        final TaskService taskService = this.taskServiceSupplier.get();
        final GetTaskInfoParams params = GetTaskInfoParams.create().taskId( TaskId.from( taskId ) ).
            local( local == null ? false : local ).
            build();

        final TaskInfo taskInfo = taskService.getTaskInfo( params );

        return taskInfo == null ? null : new TaskMapper( taskInfo );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        taskServiceSupplier = context.getService( TaskService.class );
    }
}
