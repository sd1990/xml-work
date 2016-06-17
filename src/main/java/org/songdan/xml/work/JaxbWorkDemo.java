package org.songdan.xml.work;

import org.songdan.xml.model.Address;
import org.songdan.xml.model.Person;
import org.songdan.xml.model.RequestContent;
import org.songdan.xml.model.RequestParam;
import org.songdan.xml.util.AnnotationRuntimeModifyJavassist;
import org.songdan.xml.util.ModifyObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Songdan
 * @date 2016/6/15
 */
public class JaxbWorkDemo {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            try {
                testRuntimeModifyJaxbObject2XML();
//                testRuntimeConcurrentModifyJaxbObject2XML();
            }
            catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    private static void testRuntimeModifyJaxbObject2XML() throws NoSuchFieldException, IllegalAccessException {


        final ModifyObject modifyObject = new ModifyObject();
        modifyObject.setType(ElementType.TYPE);
        modifyObject.setTargetClassName("org.songdan.xml.model.RequestParam");
        modifyObject.setTargetAnnotationName("javax.xml.bind.annotation.XmlRootElement");
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("name", "TEST_NAME");
        modifyObject.setReplaceMap(replaceMap);

        final ModifyObject modifyObject1 = new ModifyObject();
        modifyObject1.setType(ElementType.FIELD);
        modifyObject1.setTargetClassName("org.songdan.xml.model.RequestParam");
        modifyObject1.setTargetAnnotationName("javax.xml.bind.annotation.XmlAttribute");
        modifyObject1.setFieldName("className");
        Map<String, Object> replaceMap1 = new HashMap<>();
        replaceMap1.put("name", "TEST_ATTRIBUTE_NAME");
        modifyObject1.setReplaceMap(replaceMap1);

        // 替换FIELD
        final ModifyObject modifyObject2 = new ModifyObject();
        modifyObject2.setType(ElementType.FIELD);
        modifyObject2.setTargetClassName("org.songdan.xml.model.RequestParam");
        modifyObject2.setFieldName("requestContent");
        modifyObject2.setTargetAnnotationName("javax.xml.bind.annotation.XmlElement");
        Map<String, Object> replaceMap2 = new HashMap<>();
        replaceMap2.put("name", "TEST_FIELD_NAME");
        modifyObject2.setReplaceMap(replaceMap2);

        final ModifyObject modifyObject3 = new ModifyObject();
        modifyObject3.setType(ElementType.FIELD);
        modifyObject3.setTargetClassName("org.songdan.xml.model.RequestContent");
        modifyObject3.setFieldName("list");
        modifyObject3.setTargetAnnotationName("javax.xml.bind.annotation.XmlElement");
        Map<String, Object> replaceMap3 = new HashMap<>();
        replaceMap3.put("name", "TEST_LIST_FIELD_NAME");
        replaceMap3.put("type", Person.class);
        modifyObject3.setReplaceMap(replaceMap3);

        Map<String, List<ModifyObject>> map = new HashMap<>();
        map.put("org.songdan.xml.model.RequestParam", new ArrayList<ModifyObject>(){

            {
                add(modifyObject);
                add(modifyObject1);
                add(modifyObject2);
            }
        });
        map.put("org.songdan.xml.model.RequestContent", new ArrayList<ModifyObject>(){

            {
                add(modifyObject3);
            }
        });
        AnnotationRuntimeModifyJavassist.modifyRuntime(map);
        RequestParam<Person> personRequestParam = getPersonRequestParam();
        jaxbObject2Xml(personRequestParam);
    }
    private static void testRuntimeConcurrentModifyJaxbObject2XML() throws NoSuchFieldException, IllegalAccessException {


        final ModifyObject modifyObject = new ModifyObject();
        modifyObject.setType(ElementType.TYPE);
        modifyObject.setTargetClassName("org.songdan.xml.model.RequestParam");
        modifyObject.setTargetAnnotationName("javax.xml.bind.annotation.XmlRootElement");
        Map<String, Object> replaceMap = new HashMap<>();
        replaceMap.put("name", "TEST_NAME");
        modifyObject.setReplaceMap(replaceMap);

        // 替换FIELD
        final ModifyObject modifyObject2 = new ModifyObject();
        modifyObject2.setType(ElementType.FIELD);
        modifyObject2.setTargetClassName("org.songdan.xml.model.RequestParam");
        modifyObject2.setFieldName("requestContent");
        modifyObject2.setTargetAnnotationName("javax.xml.bind.annotation.XmlElement");
        Map<String, Object> replaceMap2 = new HashMap<>();
        replaceMap2.put("name", "TEST_FIELD_NAME");
        modifyObject2.setReplaceMap(replaceMap2);

        final ModifyObject modifyObject3 = new ModifyObject();
        modifyObject3.setType(ElementType.FIELD);
        modifyObject3.setTargetClassName("org.songdan.xml.model.RequestContent");
        modifyObject3.setFieldName("list");
        modifyObject3.setTargetAnnotationName("javax.xml.bind.annotation.XmlElement");
        Map<String, Object> replaceMap3 = new HashMap<>();
        replaceMap3.put("name", "TEST_LIST_FIELD_NAME");
        replaceMap3.put("type", Person.class);
        modifyObject3.setReplaceMap(replaceMap3);

        Map<String, List<ModifyObject>> map = new HashMap<>();
        map.put("org.songdan.xml.model.RequestParam", new ArrayList<ModifyObject>(){

            {
                add(modifyObject);
                add(modifyObject2);
            }
        });
        map.put("org.songdan.xml.model.RequestContent", new ArrayList<ModifyObject>(){

            {
                add(modifyObject3);
            }
        });
        AnnotationRuntimeModifyJavassist.modifyRuntimeConcurrent(map);
        RequestParam<Person> personRequestParam = getPersonRequestParam();
        jaxbObject2Xml(personRequestParam);
    }

    private static RequestParam<Person> getPersonRequestParam() {
        Person p = new Person();
        p.setAge(23);
        p.setName("hello jaxb");
        Address address = new Address();
        address.setProvince("BeiJing");
        address.setCity("Beijing");
        address.setDetail("chuang er xin ke ji da sha");
        //        p.setAddress(address);
        RequestParam<Person> request = new RequestParam<>();
        request.setClassName("REQUEST_FPCX_PTCX_PARAM");

        RequestContent<Person> content = new RequestContent<>();
        content.setClassName("REQUEST_FPCX_PTCX");
        List<Person> persons = new ArrayList<>();
        persons.add(p);
        content.setList(persons);
        content.setSize(persons.size());

        request.setRequestContent(content);
        return request;
    }

    /**
     * javaBean 转换为 XML
     *
     * @return
     */
    private static void jaxbObject2Xml(Object param) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(param.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            //            marshaller.s
            StringWriter writer = new StringWriter();
            marshaller.marshal(param, writer);
            System.out.println(writer.toString());
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
