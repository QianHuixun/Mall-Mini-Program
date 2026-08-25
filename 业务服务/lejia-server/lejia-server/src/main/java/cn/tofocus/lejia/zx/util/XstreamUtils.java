package cn.tofocus.lejia.zx.util;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;

public class XstreamUtils {
    public static <T> T toBean(String xml, Class<T> clazz) {
        XStream stream = new XStream();
        stream.processAnnotations(clazz);
        stream.autodetectAnnotations(true);
        stream.setClassLoader(clazz.getClassLoader());
        return (T) stream.fromXML(xml);
    }

    public static String toXml(Object obj, Class clazz) {
        XStream stream = new XStream(new Xpp3DomDriver(new NoNameCoder()));
        stream.processAnnotations(clazz);
        stream.autodetectAnnotations(true);
        stream.setClassLoader(clazz.getClassLoader());
        return stream.toXML(obj);
    }
}
