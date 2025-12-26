package com.deadtiger.advcreation.plugin.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.deadtiger.advcreation.plugin.transformer.MyASMHelper.printInstructions;
import static org.objectweb.asm.Opcodes.*;

//import jdk.internal.org.objectweb.asm.Type;
//change the getBlockReachDistance method in net.minecraft.client.multiplayer.PlayerControllerMP
//java line 367 corresponding to ASMIFIED line  1414: change 5.0 to a getter call
//java line 367 corresponding to ASMIFIED line  1419: change 4.5 to a getter call
public class GeneralTransformer implements IClassTransformer
{

    private static Map<String, ArrayList<Method>> classToTransformMethodMap = new HashMap<>();

    private static Map<Integer, String> insnOpcodes= new HashMap<>();
    private static Map<Integer, String> fieldInsnOpcodes= new HashMap<>();

    public static boolean checkedLLibraryUsage = false;
    public static boolean usingLLibrary = false;

    public static ArrayList<String> transformersAlreadyExecuted = new ArrayList<>();

    static
    {

        System.out.println("Try new ASMHelper style");
        try
        {

            //Replace with lambda expressions when we move to a requirement of java 8
            //DONE
//            addtransformerToClass("removeNoClipAssignment", "net.minecraft.entity.player.EntityPlayer");//good

//            addtransformerToClass("openCustomInventory", "net.minecraft.client.Minecraft"); //good

            //DONE
//            addtransformerToClass("changeTerrainRendering", "net.minecraft.client.renderer.EntityRenderer"); //good
            //DONE
//            addtransformerToClass("enableCutThroughLandscape", "net.minecraft.client.renderer.chunk.RenderChunk");

//            addtransformerToClass("disableScrollInventory", "net.minecraft.client.Minecraft");//good
//            addtransformerToClass("extendPlayerReach", "net.minecraft.client.multiplayer.PlayerControllerMP"); //no longer necessary as there is a property in EntityPlayer that controls this which can be changed with an event

            //DONE
//            addtransformerToClass("allowRelativeMovement", "net.minecraft.entity.EntityLivingBase"); //EntityLivingBase Overrides this method now//good

//            addtransformerToClass("testOnUpdate", "net.minecraft.entity.player.EntityPlayer"); //EntityLivingBase Overrides this method now//good

            //DONE
//            addtransformerToClass("allowEntityRelativeMovement", "net.minecraft.entity.Entity");//good
            //DONE
//            addtransformerToClass("allowThirdPersonView", "net.minecraft.client.renderer.EntityRenderer");//good
            //DONE
//            addtransformerToClass("isometricCameraRotation", "net.minecraft.client.renderer.EntityRenderer"); //good
            //DONE
//            addtransformerToClass("isometricRayTrace", "net.minecraft.client.renderer.EntityRenderer");//good
            //DONE
//            addtransformerToClass("turnPlayerToCursor", "net.minecraft.client.renderer.EntityRenderer");//good
            //DONE
//            addtransformerToClass("disableMouseGrabbing", "net.minecraft.util.MouseHelper");//good
            addtransformerToClass("fixLlibraryGetViewDistanceHook", "net.ilexiconn.llibrary.server.core.patcher.LLibraryHooks");//good
            addtransformerToClass("removeLlibraryOrientCameraCoreMod", "net.ilexiconn.llibrary.server.core.patcher.LLibraryRuntimePatcher");//good
            addtransformerToClass("addStatueNameToTileEntity", "com.bewitchment.common.block.BlockStatue");//good
            addtransformerToClass("addSiphoningFlowerOwnerIdToTileEntity", "com.bewitchment.common.block.plants.BlockSiphoningFlower");//good

        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }


    public static void addtransformerToClass(String methodName, String className) throws NoSuchMethodException
    {
        if (classToTransformMethodMap.containsKey(className))
            classToTransformMethodMap.get(className).add(GeneralTransformer.class.getMethod(methodName, ClassNode.class, boolean.class));
        else
        {
            ArrayList<Method> methodList = new ArrayList<Method>();
            methodList.add(GeneralTransformer.class.getMethod(methodName, ClassNode.class, boolean.class));
            classToTransformMethodMap.put(className, methodList);
        }

    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] clazz)
    {
        if (classToTransformMethodMap.containsKey(transformedName))
        {
            ArrayList<Method> transformMethodList = classToTransformMethodMap.get(transformedName);
            try
            {
                ClassNode classNode = MyASMHelper.readClassFromBytes(clazz);
                for (Method transformMethod : transformMethodList)
                {
                    if (transformMethod == null)
                    {
                        continue;
                    }
                    if(transformersAlreadyExecuted.contains(transformedName +  transformMethod.getName()))
                        System.out.println("WARNING: DID NOT Transform: " + transformedName + " with method: " + transformMethod.getName() + "as it already happened");
                    else
                    {
                        System.out.println("Transforming: " + transformedName + " with method: " + transformMethod.getName());
                        transformMethod.invoke(null, classNode, MyASMHelper.isObfuscated(name, transformedName));
                        System.out.println("Successfully Transformed: " + transformedName + " with method: " + transformMethod.getName());
                        transformersAlreadyExecuted.add(transformedName +  transformMethod.getName());
                    }

                }
                return MyASMHelper.writeClassToBytes(classNode, ClassWriter.COMPUTE_MAXS | (transformedName.equals("net.minecraft.entity.EntityLivingBase") ? 0 : ClassWriter.COMPUTE_FRAMES));
            }
            catch (Exception e)
            {
                System.out.println("Something went wrong trying to transform " + transformedName);
                e.printStackTrace();
            }
        }


        return clazz;
    }
    //NOT USED ANYMORE
    //Moved to EntityPlayerMixin class injecting setNoClip() into the OnUpdate() method
    public static void removeNoClipAssignment(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "B_" : "onUpdate";//good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "()V" : "()V";//good

        String field_name = (isObfuscated ? "Q" : "noClip");//good

        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                AbstractInsnNode targetNode = null;

                //new style of ASM look up using ASM Helper
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, PUTFIELD);
                if (((FieldInsnNode) injectPoint).name.equals(field_name))
                    targetNode = injectPoint;

