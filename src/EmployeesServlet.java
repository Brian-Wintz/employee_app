import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bkw.GenericBean;
import com.bkw.GenericDataAccess;
import com.bkw.IGenericField;
import com.bkw.employee.Employee;

//import com.google.gson.*;

@WebServlet("/employees")
public class EmployeesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<GenericBean<IGenericField, Object>> employees = getEmployees();

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        //GsonBuilder builder=new GsonBuilder();
        //builder.setPrettyPrinting();
        //builder.serializeNulls();
        //Gson gson=builder.create();
        String json="{ \"Employees\": [";

        for(int i=0; i<employees.size(); ++i) {
            String jsonEmp=employees.get(i).toString();
            json+=(i>0?",":"")+"{ \"Employee\": "+jsonEmp + "\n}";
        }
        json+="]}";
        //json=gson.toJson(employees);
        //System.out.println(json);
        //out.println(new JSONSerializer().serialize(employees));
        out.println(json);
    }

    private List<GenericBean<IGenericField, Object>> getEmployees() {
        // Get the employees from the database.
        Employee emp=new Employee();
        return GenericDataAccess.readAll(emp,Employee.Field.values());
    }

    public static void main(String[] args) {
        Employee emp=new Employee();
        List<GenericBean<IGenericField, Object>> employees = GenericDataAccess.readAll(emp,Employee.Field.values());

        //GsonBuilder builder=new GsonBuilder();
        //builder.setPrettyPrinting();
        //builder.serializeNulls();
        //Gson gson=builder.create();
        String json="{ \"Employees\": [";

        for(int i=0; i<employees.size(); ++i) {
            // Need to convert from GenericBean to an Employee instance
            //Employee e=new Employee(employees.get(i));
            String jsonEmp=employees.get(i).toString();
            json+=(i>0?",":"")+"{ \"Employee\": "+jsonEmp + "\n}";
        }
        json+="]}";
        //json=gson.toJson(employees);
        System.out.println(json);

    }

}
