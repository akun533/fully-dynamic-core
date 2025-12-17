package org.example.controller;

import org.example.service.DynamicCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用CRUD控制器，提供完整的增删改查API
 */
@RestController
@RequestMapping("/api/crud")
public class GenericCrudController {

    @Autowired
    private DynamicCrudService crudService;

    /**
     * 插入数据
     *
     * @param tableName 表名
     * @param data      要插入的数据
     * @return 操作结果
     */
    @PostMapping("/{tableName}")
    public Map<String, Object> insert(@PathVariable String tableName,
                                      @RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();

        try {
            int result = crudService.insert(tableName, data);
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
     * 查询数据
     *
     * @param tableName  表名
     * @param conditions 查询条件（可选）
     * @return 查询结果
     */
    @GetMapping("/{tableName}")
    public Map<String, Object> select(@PathVariable String tableName,
                                      @RequestParam(required = false) Map<String, Object> conditions) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Object[]> result = crudService.select(tableName, conditions);
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
     * 更新数据
     *
     * @param tableName  表名
     * @param conditions 更新条件
     * @param data       要更新的数据
     * @return 操作结果
     */
    @PutMapping("/{tableName}")
    public Map<String, Object> update(@PathVariable String tableName,
                                      @RequestParam Map<String, Object> conditions,
                                      @RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();

        try {
            int result = crudService.update(tableName, data, conditions);
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
     * 删除数据
     *
     * @param tableName  表名
     * @param conditions 删除条件
     * @return 操作结果
     */
    @DeleteMapping("/{tableName}")
    public Map<String, Object> delete(@PathVariable String tableName,
                                      @RequestParam Map<String, Object> conditions) {
        Map<String, Object> response = new HashMap<>();

        try {
            int result = crudService.delete(tableName, conditions);
            response.put("success", true);
            response.put("affectedRows", result);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈跟踪以帮助调试
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
        }

        return response;
    }
}