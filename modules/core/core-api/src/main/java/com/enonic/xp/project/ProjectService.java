package com.enonic.xp.project;

public interface ProjectService {
    Projects list();

    Project get(ProjectName projectName);

    Project create(CreateProjectParams params);

    Project modify (ModifyProjectParams params);

    Project delete(ProjectName projectName);
}
