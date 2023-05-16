**University Database Application README**       

This README file describes how the random data was generated and loaded into the university database for the application.

**Data Generation**

Random data was generated for the university database using the following steps:

**Departments:** A fixed set of department names was created, representing different majors and minors offered by the university.

**Student Information:** Random student information was generated, including first name, last name, and year. The names were chosen randomly from a predefined list of 30 names.

**Major and Minor Selection:** Each student was assigned a major by selecting a department randomly from the list of departments. Additionally, some students were assigned double majors by selecting another department from the list, ensuring that the second major was different from the first. Minors were also assigned to students by selecting a department randomly, with the condition that the minor department is different from the major(s).

**Course Information:** A list of courses was generated with random course names, departments, and credit values. Each course was assigned to a department, and the number of credits for each course was randomly set within a predefined range.

**Enrollment and Grades:** Students were enrolled in random courses, and a grade (A, B, C, D, or F) was randomly assigned for each course taken. The GPA of each student was calculated based on the grades received and the credit values of the courses.

Multiple random students were generated by looping through 100 times to create 100 students

**Data Loading**

The generated random data was loaded into the university database using the following tables:

**Departments**: This table stores the department information, including the department name.

**Students**: This table stores the student information, including ID, first name, last name, and year.

**Majors**: This table stores the major(s) of each student, with each row containing a student ID and the department name of the major.

**Minors**: This table stores the minor of each student, with each row containing a student ID and the department name of the minor.

**Classes**: This table stores the course information, including the course name, department, and number of credits.

**HasTaken**: This table stores the enrollment and grade information, with each row containing a student ID, course name, and the grade received in the course.

**IsTaking**: This table stores the enrollment information, with each row containing a student ID and the course name.

To load the random data into the database, prepared statements were used to insert the data into the respective tables. The prepared statements were executed in loops to generate the required number of students, courses, and enrollments, using the random data generation process described above.

Once the data was loaded into the database, various queries were written to search for students based on different criteria, such as name, year, and GPA. These queries made use of the loaded data to display the results in the desired format.

**Running the application**

The application can be run in terminal using this command:  
**java -classpath pathtomysqlconnectorjarfile universitydb.java localhost:3306/universitydb root password**

You would put in your path to the jar file and your password in the command line.