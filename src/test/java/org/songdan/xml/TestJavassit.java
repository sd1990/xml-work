package org.songdan.xml;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import org.junit.Test;
import org.songdan.xml.model.RequestParam;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Songdan
 * @date 2016/6/16
 */
public class TestJavassit {

    @Test
    public void test1() throws NotFoundException, ClassNotFoundException, NoSuchFieldException, CannotCompileException,
            IllegalAccessException, InstantiationException {
//        RequestParam requestParam = new RequestParam();
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("org.songdan.xml.model.RequestParam");
        CtField ctField = cc.getDeclaredField("requestContent");

        ClassFile cfile = cc.getClassFile();
        ConstPool cpool = cfile.getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation("javax.xml.bind.annotation.XmlElement", cpool);
        annot.addMemberValue("name", new StringMemberValue("TEST_JAVASSIT", cpool));
        attr.addAnnotation(annot);
        ctField.getFieldInfo().addAttribute(attr);

        Class aClass = cc.toClass();
        System.out
                .println(RequestParam.class.getDeclaredField("requestContent").getAnnotation(XmlElement.class).name());
    }

}