                if (targetNode != null)
                    MyASMHelper.removePreviousAmountOfInstructions(targetNode, 4, method);

            }
        }
    }

    //NOT USED ANYMORE
    //Moved to ClientEventHandler class using event GuiOpenEvent to replace normal inventory with custom in PLACE mode
    public static void openCustomInventory(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "aE" : "processKeyBinds";//good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "()V" : "()V";//good

        String method_name = (isObfuscated ? "a" : "openInventory"); //good
        String class_name = (isObfuscated ? "chz" : "net/minecraft/client/tutorial/Tutorial"); //good

        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                AbstractInsnNode targetNode = null;
                //new style of ASM look up using ASM Helper
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKEVIRTUAL);
                while (!(((MethodInsnNode) injectPoint).name.equals(method_name) &&
                        (((MethodInsnNode) injectPoint).owner).equals(class_name)))
                {
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                    if (injectPoint == null)
                        break;
                }

                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("method openCustomInventory couldn't find its targetnode");

                if (targetNode != null)
                {
                    //delete the method call but also all the variables being loaded beforehand
                    AbstractInsnNode popNode = targetNode;
                    popNode = MyASMHelper.removeNextAmountOfInstructions(popNode, 1, method);
                    popNode = popNode.getNext();
                    popNode = popNode.getNext();
                    popNode = MyASMHelper.removeNextAmountOfInstructions(popNode, 3, method);
                    popNode = popNode.getNext();
                    popNode = MyASMHelper.removeNextAmountOfInstructions(popNode, 3, method);

                    //create call to a custom method
                    InsnList toInsert = new InsnList();
                    String desc_types = (isObfuscated ? "(Lchz;Lbib;)V" : "(Lnet/minecraft/client/tutorial/Tutorial;Lnet/minecraft/client/Minecraft;)V");//good
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModMinecraft", "openCustomInventory", desc_types, false));
                    method.instructions.insertBefore(popNode, toInsert);

                }
            }
        }
    }
    //NOT USED ANYMORE
    //Moved to EntityRendererMixin class redirecting isSpectator to setSpectatorToTrue(...) in the renderWorldPass() method
    public static void changeTerrainRendering(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "a" : "renderWorldPass";  //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(IFJ)V" : "(IFJ)V"; //good
        String method_name = (isObfuscated ? "y" : "isSpectator"); //good

        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                printInstructions(method);
                AbstractInsnNode targetNode = null;
                //new style of ASM look up using ASM Helper
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKEVIRTUAL);
                while (!(((MethodInsnNode) injectPoint).name.equals(method_name)))
                {
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                {
                    targetNode = injectPoint;
                    System.out.println("MethodInsnNode FOUND" + ((MethodInsnNode) targetNode).name);
                }
                else
                    System.out.println("method changeTerrainRendering couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removePreviousAmountOfInstructions(targetNode, 4, method);

                    InsnList toInsert = new InsnList();
                    toInsert.add(new LdcInsnNode(true));
                    method.instructions.insert(popNode, toInsert);
                }
            }
        }
    }

    /**
     * trying to insert a call to my custom method
     * changeThirdPersonToIsometric(float partialTicks,
     * double d0, double d1, double d2,
     * Minecraft mc, float thirdPersonDistancePrev,
     * Entity entity )
     * <p>
     * The ASMIFIED code that is to be injected:
     * mv.visitVarInsn(Opcodes.FLOAD, 1); //float partialTicks (good)
     * mv.visitVarInsn(Opcodes.DLOAD, 4); //double d0 (good)
     * mv.visitVarInsn(Opcodes.DLOAD, 6); //double d1 (good)
     * mv.visitVarInsn(Opcodes.DLOAD, 8); //double d2 (good)
     * mv.visitVarInsn(Opcodes.ALOAD, 0); //this (good)
     * mv.visitFieldInsn(Opcodes.GETFIELD, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "mc", "Lnet/minecraft/client/Minecraft;"); // this.mc
     * mv.visitVarInsn(Opcodes.ALOAD, 0); //this (good)
     * mv.visitFieldInsn(Opcodes.GETFIELD, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "thirdPersonDistancePrev", "F"); //this.thirdPersonDistancePrev
     * mv.visitVarInsn(Opcodes.ALOAD, 2); // entity (good)
     * mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "changeThirdPersonToIsometric", "(FDDDLnet/minecraft/client/Minecraft;FLnet/minecraft/entity/Entity;)V", false);
     *
     * @param classNode
     * @param isObfuscated
     */
    //NOT USED ANYMORE
    //Moved to RenderChunkMixin class redirecting renderBlock to renderCutthroughBlocks(...) in the rebuildChunk() method ");
    public static void enableCutThroughLandscape(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "b" : "rebuildChunk"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(FFFLbxl;)V" : "(FFFLnet/minecraft/client/renderer/chunk/ChunkCompileTaskGenerator;)V"; //good

        String method_name = (isObfuscated ? "a" : "renderBlock"); //good
        String class_name = (isObfuscated ? "bvm" : "net/minecraft/client/renderer/BlockRendererDispatcher"); //good
        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                printInstructions(method);
                AbstractInsnNode targetNode = null;
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKEVIRTUAL);
                while (!(((MethodInsnNode) injectPoint).name.equals(method_name) &&
                        (((MethodInsnNode) injectPoint).owner).equals(class_name)))
                {
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                    if (injectPoint == null)
                        break;
                }

                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("enableCutThroughLandscape couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                    InsnList toInsert = new InsnList();
                    String className = (isObfuscated ? "(Lbvm;Lawt;Let;Lamy;Lbuk;)Z" : "(Lnet/minecraft/client/renderer/BlockRendererDispatcher;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z"); //good
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModBlockRendererDispatcher", "customRenderBlock", className, false));
                    method.instructions.insertBefore(popNode, toInsert);

                }
            }
        }
    }
    //NOT USED ANYMORE
    public static void disableScrollInventory(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "aG" : "runTickMouse"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "()V" : "()V";    //good

        String method_name = (isObfuscated ? "f" : "changeCurrentItem"); //good
        String class_name = (isObfuscated ? "aec" : "net/minecraft/entity/player/InventoryPlayer"); //good

        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                AbstractInsnNode targetNode = null;
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKEVIRTUAL);
                while (!(((MethodInsnNode) injectPoint).name.equals(method_name) &&
                        (((MethodInsnNode) injectPoint).owner).equals(class_name)))
                {
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("disableScrollInventory couldn't find its targetnode");

                if (targetNode != null)
                {
                    //delete the method call but also all the variables being loaded beforehand
                    AbstractInsnNode popNode = MyASMHelper.removePreviousAmountOfInstructions(targetNode, 5, method);

                    //create call to a custom method
                    InsnList toInsert = new InsnList();
                    toInsert.add(new VarInsnNode(ALOAD, 0));
                    toInsert.add(new VarInsnNode(ILOAD, 4));
                    String desc_types = (isObfuscated ? "(Lbib;I)V" : "(Lnet/minecraft/client/Minecraft;I)V"); //good
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModMinecraft", "handleChangeCurrentSelectionToolbar", desc_types, false));
                    method.instructions.insert(popNode, toInsert);

                }
            }
        }
    }

    /**
     * First thing that is changed is :
     * CODE:
     * replace:
     * return this.currentGameType.isCreative() ? 5.0F : 4.5F;
     * with:
     * return this.currentGameType.isCreative() ? 16.0F : 16.0F;
     * ASM:
     * replace:
     * mv.visitLdcInsn(new Float("5.0"));
     * with:
     * mv.visitMethodInsn(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModPlayerControllerMP", "getCustomReachDistance", "()F" , false);
     * <p>
     * <p>
     * The second thing that is changed is:
     * CODE:
     * replace:
     * return this.currentGameType.isCreative() ? 5.0F : 4.5F;
     * with:
     * return this.currentGameType.isCreative() ? 16.0F : 16.0F;
     * ASM:
     * replace:
     * mv.visitLdcInsn(new Float("5.0"));
     * with:
     * mv.visitMethodInsn(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModPlayerControllerMP", "getCustomReachDistance", "()F" , false);
     *
     * @param classNode
     * @param isObfuscated
     */
    //NOT USED ANYMORE
    public static void extendPlayerReach(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "d" : "getBlockReachDistance";//good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "()F" : "()F";//good


        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                AbstractInsnNode targetNode = null;

                //find the first LDC_INSN instruction
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstruction(method);
                injectPoint = MyASMHelper.getOrFindInstructionOfType(injectPoint, LdcInsnNode.LDC_INSN);

                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("extendPlayerReach couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                    InsnList toInsert = new InsnList();
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModPlayerControllerMP", "getCustomReachDistance", "()F", false));
                    method.instructions.insertBefore(popNode, toInsert);

                    //find the second LDC_INSN instruction
                    targetNode = null;
                    injectPoint = MyASMHelper.getOrFindInstructionOfType(popNode, LdcInsnNode.LDC_INSN);

                    if (injectPoint != null)
                        targetNode = injectPoint;
                    else
                        System.out.println("extendPlayerReach couldn't find its second targetnode");

                    if (targetNode != null)
                    {
                        popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                        toInsert = new InsnList();
                        toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModPlayerControllerMP", "getCustomReachDistance", "()F", false));
                        method.instructions.insertBefore(popNode, toInsert);
                    }
                }
            }
        }
    }


    /**
     * trying to insert a call to my custom method
     * call to
     * customMoveRelative(float strafe, float up, float forward, float friction, Entity entity)
     * <p>
     * The ASMIFIED code that is to be injected:
     * mv.visitVarInsn(Opcodes.FLOAD, 1);
     * mv.visitVarInsn(Opcodes.FLOAD, 2);
     * mv.visitVarInsn(Opcodes.FLOAD, 3);
     * mv.visitVarInsn(Opcodes.FLOAD, 4);
     * mv.visitVarInsn(Opcodes.ALOAD, 0);
     * mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "customMoveRelative", "(FFFFLnet/minecraft/entity/Entity;)V", false);
     *
     * @param classNode
     * @param isObfuscated
     */
    //NOT USED ANYMORE
    //Moved to EntityLivingBaseMixin class injecting tryToMoveRelativeToCamera() into the moveRelative() method ");
    public static void allowRelativeMovement(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "b" : "moveRelative"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(FFFF)V" : "(FFFF)V"; //good
        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
//            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                AbstractInsnNode targetNode = null;

                //find the first VAR_INSN instruction
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstruction(method);
                injectPoint = MyASMHelper.getOrFindInstructionOfType(injectPoint, VarInsnNode.VAR_INSN);

                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("allowRelativeMovement couldn't find its second targetnode");

                if (targetNode != null)
                {
//                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 113, method); // old from mc 1.12
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 151, method);
//
                    InsnList toInsert = new InsnList();
//                    call super.moverelative
                    toInsert.add(new VarInsnNode(ALOAD, 0));     //this (good)
                    toInsert.add(new VarInsnNode(FLOAD, 1));     //float strafe(good)
                    toInsert.add(new VarInsnNode(FLOAD, 2));    //float up (good)
                    toInsert.add(new VarInsnNode(FLOAD, 3));    //float forward (good)
                    toInsert.add(new VarInsnNode(FLOAD, 4));    //float friction (good)
                    String className = (isObfuscated ? "(FFFF)V" : "(FFFF)V"); //good
                    String ownerName = (isObfuscated ? "vg" : "net/minecraft/entity/Entity"); //good
                    String methodName = (isObfuscated ? "b" : "moveRelative"); //good
                    toInsert.add(new MethodInsnNode(INVOKESPECIAL, ownerName, methodName, className, false));


//                    toInsert.add(new VarInsnNode(FLOAD, 1));     //float strafe(good)
//                    toInsert.add(new VarInsnNode(FLOAD, 2));    //float up (good)
//                    toInsert.add(new VarInsnNode(FLOAD, 3));    //float forward (good)
//                    toInsert.add(new VarInsnNode(FLOAD, 4));    //float friction (good)
//                    toInsert.add(new VarInsnNode(ALOAD, 0));     //this (good)
//                    String className = (isObfuscated ? "(FFFFLvn;)V" : "(FFFFLnet/minecraft/entity/EntityLivingBase;)V"); //good
//                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "customMoveRelative", className, false));
                    /*
                    methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
                    methodVisitor.visitInsn(DUP);
                    methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                    methodVisitor.visitLdcInsn("Completely ignored arguments: ");
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                    methodVisitor.visitVarInsn(ALOAD, 24);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                     */
//                    toInsert.add(new FieldInsnNode(GETSTATIC,"java/lang/System", "out","Ljava/io/PrintStream;"));
//                    toInsert.add(new LdcInsnNode("Successfully called a system.out.println in moveRelative :P"));
//                    toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));

                    method.instructions.insertBefore(popNode, toInsert);
//                    System.out.println("Trying to print the new instructions --------------------");
//                    printInstructions(method);
                }
            }
        }
    }



