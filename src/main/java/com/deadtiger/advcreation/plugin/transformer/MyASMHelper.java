package com.deadtiger.advcreation.plugin.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;

public class MyASMHelper
{
    private static Map<Integer, String> insnOpcodes= new HashMap<>();
    private static Map<Integer, String> fieldInsnOpcodes= new HashMap<>();

    static
    {
        insnOpcodes.put(106,"FMUL");
        insnOpcodes.put(98,"FADD");
        insnOpcodes.put(149,"FCMPL");
        insnOpcodes.put(150,"FCMPG");
        insnOpcodes.put(12,"FCONST_1");
        insnOpcodes.put(110,"FDIV");
        insnOpcodes.put(89,"DUP");
        insnOpcodes.put(102,"FSUB");
        insnOpcodes.put(141,"F2D");
        insnOpcodes.put(99,"DADD");
        insnOpcodes.put(177,"RETURN");

        fieldInsnOpcodes.put(181,"PUTFIELD");
        fieldInsnOpcodes.put(180,"GETFIELD");
    }

    public static String lookupInsnOpcode(int opcode)
    {
        if(insnOpcodes.containsKey(opcode))
            return insnOpcodes.get(opcode);
        return Integer.toString(opcode);
    }

    public static String lookupFieldInsnOpcode(int opcode)
    {
        if(fieldInsnOpcodes.containsKey(opcode))
            return fieldInsnOpcodes.get(opcode);
        return Integer.toString(opcode);
    }


    public static AbstractInsnNode removePreviousAmountOfInstructions(AbstractInsnNode popNode, int amount, MethodNode method)
    {
        for (int i = 0; i < amount; i++) {
            popNode = popNode.getPrevious();
            method.instructions.remove(popNode.getNext());
        }
        return  popNode;
    }

    public static boolean isObfuscated(String name, String transformedName)
    {
        return !name.equals(transformedName);
    }

    public static AbstractInsnNode removeNextAmountOfInstructions(AbstractInsnNode popNode, int amount, MethodNode method)
    {
        System.out.println("removing the next " + amount + " instructions");
        for (int i = 0; i < amount; i++)
        {
            popNode = popNode.getNext();
            printInstruction(popNode.getPrevious(),i);
            method.instructions.remove(popNode.getPrevious());
        }
        return popNode;
    }


    //From this point on all code has been taken from ASMHelper by squeek502
    //https://github.com/squeek502/ASMHelper
    // I had to take it because there were issues with the gradle integration in newer versions of forge

    public static ClassNode readClassFromBytes(byte[] bytes) {
        return readClassFromBytes(bytes, 0);
    }

    public static ClassNode readClassFromBytes(byte[] bytes, int flags) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, flags);
        return classNode;
    }

    public static byte[] writeClassToBytes(ClassNode classNode, int flags) {
        if (ObfHelper.isObfuscated() && !ObfHelper.runsAfterDeobfRemapper()) {
            ClassWriter writer = new ObfRemappingClassWriter(flags);
            classNode.accept(writer);
            return writer.toByteArray();
        } else {
            return writeClassToBytesNoDeobf(classNode, flags);
        }
    }

    public static byte[] writeClassToBytesNoDeobf(ClassNode classNode, int flags) {
        ClassWriter writer = new ClassWriter(flags);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static AbstractInsnNode findFirstInstructionWithOpcode(MethodNode method, int opcode) {
        return getOrFindInstructionWithOpcode(method.instructions.getFirst(), opcode);
    }

    public static AbstractInsnNode getOrFindInstructionWithOpcode(AbstractInsnNode firstInsnToCheck, int opcode) {
        return getOrFindInstructionWithOpcode(firstInsnToCheck, opcode, false);
    }

    public static AbstractInsnNode getOrFindInstructionWithOpcode(AbstractInsnNode firstInsnToCheck, int opcode, boolean reverseDirection) {
        for(AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext()) {
            if (instruction.getOpcode() == opcode) {
                return instruction;
            }
        }

        return null;
    }

    public static AbstractInsnNode findNextInstructionWithOpcode(AbstractInsnNode instruction, int opcode) {
        return getOrFindInstructionWithOpcode(instruction.getNext(), opcode);
    }

    public static AbstractInsnNode findFirstInstruction(MethodNode method) {
        return getOrFindInstruction(method.instructions.getFirst());
    }

    public static AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck) {
        return getOrFindInstruction(firstInsnToCheck, false);
    }

    public static AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck, boolean reverseDirection) {
        for(AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext()) {
            if (!isLabelOrLineNumber(instruction)) {
                return instruction;
            }
        }

        return null;
    }

    public static boolean isLabelOrLineNumber(AbstractInsnNode insn) {
        return insn.getType() == 8 || insn.getType() == 15;
    }

    public static AbstractInsnNode getOrFindInstructionOfType(AbstractInsnNode firstInsnToCheck, int type) {
        return getOrFindInstructionOfType(firstInsnToCheck, type, false);
    }

    public static AbstractInsnNode getOrFindInstructionOfType(AbstractInsnNode firstInsnToCheck, int type, boolean reverseDirection) {
        for(AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext()) {
            if (instruction.getType() == type) {
                return instruction;
            }
        }

        return null;
    }


    public static void printInstructions(MethodNode method)
    {
        int instructCount = 0;
        for (AbstractInsnNode instruction: method.instructions.toArray())
        {
            printInstruction(instruction, instructCount);
            instructCount++;

        }
    }

    public static void printInstruction(AbstractInsnNode instruction, int instructCount)
    {
        try
        {
            System.out.println(instructCount + " MethodInsnNode " + ((MethodInsnNode) instruction).getOpcode() +
                    " owner " + ((MethodInsnNode) instruction).owner +
                    " name " + ((MethodInsnNode) instruction).name +
                    " desc " + ((MethodInsnNode) instruction).desc
            );
        }
        catch(Exception e) { }
        try
        {
            System.out.println(instructCount + " VarInsnNode " + ((VarInsnNode) instruction).getType() + " " + ((VarInsnNode) instruction).var);
        }
        catch(Exception e) { }
        try
        {
            System.out.println(instructCount + " InsnNode " + lookupInsnOpcode(((InsnNode) instruction).getOpcode()));
        }
        catch(Exception e) { }
        try
        {
            if( ((LdcInsnNode) instruction).cst instanceof String)
                System.out.println(instructCount + " LdcInsnNode " + ((LdcInsnNode) instruction).getOpcode() + " LDC " +  ((LdcInsnNode) instruction).getType() + " string value " +((String) ((LdcInsnNode) instruction).cst) );
            else
            System.out.println(instructCount + " LdcInsnNode " + ((LdcInsnNode) instruction).getOpcode() + " LDC " +  ((LdcInsnNode) instruction).getType() + " value " + ((LdcInsnNode) instruction).cst );
        }
        catch(Exception e) { }
        try
        {
            System.out.println(instructCount + " FieldInsnNode " + lookupFieldInsnOpcode(((FieldInsnNode) instruction).getOpcode()) +
                    " owner " + ((FieldInsnNode) instruction).owner +
                    " name " + ((FieldInsnNode) instruction).name +
                    " desc " + ((FieldInsnNode) instruction).desc
            );
        }
        catch(Exception e) { }
    }

}
