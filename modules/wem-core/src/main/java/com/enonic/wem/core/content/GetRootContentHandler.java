package com.enonic.wem.core.content;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.command.CommandHandler;

public class GetRootContentHandler
    extends CommandHandler<GetRootContent>
{
    @Inject
    private NodeService nodeService;

    @Override
    public void handle()
        throws Exception
    {
        final Contents contents = new GetRootContentService( this.context, this.command, this.nodeService ).execute();

        command.setResult( contents );
    }
}
