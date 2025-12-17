package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.List;

@Service
public class DynamicSqlGenerator {
    
    /**
     * 根据对象生成INSERT SQL语句
     */
    public String generateInsertSql(Object obj, String tableName) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        List<String> columnNames = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        
        for (Field field : fields) {
            field.setAccessible(true);
            columnNames.add(field.getName());
            placeholders.add("?");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (");
        sql.append(String.join(", ", columnNames));
        sql.append(") VALUES (");
        sql.append(String.join(", ", placeholders));
        sql.append(")");
        
        return sql.toString();
    }
    
    /**
     * 根据Map生成INSERT SQL语句
     */
    public String generateInsertSql(Map<String, Object> data, String tableName) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("数据不能为空");
        }
        
        List<String> columnNames = new ArrayList<>(data.keySet());
        List<String> placeholders = new ArrayList<>();
        
        for (int i = 0; i < columnNames.size(); i++) {
            placeholders.add("?");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (");
        sql.append(String.join(", ", columnNames));
        sql.append(") VALUES (");
        sql.append(String.join(", ", placeholders));
        sql.append(")");
        
        return sql.toString();
    }
    
    /**
     * 获取用于INSERT的数据值列表
     */
    public List<Object> getInsertValues(Map<String, Object> data) {
        return new ArrayList<>(data.values());
    }
    
    /**
     * 根据对象生成UPDATE SQL语句
     */
    public String generateUpdateSql(Object obj, String tableName, String idField) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        List<String> setClauses = new ArrayList<>();
        
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.getName().equals(idField)) {
                setClauses.add(field.getName() + " = ?");
            }
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ");
        sql.append(String.join(", ", setClauses));
        sql.append(" WHERE ").append(idField).append(" = ?");
        
        return sql.toString();
    }
    
    /**
     * 根据数据和条件生成UPDATE SQL语句
     */
    public String generateUpdateSql(String tableName, Map<String, Object> data, Map<String, Object> conditions) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("更新数据不能为空");
        }
        
        List<String> setClauses = new ArrayList<>();
        
        for (String key : data.keySet()) {
            setClauses.add(key + " = ?");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET ");
        sql.append(String.join(", ", setClauses));
        
        if (conditions != null && !conditions.isEmpty()) {
            sql.append(" WHERE ");
            List<String> whereConditions = new ArrayList<>();
            
            for (String key : conditions.keySet()) {
                whereConditions.add(key + " = ?");
            }
            
            sql.append(String.join(" AND ", whereConditions));
        }
        
        return sql.toString();
    }
    
    /**
     * 根据条件生成SELECT SQL语句
     */
    public String generateSelectSql(String tableName, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(tableName);
        
        if (conditions != null && !conditions.isEmpty()) {
            sql.append(" WHERE ");
            List<String> whereConditions = new ArrayList<>();
            
            for (String key : conditions.keySet()) {
                whereConditions.add(key + " = ?");
            }
            
            sql.append(String.join(" AND ", whereConditions));
        }
        
        return sql.toString();
    }
    
    /**
     * 根据条件生成DELETE SQL语句
     */
    public String generateDeleteSql(String tableName, Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(tableName);
        
        if (conditions != null && !conditions.isEmpty()) {
            sql.append(" WHERE ");
            List<String> whereConditions = new ArrayList<>();
            
            for (String key : conditions.keySet()) {
                whereConditions.add(key + " = ?");
            }
            
            sql.append(String.join(" AND ", whereConditions));
        }
        
        return sql.toString();
    }
}