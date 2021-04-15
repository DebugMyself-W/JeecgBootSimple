package com.aoyang.common.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * 使用jdk类库实现xml转化pojo
 *
 * @Author: went
 * @Date: 2021年01月25日09:05:45
 */
public class XmlUtils {

    private XmlUtils(){};

    public static <T> T xmlFileToObject(String xmlFilePath, Class<T> clazz) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = contextClassLoader.getResourceAsStream(xmlFilePath)) {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T result = (T) unmarshaller.unmarshal(inputStream);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("convert xml to POJO failure!", e);
        }
    }

    public static <T> T xmlStringToObject(String xmlString, Class<T> clazz) {
        try (Reader reader = new StringReader(xmlString)) {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T result = (T) unmarshaller.unmarshal(reader);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("convert xml string to POJO failure!", e);
        }
    }
}
