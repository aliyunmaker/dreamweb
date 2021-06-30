package cc.landingzone.dreamweb.dao;

import java.util.List;

import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.SolutionConfig;

public interface SolutionConfigDao {

    SolutionConfig getSolutionConfigById(Integer id);

    SolutionConfig getSolutionConfigByName(String name);

    int getSolutionNumber(String module, String searchInput);

    List<SolutionConfig> searchSolution(String module, String searchInput, Page page);

    int addSolutionConfig(SolutionConfig solutionConfig);

    void updateSolutionConfig(SolutionConfig solutionConfig);

    void deleteSolutionConfig(Integer id);

}
