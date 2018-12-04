package com.enonic.xp.impl.server.rest;

import com.enonic.xp.jaxrs.JaxRsComponent;

/*@Path(ResourceConstants.API_ROOT + "task")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")*/
public final class TaskResource
    implements JaxRsComponent
{
    /*private TaskService taskService;

    @GET
    @Path("info")
    public TaskInfoJson exportNodes( @QueryParam("id") final String id )
    {
        final TaskId taskId = TaskId.from( id );
        final TaskInfo taskInfo = taskService.getTaskInfo( taskId );

        return new TaskInfoJson(taskInfo);
    }

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }*/
}