//    public static void testOnUpdate(ClassNode classNode, boolean isObfuscated)
//    {
//        final String ENTITY_COLLIDE = isObfuscated ? "B_" : "onUpdate"; //good
//        final String ENTITY_COLLIDE_DESC = isObfuscated ? "()V" : "()V"; //good
//        int methodCount = 0;
//        for (MethodNode method : classNode.methods)
//        {
//            //testmethod to see the output of the obscured jar
//            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
//            methodCount++;
//            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
//            {
//                printInstructions(method);
//            }
//        }
//    }

    /**
     * trying to insert a call to my custom method
     * call to
     * customMoveRelative(float strafe, float up, float forward, float friction, Entity entity)
     * <p>
     * The ASMIFIED code that is to be injected:
     * mv.visitVarInsn(Opcodes.FLOAD, 1);
     * mv.visitVarInsn(Opcodes.FLOAD, 2);
     * mv.visitVarInsn(Opcodes.FLOAD, 3);
     * mv.visitVarInsn(Opcodes.FLOAD, 4);
     * mv.visitVarInsn(Opcodes.ALOAD, 0);
     * mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "customMoveRelative", "(FFFFLnet/minecraft/entity/Entity;)V", false);
     *
     * @param classNode
     * @param isObfuscated
     */
    //NOT USED ANYMORE
    //Moved to EntityLivingBaseMixin class injecting tryToMoveRelativeToCamera() into the moveRelative() method ");
    public static void allowEntityRelativeMovement(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "b" : "moveRelative";
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(FFFF)V" : "(FFFF)V";
        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                AbstractInsnNode targetNode = null;

                //find the first VAR_INSN instruction
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstruction(method);
                injectPoint = MyASMHelper.getOrFindInstructionOfType(injectPoint, VarInsnNode.VAR_INSN);

                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("allowRelativeMovement couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 113, method);

                    InsnList toInsert = new InsnList();
                    toInsert.add(new VarInsnNode(FLOAD, 1));     //float strafe(good)
                    toInsert.add(new VarInsnNode(FLOAD, 2));    //float up (good)
                    toInsert.add(new VarInsnNode(FLOAD, 3));    //float forward (good)
                    toInsert.add(new VarInsnNode(FLOAD, 4));    //float friction (good)
                    toInsert.add(new VarInsnNode(ALOAD, 0));     //this (good)
                    String className = (isObfuscated ? "(FFFFLvg;)V" : "(FFFFLnet/minecraft/entity/Entity;)V");
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "customMoveRelative", className, false));
                    method.instructions.insertBefore(popNode, toInsert);
                }
            }
        }
    }

