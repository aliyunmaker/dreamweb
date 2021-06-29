package cc.landingzone.dreamweb.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cc.landingzone.dreamweb.dao.SolutionConfigDao;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.SolutionConfig;

@Component
public class SolutionConfigService {

    @Autowired
    private SolutionConfigDao solutionConfigDao;

    /**
     * 获取所有解决方案
     * 
     * @return
     */
    public List<SolutionConfig> listSolutionConfig() {
        return solutionConfigDao.listSolutionConfig();
    }

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
     * 根据模块和搜索栏获取解决方案的数目
     * 
     * @param module
     * @param searchInput
     * @return
     */
    public int getSolutionNumber(String module, String searchInput) {
        return solutionConfigDao.getSolutionNumber(module, searchInput);
    }

    /**
     * 根据模块和搜索栏获取一页解决方案
     * 
     * @param module
     * @param searchInput
     * @param page
     * @return
     */
    public List<SolutionConfig> searchSolution(String module, String searchInput, Page page) {
        return solutionConfigDao.searchSolution(module, searchInput, page);
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
