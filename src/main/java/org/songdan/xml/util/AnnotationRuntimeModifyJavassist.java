package org.songdan.xml.util;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 运行时修改注解的值
 * @author Songdan
 * @date 2016/6/16
 */
public class AnnotationRuntimeModifyJavassist {

    private static Lock lock = new ReentrantLock();

    /**
     * 运行时修改指定类上注解的属性
     */
    public static void modifyRuntime(Map<String, List<ModifyObject>> modifyObjectMap) {
        lock.lock();
        try{
            ClassPool classPool = ClassPool.getDefault();
            for (Map.Entry<String, List<ModifyObject>> modifyObjectEntry : modifyObjectMap.entrySet()) {
                /*
                1、获取注解在类上的位置，分情况处理
                 */
                modifyClass(classPool, modifyObjectEntry);
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * 同一个类上要修改的modifyObject作为一组修改
     * @param classPool
     * @param modifyObjectEntry
     */
    private static void modifyClass(ClassPool classPool, Map.Entry<String, List<ModifyObject>> modifyObjectEntry) {
        try {
            CtClass ctClass = classPool.get(modifyObjectEntry.getKey());
            for (ModifyObject modifyObject : modifyObjectEntry.getValue()) {
                ElementType type = modifyObject.getType();
                String annotationName = modifyObject.getTargetAnnotationName();
                switch (type) {
                    default:
                    case TYPE:
                        changeTypeAnnotation(ctClass, modifyObject.getReplaceMap(), annotationName);
                        break;
                    case FIELD:
                        changeFieldAnnotation(ctClass, modifyObject.getFieldName(),
                                modifyObject.getTargetAnnotationName(), modifyObject.getReplaceMap());
                        break;
                }
            }
            ctClass.toClass();
        }
        catch (CannotCompileException e) {
            e.printStackTrace();
        }
        catch (NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改类上的注解
     * @param ctClass
     * @param replaceMap
     * @param annotationName
     */
    private static void changeTypeAnnotation(CtClass ctClass, Map<String, Object> replaceMap, String annotationName) {
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute annotationsAttribute =
                new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation annotation =
                new javassist.bytecode.annotation.Annotation(annotationName, constPool);
        for (Map.Entry<String, Object> stringObjectEntry : replaceMap.entrySet()) {
            Object value = stringObjectEntry.getValue();
            String key = stringObjectEntry.getKey();
            if (value instanceof Class) {
                Class<?> c = (Class<?>) value;
                annotation.addMemberValue(key, new ClassMemberValue(c.getName(), constPool));
            }
            else {
                annotation.addMemberValue(key, new StringMemberValue((String) value, constPool));
            }
        }
        annotationsAttribute.setAnnotation(annotation);
        classFile.addAttribute(annotationsAttribute);
    }

    /**
     * 修改字段上的注解
     * @param ctClass
     * @param fieldName
     * @param annotationName
     * @param replaceMap
     * @throws NotFoundException
     */
    private static void changeFieldAnnotation(CtClass ctClass, String fieldName, String annotationName,
            Map<String, Object> replaceMap) throws NotFoundException {
        ClassFile classFile = ctClass.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        CtField field = ctClass.getDeclaredField(fieldName);
        FieldInfo fieldInfo = field.getFieldInfo();
        AnnotationsAttribute annotationsAttribute =
                new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        javassist.bytecode.annotation.Annotation annotation =
                new javassist.bytecode.annotation.Annotation(annotationName, constPool);
        for (Map.Entry<String, Object> stringObjectEntry : replaceMap.entrySet()) {
            Object value = stringObjectEntry.getValue();
            String key = stringObjectEntry.getKey();
            if (value instanceof Class) {
                Class<?> c = (Class<?>) value;
                annotation.addMemberValue(key, new ClassMemberValue(c.getName(), constPool));
            }
            else {
                annotation.addMemberValue(key, new StringMemberValue((String) value, constPool));
            }
        }
        annotationsAttribute.setAnnotation(annotation);
        fieldInfo.addAttribute(annotationsAttribute);
    }

}

