package com.grade.quickid.model.Estadisticas.aplication;

import com.google.common.escape.Escaper;
import com.grade.quickid.model.Estadisticas.domain.Estadistica;

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

    void removeChild(String name) {
        children.remove(name);
    }

    EstadisticaFecha getChild(String name) {
        return children.get(name);
    }

    Set<EstadisticaFecha> getChildren() {
        return Collections.unmodifiableSet(
                new LinkedHashSet<EstadisticaFecha>(children.values()));
    }
}