//    /**
//     * trying to insert a call to my custom method
//     * call to
//     * customMoveRelative(float strafe, float up, float forward, float friction, Entity entity)
//     * <p>
//     * The ASMIFIED code that is to be injected:
//     * mv.visitVarInsn(Opcodes.FLOAD, 1);
//     * mv.visitVarInsn(Opcodes.FLOAD, 2);
//     * mv.visitVarInsn(Opcodes.FLOAD, 3);
//     * mv.visitVarInsn(Opcodes.FLOAD, 4);
//     * mv.visitVarInsn(Opcodes.ALOAD, 0);
//     * mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "customMoveRelative", "(FFFFLnet/minecraft/entity/Entity;)V", false);
//     *
//     * @param classNode
//     * @param isObfuscated
//     */
//    public static void allowEntityRelativeMovement(ClassNode classNode, boolean isObfuscated)
//    {
//        final String ENTITY_COLLIDE = isObfuscated ? "b" : "moveRelative";
//        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(FFFF)V" : "(FFFF)V";
//        for (MethodNode method : classNode.methods)
//        {
////            System.out.println("method " + method.name + "" + method.desc);
//            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
//            {
//                AbstractInsnNode targetNode = null;
//
//                //find the first VAR_INSN instruction
//                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstruction(method);
//                injectPoint = MyASMHelper.getOrFindInstructionOfType(injectPoint, VarInsnNode.VAR_INSN);
//
//                if (injectPoint != null)
//                    targetNode = injectPoint;
//                else
//                    System.out.println("allowEntityRelativeMovement couldn't find itstargetnode");
//
//                if (targetNode != null)
//                {
//                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 113, method);
//                    String motionXField = isObfuscated ? "s" : "motionX"; //good
//                    String motionYField = isObfuscated ? "t" : "motionY"; //good
//                    String motionZField = isObfuscated ? "u" : "motionZ"; //good
//
//                    String EntityClass = isObfuscated ? "vg" : "net/minecraft/entity/Entity"; //good
//
//                    InsnList toInsert = new InsnList();
//                    //change motionX field
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    toInsert.add(new InsnNode(DUP));
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, motionXField, "D"));
//                    toInsert.add(new VarInsnNode(FLOAD, 1));     //float strafe(good)
//                    toInsert.add(new VarInsnNode(FLOAD, 2));    //float up (good)
//                    toInsert.add(new VarInsnNode(FLOAD, 3));    //float forward (good)
//                    toInsert.add(new VarInsnNode(FLOAD, 4));    //float friction (good)
////                    this.rotationYaw
//                    toInsert.add(new VarInsnNode(ALOAD, 0));     //this
//                    String rotationYawField = isObfuscated ? "v" : "rotationYaw"; //good
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, rotationYawField, "F")); //good
//                    //this.posY
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    String posYField = isObfuscated ? "q" : "posY"; //good
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, posYField, "D")); //good
//                    //this instanceof EntityPlayer
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    String EntityPlayerClass = isObfuscated ? "aed" : "net/minecraft/entity/player/EntityPlayer";
//                    toInsert.add( new TypeInsnNode(INSTANCEOF, EntityPlayerClass));
//                    String customCallDescName = (isObfuscated ? "(FFFFFDZ)D" : "(FFFFFDZ)D");
//
////                    String customCallDescName = (isObfuscated ? "()V" : "()V");
//
//                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "calcMotionX", customCallDescName, false));
//                    toInsert.add(new InsnNode(DADD));
//                    toInsert.add(new FieldInsnNode(PUTFIELD, EntityClass, motionXField, "D"));
//
//                    //change motionY field
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    toInsert.add(new InsnNode(DUP));
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, motionYField, "D"));
//                    toInsert.add(new VarInsnNode(FLOAD, 1));     //float strafe(good)
//                    toInsert.add(new VarInsnNode(FLOAD, 2));    //float up (good)
//                    toInsert.add(new VarInsnNode(FLOAD, 3));    //float forward (good)
//                    toInsert.add(new VarInsnNode(FLOAD, 4));    //float friction (good)
////                    this.rotationYaw
//                    toInsert.add(new VarInsnNode(ALOAD, 0));     //this
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, rotationYawField, "F")); //good
//                    //this.posY
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, posYField, "D")); //good
//                    //this instanceof EntityPlayer
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    toInsert.add( new TypeInsnNode(INSTANCEOF, EntityPlayerClass));
//
//                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "calcMotionY", customCallDescName, false));
//                    toInsert.add(new InsnNode(DADD));
//                    toInsert.add(new FieldInsnNode(PUTFIELD, EntityClass, motionYField, "D"));
//
//                    //change motionZ field
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    toInsert.add(new InsnNode(DUP));
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, motionZField, "D"));
//                    toInsert.add(new VarInsnNode(FLOAD, 1));     //float strafe(good)
//                    toInsert.add(new VarInsnNode(FLOAD, 2));    //float up (good)
//                    toInsert.add(new VarInsnNode(FLOAD, 3));    //float forward (good)
//                    toInsert.add(new VarInsnNode(FLOAD, 4));    //float friction (good)
////                    this.rotationYaw
//                    toInsert.add(new VarInsnNode(ALOAD, 0));     //this
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, rotationYawField, "F")); //good
//                    //this.posY
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, posYField, "D")); //good
//                    //this instanceof EntityPlayer
//                    toInsert.add(new VarInsnNode(ALOAD, 0));
//                    toInsert.add( new TypeInsnNode(INSTANCEOF, EntityPlayerClass));
//
//                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "calcMotionZ", customCallDescName, false));
//                    toInsert.add(new InsnNode(DADD));
//                    toInsert.add(new FieldInsnNode(PUTFIELD, EntityClass, motionZField, "D"));
//
//                    method.instructions.insertBefore(popNode, toInsert);
//                    System.out.println("Trying to print the new instructions --------------------");
//                    printInstructions(method);
//                }
//            }
//        }
//    }



    /**
     * trying to insert a call to my custom method but its hard, boyz
     * call to
     * changeThirdPersonToIsometric(float partialTicks,
     * double d0, double d1, double d2,
     * Minecraft mc, float thirdPersonDistancePrev,
     * Entity entity )
     * <p>
     * The ASMIFIED code that is to be injected:
     * mv.visitVarInsn(Opcodes.FLOAD, 1); //float partialTicks (good)
     * mv.visitVarInsn(Opcodes.DLOAD, 4); //double d0 (good)
     * mv.visitVarInsn(Opcodes.DLOAD, 6); //double d1 (good)
     * mv.visitVarInsn(Opcodes.DLOAD, 8); //double d2 (good)
     * mv.visitVarInsn(Opcodes.ALOAD, 0); //this (good)
     * mv.visitFieldInsn(Opcodes.GETFIELD, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "mc", "Lnet/minecraft/client/Minecraft;"); // this.mc
     * mv.visitVarInsn(Opcodes.ALOAD, 0); //this (good)
     * mv.visitFieldInsn(Opcodes.GETFIELD, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "thirdPersonDistancePrev", "F"); //this.thirdPersonDistancePrev
     * mv.visitVarInsn(Opcodes.ALOAD, 2); // entity (good)
     * mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "changeThirdPersonToIsometric", "(FDDDLnet/minecraft/client/Minecraft;FLnet/minecraft/entity/Entity;)V", false);
     *
     * @param classNode
     * @param isObfuscated
     */
    //NOT USED ANYMORE
    //Moved to EntityRendererMixin (2) class injecting allowIsometricView() into the orientCamera() method ");
    public static void allowThirdPersonView(ClassNode classNode, boolean isObfuscated)
    {
        if(!GeneralTransformer.checkedLLibraryUsage)
        {
            System.out.println("Checking your mods for incompatibilities with Advanced Creation");
            for (ModContainer mod : Loader.instance().getModList())
            {
                System.out.println("modid: " + mod.getModId());
                if(mod.getModId().contains("llibrary"))
                    GeneralTransformer.usingLLibrary =true;
            }
            GeneralTransformer.checkedLLibraryUsage = true;

            if(GeneralTransformer.usingLLibrary)
                System.out.println("you are using Llibrary, Advanced Creation will make the necessary adjustments");
            else
                System.out.println("you are NOT using Llibrary, Advanced Creation DOES NOT need to make any adjustments");
        }



        final String ENTITY_COLLIDE = isObfuscated ? "f" : "orientCamera"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(F)V" : "(F)V";  //good

        String EntityClass = isObfuscated ? "vg" : "net/minecraft/entity/Entity"; //good

        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
//            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
//                printInstructions(method);
                AbstractInsnNode targetNode = null;

                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, ALOAD);
                while (!((injectPoint).getNext().getOpcode() == GETFIELD &&
                        ((injectPoint).getNext().getNext() instanceof LdcInsnNode)))
                {
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, ALOAD);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("allowThirdPersonView couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 301, method);

                    InsnList toInsert = new InsnList();
                    toInsert.add(new VarInsnNode(FLOAD, 1));     //float partialTicks (good)
                    toInsert.add(new VarInsnNode(DLOAD, 4));    //double d0 (good)
                    toInsert.add(new VarInsnNode(DLOAD, 6));    //double d1 (good)
                    toInsert.add(new VarInsnNode(DLOAD, 8));    //double d2 (good)
//                    toInsert.add(new VarInsnNode(ALOAD, 0));     //this (good)

//                    String className_mc = (isObfuscated ? "Lbib;" : "Lnet/minecraft/client/Minecraft;");
//                    String fieldName_mc = (isObfuscated ? "field_78531_r" : "mc");
//                    String fieldName_mc = (isObfuscated ? "h" : "mc");

//                    toInsert.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", fieldName_mc, className_mc)); // this.mc
                    toInsert.add(new VarInsnNode(ALOAD, 0));     //this (good)

                    String fieldName_dist = (isObfuscated ? "field_78491_C" : "thirdPersonDistancePrev");
                    toInsert.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/renderer/EntityRenderer", fieldName_dist, "F")); //this.thirdPersonDistancePrev

                    //entity.rotationYaw
                    toInsert.add(new VarInsnNode(ALOAD, 2));     // entity (good)
                    String rotationYawField = isObfuscated ? "v" : "rotationYaw"; //good
                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, rotationYawField, "F")); //good

                    //entity.rotationPitch
                    toInsert.add(new VarInsnNode(ALOAD, 2));     // entity (good)
                    String rotationPitchField = isObfuscated ? "w" : "rotationPitch";
                    toInsert.add(new FieldInsnNode(GETFIELD, EntityClass, rotationPitchField, "F")); //good

