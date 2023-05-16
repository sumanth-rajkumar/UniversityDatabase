package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class universitydb {

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java UniDB <db_url> <username> <password>");
            return;
        }

        String dbUrl = "jdbc:mysql://" + args[0];
        String dbUsername = args[1];
        String dbPassword = args[2];
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            String[] tableNames = {"HasTaken", "IsTaking", "Minors", "Majors", "Students", "Classes", "Departments"};
            for (String tableName : tableNames) {
                // Disable foreign key checks
                PreparedStatement disableForeignKeyChecks = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 0;");
                disableForeignKeyChecks.execute();

                // Truncate table
                PreparedStatement truncateTable = connection.prepareStatement("TRUNCATE TABLE " + tableName + ";");
                truncateTable.execute();

                // Enable foreign key checks
                PreparedStatement enableForeignKeyChecks = connection.prepareStatement("SET FOREIGN_KEY_CHECKS = 1;");
                enableForeignKeyChecks.execute();
            }

            String[] firstNames = {"John", "Jane", "Michael", "Michelle", "David", "Sarah", "Alice", "Bob", "Charlie", "Emma", "Frank", "Grace", "Henry", "Arjun", "Sumanth", "Yogesh", "Sandipan", "Shubham", "Isabella", "Rachel", "Olivia", "Quinn", "Peter", "Tony", "Uma", "Xander", "Chandru", "Jaashritha", "Mathu", "Oliver"};
            String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Taylor", "Miller", "Adams", "Rajkumar", "Singh", "Mondal", "Ramani", "Sulur", "Nguyen", "Jones", "Cotton", "Hilbert", "Stark", "Yates", "Zhang", "Parker", "Ortiz", "Barton", "Ramonav", "Banner", "Odinson", "Rogers", "Sekaran", "Queen", "Allen", "Carter"};
            String[] departments = {"Bio", "Chem", "CS", "Eng", "Math", "Phys"};
            String[][] classes = {
                {"Introduction to Biology", "4"},
                {"General Chemistry", "4"},
                {"Intro to Computer Science", "3"},
                {"English Literature", "3"},
                {"Calculus I", "4"},
                {"Physics Mechanics", "4"},
                {"Organic Chemistry", "4"},
                {"Data Structures", "3"},
                {"Multivariable Calculus", "4"},
                {"Thermodynamics", "4"},
                {"Software Methodology", "4"},
                {"Calculus II", "4"},
                {"Intro to Writing for Business", "3"},
                {"Expository Writing", "3"},
                {"Intro to Engineering", "3"},
                {"Analytical Physics I" ,"3"},
                {"Analytical Physics II", "3"}
            };
            String[] grades = {"A", "B", "C", "D", "F"};
            Random random = new Random();
            String[] campuses = {"Busch", "CAC", "Livi", "CD"};

            for (String department : departments) {
                String campus = campuses[random.nextInt(campuses.length)];

                PreparedStatement insertDepartment = connection.prepareStatement(
                    "INSERT INTO Departments (name, campus) VALUES (?, ?)");
                insertDepartment.setString(1, department);
                insertDepartment.setString(2, campus);
                insertDepartment.executeUpdate();
            }
            // Insert classes into the Classes table if they don't already exist
            for (String[] cls : classes) {
                PreparedStatement checkClass = connection.prepareStatement(
                    "SELECT * FROM Classes WHERE name = ?");
                checkClass.setString(1, cls[0]);
                ResultSet resultSet = checkClass.executeQuery();

                if (!resultSet.next()) {
                    PreparedStatement insertClass = connection.prepareStatement(
                        "INSERT INTO Classes (name, credits) VALUES (?, ?)");
                    insertClass.setString(1, cls[0]);
                    insertClass.setInt(2, Integer.parseInt(cls[1]));
                    insertClass.executeUpdate();
                }
            }

            HashSet<Integer> studentIds = new HashSet<>();

            for (int i = 0; i < 100; i++) {
                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];
                int id;

                do {
                    id = 100000000 + random.nextInt(900000000);
                } while (studentIds.contains(id));

                studentIds.add(id);

                PreparedStatement insertStudent = connection.prepareStatement(
                    "INSERT INTO Students (first_name, last_name, id) VALUES (?, ?, ?)");
                insertStudent.setString(1, firstName);
                insertStudent.setString(2, lastName);
                insertStudent.setInt(3, id);
                insertStudent.executeUpdate();

                String major1 = departments[random.nextInt(departments.length)];
                String major2 = null;
                boolean hasDoubleMajor = random.nextBoolean(); // Randomly decide if the student has a double major

                if (hasDoubleMajor) {
                    // Ensure that the second major is different from the first major
                    do {
                        major2 = departments[random.nextInt(departments.length)];
                    } while (major1.equals(major2));
                }

                PreparedStatement insertMajor1 = connection.prepareStatement(
                    "INSERT INTO Majors (sid, dname) VALUES (?, ?)");
                insertMajor1.setInt(1, id);
                insertMajor1.setString(2, major1);
                insertMajor1.executeUpdate();

                if (hasDoubleMajor) {
                    PreparedStatement insertMajor2 = connection.prepareStatement(
                        "INSERT INTO Majors (sid, dname) VALUES (?, ?)");
                    insertMajor2.setInt(1, id);
                    insertMajor2.setString(2, major2);
                    insertMajor2.executeUpdate();
                }
                if (!hasDoubleMajor) {
                    String minor;
                    boolean hasMinor = random.nextBoolean(); // Randomly decide if the student has a minor

                    if (hasMinor) {
                        // Ensure that the minor is different from the major
                        do {
                            minor = departments[random.nextInt(departments.length)];
                        } while (minor.equals(major1));

                        PreparedStatement insertMinor = connection.prepareStatement(
                            "INSERT INTO Minors (sid, dname) VALUES (?, ?)");
                        insertMinor.setInt(1, id);
                        insertMinor.setString(2, minor);
                        insertMinor.executeUpdate();
                    }
                }
                int year = i % 4; // Distribute students evenly across years
                int minCompletedCredits, maxCompletedCredits;
                int maxCredits = Arrays.stream(classes).mapToInt(course -> Integer.parseInt(course[1])).sum();
                switch (year) {
                case 0:
                    minCompletedCredits = 0;
                    maxCompletedCredits = 29;
                    break;
                case 1:
                    minCompletedCredits = 30;
                    maxCompletedCredits = 59;
                    break;
                case 2:
                    minCompletedCredits = 60;
                    maxCompletedCredits = 89;
                    break;
                case 3:
                    minCompletedCredits = 90;
                    maxCompletedCredits = maxCredits;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + year);
                }
                double probabilityOfHavingClasses = 0.9;
                boolean hasClasses = random.nextDouble() < probabilityOfHavingClasses;
                List<String> allClasses = Arrays.stream(classes).map(course -> course[0]).collect(Collectors.toList());
                Collections.shuffle(allClasses);

                int classIndex = 0;
                Set<String> takenClasses = new HashSet<>();

                if (hasClasses) {
                    int numberOfCoursesCompleted;
                    if (maxCompletedCredits > minCompletedCredits) {
                        numberOfCoursesCompleted = random.nextInt(maxCompletedCredits - minCompletedCredits + 1) + minCompletedCredits;
                    } else {
                        numberOfCoursesCompleted = minCompletedCredits;
                    }
                    numberOfCoursesCompleted = numberOfCoursesCompleted / 4;

                    for (int j = 0; j < numberOfCoursesCompleted && classIndex < allClasses.size(); j++) {
                        String completedClassName = allClasses.get(classIndex++);
                        takenClasses.add(completedClassName);

                        String grade = grades[random.nextInt(grades.length - 1)];
                        PreparedStatement insertHasTaken = connection.prepareStatement(
                            "INSERT INTO HasTaken (sid, name, grade) VALUES (?, ?, ?)");
                        insertHasTaken.setInt(1, id);
                        insertHasTaken.setString(2, completedClassName);
                        insertHasTaken.setString(3, grade);
                        insertHasTaken.executeUpdate();
                    }
                }

                int numberOfCoursesTaking = random.nextInt(5) + 1;
                for (int j = 0; j < numberOfCoursesTaking && classIndex < allClasses.size(); j++) {
                    String className = allClasses.get(classIndex++);
                    if (!takenClasses.contains(className)) {
                        PreparedStatement insertIsTaking = connection.prepareStatement(
                            "INSERT INTO IsTaking (sid, name) VALUES (?, ?)");
                        insertIsTaking.setInt(1, id);
                        insertIsTaking.setString(2, className);
                        insertIsTaking.executeUpdate();
                    }
                }

            }

            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Scanner scanner = new Scanner(System.in)) {

            boolean quit = false;

            while (!quit) {
                System.out.println("Welcome to the university database. Queries available:");
                System.out.println("1. Search students by name");
                System.out.println("2. Search students by year");
                System.out.println("3. Search students with GPA above a threshold");
                System.out.println("4. Search students with GPA below a threshold");
                System.out.println("5. Report department statistics");
                System.out.println("6. Get class statistics");
                System.out.println("7. Execute arbitrary SQL");
                System.out.println("8. Exit the application.");
                System.out.println("Which query would you like to run (1-8)?");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                case 1:
                    searchStudentsByName(connection, scanner);
                    break;
                case 2:
                    searchStudentsByYear(connection, scanner);
                    break;
                case 3:
                    searchStudentsByGPA(connection, scanner, true);
                    break;
                case 4:
                    searchStudentsByGPA(connection, scanner, false);
                    break;
                case 5:
                    reportDepartmentStatistics(connection, scanner);
                    break;
                case 6:
                    getClassStatistics(connection, scanner);
                    break;
                case 7:
                    executeArbitrarySQL(connection, scanner);
                    break;
                case 8:
                    quit = true;
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 8.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchStudentsByName(Connection connection, Scanner scanner) {
        System.out.println("Enter the name or part of the name to search:");
        String searchName = scanner.next();

        try {
            String query = "SELECT s.id, s.first_name, s.last_name, mm.majors, mm.minor, " +
                "COALESCE(SUM(CASE ht.grade " +
                "    WHEN 'A' THEN 4.0 " +
                "    WHEN 'B' THEN 3.0 " +
                "    WHEN 'C' THEN 2.0 " +
                "    WHEN 'D' THEN 1.0 " +
                "    WHEN 'F' THEN 0.0 " +
                "END * c.credits) / NULLIF(SUM(c.credits), 0), 0.00) AS gpa, " +
                "SUM(CASE WHEN ht.grade <> 'F' THEN c.credits ELSE 0 END) AS total_credits " +
                "FROM Students s " +
                "JOIN (SELECT m.sid, GROUP_CONCAT(DISTINCT m.dname ORDER BY m.dname ASC) AS majors, MIN(m2.dname) AS minor " +
                "      FROM Majors m " +
                "      LEFT JOIN Minors m2 ON m.sid = m2.sid AND m2.dname <> m.dname " +
                "      GROUP BY m.sid) AS mm ON s.id = mm.sid " +
                "LEFT JOIN HasTaken ht ON s.id = ht.sid " +
                "LEFT JOIN Classes c ON ht.name = c.name " +
                "WHERE s.first_name LIKE ? OR s.last_name LIKE ? " +
                "GROUP BY s.id, s.first_name, s.last_name, mm.majors, mm.minor " +
                "ORDER BY s.last_name, s.first_name, s.id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchName + "%");
            preparedStatement.setString(2, "%" + searchName + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            int count = 0;
            while (resultSet.next()) {
                count++;
                System.out.println(resultSet.getString("last_name") + ", " + resultSet.getString("first_name"));
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Majors: " + resultSet.getString("majors").replace(",", ", "));
                String minor = resultSet.getString("minor");
                if (minor != null) {
                    System.out.println("Minor: " + minor);
                }
                System.out.printf("GPA: %.2f%n", resultSet.getDouble("gpa"));
                System.out.println("Credits: " + resultSet.getInt("total_credits"));
                System.out.println();
            }

            if (count == 0) {
                System.out.println("No students found matching the name.");
            } else {
                System.out.println(count + " students found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchStudentsByYear(Connection connection, Scanner scanner) {
        System.out.println("Enter the year (Fr, So, Ju, Sr):");
        String year = scanner.next();

        int minCredits, maxCredits;
        switch (year.toLowerCase()) {
        case "fr":
            minCredits = 0;
            maxCredits = 29;
            break;
        case "so":
            minCredits = 30;
            maxCredits = 59;
            break;
        case "ju":
            minCredits = 60;
            maxCredits = 89;
            break;
        case "sr":
            minCredits = 90;
            maxCredits = Integer.MAX_VALUE;
            break;
        default:
            System.out.println("Invalid input. Please use one of the following: Fr, So, Ju, Sr.");
            return;
        }

        try {
            String query = "SELECT s.id, s.first_name, s.last_name, GROUP_CONCAT(DISTINCT m.dname) AS majors, MIN(m2.dname) AS minor, " +
                "COALESCE(SUM(CASE ht.grade " +
                "    WHEN 'A' THEN 4.0 " +
                "    WHEN 'B' THEN 3.0 " +
                "    WHEN 'C' THEN 2.0 " +
                "    WHEN 'D' THEN 1.0 " +
                "    WHEN 'F' THEN 0.0 " +
                "END * c.credits) / NULLIF(SUM(c.credits), 0), 0.00) AS gpa, " +
                "SUM(CASE WHEN ht.grade <> 'F' THEN c.credits ELSE 0 END) AS total_credits " +
                "FROM Students s " +
                "JOIN Majors m ON s.id = m.sid " +
                "LEFT JOIN Minors m2 ON s.id = m2.sid AND m2.dname <> m.dname " +
                "LEFT JOIN HasTaken ht ON s.id = ht.sid " +
                "LEFT JOIN Classes c ON ht.name = c.name " +
                "GROUP BY s.id, s.first_name, s.last_name " +
                "HAVING (total_credits BETWEEN ? AND ?) " +
                "ORDER BY s.last_name, s.first_name, s.id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, minCredits);
            preparedStatement.setInt(2, maxCredits);

            ResultSet resultSet = preparedStatement.executeQuery();

            int count = 0;
            while (resultSet.next()) {
                count++;
                System.out.println(resultSet.getString("last_name") + ", " + resultSet.getString("first_name"));
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Major: " + resultSet.getString("majors"));
                String minor = resultSet.getString("minor");
                if (minor != null) {
                    System.out.println("Minor: " + minor);
                }
                System.out.printf("GPA: %.2f%n", resultSet.getDouble("gpa"));
                System.out.println("Credits: " + resultSet.getInt("total_credits"));
                System.out.println();
            }

            if (count == 0) {
                System.out.println("No students found in the specified year.");
            } else {
                System.out.println(count + " students found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchStudentsByGPA(Connection connection, Scanner scanner, boolean aboveThreshold) {
        System.out.println("Enter the GPA threshold:");
        double threshold = scanner.nextDouble();

        try {
            String query = "SELECT s.id, s.first_name, s.last_name, GROUP_CONCAT(DISTINCT m.dname) AS majors, MIN(m2.dname) AS minor, " +
                "COALESCE(SUM(CASE ht.grade " +
                "    WHEN 'A' THEN 4.0 " +
                "    WHEN 'B' THEN 3.0 " +
                "    WHEN 'C' THEN 2.0 " +
                "    WHEN 'D' THEN 1.0 " +
                "    WHEN 'F' THEN 0.0 " +
                "END * c.credits) / NULLIF(SUM(c.credits), 0), 0.0) AS gpa, " +
                "SUM(CASE WHEN ht.grade <> 'F' THEN c.credits ELSE 0 END) AS total_credits  " +
                "FROM Students s " +
                "JOIN Majors m ON s.id = m.sid " +
                "LEFT JOIN Minors m2 ON s.id = m2.sid AND m2.dname <> m.dname " +
                "LEFT JOIN HasTaken ht ON s.id = ht.sid " +
                "LEFT JOIN Classes c ON ht.name = c.name " +
                "GROUP BY s.id, s.first_name, s.last_name " +
                "HAVING (" +
                "  (" + (aboveThreshold ? "gpa > 0 AND gpa > ?" : "gpa <= ?") + ")" +
                "  OR (gpa = 0 AND ? <= 0)" +
                ") " +
                "ORDER BY s.last_name, s.first_name, s.id";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, threshold);
            preparedStatement.setDouble(2, threshold);

            ResultSet resultSet = preparedStatement.executeQuery();

            int count = 0;
            while (resultSet.next()) {
                count++;
                System.out.println(resultSet.getString("last_name") + ", " + resultSet.getString("first_name"));
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Major: " + resultSet.getString("majors"));
                String minor = resultSet.getString("minor");
                if (minor != null) {
                    System.out.println("Minor: " + minor);
                }
                System.out.printf("GPA: %.2f%n", resultSet.getDouble("gpa"));
                System.out.println("Credits: " + resultSet.getInt("total_credits"));
                System.out.println();
            }

            if (count == 0) {
                System.out.println("No students found with the specified GPA criteria.");
            } else {
                System.out.println(count + " students found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reportDepartmentStatistics(Connection connection, Scanner scanner) {
        System.out.println("Enter the department name:");
        String departmentName = scanner.next();

        try {
            String query = "SELECT s.id, s.first_name, s.last_name, SUM(c.credits * " +
                "CASE ht.grade " +
                "    WHEN 'A' THEN 4.0 " +
                "    WHEN 'B' THEN 3.0 " +
                "    WHEN 'C' THEN 2.0 " +
                "    WHEN 'D' THEN 1.0 " +
                "    WHEN 'F' THEN 0.0 " +
                "END) / SUM(c.credits) as gpa " +
                "FROM Students s " +
                "JOIN HasTaken ht ON s.id = ht.sid " +
                "JOIN Classes c ON ht.name = c.name " +
                "JOIN Majors m ON s.id = m.sid " +
                "WHERE m.dname = ? " +
                "GROUP BY s.id, s.first_name, s.last_name " +
                "HAVING COUNT(ht.name) > 0";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, departmentName);
            ResultSet resultSet = preparedStatement.executeQuery();

            int studentCount = 0;
            double totalGPA = 0;

            while (resultSet.next()) {
                studentCount++;
                totalGPA += resultSet.getDouble("gpa");
            }

            if (studentCount > 0) {
                System.out.println("Department: " + departmentName);
                System.out.println("Number of students: " + studentCount);
                System.out.printf("Average GPA: %.2f%n", totalGPA / studentCount);
            } else {
                System.out.println("No students found in the " + departmentName + " department.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getClassStatistics(Connection connection, Scanner scanner) {
        System.out.println("Please enter the name of the class:");
        String className = scanner.nextLine();

        try {
            // Get the number of students currently taking the class
            String query = "SELECT COUNT(*) AS count FROM IsTaking WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, className);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int currentEnrollment = resultSet.getInt("count");
            resultSet.close();

            // Get the number of students who've taken the class and their grades
            query = "SELECT grade, COUNT(*) AS count FROM HasTaken WHERE name = ? GROUP BY grade";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, className);
            resultSet = preparedStatement.executeQuery();

            // Print the results
            System.out.printf("%d students currently enrolled\n", currentEnrollment);
            System.out.println("Grades of previous enrollees:");
            while (resultSet.next()) {
                System.out.printf("%s %d%n", resultSet.getString("grade"), resultSet.getInt("count"));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeArbitrarySQL(Connection connection, Scanner scanner) {
        System.out.println("Enter your SQL query:");
        String sql = scanner.nextLine();

        try {
            Statement statement = connection.createStatement();
            boolean hasResultSet = statement.execute(sql);

            if (hasResultSet) {
                ResultSet resultSet = statement.getResultSet();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Print column names
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + (i < columnCount ? "\t\t" : "\n"));
                }

                // Print rows
                while (resultSet.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(resultSet.getString(i) + (i < columnCount ? "\t\t" : "\n"));
                    }
                }
            } else {
                int updateCount = statement.getUpdateCount();
                System.out.println("Query executed successfully. Rows affected: " + updateCount);
            }
        } catch (Exception e) {
            System.out.println("Error executing the query: " + e.getMessage());
        }
    }

}




