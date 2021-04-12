package com.grade.quickid.model.estadisticas.aplication;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class EstadisticaFecha {
    final  String name;
    final  Map<String, EstadisticaFecha> children;
    final  Object customObject;

    public EstadisticaFecha(String name, Object customObject) {
        this.name = name;
        this.children = new LinkedHashMap<String, EstadisticaFecha>();
        this.customObject = customObject;
    }
    String getName() {
        return name;
    }
    void addChild(EstadisticaFecha child) {
        children.put(child.getName(), child);
    }
}