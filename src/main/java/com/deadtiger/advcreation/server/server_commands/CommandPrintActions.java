package com.deadtiger.advcreation.server.server_commands;

import com.deadtiger.advcreation.handler.ServerConfigurationHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandPrintActions implements ICommand {
    private final List aliases;

    public CommandPrintActions() {
        aliases = new ArrayList();
        aliases.add("printActions");
    }

    @Override
    public String getName() {
        return "printActions";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "printActions <boolean>";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {

        if(args.length == 0)
        {
            sender.sendMessage( new TextComponentString("Invalid argument"));
            return;
        }

        sender.sendMessage(new TextComponentString("printActions: [" + args[0]
                + "]"));

        boolean arg = Boolean.parseBoolean(args[0]);
        ServerConfigurationHandler.PRINT_ACTIONS = arg;

    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
