package com.enonic.xp.impl.task.cluster;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.impl.task.TaskManager;
import com.enonic.xp.task.TaskInfo;

@Component(immediate = true)
public final class TaskTransportRequestHandler
{
    private TaskManager taskManager;

    public TaskTransportResponse messageReceived( final TaskTransportRequest request )
    {
        final List<TaskInfo> taskInfos;
        switch ( request.getType() )
        {
            case BY_ID:
                taskInfos = Optional.ofNullable( taskManager.getTaskInfo( request.getTaskId() ) ).
                    map( Collections::singletonList ).
                    orElse( Collections.emptyList() );
                break;
            case RUNNING:
                taskInfos = taskManager.getRunningTasks();
                break;
            default:
                taskInfos = taskManager.getAllTasks();
                break;
        }

        return new TaskTransportResponse( taskInfos );
    }

    @Reference
    public void setTaskManager( final TaskManager taskManager )
    {
        this.taskManager = taskManager;
    }
}
