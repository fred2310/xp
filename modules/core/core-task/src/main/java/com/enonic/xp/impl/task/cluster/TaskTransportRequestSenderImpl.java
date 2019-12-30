package com.enonic.xp.impl.task.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.util.ExceptionUtil;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

@Component
public final class TaskTransportRequestSenderImpl
    implements TaskTransportRequestSender
{
    private static final long TRANSPORT_REQUEST_TIMEOUT_SECONDS = 5L;

    public static final String ACTION = "xp/task";

    private HazelcastInstance hazelcastInstance;

    private IExecutorService executorService;


    @Activate
    public void activate()
    {
        executorService = hazelcastInstance.getExecutorService( ACTION );
    }

    @Deactivate
    public void deactivate()
    {
        executorService.shutdown();
    }

    @Override
    public List<TaskInfo> getByTaskId( final TaskId taskId )
    {
        return send( new TaskTransportRequest( TaskTransportRequest.Type.BY_ID, taskId ) );
    }

    @Override
    public List<TaskInfo> getRunningTasks()
    {
        return send( new TaskTransportRequest( TaskTransportRequest.Type.RUNNING, null ) );
    }

    @Override
    public List<TaskInfo> getAllTasks()
    {
        return send( new TaskTransportRequest( TaskTransportRequest.Type.ALL, null ) );
    }

    private List<TaskInfo> send( final TaskTransportRequest request )
    {
        final List<TaskInfo> taskInfoBuilder = new ArrayList<>();
        final Map<Member, Future<TaskTransportResponse>> resultsFromMembers =
            executorService.submitToAllMembers( new TaskTransportResponseHandler( request ) );

        for ( Future<TaskTransportResponse> responseFuture : resultsFromMembers.values() )
        {
            try
            {
                TaskTransportResponse response = responseFuture.get( TRANSPORT_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS );
                taskInfoBuilder.addAll( response.getTaskInfos() );
            }
            catch ( TimeoutException e )
            {
                resultsFromMembers.values().forEach( f -> f.cancel( true ) );
                throw new RuntimeException( e );
            }
            catch ( InterruptedException | ExecutionException e )
            {
                throw ExceptionUtil.rethrow( e );
            }
        }
        return taskInfoBuilder;
    }

    @Reference
    public void setHazelcastInstance( final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }
}
