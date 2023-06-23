package com.bkw.employee;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.time.LocalDate;

import com.bkw.GenericBean;
import com.bkw.IGenericField;
import com.bkw.IGenericDataType;
import com.bkw.GenericDataAccess;

public class Employee extends GenericBean<IGenericField, Object> {

    public enum Field implements IGenericField{
        FIRSTNAME("FirstName",GenericBean.DataType.STRING),
        LASTNAME("LastName",GenericBean.DataType.STRING),
        EMPLOYEEID("EmployeeID",GenericBean.DataType.INTEGER,true),
        EMPLOYER("Employer",GenericBean.DataType.STRING),
        ADDRESS("Address",GenericBean.DataType.STRING),
        CITY("City",GenericBean.DataType.STRING),
        STATE("State",GenericBean.DataType.STRING),
        STARTDATE("StartDate",GenericBean.DataType.DATE),
        MOBILEPHONE("MobilePhone",GenericBean.DataType.STRING),
        POSTALCODE("PostalCode",GenericBean.DataType.STRING);

        private String fieldName;
        private boolean isKey;
        private IGenericDataType dataType;

        Field(String fieldName, IGenericDataType dataType, boolean isKey) {
            this.fieldName = fieldName;
            this.dataType = dataType;
            this.isKey = isKey;
        }

        Field(String fieldName, IGenericDataType dataType) {
            this.fieldName = fieldName;
            this.dataType = dataType;
            this.isKey = false;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public IGenericDataType getDataType() {
            return this.dataType;
        }

        public boolean isKey() {
            return this.isKey;
        }
    }

    public Employee(GenericBean<IGenericField, Object> bean) {
        for(Employee.Field field: Employee.Field.values()) {
            if(bean.containsKey(field)) {
                this.put(field,bean.get(field));
            }
        }
    }

    public Employee() {
        super();
    }

    public Employee(Integer employeeID) {
        super();
        map.put(Field.EMPLOYEEID,employeeID);
    }

    public Employee(String firstName, String lastName, Integer employeeID, String employer) {
        super();
        map.put(Field.FIRSTNAME,firstName);
        map.put(Field.LASTNAME,lastName);
        map.put(Field.EMPLOYEEID,employeeID);
        map.put(Field.EMPLOYER,employer);
    }

    @Override
    public String getTableName() {
        return "employees";
    }

    @Override
    public String getName() {
        return "Employee";
    }

    public String getFirstName() {
        return (String)this.get(Field.FIRSTNAME);
    }

    public void setFirstName(String firstName) {
        this.put(Field.FIRSTNAME,firstName);
    }

    public String getLastName() {
        return (String)this.get(Field.LASTNAME);
    }

    public void setLastName(String lastName) {
        this.put(Field.LASTNAME,lastName);
    }

    public Integer getEmployeeID() {
        return(Integer)this.get(Field.EMPLOYEEID);
    }

    public void setEmployeeID(Integer employeeID) {
        this.put(Field.EMPLOYEEID,employeeID);
    }

    public void setEmployeeID(String employeeID) {
        this.put(Field.EMPLOYEEID,Integer.valueOf(employeeID));
    }

    public String getEmployer() {
        return (String)this.get(Field.EMPLOYER);
    }

    public void setEmployer(String employer) {
        this.put(Field.EMPLOYER,employer);
    }

    public String getAddress() {
        return (String)this.get(Field.ADDRESS);
    }

    public void setAddress(String address) {
        this.put(Field.ADDRESS,address);
    }

    public String getCity() {
        return (String)this.get(Field.CITY);
    }

    public void setCity(String city) {
        this.put(Field.CITY,city);
    }

    public String getState() {
        return (String)this.get(Field.STATE);
    }

    public void setState(String state) {
        this.put(Field.STATE,state);
    }

    public Date getStartDate() {
        return (Date)this.get(Field.STARTDATE);
    }

    public void setStartDate(Date startDate) {
        this.put(Field.STARTDATE,startDate);
    }

    public void setStartDate(String startDate) {
        this.put(Field.STARTDATE,java.sql.Date.valueOf(startDate));
    }

    public String getMobilePhone() {
        return (String)this.get(Field.MOBILEPHONE);
    }

    public void setMobilePhone(String mobilePhone) {
        this.put(Field.MOBILEPHONE,mobilePhone);
    }

    public String getPostalCode() {
        return (String)this.get(Field.POSTALCODE);
    }

    public void setPostalCode(String postalCode) {
        this.put(Field.POSTALCODE,postalCode);
    }

    @Override
    public String toString() {
        //String result="{\n  \""+this.getName()+"\": {";
        String result="{\n";
        boolean isFirst=true;
        for(Employee.Field field: Employee.Field.values()) {
            if(this.get(field)!=null) {
                String fieldName=field.getFieldName();
                String value="";
                if(field.getDataType()==GenericBean.DataType.STRING)
                    value="\""+this.get(field)+"\"";
                else
                    if(field.getDataType()==GenericBean.DataType.DATE) {
                        Date date=(Date)this.get(field);
                        LocalDate localDate=date.toLocalDate();
                        value="\""+localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)+"\"";
                        //value="\""+date.toLocaleString()+"\"";
                    }
                    else
                        value=this.get(field).toString();
                result+=(!isFirst?",":"")+"\n  \""+fieldName+"\": "+value;
                isFirst=false;
            }
        }
        result+="\n}";
        return result;
    }

    public static void main(String[] args) {
        // Read all employees
        Employee emp=new Employee();
        List<GenericBean<IGenericField, Object>> list=GenericDataAccess.readAll(emp,Employee.Field.values());
        System.out.println("All:"+list);

        // Read existing employee
        emp=new Employee(1);
        emp=(Employee)GenericDataAccess.read(emp,Employee.Field.values());
        System.out.println("Read: "+emp);

        if(emp.getFirstName().equalsIgnoreCase("Alex"))
            emp.setFirstName("Bob");
        else
            emp.setFirstName("Alex");
        GenericDataAccess.update(emp,Employee.Field.values());

        // Read updated employee
        Employee emp2=(Employee)GenericDataAccess.read(emp,Employee.Field.values());
        System.out.println("Updated: "+emp);

        // Create copy of updated employee
        emp2.setEmployeeID(emp2.getEmployeeID()+100);
        emp2.setFirstName("Jim");
        GenericDataAccess.create(emp2,Employee.Field.values());

        // Delete the created copy
        GenericDataAccess.delete(emp2,Employee.Field.values());
    }
}