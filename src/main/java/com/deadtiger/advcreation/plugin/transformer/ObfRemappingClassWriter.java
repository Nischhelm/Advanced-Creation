package com.deadtiger.advcreation.plugin.transformer;

import org.objectweb.asm.ClassWriter;



public class ObfRemappingClassWriter extends ClassWriter
{
    public ObfRemappingClassWriter(int flags) {
        super(flags);
    }

    protected String getCommonSuperClass(String type1, String type2) {
        ClassLoader classLoader = this.getClass().getClassLoader();

        Class c;
        Class d;
        try {
            c = Class.forName(ObfHelper.toDeobfClassName(type1.replace('/', '.')), false, classLoader);
            d = Class.forName(ObfHelper.toDeobfClassName(type2.replace('/', '.')), false, classLoader);
        } catch (Exception var7) {
            throw new RuntimeException(var7);
        }

        if (c.isAssignableFrom(d)) {
            return type1;
        } else if (d.isAssignableFrom(c)) {
            return type2;
        } else if (!c.isInterface() && !d.isInterface()) {
            do {
                c = c.getSuperclass();
            } while(!c.isAssignableFrom(d));

            return ObfHelper.toObfClassName(c.getName()).replace('.', '/');
        } else {
            return "java/lang/Object";
        }
    }
}

