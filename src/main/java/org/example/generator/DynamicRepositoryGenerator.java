package org.example.generator;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;

import java.lang.reflect.ParameterizedType;

public class DynamicRepositoryGenerator {
    
    private static final String BASE_PACKAGE = "org.example.repository.dynamic";
    
    /**
     * 生成动态Repository接口
     * @param entityClass 实体类
     * @param repositoryName Repository名称
     * @return 生成的Repository接口Class对象
     * @throws Exception
     */
    public static Class<?> generateRepositoryInterface(Class<?> entityClass, String repositoryName) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        
        // 创建接口
        CtClass ctInterface = pool.makeInterface(BASE_PACKAGE + "." + repositoryName);
        
        // 继承JpaRepository
        CtClass jpaRepositoryClass = pool.get("org.springframework.data.jpa.repository.JpaRepository");
        ctInterface.setSuperclass(jpaRepositoryClass);
        
        // 添加泛型参数
        ClassFile classFile = ctInterface.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        
        // 添加@Repository注解
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation repositoryAnnotation = new Annotation("org.springframework.stereotype.Repository", constPool);
        attr.addAnnotation(repositoryAnnotation);
        classFile.addAttribute(attr);
        
        return ctInterface.toClass();
    }
}