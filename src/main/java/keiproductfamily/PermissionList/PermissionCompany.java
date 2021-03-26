package keiproductfamily.PermissionList;

import java.util.ArrayList;

public class PermissionCompany {
    private final ArrayList<String> employees = new ArrayList<String>();//団体所属者リスト
    private final String companyName;
    public PermissionCompany(String companyName){
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }
    public ArrayList<String> getEmployees(){
        return employees;
    }
    void addEmployee(String name){
        employees.add(name);
    }
    void removeEmployee(String name){
        employees.remove(name);
    }
}
