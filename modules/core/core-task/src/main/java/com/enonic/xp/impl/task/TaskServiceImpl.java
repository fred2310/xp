package com.enonic.xp.impl.task;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.task.cluster.TaskTransportRequestSender;
import com.enonic.xp.impl.task.script.NamedTaskScriptFactory;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.GetTaskInfoParams;
import com.enonic.xp.task.GetTasksParams;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskNotFoundException;
import com.enonic.xp.task.TaskService;

import static com.enonic.xp.impl.task.script.NamedTaskScript.SCRIPT_METHOD_NAME;

@Component(immediate = true)
public final class TaskServiceImpl
    implements TaskService
{
    private TaskManager taskManager;

    private TaskTransportRequestSender taskTransportRequestSender;

    private TaskDescriptorService taskDescriptorService;

    private NamedTaskScriptFactory namedTaskScriptFactory;

    @Override
    public TaskId submitTask( final RunnableTask runnable, final String description )
    {
        return taskManager.submitTask( runnable, description, "" );
    }

    @Override
    public TaskId submitTask( final DescriptorKey key, final PropertyTree config )
    {
        final TaskDescriptor descriptor = taskDescriptorService.getTasks( key.getApplicationKey() ).
            filter( ( taskDesc ) -> taskDesc.getKey().equals( key ) ).first();
        if ( descriptor == null )
        {
            throw new TaskNotFoundException( key );
        }

        final RunnableTask runnableTask = namedTaskScriptFactory.create( descriptor, config );
        if ( runnableTask == null )
        {
            throw new TaskNotFoundException( key, "Missing exported function '" + SCRIPT_METHOD_NAME + "' in task script" );
        }

        return taskManager.submitTask( runnableTask, descriptor.getDescription(), key.toString() );
    }

    @Deprecated
    @Override
    public TaskInfo getTaskInfo( final TaskId taskId )
    {
        final GetTaskInfoParams params = GetTaskInfoParams.create().
            taskId( taskId ).
            build();
        return getTaskInfo( params );
    }

    @Override
    public TaskInfo getTaskInfo( final GetTaskInfoParams params )
    {
        if ( params.isLocal() )
        {
            return taskManager.getTaskInfo( params.getTaskId() );
        }
        else
        {
            final List<TaskInfo> taskInfos = taskTransportRequestSender.getByTaskId( params.getTaskId() );
            return taskInfos.isEmpty() ? null : taskInfos.get( 0 );
        }
    }

    @Deprecated
    @Override
    public List<TaskInfo> getAllTasks()
    {
        final GetTasksParams params = GetTasksParams.create().build();
        return getTasks( params );
    }

    @Deprecated
    @Override
    public List<TaskInfo> getRunningTasks()
    {
        final GetTasksParams params = GetTasksParams.create().
            running( true ).
            build();
        return getTasks( params );
    }

    @Override
    public List<TaskInfo> getTasks( final GetTasksParams params )
    {
        if ( params.isLocal() )
        {
            if ( params.isRunning() )
            {
                return taskManager.getRunningTasks();
            }
            else
            {
                return taskManager.getAllTasks();
            }
        }
        else
        {
            if ( params.isRunning() )
            {
                return taskTransportRequestSender.getRunningTasks();
            }
            else
            {
                return taskTransportRequestSender.getAllTasks();
            }
        }
    }

    @Reference
    public void setTaskManager( final TaskManager taskManager )
    {
        this.taskManager = taskManager;
    }

    @Reference
    public void setTaskTransportRequestSender( final TaskTransportRequestSender taskTransportRequestSender )
    {
        this.taskTransportRequestSender = taskTransportRequestSender;
    }

    @Reference
    public void setTaskDescriptorService( final TaskDescriptorService taskDescriptorService )
    {
        this.taskDescriptorService = taskDescriptorService;
    }

    @Reference
    public void setNamedTaskScriptFactory( final NamedTaskScriptFactory namedTaskScriptFactory )
    {
        this.namedTaskScriptFactory = namedTaskScriptFactory;
    }
}
