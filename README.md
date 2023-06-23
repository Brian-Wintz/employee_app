# Employee App

The initial purpose of this project is to understand how to manage persistence of a POJO (plain old java object) using a MariaDB database. The objectives are as follows:
* Provide a generic mechanism that provides for create/read/update/delete (crud) management of a database record for a POJO
* Identify what information needs to be configured by the POJO to allow this generic crud management to work
* Document the generic framework

## Java Object DB Persistence
For this example, a simple Employee POJO is used that contains the following fields:
  String FirstName - required first name of employee
  String LastName - required last name of employee
  Integer EmployeeID - required/key for identifying employee record
  String Employer - required employer name for this employee
  String Address - employee's address (not required)
  String City - employee's city (not required)
  String State - employee's state (not required)
  Date StartDate - date on which employee started (not required)
  String MobilePhone - employee's mobile phone number (not required)
  String PostalCode - employee's postal code (not required)

This Employee class contains the expected getters and setters for each ot these fields, such as getLastName and setLastName.  In order to provide a generic access mechanism, this Employee class extends a GenericBean class.  This Generic Bean class holds each of the field's values in an internal HashMap instance which is keyed off of a Field enumeration defined in the Employee class.  This Field enumeration implements an IGenericField interface which specifies methods for retrieving information about a specific enumeration value, such as it's associated table field name, it's data type and whether it is a key field.  This enumeration and its's interface are critical to providing a generic crud implementation. Below is a diagram showing the relationships between these classes and interfaces:
![image](https://github.com/Brian-Wintz/employee_app/assets/133924124/f75eb77d-b3eb-4706-be99-435af5b1b012)
From this diagram the Employee POJO extends the GenericBean class and overrides the required getTableName and getName methods, as well as providing the crucial Field enumerator that implements the IGenericField interface.  The GenericBean parent class provides a way to access the Employee data generically utilizing the IGenericField interface as the key values into its internal HashMap.  The GenericBean class also defines the DataType enumeration that implements the IGenericDataType interface for extracting String representation of the data type.
Now that the Employee POJO is defined the next step is to define a GenericDataAccess class that makes use of the configuration information available in the Employee POJO to manage the crud operations.  For the purposes of this example, the necessary URL, user and password required for connecting to the MariaDB database are hard coded into the solution.  Normally these values would be externally configured.  An important aspect of this GenericDataAccess class is that it knows nothing about the Employee POJO that it is managing but works with the generic GenericBean and IGenericField class and interface to manage the persistence.  Below is a diagram showing the interaction of this GenericDataAccessClass and the Employee POJO:
![image](https://github.com/Brian-Wintz/employee_app/assets/133924124/64f9d89b-bb78-48e5-ba2f-707bfe0ca30f)
The main methods in the GenericDataAccess class are the create, read, update and delete methods which take a GenericBean instance, which is the generic representation of the Employee instance, and an array of IGenericField instances.  The IGenericField array is extracted from the Field enumeration by using the built in enum values() method.  Since this Field enum implements the IGenericField interface the resulting array can be treated as a collection of IGenericField instances.  In addition to the crud methods, the GenericDataAccess also has protected getKeyWhere and getStringValue methods that are used within the implementation to build the required SQL queries for managing the crud functionality.

## Building Web UI
The previous part of this implementation focused on managing persistence of an Employee java object using a generic implementation that could be used to manage any java object's persistence to a DB table.  This section extends this functionality to provide a REST API to display these records on a web page.  In order to be able to make use of a REST API it is first essential to provide a mechanism for exposing the Employee class as a json string.  The standard toString method was used to provide a json formatted representation of the Employee instance.  One challenge that I found is that Date type fields need to be formatted as an ISO date string (YYYY-MM-DD) so that it can be used within the web page.  Once a json String is provided by the Employee class, a new EmployeesServlet was created which extends the HttpServlet class (from Tomcat's javax.servlet.jar) and overrides the doGet method to process a REST API request to /employees URI to retrieve all the Employees as a json array. The configuration of this REST service is in the project's html/WEB-INF/web.xml file.  This REST API can be tested in a local Tomcat implementation by running http://localhost:8080/company/employees (company is the name of the webapp) which displays the json records for the three Employee records in my database:
{ "Employees": [{
  "Employee": {
  "FirstName": "Bob",
  "LastName": "Wintz",
  "EmployeeID": 1,
  "Employer": "QAD Inc",
  "Address": "123 E Main St",
  "City": "Santa Barbara",
  "State": "CA",
  "StartDate": "2023-06-16",
  "MobilePhone": "8057221234",
  "PostalCode": "93111"
}},{
  "Employee": {
  "FirstName": "Doug",
  "LastName": "Williams",
  "EmployeeID": 2,
  "Employer": "Widgets R Us",
  "Address": "893 State St",
  "City": "Goleta",
  "State": "CA",
  "StartDate": "2020-05-22",
  "MobilePhone": "8051234567",
  "PostalCode": "93112"
}},{
  "Employee": {
  "FirstName": "Bob",
  "LastName": "Johnson",
  "EmployeeID": 3,
  "Employer": "BKW Inc",
  "Address": "",
  "City": "Santa Barbara",
  "State": "CA",
  "StartDate": "2023-06-22",
  "MobilePhone": "",
  "PostalCode": "93111"
}}]}
Next, a company.html page that makes use of jquery to construct a table to display these Employee records was created.  This required figuring out how to make use of jquery's $.ajax syntax, as well as how to properly add rows to the table.  When the page is loaded (onload), it makes a call to the /employees REST API and processes the resulting Employee data to dynamically add a row for each record into the table.  Below is an example showing the three records currently in my database table:
![image](https://github.com/Brian-Wintz/employee_app/assets/133924124/828106a7-0df9-47bd-ac12-801cdeb7797a)
Below is a diagram showing the relationship between these main components:
![image](https://github.com/Brian-Wintz/employee_app/assets/133924124/77db64f4-e350-4a9f-b465-b71f7b77dd35)
The flow is as follows:
1. A request is made to display the company.html (http://localhost:8080/company/company.html)
2. On load of this page, javascript is executed which makes a REST API call (http://localhost:8080/company/employees)
3. The EmployeeServlet, which is configured to process requests for /employees, uses the GenericDataAccess class to retrieve all the Employee records by calling its readAll method
4. The GenericDataAccess:readAll method constructs a SQL query for the provided bean instance and uses the array of IGenericField instances to process the resulting DB table fields into a dynamically created Employee instance Note that this method doesn't know that it is processing an Employee other than the bean provided is actually an Employee instance, which is used to create Employee instances.
5. Each Employee instance is added to an ArrayList instance which is returned to the caller.
6. When the EmployeeServlet gets this List of Employee instances back it loops over them and constructs a json string which is written as a response to the REST API request.
7. When REST API call returns to company.html, it loops over each of the Employee records and appends a new table row (<tr>...</tr>) to the HTML table.  The cells are managed as editable div tags to allow for modification, with the exception of the StartDate which is shown as a date type input value - this allows for better managed of this date value.

With this solution I've been able to show an end-to-end implementation of a simple web page that illustrates the following:
* JDBC Connection Pooling
* Java Object DB Management
* Configuring and Implementing a REST API as a Servlet
* Calling a REST API from a Web Page
* Constructing Dynamic HTML
* Implementing JQuery

