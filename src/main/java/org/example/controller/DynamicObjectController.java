package org.example.controller;

import org.example.generator.DynamicEntityGenerator;
import org.example.generator.DynamicRepositoryGenerator;
import org.example.service.DynamicSqlGenerator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dynamic")
public class DynamicObjectController {
    
    @Autowired
    private DynamicSqlGenerator sqlGenerator;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private ConfigurableApplicationContext applicationContext;
    
    /**
     * 动态创建实体类和Repository
     */
    @PostMapping("/create-entity")
    public Map<String, Object> createEntity(@RequestParam String className,
                                          @RequestBody Map<String, String> fields) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 生成实体类
            Class<?> entityClass = DynamicEntityGenerator.generateEntityClass(className, fields);
            
            // 生成Repository接口
            String repositoryName = className + "Repository";
            Class<?> repositoryClass = DynamicRepositoryGenerator.generateRepositoryInterface(entityClass, repositoryName);
            
            // 注册Bean到Spring容器
            registerBean(className, entityClass);
            registerBean(repositoryName, repositoryClass);
            
            response.put("success", true);
            response.put("message", "实体类和Repository创建成功");
            response.put("entityClass", entityClass.getName());
            response.put("repositoryClass", repositoryClass.getName());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 动态执行插入操作
     */
    @PostMapping("/{tableName}/insert")
    public Map<String, Object> insert(@PathVariable String tableName,
                                    @RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = sqlGenerator.generateInsertSql(data, tableName);
            Query query = entityManager.createNativeQuery(sql);
            
            // 设置参数（简化处理，实际应按顺序设置）
            int index = 1;
            for (Object value : data.values()) {
                query.setParameter(index++, value);
            }
            
            int result = query.executeUpdate();
            
            response.put("success", true);
            response.put("affectedRows", result);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈跟踪以帮助调试
            response.put("success", false);
            response.put("message", "插入失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 动态执行查询操作
     */
    @GetMapping("/{tableName}")
    public Map<String, Object> select(@PathVariable String tableName,
                                    @RequestParam(required = false) Map<String, Object> conditions) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = sqlGenerator.generateSelectSql(tableName, conditions);
            Query query = entityManager.createNativeQuery(sql);
            
            // 设置参数
            if (conditions != null) {
                int index = 1;
                for (Object value : conditions.values()) {
                    query.setParameter(index++, value);
                }
            }
            
            List<Object> result = query.getResultList();
            
            response.put("success", true);
            response.put("data", result);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈跟踪以帮助调试
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 动态执行更新操作
     */
    @PutMapping("/{tableName}")
    public Map<String, Object> update(@PathVariable String tableName,
                                    @RequestParam Map<String, Object> conditions,
                                    @RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = sqlGenerator.generateUpdateSql(tableName, data, conditions);
            Query query = entityManager.createNativeQuery(sql);
            
            // 设置参数
            int index = 1;
            for (Object value : data.values()) {
                query.setParameter(index++, value);
            }
            
            for (Object value : conditions.values()) {
                query.setParameter(index++, value);
            }
            
            int result = query.executeUpdate();
            
            response.put("success", true);
            response.put("affectedRows", result);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈跟踪以帮助调试
            response.put("success", false);
            response.put("message", "更新失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 动态执行删除操作
     */
    @DeleteMapping("/{tableName}")
    public Map<String, Object> delete(@PathVariable String tableName,
                                    @RequestParam Map<String, Object> conditions) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String sql = sqlGenerator.generateDeleteSql(tableName, conditions);
            Query query = entityManager.createNativeQuery(sql);
            
            // 设置参数
            int index = 1;
            for (Object value : conditions.values()) {
                query.setParameter(index++, value);
            }
            
            int result = query.executeUpdate();
            
            response.put("success", true);
            response.put("affectedRows", result);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈跟踪以帮助调试
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 注册Bean到Spring容器
     */
    private void registerBean(String beanName, Class<?> beanClass) {
        DefaultListableBeanFactory beanFactory = 
            (DefaultListableBeanFactory) ((WebApplicationContext) applicationContext).getAutowireCapableBeanFactory();
        
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }
}