package keiproductfamily.PermissionList;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.DimensionManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class PermissionCompanyList {
    public static ArrayList<PermissionCompany> companies = new ArrayList<PermissionCompany>();
    public static HashMap<String, ArrayList<PermissionCompany>> permissionHolders = new HashMap<String, ArrayList<PermissionCompany>>();

    public static boolean read() {
        companies.clear();
        permissionHolders.clear();
        File RootDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "KEI");
        RootDir.mkdirs();

        /////////////////
        // Compny List //
        /////////////////

        File saveDir = new File(RootDir, "PermissionCompanyList.kei");
        if (!saveDir.exists()) {
            try {
                saveDir.createNewFile();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveDir), StandardCharsets.UTF_8));
                writer.write(PermissionDefault.defaultCompany);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(saveDir), StandardCharsets.UTF_8));
            String str;
            PermissionCompany newCompany = null;
            while ((str = reader.readLine()) != null) {
                if (str.startsWith("#")) {
                    continue;
                }
                str = str.trim();
                if (str.length() > 0) {
                    if (str.endsWith("{")) {
                        if (newCompany != null) {
                            throw new MoneyFileFormatException("Not End Company! File:companyList.kei company:" + newCompany);
                        }
                        str = str.substring(0, str.length() - 1);
                        newCompany = new PermissionCompany(str.trim());
                    } else if (str.startsWith("}")) {
                        if (newCompany == null) {
                            throw new MoneyFileFormatException("Not Start Company! onEnd. File:companyList.kei company:null");
                        }
                        companies.add(newCompany);
                        newCompany = null;
                    } else {
                        if (newCompany == null) {
                            throw new MoneyFileFormatException("Not Start Company! onAddMember. File:companyList.kei company:null");
                        }
                        newCompany.addEmployee(str);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        /////////////////
        // Compny List //
        /////////////////

        saveDir = new File(RootDir, "PermissionHolderList.kei");
        if (!saveDir.exists()) {
            try {
                saveDir.createNewFile();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveDir), StandardCharsets.UTF_8));
                writer.write(PermissionDefault.defaultHodler);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(saveDir), StandardCharsets.UTF_8));
            String str;
            String iparmission = null;
            ArrayList<PermissionCompany> affiliationList = null;
            while ((str = reader.readLine()) != null) {
                if (str.startsWith("#")) {
                    continue;
                }
                str = str.trim();
                if (str.length() > 0) {
                    if (str.endsWith("{")) {
                        if (iparmission != null) {
                            throw new MoneyFileFormatException("Not End iParmission! File:companyList.kei iParmission:" + iparmission);
                        }
                        iparmission = str.substring(0, str.length() - 1).trim();
                        affiliationList = new ArrayList<PermissionCompany>();
                    } else if (str.startsWith("}")) {
                        if (iparmission == null || affiliationList == null) {
                            throw new MoneyFileFormatException("Not Start Company! onEnd. File:companyList.kei company:null");
                        }
                        permissionHolders.put(iparmission, affiliationList);
                        iparmission = null;
                        affiliationList = null;
                    } else {
                        if (iparmission == null || affiliationList == null) {
                            throw new MoneyFileFormatException("Not Start Company! onAddMember. File:companyList.kei company:null");
                        }
                        for (PermissionCompany com : companies) {
                            if (com.getCompanyName().equals(str)) {
                                affiliationList.add(com);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String Everyone = "Everyone";

    public static boolean canUseByKey(String keyName, ICommandSender sender) {
        if (permissionHolders.containsKey(keyName)) {
            for (PermissionCompany company : permissionHolders.get(keyName)) {
                if (company.getEmployees().contains(Everyone) || company.getEmployees().contains(sender.getCommandSenderName())) {
                    return true;
                }
            }
        }
        sender.addChatMessage(new ChatComponentText("You don't have use Permission! Input Arg:" + sender.getCommandSenderName()));
        return false;
    }
}
