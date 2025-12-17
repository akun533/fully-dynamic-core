package org.example.controller;

import org.example.generator.DynamicEntityGenerator;
import org.example.service.DynamicCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test-dynamic")
public class TestDynamicController {
    
    @Autowired
    private DynamicCrudService dynamicCrudService;
    
    /**
     * 测试动态创建实体类
     */
    @PostMapping("/create-entity")
    public Map<String, Object> createEntity() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 定义字段
            Map<String, String> fields = new HashMap<>();
            fields.put("name", "string");
            fields.put("email", "string");
            fields.put("age", "int");
            
            // 生成实体类
            Class<?> entityClass = DynamicEntityGenerator.generateEntityClass("Person", fields);
            
            response.put("success", true);
            response.put("className", entityClass.getName());
            response.put("message", "实体类创建成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
    /**
     * 测试动态插入数据
     */
    @PostMapping("/insert")
    public Map<String, Object> insertData(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int result = dynamicCrudService.insert("person", data);
            response.put("success", true);
            response.put("affectedRows", result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "插入失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
}