//                    if(GeneralTransformer.usingLLibrary)
//                    {
//                        String className = (isObfuscated ? "(FDDDFFF)F" : "(FDDDFFF)F");
//                        toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "changeThirdPersonToIsometric", className, false));
//                        toInsert.add(new FieldInsnNode(PUTFIELD,"net.ilexiconn.llibrary.server.core.patcher.LLibraryHooks","prevRenderViewDistance","F"));
//                    }
//                    else
//                    {
                        String className = (isObfuscated ? "(FDDDFFF)V" : "(FDDDFFF)V");
                        toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "changeThirdPersonToIsometricNoReturn", className, false));

//                    }



                    method.instructions.insertBefore(popNode, toInsert);
                }
            }
        }
    }


    /**
     * insert the following code
     * mv.visitVarInsn(Opcodes.FLOAD, 1); //Partialticks
     * mv.visitVarInsn(Opcodes.ALOAD, 8); //entity
     * mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "rotateCamera", "(FLnet/minecraft/entity/Entity;)V", false);
     *
     * @param classNode
     * @param isObfuscated
     */
    //NOT USED ANYMORE
    //Moved to EntityRendererMixin (3) class injecting setThirdPersonDistance(...) in the updateRenderer() method
    public static void isometricCameraRotation(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "e" : "updateRenderer"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "()V" : "()V";     //good
        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
//                printInstructions(method);
                AbstractInsnNode targetNode = null;
                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if (instruction instanceof VarInsnNode)
                    {
                        if (instruction.getOpcode() == ALOAD)
                        {
                            if (instruction.getNext() instanceof LdcInsnNode)
                            {
                                if (instruction.getNext().getNext() instanceof FieldInsnNode)
                                {
                                    targetNode = instruction.getNext();
                                    break;
                                }

                            }
                        }
                    }
                }

                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, ALOAD);
                while (!(injectPoint.getNext() instanceof LdcInsnNode &&
                        (injectPoint.getNext().getNext() instanceof FieldInsnNode)))
                {
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, ALOAD);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                    targetNode = injectPoint.getNext();
                else
                    System.out.println("IsometricCameraRotation couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                    InsnList toInsert = new InsnList();
//                    toInsert.add(new FieldInsnNode(GETSTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "customCameraDistance", "F"));
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "getUpdateRendererCameraDistance","()F" , false));

                    method.instructions.insertBefore(popNode, toInsert);
                }
            }
        }
    }

    //NOT USED ANYMORE
    //Moved to EntityRendererMixin (4) class redirecting raytrace(...) to customRaytrace(...) in the getMouseOver() method ");
    //And   to EntityRendererMixin (5) class redirecting getPositionEyes(...) to getCustomPositionEyes(...) in the getMouseOver() method ");
    //and   to EntityRendererMixin (6) class redirecting addVector(...) to customVectorCalculation(...) in the getMouseOver() method ");
    public static void isometricRayTrace(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "a" : "getMouseOver"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(F)V" : "(F)V"; //good
        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
//            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
            methodCount++;

            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
//                printInstructions(method);

                //first replace retrace with my own method for raytracing
                AbstractInsnNode targetNode = null;

                String method_name = (isObfuscated ? "a" : "rayTrace"); //good
                String class_name = (isObfuscated ? "vg" : "net/minecraft/entity/Entity"); //good
                int count = 0;
                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKEVIRTUAL);
                while (!(((MethodInsnNode) injectPoint).name.equals(method_name) &&
                        (((MethodInsnNode) injectPoint).owner).equals(class_name)))
                {
                    if (injectPoint == null)
                        break;
//                    System.out.println (count + " look at function " + ((MethodInsnNode) injectPoint).name + " belonging to class " + ((MethodInsnNode) injectPoint).owner);
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                    count++;

                }
                count++;

                if (injectPoint != null)
                {
                    targetNode = injectPoint;
                    System.out.println (count + " look at function " + ((MethodInsnNode) injectPoint).name + " belonging to class " + ((MethodInsnNode) injectPoint).owner);
                }
                else
                    System.out.println("isometricRayTrace couldn't find its targetnode");


                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                    InsnList toInsert = new InsnList();
                    String desc_types = (isObfuscated ? "(Lvg;DF)Lbhc;" : "(Lnet/minecraft/entity/Entity;DF)Lnet/minecraft/util/math/RayTraceResult;"); //good
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntity", "rayTrace", desc_types, false));
                    method.instructions.insertBefore(popNode, toInsert);
                }

                //Second replace getPositionsEyes with my own method
                targetNode = null;

                method_name = (isObfuscated ? "f" : "getPositionEyes");
                 class_name = (isObfuscated ? "vg" : "net/minecraft/entity/Entity");
                count = 0;
                injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKEVIRTUAL);
                while (!(((MethodInsnNode) injectPoint).name.equals(method_name) &&
                        (((MethodInsnNode) injectPoint).owner).equals(class_name)))
                {
                    if (injectPoint == null)
                        break;
//                    System.out.println (count + " look at function " + ((MethodInsnNode) injectPoint).name + " belonging to class " + ((MethodInsnNode) injectPoint).owner);
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                    count++;

                }
                count++;



                if (injectPoint != null)
                {
                    targetNode = injectPoint;
                    System.out.println (count + " look at function " + ((MethodInsnNode) injectPoint).name + " belonging to class " + ((MethodInsnNode) injectPoint).owner);
                }
                else
                    System.out.println("isometricRayTrace couldn't find its second targetnode");


                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                    InsnList toInsert = new InsnList();
                    String desc_types = (isObfuscated ? "(Lvg;F)Lbhe;" : "(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/math/Vec3d;"); //good

                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "getCurrStartCursorVector", desc_types, false));
                    method.instructions.insertBefore(popNode, toInsert);
                }

                //Third replace add with my own method
                targetNode = null;

                method_name = (isObfuscated ? "b" : "addVector");
                class_name = (isObfuscated ? "bhe" : "net/minecraft/util/math/Vec3d");

                injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKEVIRTUAL);
                count = 0;
                while (!(((MethodInsnNode) injectPoint).name.equals(method_name) &&
                        (((MethodInsnNode) injectPoint).owner).equals(class_name)))
                {
                    if (injectPoint == null)
                        break;
//                    System.out.println ( count + " look at function " + ((MethodInsnNode) injectPoint).name +" (" + ((MethodInsnNode) injectPoint).desc + ") belonging to class " + ((MethodInsnNode) injectPoint).owner);
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                    count++;

                }
                count++;
                if (injectPoint != null)
                {
                    targetNode = injectPoint;
                    System.out.println (count + " look at function " + ((MethodInsnNode) injectPoint).name + " belonging to class " + ((MethodInsnNode) injectPoint).owner);
                }
                else
                    System.out.println("isometricRayTrace couldn't find its third targetnode");


                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                    InsnList toInsert = new InsnList();
                    String desc_types = (isObfuscated ? "(Lbhe;DDDLvg;)Lbhe;" : "(Lnet/minecraft/util/math/Vec3d;DDDLnet/minecraft/entity/Entity;)Lnet/minecraft/util/math/Vec3d;"); //good
                    toInsert.add(new VarInsnNode(ALOAD, 2));
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "getCurrCursorPointVector", desc_types, false));
                    method.instructions.insertBefore(popNode, toInsert);
                }

            }
        }
    }

    //NOT USED ANYMORE
    //Moved to EntityRendererMixin (7) class redirecting turn(...) to turnPlayerToCursor(...) in the updateCameraAndRender() method ");
    public static void turnPlayerToCursor(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "a" : "updateCameraAndRender"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(FJ)V" : "(FJ)V"; //good
        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
            methodCount++;
            //System.out.println(method.name + " " + method.desc +" Check for " + ENTITY_COLLIDE);
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                printInstructions(method);
                AbstractInsnNode targetNode = null;
                String method_name = (isObfuscated ? "c" : "turn"); //good
                String class_name = (isObfuscated ? "bud" : "net/minecraft/client/entity/EntityPlayerSP"); //good

                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKEVIRTUAL);
                while (!(((MethodInsnNode) injectPoint).name.equals(method_name) &&
                        (((MethodInsnNode) injectPoint).owner).equals(class_name)))
                {
                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("turnPlayerToCursor couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                    InsnList toInsert = new InsnList();
                    String className = (isObfuscated ? "(Lbud;FFF)V" : "(Lnet/minecraft/client/entity/EntityPlayerSP;FFF)V"); //good
                    toInsert.add(new VarInsnNode(FLOAD, 1));
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "changePlayerRotation", className, false));
                    method.instructions.insertBefore(popNode, toInsert);

                    // Replacing the second turn method
                    targetNode = null;
                    method_name = (isObfuscated ? "c" : "turn"); //good
                    class_name = (isObfuscated ? "bud" : "net/minecraft/client/entity/EntityPlayerSP"); //good

                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(popNode, INVOKEVIRTUAL);
                    while (!(((MethodInsnNode) injectPoint).name.equals(method_name) &&
                            (((MethodInsnNode) injectPoint).owner).equals(class_name)))
                    {
                        injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKEVIRTUAL);
                        if (injectPoint == null)
                            break;
                    }
                    if (injectPoint != null)
                        targetNode = injectPoint;
                    else
                        System.out.println("isometricRayTrace couldn't find its targetnode");


                    if (targetNode != null)
                    {
                        popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                        toInsert = new InsnList();
                        className = (isObfuscated ? "(Lbud;FFF)V" : "(Lnet/minecraft/client/entity/EntityPlayerSP;FFF)V"); //good
                        toInsert.add(new VarInsnNode(FLOAD, 1));
                        toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", "changePlayerRotation", className, false));
                        method.instructions.insertBefore(popNode, toInsert);
                    }
                }
            }
        }
    }

    /**
     * replace:
     * Mouse.setGrabbed(true);
     * with:
     * ifFirstPersonGrabMouse();
     * <p>
     * replace:
     * mv.visitInsn(Opcodes.ICONST_1);
     * mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/lwjgl/input/Mouse", "setGrabbed", "(XZ)V", false);
     * with:
     * mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModMouseHelper", "ifFirstPersonGrabMouse", "()V", false);
     *
     * @param classNode
     * @param isObfuscated
     */
    public static void disableMouseGrabbing(ClassNode classNode, boolean isObfuscated)
    {
        final String ENTITY_COLLIDE = isObfuscated ? "a" : "grabMouseCursor"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "()V" : "()V"; //good

        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                AbstractInsnNode targetNode = null;

                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, ICONST_1);

                if (injectPoint != null)
                    targetNode = injectPoint.getPrevious();
                else
                    System.out.println("isometricRayTrace couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 3, method);

                    InsnList toInsert = new InsnList();
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "com/deadtiger/advcreation/plugin/modded_classes/ModMouseHelper", "ifFirstPersonGrabMouse", "()V", false));
                    method.instructions.insertBefore(popNode, toInsert);
                }
            }
        }
    }

    public static void fixLlibraryGetViewDistanceHook(ClassNode classNode, boolean isObfuscated)
    {

        final String ENTITY_COLLIDE = "getViewDistance"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(F)V" : "(F)V";  //good

        String EntityClass = isObfuscated ? "vg" : "net/minecraft/entity/Entity"; //good

        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
//            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
//                printInstructions(method);
                AbstractInsnNode targetNode = null;

                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, LDC);
                while ((injectPoint).getOpcode() != LDC && (((double)((LdcInsnNode) injectPoint).cst) == 4.0))
                {

                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, ALOAD);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("fixLlibraryGetViewDistanceHook couldn't find its targetnode");

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, 1, method);

                    InsnList toInsert = new InsnList();

                    String fieldName_dist = "customCameraDistance";
                    toInsert.add(new FieldInsnNode(GETFIELD, "com/deadtiger/advcreation/plugin/modded_classes/ModEntityRenderer", fieldName_dist, "F")); //this.thirdPersonDistancePrev
                    method.instructions.insertBefore(popNode, toInsert);
                }
            }
        }
    }

    public static void removeLlibraryOrientCameraCoreMod(ClassNode classNode, boolean isObfuscated)
    {

        final String ENTITY_COLLIDE = "onInit"; //good
        final String ENTITY_COLLIDE_DESC = isObfuscated ? "()V" : "()V";  //good

        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
//            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc);
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
//                printInstructions(method);
                AbstractInsnNode targetNode = null;

                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, LDC);
                while (! ((injectPoint).getOpcode() == LDC && ((LdcInsnNode) injectPoint).cst instanceof String && (((String)((LdcInsnNode) injectPoint).cst).equals("orientCamera"))))
                {

                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, LDC);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                    targetNode = injectPoint.getPrevious().getPrevious().getPrevious();
                else
                    System.out.println("removeLlibraryOrientCameraCoreMod couldn't find its targetnode");
                int count = 0;
                while (!((injectPoint).getOpcode() == LDC && ((LdcInsnNode) injectPoint).cst instanceof String &&  ((String)((LdcInsnNode) injectPoint).cst).equals("applyRotations")))
                {
                    count++;
                    injectPoint = injectPoint.getNext();
                    if (injectPoint == null)
                        break;
                }
                count -= 2;

                if (targetNode != null)
                {
                    AbstractInsnNode popNode = MyASMHelper.removeNextAmountOfInstructions(targetNode, count, method);
                }
            }
        }
    }

    public static void addStatueNameToTileEntity(ClassNode classNode, boolean isObfuscated)
    {

//        final String ENTITY_COLLIDE =  isObfuscated ? "func_149915_a": "createNewTileEntity"; //good
        final String ENTITY_COLLIDE =  "func_149915_a"; //good
//        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(Lams;I)Lavh;" : "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;";  //good
        final String ENTITY_COLLIDE_DESC = "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;";  //good

        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc + " name " + method.name.equals(ENTITY_COLLIDE) + " desc " + method.desc.equals(ENTITY_COLLIDE_DESC));
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                printInstructions(method);
                AbstractInsnNode targetNode = null;

                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKESPECIAL);
                while (! ((injectPoint).getOpcode() == INVOKESPECIAL ))
                {

                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKESPECIAL);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("addStatueNameToTileEntity couldn't find its targetnode");

                if (targetNode != null)
                {

                    InsnList toInsert = new InsnList();
                    toInsert.add(new VarInsnNode(ASTORE, 3));

//                    methodVisitor.visitVarInsn(ALOAD, 6);
//                    methodVisitor.visitVarInsn(ALOAD, 0);
//                    methodVisitor.visitFieldInsn(GETFIELD, "com/bewitchment/common/block/BlockStatue", "statue", "Lcom/bewitchment/client/misc/Statues$Statue;");
//                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/bewitchment/client/misc/Statues$Statue", "getName", "()Ljava/lang/String;", false);
//                    methodVisitor.visitFieldInsn(PUTFIELD, "com/bewitchment/common/block/tile/entity/TileEntityStatue", "name", "Ljava/lang/String;");

                    toInsert.add(new VarInsnNode(ALOAD, 3));
                    toInsert.add(new VarInsnNode(ALOAD, 0));
                    toInsert.add(new FieldInsnNode(GETFIELD, "com/bewitchment/common/block/BlockStatue", "statue", "Lcom/bewitchment/client/misc/Statues$Statue;"));
                    toInsert.add(new MethodInsnNode(INVOKEVIRTUAL, "com/bewitchment/client/misc/Statues$Statue", "getName", "()Ljava/lang/String;", false));
                    toInsert.add(new FieldInsnNode(PUTFIELD, "com/bewitchment/common/block/tile/entity/TileEntityStatue", "name", "Ljava/lang/String;"));
                    toInsert.add(new VarInsnNode(ALOAD, 3));

                    method.instructions.insert(targetNode, toInsert);
                }
            }
        }
    }

    public static void addSiphoningFlowerOwnerIdToTileEntity(ClassNode classNode, boolean isObfuscated)
    {

//        final String ENTITY_COLLIDE =  isObfuscated ? "func_149915_a": "createNewTileEntity"; //good
        final String ENTITY_COLLIDE =  "func_149915_a"; //good
//        final String ENTITY_COLLIDE_DESC = isObfuscated ? "(Lams;I)Lavh;" : "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;";  //good
        final String ENTITY_COLLIDE_DESC = "(Lnet/minecraft/world/World;I)Lnet/minecraft/tileentity/TileEntity;";  //good
        System.out.println("isObfuscated " + isObfuscated + " looking for method " + ENTITY_COLLIDE + ENTITY_COLLIDE_DESC);

        int methodCount = 0;
        for (MethodNode method : classNode.methods)
        {
            //testmethod to see the output of the obscured jar
            System.out.println("method " + methodCount+" : " + method.name + "" + method.desc + " name " + method.name.equals(ENTITY_COLLIDE) + " desc " + method.desc.equals(ENTITY_COLLIDE_DESC));
            methodCount++;
            if (method.name.equals(ENTITY_COLLIDE) && method.desc.equals(ENTITY_COLLIDE_DESC))
            {
                printInstructions(method);
                AbstractInsnNode targetNode = null;

                AbstractInsnNode injectPoint = MyASMHelper.findFirstInstructionWithOpcode(method, INVOKESPECIAL);
                while (! ((injectPoint).getOpcode() == INVOKESPECIAL ))
                {

                    injectPoint = MyASMHelper.findNextInstructionWithOpcode(injectPoint, INVOKESPECIAL);
                    if (injectPoint == null)
                        break;
                }
                if (injectPoint != null)
                    targetNode = injectPoint;
                else
                    System.out.println("addSiphoningFlowerOwnerIdToTileEntity couldn't find its targetnode");

                if (targetNode != null)
                {

                    InsnList toInsert = new InsnList();
                    toInsert.add(new VarInsnNode(ASTORE, 3));

//                    methodVisitor.visitVarInsn(ALOAD, 6);
//                    methodVisitor.visitVarInsn(ALOAD, 0);
//                    methodVisitor.visitFieldInsn(GETFIELD, "com/bewitchment/common/block/BlockStatue", "statue", "Lcom/bewitchment/client/misc/Statues$Statue;");
//                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/bewitchment/client/misc/Statues$Statue", "getName", "()Ljava/lang/String;", false);
//                    methodVisitor.visitFieldInsn(PUTFIELD, "com/bewitchment/common/block/tile/entity/TileEntityStatue", "name", "Ljava/lang/String;");

                    toInsert.add(new VarInsnNode(ALOAD, 3));
                    toInsert.add(new LdcInsnNode(""));
                    toInsert.add(new FieldInsnNode(PUTFIELD, "com/bewitchment/common/block/tile/entity/TileEntitySiphoningFlower", "ownerId", "Ljava/lang/String;"));
                    toInsert.add(new VarInsnNode(ALOAD, 3));

                    method.instructions.insert(targetNode, toInsert);
                }
            }
        }
    }
}