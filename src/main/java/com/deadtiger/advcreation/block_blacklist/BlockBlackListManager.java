package com.deadtiger.advcreation.block_blacklist;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class BlockBlackListManager
{
    public static ArrayList<String> BLACKLISTED_BLOCKS = new ArrayList<>();
    public static final String BLACKLIST_FILENAME = "advcreation_block_blacklist.txt";

    public static boolean BLACKLIST_ACTIVE = false;

    public static void initialiseBlackList(File blacklistFile)
    {

        if (!blacklistFile.exists())
        {
            createBlackListFile(blacklistFile);
            return;
        }

        readBlackListFile(blacklistFile);

    }

    public static boolean IsOnBlackList(Item item)
    {
        String itemID =  Item.REGISTRY.getNameForObject(item).getResourceDomain() + ":" + Item.REGISTRY.getNameForObject(item).getResourcePath();
        for (String entry: BLACKLISTED_BLOCKS)
        {
            if(itemID.contains(entry))
            {
                return true;
            }

        }
        return false;
    }

    public static boolean IsOnBlackList(ItemStack stack)
    {
        return IsOnBlackList(stack.getItem());
    }


    private static boolean createBlackListFile(File blacklistFile)
    {

        OutputStream outputstream = null;
        boolean flag2;

        try
        {
            outputstream = new FileOutputStream(blacklistFile);
            try (PrintWriter p = new PrintWriter(outputstream))
            {
                p.println("# Advanced Creation Block Blacklist");
                p.println("# ");
                p.println("# If your game keeps crashing because you equipped a modded block or item that Advanced Creation cannot handle, then you should add the block/item ID to the list below.");
                p.println("# This blacklisting will just prevent you from using the block in Advanced Creation's isometric view mode. It can still be used in other views both in creative and survival.");
                p.println("# ");
                p.println("# Don't know the ID of the block/item?");
                p.println("# Just  press F5 + H while in Minecraft this will enable advanced tooltips. Then open your inventory and hover over the offending block/item.");
                p.println("# For cobblestone It will say 'minecraft:cobblestone' that is the block ID of the cobblestone block.");
                p.println("# For modded blocks/items the block/item ID will have the mod-ID in it. example: 'advcreation:lava_block'.");
                p.println("# ");
                p.println("# Add this block/item ID to the list below without a '#' and use a new line for each blacklisted block/item.");
                p.println("# You don't even need to add the whole ID you can just add 'stone' and that will blacklist all blocks/items that have the word 'stone' in the block/item ID.");
                p.println("# Do I really need to tell you that that will blacklist nearly half of all blocks/items.");
                p.println("");
                p.println("# FUNCTIONAL INFORMATION");
                p.println("# To reload this file into minecraft after editing it. Exit a world and go back into the world");
                p.println("# On dedicated and LAN servers, the server will send it's blacklist to all players that enter the server. To reload this file the server needs to restart.");
                p.println("# After exiting the server the player's blacklist will be wiped and only their local blacklist is loaded again.");
                p.println("# A player's local blacklist will not be shared with other players on a server if it is not their own LAN server.");
                p.println("");
                p.println("Blacklisted blocks/items:");
                p.println("#advcreation:lava_block");
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            return false;
        }
        catch (Throwable var13)
        {
            flag2 = false;
        }
        finally
        {
            IOUtils.closeQuietly(outputstream);
        }
        return flag2;
    }

    private static boolean readBlackListFile(File blackListFile)
    {
        Scanner reader = null;
        boolean res = false;

        System.out.println("Reading Advanced Creation's Block BlackList file");
        if (blackListFile.exists())
        {
            try
            {
                reader = new Scanner(blackListFile);


                //readlines for input stream and parse into properties
                while (reader.hasNextLine())
                {
                    String data = reader.nextLine();
                    if(data.contains("#"))
                        data = data.split("#")[0];
                    data = data.toLowerCase().trim();

                    if(data.isEmpty() || data.contains("blacklisted blocks/items:"))
                        continue;
                    if(!BLACKLISTED_BLOCKS.contains(data))
                        BLACKLISTED_BLOCKS.add(data);
                }
                res = true;
            }
            catch (FileNotFoundException e)
            {
                System.out.println("Something went wrong try to load " + blackListFile.getAbsolutePath());
                e.printStackTrace();
                res = false;
            }
            finally
            {
                if (reader != null)
                    reader.close();
            }

        }
        else
        {
            System.out.println("No file found at " + blackListFile.getAbsolutePath());
            res = false;
        }

        if(BLACKLISTED_BLOCKS.size() != 0)
        {
            BLACKLIST_ACTIVE = true;
            System.out.println("Advanced Creation has SUCCESFULLY loaded blocks/items that are blacklisted for use in Isometric view.");
            System.out.println("These are the following:");
            for (int i = 0; i < BLACKLISTED_BLOCKS.size(); i++)
            {
                String entry = BLACKLISTED_BLOCKS.get(i);
                System.out.println("Entry " + i + " '" + entry + "' applies to blocks/items:");

                for (Item item:Item.REGISTRY)
                {
                    String itemText = Item.REGISTRY.getNameForObject(item).getResourceDomain() + ":" + Item.REGISTRY.getNameForObject(item).getResourcePath();;

                    if(itemText.contains(entry))
                    {
                        System.out.println(itemText);
                    }
                }
//                for (Item item: ForgeRegistries.ITEMS)
//                {
//                    String itemText = (new StringTextComponent(ForgeRegistries.ITEMS.getKey(item).toString())).getText();
//
//                    if(itemText.contains(entry))
//                    {
//                        System.out.println(itemText);
//                    }
//                }

            }
        }
        else
            System.out.println("Advanced Creation has found no blocks/items that are blacklisted for use in Isometric view");

        return res;
    }

    public static void addBlacklistedEntries(ArrayList<String> newList)
    {
        if(newList.isEmpty())
            return;

        System.out.println("Advanced Creation adds the following blocks/items that are blacklisted for use in Isometric view.");
        System.out.println("(This is probably a server requesting this to protect you and itself from crashes)");
        System.out.println("These are the following:");
        for (int i = 0; i < newList.size(); i++)
        {
            String entry = newList.get(i);
            System.out.println("Entry " + i + " '" + entry + "' applies to blocks/items:");

            for (Item item:Item.REGISTRY)
            {
                String itemText = Item.REGISTRY.getNameForObject(item).getResourceDomain() + ":" + Item.REGISTRY.getNameForObject(item).getResourcePath();;

                if(itemText.contains(entry))
                {
                    BLACKLISTED_BLOCKS.add(itemText);
                    System.out.println(itemText);
                }
            }
        }
        BLACKLIST_ACTIVE = true;
    }

    public static void refreshBlackList(File McDataDir)
    {
        System.out.println("Refreshing Advanced Creation's Block BlackList");
        BLACKLIST_ACTIVE = false;
        BLACKLISTED_BLOCKS.clear();
        initialiseBlackList(new File(Minecraft.getMinecraft().mcDataDir, BlockBlackListManager.BLACKLIST_FILENAME));

    }

}
