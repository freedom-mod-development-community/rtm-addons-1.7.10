package keiproductfamily;

import keiproductfamily.PermissionList.PermissionCompanyList;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandKEIPF extends CommandBase {
    @Override
    public String getCommandName() {
        return "KEI";
    }

    private String EnabledChunkLoaderList = "EnabledChunkLoaderList";
    private String PermissionFileReLoad = "PermissionFileReLoad";

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] arg) {
        if(arg[0].equals(EnabledChunkLoaderList)){
            if(PermissionCompanyList.canUseByKey(EnabledChunkLoaderList, sender)) {
                //TODO
                //ChunkLoadBlock.sendChutEnabledLoaderList(sender);
            }
        }else if(arg[0].equals(PermissionFileReLoad)){
            if(PermissionCompanyList.canUseByKey(PermissionFileReLoad, sender)){
                if(PermissionCompanyList.read()){
                    sender.addChatMessage(new ChatComponentText("Permission CompanyList & HolderList ReLoaded!"));
                }else{
                    sender.addChatMessage(new ChatComponentText("Exception occurred! Permission CompanyList & HolderList Was Not ReLoad!"));
                }

            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String... arg) {
        if(arg.length == 1){
            return getListOfStringsMatchingLastWord(arg,
                    EnabledChunkLoaderList, PermissionFileReLoad
            );
        }
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "KEI:commandUse.";
    }
}
