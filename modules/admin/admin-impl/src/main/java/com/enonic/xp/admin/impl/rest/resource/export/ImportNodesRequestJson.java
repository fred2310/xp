package com.enonic.xp.admin.impl.rest.resource.export;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class ImportNodesRequestJson
{
    private final RepoPath targetRepoPath;

    private final String exportName;

    private final boolean dryRun;

    private final boolean importWithIds;

    private final boolean importWithPermissions;

    private final String xslSource;

    private final Map<String, Object> xslParams;

    @JsonCreator
    public ImportNodesRequestJson( @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("targetRepoPath") final String targetRepoPath, //
                                   @JsonProperty("importWithIds") final Boolean importWithIds, //
                                   @JsonProperty("importWithPermissions") final Boolean importWithPermissions, //
                                   @JsonProperty("dryRun") final Boolean dryRun, //
                                   @JsonProperty("xslSource") final String xslSource, //
                                   @JsonProperty("xslParams") final Map<String, Object> xslParams )

    {

        Preconditions.checkNotNull( exportName, "exportName not given" );
        Preconditions.checkNotNull( targetRepoPath, "targetRepoPath not given" );

        this.targetRepoPath = RepoPath.from( targetRepoPath );
        this.exportName = exportName;
        this.dryRun = dryRun != null ? dryRun : false;
        this.importWithIds = importWithIds != null ? importWithIds : true;
        this.importWithPermissions = importWithPermissions != null ? importWithPermissions : true;
        this.xslSource = xslSource;
        this.xslParams = xslParams;
    }

    public RepoPath getTargetRepoPath()
    {
        return targetRepoPath;
    }

    public String getExportName()
    {
        return exportName;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public boolean isImportWithIds()
    {
        return importWithIds;
    }

    public boolean isImportWithPermissions()
    {
        return importWithPermissions;
    }

    public String getXslSource()
    {
        return xslSource;
    }

    public Map<String, Object> getXslParams()
    {
        return xslParams;
    }
}
