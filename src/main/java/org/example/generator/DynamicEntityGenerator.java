package org.example.generator;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import java.util.Map;

public class DynamicEntityGenerator {
    
    private static final String BASE_PACKAGE = "org.example.entity.dynamic";
    
    /**
     * 根据表名和字段定义动态生成实体类
     * @param className 类名
     * @param fields 字段定义，key为字段名，value为字段类型
     * @return 生成的Class对象
     * @throws Exception
     */
    public static Class<?> generateEntityClass(String className, Map<String, String> fields) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass(BASE_PACKAGE + "." + className);
        
        // 添加@Entity注解
        ClassFile classFile = cc.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation entityAnnotation = new Annotation("jakarta.persistence.Entity", constPool);
        attr.addAnnotation(entityAnnotation);
        
        Annotation tableAnnotation = new Annotation("jakarta.persistence.Table", constPool);
        tableAnnotation.addMemberValue("name", new StringMemberValue(className.toLowerCase(), constPool));
        attr.addAnnotation(tableAnnotation);
        
        classFile.addAttribute(attr);
        
        // 添加@Id字段
        CtField idField = new CtField(pool.get("java.lang.Long"), "id", cc);
        idField.setModifiers(Modifier.PRIVATE);
        cc.addField(idField);
        
        // 添加@Id和@GeneratedValue注解到id字段
        AnnotationsAttribute idAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation idAnnotation = new Annotation("jakarta.persistence.Id", constPool);
        idAttr.addAnnotation(idAnnotation);
        
        Annotation generatedValueAnnotation = new Annotation("jakarta.persistence.GeneratedValue", constPool);
        // 简化处理，不添加strategy属性
        idAttr.addAnnotation(generatedValueAnnotation);
        
        idField.getFieldInfo().addAttribute(idAttr);
        
        // 为每个字段生成getter和setter方法
        CtMethod setIdMethod = CtNewMethod.setter("setId", idField);
        cc.addMethod(setIdMethod);
        
        CtMethod getIdMethod = CtNewMethod.getter("getId", idField);
        cc.addMethod(getIdMethod);
        
        // 添加其他字段
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldType = entry.getValue();
            
            CtField field = new CtField(resolveType(pool, fieldType), fieldName, cc);
            field.setModifiers(Modifier.PRIVATE);
            cc.addField(field);
            
            // 添加@Column注解
            AnnotationsAttribute columnAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            Annotation columnAnnotation = new Annotation("jakarta.persistence.Column", constPool);
            columnAnnotation.addMemberValue("name", new StringMemberValue(fieldName, constPool));
            columnAttr.addAnnotation(columnAnnotation);
            field.getFieldInfo().addAttribute(columnAttr);
            
            // 生成getter和setter方法
            CtMethod setter = CtNewMethod.setter("set" + capitalize(fieldName), field);
            cc.addMethod(setter);
            
            CtMethod getter = CtNewMethod.getter("get" + capitalize(fieldName), field);
            cc.addMethod(getter);
        }
        
        // 添加toString方法
        StringBuilder toStringBody = new StringBuilder();
        toStringBody.append("public String toString() { return \"")
                   .append(className)
                   .append("{\" +\n");
        
        boolean first = true;
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            if (!first) {
                toStringBody.append("\"").append(", ").append(fieldName).append("='\" + ")
                           .append(fieldName)
                           .append(" + '\'' +\n");
            } else {
                toStringBody.append("\"").append(fieldName).append("='\" + ")
                           .append(fieldName)
                           .append(" + '\'' +\n");
                first = false;
            }
        }
        toStringBody.append("\"}\"; }");
        
        CtMethod toStringMethod = CtNewMethod.make(toStringBody.toString(), cc);
        cc.addMethod(toStringMethod);
        
        return cc.toClass();
    }
    
    /**
     * 解析字段类型
     */
    private static CtClass resolveType(ClassPool pool, String type) throws NotFoundException {
        switch (type.toLowerCase()) {
            case "string":
                return pool.get("java.lang.String");
            case "int":
            case "integer":
                return pool.get("java.lang.Integer");
            case "long":
                return pool.get("java.lang.Long");
            case "double":
                return pool.get("java.lang.Double");
            case "boolean":
                return pool.get("java.lang.Boolean");
            case "date":
                return pool.get("java.util.Date");
            default:
                return pool.get(type);
        }
    }
    
    /**
     * 首字母大写
     */
    private static String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}