package com.bkw;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class GenericDataAccess {
    // DB connection constants (should be externalized)
    private static String url="jdbc:mariadb://localhost:3306/sampledb";
    private static String user="root";
    private static String password="admin";

    // For a specified GenericBean, build a where clause to isolate the record based on its key field values
    protected static String getKeyWhere(GenericBean bean,IGenericField[] fields) {
        String whereClause="";
        for(IGenericField field: fields) {
            if(field.isKey()) whereClause+=(whereClause.length()>0?" and ":"")+field.getFieldName()+"="+getStringValue(bean,field);
        }
        if(whereClause.length()>0) whereClause=" where "+whereClause;
        else whereClause=" where true";

        return whereClause;
    }

    // Retrieves a String value appropriate for use in a SQL update or insert statement
    protected static String getStringValue(GenericBean bean,IGenericField field) {
        String result="";
        if(bean.containsKey(field)) {
            if(field.getDataType()==GenericBean.DataType.STRING)
                result="'"+bean.get(field)+"'";
            else {
                if(field.getDataType()==GenericBean.DataType.DATE) {
                    java.sql.Date sqlDate=new java.sql.Date(((Date)bean.get(field)).getTime());
                    result="'" + new SimpleDateFormat("yyyy-MM-dd").format(sqlDate) + "'";
                }
                else
                result=bean.get(field).toString();
            }
        }
        return result;
    }

    // Deletes the specified GenericBean based on it's key field values (only key field values need to be specified)
    public static void delete(GenericBean bean,IGenericField[] fields) {
        BasicConnectionPool bcp=null;
        Connection conn=null;
        try {
            bcp=BasicConnectionPool.create(url,user,password);
            conn=bcp.getConnection();
            Statement stmt = conn.createStatement();
            String delete="delete from "+bean.getTableName()+getKeyWhere(bean,fields);
System.out.println("delete:"+delete);
            stmt.executeUpdate(delete);
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(bcp!=null && conn!=null) bcp.releaseConnection(conn);
        }
    }

    // Creates a record for the specified GenericBean
    public static GenericBean create(GenericBean bean, IGenericField[] fieldEnum) {
        BasicConnectionPool bcp=null;
        Connection conn=null;
        GenericBean emp=null;
        try {
            bcp=BasicConnectionPool.create(url,user,password);
            conn=bcp.getConnection();
            Statement stmt = conn.createStatement();
            String insert="insert into "+bean.getTableName();
            String fields="";
            String values="";
            for(IGenericField field: fieldEnum) {
                if(bean.containsKey(field)) {
                    fields+=(fields.length()>0?",":"")+field.getFieldName();
                    String value=GenericDataAccess.getStringValue(bean, field);
                    values+=(values.length()>0?",":"")+value;
                }
            }
            insert+="("+fields+") values("+values+")";
System.out.println("insert:"+insert);
            stmt.executeUpdate(insert);
            emp=bean;
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(bcp!=null && conn!=null) bcp.releaseConnection(conn);
        }
        return emp;
    }

    // Updates an existing record for the provided GenericBean using the key field values
    public static GenericBean update(GenericBean bean, IGenericField[] fields) {
        BasicConnectionPool bcp=null;
        Connection conn=null;
        GenericBean emp=null;
        try {
            bcp=BasicConnectionPool.create(url,user,password);
            conn=bcp.getConnection();
            Statement stmt = conn.createStatement();
            String update="update "+bean.getTableName()+" set ";
            //where "+Employee.Field.EMPLOYEEID.getFieldName()+"="+bean.getEmployeeID().toString();
            String fieldAssignment="";
            for(IGenericField field: fields) {
                if(bean.containsKey(field)) {
                    fieldAssignment+=(fieldAssignment.length()>0?",":"")+field.getFieldName()+"=";
                    String value=GenericDataAccess.getStringValue(bean, field);
                    fieldAssignment+=value;
                }
            }
            update+=fieldAssignment+getKeyWhere(bean,fields);
System.out.println("update:"+update);
            stmt.executeUpdate(update);
            //System.out.println(bean);
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(bcp!=null && conn!=null) bcp.releaseConnection(conn);
        }
        return emp;
    }

    // Reads the record for the specified GenericBean using its key field values
    public static GenericBean read(GenericBean bean, IGenericField[] fields) {
        BasicConnectionPool bcp=null;
        Connection conn=null;
        try {
            bcp=BasicConnectionPool.create(url,user,password);
            conn=bcp.getConnection();
            Statement stmt = conn.createStatement();

            String query="select * from "+bean.getTableName()+getKeyWhere(bean,fields);
System.out.println("read:"+query);
            ResultSet rs = stmt.executeQuery(query);
            if(rs.next()){
                for(IGenericField field: fields) {
                    String fieldName=field.getFieldName();
                    //System.out.println(fieldName + ": " + rs.getString(fieldName));
                    if(field.getDataType()==GenericBean.DataType.STRING)
                        bean.put(field,rs.getString(fieldName));
                    else {
                        if(field.getDataType()==GenericBean.DataType.DATE)
                            bean.put(field,java.sql.Date.valueOf(rs.getString(fieldName)));
                        if(field.getDataType()==GenericBean.DataType.INTEGER)
                            bean.put(field,Integer.valueOf(rs.getString(fieldName)));
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(bcp!=null && conn!=null) bcp.releaseConnection(conn);
        }
        return bean;
    }
}