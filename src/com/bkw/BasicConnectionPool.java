package com.bkw;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

import com.bkw.employee.Employee;

public class BasicConnectionPool implements IConnectionPool {

    private String url;
    private String user;
    private String password;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    private static int INITIAL_POOL_SIZE = 10;

    public static BasicConnectionPool create(String url, String user,String password) throws SQLException {

        // Build up an initial pool of INITIAL_POOL_SIZE connections
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(url, user, password));
        }
        return new BasicConnectionPool(url, user, password, pool);
    }

    // Create the connection pool (private internal only)
    private BasicConnectionPool(String url, String user, String password, List<Connection> pool) {
        connectionPool=pool;
        this.url=url;
        this.user=user;
        this.password=password;
    }
    // Retrieve a connection from the pool, removing it from the connectionPool collection and adding it to the usedConnections collection

    @Override
    public Connection getConnection() {
        Connection connection = connectionPool.remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    // Release a connection back to the pool, adding it back to the connectionPool collection and removing it from the usedConnections collection
    @Override
    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    // Create a new connection
    private static Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // Returns the size of total connections (available + used)
    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }

    // Retrieve the URL for accessing the DB
    public String getUrl() {
        return this.url;
    }

    // Retrieve the user name used to authenticate access to the DB
    public String getUser() {
        return this.user;
    }

    // Retrieve the password used to authenticate access to the DB
    public String getPassword() {
        return this.password;
    }

    // Sample test of using this pool to retrieve data into Employee java object
    public static void main(String[] args) {
        BasicConnectionPool bcp=null;
        Connection conn=null;
        try {
            bcp=BasicConnectionPool.create("jdbc:mariadb://localhost:3306/sampledb","root","admin");
            conn=bcp.getConnection();
            int lastID=0;
            Statement stmt = conn.createStatement();

            // Retrieve employee records into Employee instances
            Employee emp=new Employee();
            ResultSet rs = stmt.executeQuery("select * from "+emp.getTableName());
            while(rs.next()){
                Employee employee=new Employee();
                for(Employee.Field field: Employee.Field.values()) {
                    String fieldName=field.getFieldName();
                    System.out.println(fieldName + ": " + rs.getString(fieldName));
                    if(field.getDataType()==GenericBean.DataType.STRING)
                        employee.put(field,rs.getString(fieldName));
                    else {
                        if(field.getDataType()==GenericBean.DataType.DATE)
                            employee.put(field,java.sql.Date.valueOf(rs.getString(fieldName)));
                        if(field.getDataType()==GenericBean.DataType.INTEGER)
                            employee.put(field,Integer.valueOf(rs.getString(fieldName)));
                    }
                }
                int empID=Integer.valueOf(rs.getString("employeeID"));
                if(empID>lastID) lastID=empID;
                System.out.println(employee);
            }

            // Create a new employee
            emp=new Employee();
            String insert="insert into "+emp.getTableName();
            emp.setFirstName("Bob");
            emp.setLastName("Smith");
            emp.setEmployeeID(lastID+1);
            emp.setEmployer("BKW Inc");
            String fields="";
            String values="";
            for(Employee.Field field: Employee.Field.values()) {
                if(emp.containsKey(field)) {
                    fields+=(fields.length()>0?",":"")+field.getFieldName();
                    String value;
                    if(field.getDataType()==GenericBean.DataType.STRING)
                        value="'"+emp.get(field)+"'";
                    else
                        value=emp.get(field).toString();
                    values+=(values.length()>0?",":"")+value;
                }
            }
            insert+="("+fields+") values("+values+")";
            System.out.println("##insert:"+insert);
            stmt.executeUpdate(insert);

            // Update last created record
            String update="update "+emp.getTableName()+" set "+Employee.Field.LASTNAME.getFieldName()+"=? where "+Employee.Field.EMPLOYEEID.getFieldName()+"=?";
            PreparedStatement ps1=conn.prepareStatement(update);
            ps1.setString(1,"Johnson");
            ps1.setInt(2,lastID+1);
            ps1.executeUpdate();

/***
            // Delete last created record
            PreparedStatement ps=conn.prepareStatement("delete from "+Employee.getTableName()+" where EmployeeID=?");
            ps2.setInt(1,lastID+1);
            ps2.executeUpdate();
***/
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if(bcp!=null && conn!=null) bcp.releaseConnection(conn);
        }
    }
}