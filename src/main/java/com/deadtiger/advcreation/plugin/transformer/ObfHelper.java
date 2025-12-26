package com.deadtiger.advcreation.plugin.transformer;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObfHelper
{
    private static Boolean obfuscated = null;
    private static Boolean runsAfterDeobfRemapper = null;

    public ObfHelper() {
    }

    public static void setObfuscated(boolean obfuscated) {
        ObfHelper.obfuscated = obfuscated;
    }

    public static void setRunsAfterDeobfRemapper(boolean runsAfterDeobfRemapper) {
        ObfHelper.runsAfterDeobfRemapper = runsAfterDeobfRemapper;
    }

    public static boolean runsAfterDeobfRemapper() {
        if (runsAfterDeobfRemapper == null) {
            try {
                byte[] bytes = ((LaunchClassLoader) ObfHelper.class.getClassLoader()).getClassBytes("net.minecraft.world.World");
                setRunsAfterDeobfRemapper(bytes != null);
            } catch (IOException var1) {
                runsAfterDeobfRemapper = false;
            }
        }

        return runsAfterDeobfRemapper;
    }

    public static boolean isObfuscated() {
        if (obfuscated == null) {
            try {
                byte[] bytes = ((LaunchClassLoader) ObfHelper.class.getClassLoader()).getClassBytes("net.minecraft.world.World");
                setObfuscated(bytes == null);
            } catch (IOException var1) {
                obfuscated = true;
            }
        }

        return obfuscated;
    }

    public static String toDeobfClassName(String obfClassName) {
        return isObfuscated() && !runsAfterDeobfRemapper() ? forceToDeobfClassName(obfClassName) : obfClassName;
    }

    public static String forceToDeobfClassName(String obfClassName) {
        return FMLDeobfuscatingRemapper.INSTANCE.map(obfClassName.replace('.', '/')).replace('/', '.');
    }

    public static String toObfClassName(String deobfClassName) {
        return isObfuscated() && !runsAfterDeobfRemapper() ? forceToObfClassName(deobfClassName) : deobfClassName;
    }

    public static String forceToObfClassName(String deobfClassName) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(deobfClassName.replace('.', '/')).replace('/', '.');
    }

    public static String getInternalClassName(String className) {
        return toObfClassName(className).replace('.', '/');
    }

    public static String getDescriptor(String className) {
        return "L" + getInternalClassName(className) + ";";
    }

    public static String desc(String deobfDesc) {
        if (!isObfuscated()) {
            return deobfDesc;
        } else {
            Matcher classNameMatcher = Pattern.compile("L([^;]+);").matcher(deobfDesc);
            StringBuffer obfDescBuffer = new StringBuffer(deobfDesc.length());

            while(classNameMatcher.find()) {
                classNameMatcher.appendReplacement(obfDescBuffer, getDescriptor(classNameMatcher.group(1).replace('/', '.')));
            }

            classNameMatcher.appendTail(obfDescBuffer);
            return obfDescBuffer.toString();
        }
    }
}
