package org.example;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@XmlRootElement(name = "violates")
@XmlAccessorType(XmlAccessType.FIELD)
public class ViolationsMap {
    private Map<String, Double> violate = new LinkedHashMap<>();
}
