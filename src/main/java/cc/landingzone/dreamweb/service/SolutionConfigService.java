package cc.landingzone.dreamweb.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cc.landingzone.dreamweb.dao.SolutionConfigDao;
import cc.landingzone.dreamweb.model.SolutionConfig;

@Component
public class SolutionConfigService {

    @Autowired
    private SolutionConfigDao solutionConfigDao;

    /**
     * 通过id获得解决方案
     * 
     * @param id
     * @return
     */
    public SolutionConfig getSolutionConfigById(Integer id) {
        Assert.notNull(id, "id不能为空");
        return solutionConfigDao.getSolutionConfigById(id);
    }

    /**
     * 通过名字获取解决方案
     * 
     * @param name 配置名
     * @return
     */
    public SolutionConfig getSolutionConfigByName(String name) {
        Assert.hasText(name, "配置名不能为空!");
        return solutionConfigDao.getSolutionConfigByName(name);
    }

    /**
     * 根据模块和搜索栏获取一页解决方案
     * 
     * @param module
     * @param searchInput
     * @param page
     * @return
     */
    public List<SolutionConfig> searchSolution(String module, String searchInput) {
        Assert.notNull(module, "模块不能为空!");
        Assert.notNull(searchInput, "搜索不能为空!");
        if (module.equals("全部")) {
            module = "";
        }
        return solutionConfigDao.searchSolution(module, searchInput);
    }

    /**
     * 添加解决方案配置
     *
     * @param solutionConfig
     */
    @Transactional
    public void addSolutionConfig(SolutionConfig solutionConfig) {
        Assert.notNull(solutionConfig, "数据不能为空!");
        solutionConfigDao.addSolutionConfig(solutionConfig);
    }

    /**
     * 更新解决方案配置
     * 
     * @param solutionConfig
     */
    @Transactional
    public void updateSolutionConfig(SolutionConfig solutionConfig) {
        Assert.notNull(solutionConfig, "数据不能为空!");
        solutionConfigDao.updateSolutionConfig(solutionConfig);
    }

    /**
     * 删除解决方案配置
     *
     * @param id
     */
    @Transactional
    public void deleteSolutionConfig(Integer id) {
        Assert.notNull(id, "id can not be null!");
        solutionConfigDao.deleteSolutionConfig(id);
    }

}
