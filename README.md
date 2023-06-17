# Employee App

The initial purpose of this project is to understand how to manage persistence of a POJO (plain old java object) using a MariaDB database. The objectives are as follows:
* Provide a generic mechanism that provides for create/read/update/delete (crud) management of a database record for a POJO
* Identify what information needs to be configured by the POJO to allow this generic crud management to work
* Document the generic framework

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
