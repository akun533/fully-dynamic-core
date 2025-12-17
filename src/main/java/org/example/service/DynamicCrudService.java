package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;

@Service
public class DynamicCrudService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private DynamicSqlGenerator sqlGenerator;
    
    /**
     * 动态插入数据
     */
    @Transactional
    public int insert(String tableName, Map<String, Object> data) {
        // 验证表是否存在
        validateTableExists(tableName);
        
        String sql = sqlGenerator.generateInsertSql(data, tableName);
        Query query = entityManager.createNativeQuery(sql);
        
        // 绑定参数
        List<Object> values = sqlGenerator.getInsertValues(data);
        for (int i = 0; i < values.size(); i++) {
            query.setParameter(i + 1, values.get(i));
        }
        
        return query.executeUpdate();
    }
    
    /**
     * 动态查询数据
     */
    @Transactional(readOnly = true)
    public List<Object[]> select(String tableName, Map<String, Object> conditions) {
        // 验证表是否存在
        validateTableExists(tableName);
        
        String sql = sqlGenerator.generateSelectSql(tableName, conditions);
        Query query = entityManager.createNativeQuery(sql);
        
        // 绑定查询条件参数
        if (conditions != null) {
            int index = 1;
            for (Object value : conditions.values()) {
                query.setParameter(index++, value);
            }
        }
        
        return query.getResultList();
    }
    
    /**
     * 动态更新数据
     */
    @Transactional
    public int update(String tableName, Map<String, Object> data, Map<String, Object> conditions) {
        // 验证表是否存在
        validateTableExists(tableName);
        
        String sql = sqlGenerator.generateUpdateSql(tableName, data, conditions);
        Query query = entityManager.createNativeQuery(sql);
        
        // 绑定参数
        int index = 1;
        for (Object value : data.values()) {
            query.setParameter(index++, value);
        }
        
        for (Object value : conditions.values()) {
            query.setParameter(index++, value);
        }
        
        return query.executeUpdate();
    }
    
    /**
     * 动态删除数据
     */
    @Transactional
    public int delete(String tableName, Map<String, Object> conditions) {
        // 验证表是否存在
        validateTableExists(tableName);
        
        String sql = sqlGenerator.generateDeleteSql(tableName, conditions);
        Query query = entityManager.createNativeQuery(sql);
        
        // 绑定查询条件参数
        if (conditions != null) {
            int index = 1;
            for (Object value : conditions.values()) {
                query.setParameter(index++, value);
            }
        }
        
        return query.executeUpdate();
    }
    
    /**
     * 执行任意SQL语句
     */
    @Transactional
    public void executeSql(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        query.executeUpdate();
    }
    
    /**
     * 验证表是否存在
     * @param tableName 表名
     */
    private void validateTableExists(String tableName) {
        try {
            // 尝试从表中选择一行数据来验证表是否存在
            entityManager.createNativeQuery("SELECT 1 FROM " + tableName + " LIMIT 1").getResultList();
        } catch (Exception e) {
            throw new RuntimeException("表 '" + tableName + "' 不存在或无法访问: " + e.getMessage());
        }
    }
}