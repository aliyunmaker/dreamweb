package cc.landingzone.dreamweb.dao;

import java.util.List;

import cc.landingzone.dreamweb.model.SolutionConfig;

public interface SolutionConfigDao {

    SolutionConfig getSolutionConfigById(Integer id);

    SolutionConfig getSolutionConfigByName(String name);

    List<SolutionConfig> searchSolution(String module, String searchInput, Boolean isModuleAll, Boolean isSearchBlank);

    int addSolutionConfig(SolutionConfig solutionConfig);

    void updateSolutionConfig(SolutionConfig solutionConfig);

    void deleteSolutionConfig(Integer id);

}
