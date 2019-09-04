package com.enonic.xp.project;

public interface ProjectService {
    Projects list();

    Project get(ProjectName projectName);

    void create(CreateProjectParams params);

    void modify (ModifyProjectParams params);

    void delete(ProjectName projectName);
}
