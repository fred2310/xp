package com.enonic.xp.impl.task.cluster;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.impl.task.TaskManager;
import com.enonic.xp.task.TaskInfo;

public class TaskTransportResponseHandler
    implements Callable<TaskTransportResponse>, Serializable
{
    private final TaskTransportRequest request;

    public TaskTransportResponseHandler( TaskTransportRequest request )
    {
        this.request = request;
    }

    @Override
    public TaskTransportResponse call()
    {
        BundleContext bundleContext = FrameworkUtil.getBundle( TaskManager.class ).getBundleContext();
        ServiceReference<TaskManager> serviceReference = bundleContext.getServiceReference( TaskManager.class );
        TaskManager handler = bundleContext.getService( serviceReference );
        try
        {
            return messageReceived( handler, request );
        }
        finally
        {
            bundleContext.ungetService( serviceReference );
        }
    }

    private static TaskTransportResponse messageReceived( final TaskManager taskManager, final TaskTransportRequest request )
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
}
