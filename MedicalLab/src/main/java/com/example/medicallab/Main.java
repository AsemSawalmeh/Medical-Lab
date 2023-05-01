package com.example.medicallab;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;



public class Main extends Application {
    private static String dbUsername = "root"; // database username
    private static String dbPassword = "MLab1234"; // database password
    private static String URL = "127.0.0.1"; // server location
    private static String port = "3306"; // port that mysql uses
    private static String dbName = "medical_lab"; //database on mysql to connect to
    private static Connection con;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DBConn a = new DBConn(URL,port, dbName, dbUsername, dbPassword);
        ArrayList<Visit> temp=new ArrayList<>(10);
        con = a.connectDB();
        System.out.println();
        launch();
        con.close();
    }
    public static int noOfPatients() throws SQLException {
        String sqlQuery = "select count(patient_id) from Patient";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);
        rs.next();
        int count = rs.getInt(1);
        stmt.close();
        rs.close();
        return count;
    }

    public static int noOfEmp() throws SQLException {
        String sqlQuery = "select count(emp_id) from Employees";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);
        rs.next();
        int count = rs.getInt(1);

        stmt.close();
        rs.close();
        return count;
    }

    public static List<String> topBranchWithEmp(Date startDate, Date endDate) throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        int id;
        String sqlQuery = "select V.branch_id, B.branch_name, count(*) from Visits V, Branches B" +
                " where B.branch_id = V.branch_id and V.date_time>='" + startDate + "' and V.date_time<='" + endDate + " 23:59:59'" +
                " group by V.branch_id order by count(*) desc;";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);

        if(rs.next()) {
            list.add(rs.getString(2) + "," + rs.getString(3));
            id = rs.getInt(1);

            // get top Employees in that branch
            String topEmployees = "select E.emp_name, count(*) from Employees E, Visits V" +
                    " where E.emp_id = V.emp_id and E.branch_id = V.branch_id and V.branch_id = " + id +
                    " and V.date_time>='" + startDate + "' and V.date_time<='" + endDate + " 23:59:59'" +
                    " group by V.emp_id order by count(*) desc;";
            Statement topEmpl = con.createStatement();
            ResultSet rs2 = topEmpl.executeQuery(topEmployees);

            int i = 0;
            while (rs2.next() && i <= 3) {
                list.add(rs2.getString(1) + "," + rs2.getString(2));
                i++;
            }
            topEmpl.close();
            rs2.close();
        }
        stmt.close();
        rs.close();
        return list;
    }


    public static int noOfPatientVisits(int patientId) throws SQLException {
        int count = 0;
        String sqlQuery = "select count(*) from Patient P, Visits V where P.patient_id = V.patient_id" +
                " and P.patient_id = " + patientId;

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);

        if (rs.next())
            count = rs.getInt(1);
        stmt.close();
        rs.close();
        return count;
    }

    public static String patientMostVisitedBranch(int patientId) throws SQLException {
        String branch = null;
        String sqlQuery = "select B.branch_name, count(*) from Branches B, Visits V where B.branch_id = V.branch_id" +
                " and V.patient_id = " + patientId + " group by V.branch_id order by count(*) desc;";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);

        if (rs.next())
            branch = rs.getString(1);

        rs.close();
        stmt.close();
        return branch;
    }

    public static int totalNoOfVisits(int branchId) throws SQLException {
        String sqlQuery = "select count(V.visit_id) from Visits V";

        if (branchId > 0)
            sqlQuery += " where V.branch_id = " + branchId;

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);
        rs.next();
        int count = rs.getInt(1);

        stmt.close();
        rs.close();
        return count;
    }

    public static List<Integer> filterData(String tableName, String field, String value, char criteria) throws SQLException {
        String sqlText = "SELECT * FROM " + tableName + " WHERE " + field + criteria + "'" + value + "'";
        ArrayList<Integer> ids = new ArrayList<>();

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlText);

        while (rs.next())
            ids.add(rs.getInt(1));


        rs.close();
        stmt.close();

        return ids;
    }

    public static void insertDoctor(List<Doctor> list, HashMap<String, String> fieldsValues) throws SQLException {
        StringBuilder sqlText = new StringBuilder("INSERT INTO Doctor (");
        Statement stmt = con.createStatement();

        for (String key : fieldsValues.keySet())
            sqlText.append(key).append(",");
        sqlText.deleteCharAt(sqlText.length()-1);
        sqlText.append(") VALUES (");

        for (String value : fieldsValues.values())
            sqlText.append(value).append(",");
        sqlText.deleteCharAt(sqlText.length()-1);
        sqlText.append(")");



        stmt.executeUpdate(sqlText.toString());

        ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
        rs.next();
        int id = rs.getInt(1);

        list.add(new Doctor(id, capitalize(fieldsValues.get("doctor_name").replace("'", ""))
                , capitalize(fieldsValues.get("specialty").replace("'", ""))
                , fieldsValues.get("email").replace("'", "")
                , Double.parseDouble(fieldsValues.get("discount")))
        );
        System.out.println(sqlText);
    }

    public static void insertPatient(List<Patient> list, String name, LocalDate date, String gender,
                                     String email, String address, boolean hasInsurance, ArrayList<String> phones) throws SQLException {
        String SQLtxt = "insert into Patient"+" (patient_name, date_of_birth, address, gender, email)" + " values('"
                + name.toLowerCase() + "','" + date + "','" + address.toLowerCase() + "','" + gender.toLowerCase() + "','" + email + "')";

        Statement statement = con.createStatement();
        statement.executeUpdate(SQLtxt);

        // initialize id by executing the sql query in the database file.
        Statement getLastId = con.createStatement();
        ResultSet set = getLastId.executeQuery("SELECT LAST_INSERT_ID()");
        set.next();

        int id = set.getInt(1);
        System.out.println(id);

        list.add(new Patient(id, date, name, gender, address, email, hasInsurance));
        list.get(list.size()-1).setPhone_numbers(phones);
        Statement stmt = con.createStatement();

        for (String s : phones)
            stmt.executeUpdate("insert into Patient_phones" + " (patient_id, phone_number)"
                    + " values (" + id + ",'" + s + "')");

        statement.close();
        stmt.close();
        set.close();
    }

    public static void addPatientPhone(String phoneNumber, int id) throws SQLException {
        String sqlText = "INSERT INTO Patient_phones values (" + id + "," + "'" + phoneNumber + "'" + ")";
        Statement stmt = con.createStatement();

        stmt.executeUpdate(sqlText);
        stmt.close();
    }

    public static void loadAllEmployees(List<Employee> list) throws SQLException {
        String selectPatientTXT = "select * from Employees";
        String selectPhonesTXT = "select * from emp_phone";
        Statement stmt = con.createStatement();
        Statement stmt2 = con.createStatement();

        ResultSet rs1 = stmt.executeQuery(selectPatientTXT);
        ResultSet rs2 = stmt2.executeQuery(selectPhonesTXT);

        while (rs1.next()) {
            ArrayList<String> phones = new ArrayList<>();
            int id = rs1.getInt(1);

            while (rs2.next()) {
                if (rs2.getInt(1) != id) {
                    rs2.previous();
                    break;
                }
                phones.add(rs2.getString(2));
            }
            list.add(new Employee(id, rs1.getString(2), rs1.getString(3), rs1.getDate(4).toLocalDate()
                    , rs1.getString(5), rs1.getString(6), rs1.getDate(7).toLocalDate(), rs1.getString(8)
                    , rs1.getInt(9), rs1.getDouble(10))
            );
            list.get(list.size()-1).setPhones(phones);
        }
        rs1.close();
        rs2.close();
        stmt.close();
        stmt2.close();
    }

    public static void loadAllPatients(List<Patient> list) throws SQLException {
        String selectPatientTXT = "select * from Patient";
        String selectPhonesTXT = "select * from Patient_phones";
        Statement stmt = con.createStatement();
        Statement stmt2 = con.createStatement();

        ResultSet rs1 = stmt.executeQuery(selectPatientTXT);
        ResultSet rs2 = stmt2.executeQuery(selectPhonesTXT);

        while (rs1.next()) {
            ArrayList<String> phones = new ArrayList<>();
            int id = rs1.getInt(1);

            while (rs2.next()) {
                if (rs2.getInt(1) != id) {
                    rs2.previous();
                    break;
                }
                phones.add(rs2.getString(2));
            }
            list.add(new Patient(id, rs1.getDate(2).toLocalDate(), rs1.getString(3)
                    , rs1.getString(4), rs1.getString(5), rs1.getString(6)
                    , rs1.getBoolean(7))
            );
            list.get(list.size()-1).setPhone_numbers(phones);
        }
        rs1.close();
        rs2.close();
        stmt.close();
        stmt2.close();
    }

    public static void loadAllDoctors(List<Doctor> list) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Doctor");

        while (rs.next()) {
            list.add(new Doctor(rs.getInt(1), rs.getString(2), rs.getString(3)
                    , rs.getString(4), rs.getDouble(5)));
        }
        stmt.close();
        rs.close();
    }

    public static void updateRecord(String tableName, HashMap<String, String> primaryKeys, HashMap<String, String> map) throws SQLException {
        if (map.size() == 0)
            return;

        StringBuilder sqlText = new StringBuilder("UPDATE " + tableName + " " + tableName.toUpperCase().charAt(0) + " SET ");
        Statement stmt = con.createStatement();

        for (String s : map.keySet()) {
            sqlText.append(tableName.toUpperCase().charAt(0)).append(".").append(s).append("=");
            sqlText.append(map.get(s)).append(",");
        }
        sqlText.deleteCharAt(sqlText.length()-1);

        sqlText.append(" WHERE ");

        for (String s : primaryKeys.keySet())
            sqlText.append(s).append("=").append(primaryKeys.get(s)).append(" AND ");

        System.out.println(sqlText);
        stmt.executeUpdate(sqlText.substring(0, sqlText.length()-5));
        stmt.close();
    }

    public static void deleteRecord(String tableName, HashMap<String, String> primaryKeys) throws SQLException {
        StringBuilder sqlText = new StringBuilder("DELETE FROM " + tableName + " " + tableName.charAt(0) + " WHERE ");

        for (String s : primaryKeys.keySet())
            sqlText.append(tableName.charAt(0)).append(".").append(s).append("=")
                    .append(primaryKeys.get(s)).append(" AND ");

        Statement delete = con.createStatement();
        System.out.println(sqlText.substring(0, sqlText.length()-5));
        delete.executeUpdate(sqlText.substring(0, sqlText.length()-5));
    }

    public static String capitalize(String string) {
        StringBuilder sb = new StringBuilder();
        String[] sArray = string.split(" ");

        for (String s : sArray)
            sb.append(s.toUpperCase().charAt(0)).append(s.substring(1)).append(" ");

        return sb.substring(0, sb.length()-1);
    }
    public static void loadAllVisits(List<Visit> vlist) throws SQLException {
        String selectVisitTXT = "select * from Visits";
        Statement svStmt = con.createStatement();

        ResultSet rs1 = svStmt.executeQuery(selectVisitTXT);
        ArrayList<medicalTest> temp = new ArrayList<>(10);


            while (rs1.next()) {


                vlist.add(new Visit(rs1.getInt(1),rs1.getString(2),rs1.getInt(3),
                        rs1.getInt(4),rs1.getInt(5),rs1.getInt(6),rs1.getInt(7),
                        rs1.getBoolean(8), rs1.getDate(9).toLocalDate().atTime(LocalTime.now())));

        }
    }

    public static void loadAllTests(List<medicalTest> tstlist) throws SQLException {
        String selectTestsTXT = "select * from medical_tests";
        Statement tstStmt = con.createStatement();

        ResultSet rs1 = tstStmt.executeQuery(selectTestsTXT);

        while (rs1.next()) {
            tstlist.add(new medicalTest(rs1.getInt(1),rs1.getString(2),rs1.getString(3),
                    rs1.getInt(4),rs1.getInt(5)));
            System.out.println(rs1.toString());
        }
    }

    public static void addVisit(List<Visit> lst, int visit_id, String payment_method, double charge_price, int branch_id, int patient_id, int doctor_id, int emp_id, boolean process_status, LocalDateTime date_time) throws SQLException {
        String addVisit="insert into Visits "+"(payment_method,charge_price,branch_id,patient_id,doctor_id,emp_id,date_time) "+"values "+"('"+payment_method+"',"+charge_price+
                ","+branch_id+","+patient_id+","+doctor_id+","+emp_id+",'"+ date_time+"')";

        Statement insrtStmt = con.createStatement();

        insrtStmt.executeUpdate(addVisit);
    };

    public static void addVisitNoDoc(List<Visit> lst, int visit_id, String payment_method, double charge_price, int branch_id, int patient_id, int emp_id, boolean process_status, LocalDateTime date_time) throws SQLException {
        String addVisit="insert into Visits "+"(payment_method,charge_price,branch_id,patient_id,emp_id,date_time) "+"values "+"('"+payment_method+"',"+charge_price+
                ","+branch_id+","+patient_id+","+emp_id+",'"+ date_time+"')";

        Statement insrtStmt = con.createStatement();

        insrtStmt.executeUpdate(addVisit);
    };


    public static void deleteVisit(List<?> list, String tableName, Object v) {
        String sqlText = "DELETE FROM " + tableName + " " + tableName.charAt(0) + " WHERE " + tableName.charAt(0)
                + "."+"visit_id=" + ((Visit)v).getVisit_id();

        Statement delete;
        try {
            delete = con.createStatement();
            delete.executeUpdate(sqlText);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.remove(v);
    }

    public static void deleteTest(List<?> list, String tableName, Object mt) {
        String sqlText = "DELETE FROM " + tableName + " " + tableName.charAt(0) + " WHERE " + tableName.charAt(0)
                + "."+"TSN=" + ((medicalTest)mt).getTsn();

        Statement delete;
        try {
            delete = con.createStatement();
            delete.executeUpdate(sqlText);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.remove(mt);
    }

    public static void addTestResult(List<medicalTest> testList,int vid) throws SQLException {
            System.out.println(vid);
            for(int i=0;i<testList.size();i++) {
                String addTestResult="insert into visit_tests_results "+"(visit_id,TSN,result) "+"values "+"("+vid+","+testList.get(i).getTsn()+
                        ",'"+"')";
                Statement insertTestResult=con.createStatement();
                insertTestResult.executeUpdate(addTestResult);
            };
    };

    public static void addTest(List<medicalTest> testList,int tsn,String tname,String result_range,int charge_price,int process_time) throws SQLException {
            tsn=selectLastTestInt()+1;
            System.out.println(tsn);
            System.out.println(tname);
            String addTest="insert into medical_tests "+"(TSN,test_name,result_range,charge_price,process_time) "+"values "+"("+tsn+",'"+tname+"','"+result_range+"',"+charge_price+","+process_time+");";
            Statement insertTestResult=con.createStatement();
            insertTestResult.executeUpdate(addTest);

    }

    public static int selectLastTestInt() throws SQLException {
        String sqltxt="SELECT MAX(TSN) as last_inserted_tsn FROM medical_tests";
        Statement stmt=con.createStatement();

        ResultSet rs=stmt.executeQuery(sqltxt);
        int id=0;
        while (rs.next()) {
            id=rs.getInt(1);
        }
        return id;
    }
    public static int selectLastVisitInt() throws SQLException {
        String sqltxt="SELECT MAX(visit_id) as last_inserted_visit_id FROM Visits";
        Statement stmt=con.createStatement();

        ResultSet rs=stmt.executeQuery(sqltxt);
        int id=0;
        while (rs.next()) {
            id=rs.getInt(1);
        }
        return id;
    }

    public static void loadVisitTests(ObservableList<test_result> list,int vid) throws SQLException {
        String selectTestsTXT = "select * from visit_tests_results"+" where"+" visit_id="+vid;
        Statement tstStmt = con.createStatement();

        ResultSet rs1 = tstStmt.executeQuery(selectTestsTXT);

        while (rs1.next()) {
            list.add(new test_result(rs1.getInt(1),rs1.getInt(2),rs1.getString(3)));
            System.out.println(rs1.getInt(1)+","+rs1.getInt(2));
        }
    }

    public static List<String> getTestResults(int vid) throws SQLException {
        String selectTestsTXT = "select TSN,result from visit_tests_results"+" where"+" visit_id="+vid;
        Statement tstStmt = con.createStatement();

        ResultSet rs1 = tstStmt.executeQuery(selectTestsTXT);
        List<String> tests=new ArrayList<>(50);
        while (rs1.next()) {
            tests.add(rs1.getInt(1)+" "+rs1.getString(2));
        }
        return tests;
    }
    public static void updateTestResults(ObservableList<test_result> list,int vid) throws SQLException {
        for(int i=0;i<list.size();i++) {
            System.out.println(list.get(i).getTSN()+","+list.get(i).getResult());
            String updateTestsTXT = "update visit_tests_results set result="+list.get(i).getResult()+" where visit_id="+vid+" AND TSN="+list.get(i).getTSN();
            Statement tstStmt = con.createStatement();
            tstStmt.executeUpdate(updateTestsTXT);
        }
    }

    public static String getPatientName(int pid) throws SQLException {
        String selectPname = "select patient_name from Patient"+" where"+" patient_id="+pid;
        Statement tstStmt = con.createStatement();

        ResultSet rs1 = tstStmt.executeQuery(selectPname);
        String name="";
        while (rs1.next()) {
            name+=rs1.getString(1);
        }
        return name;
    }
    public static String getBranchName(int bid) throws SQLException {
        String selectPname = "select branch_name from Branches"+" where"+" branch_id="+bid;
        Statement tstStmt = con.createStatement();

        ResultSet rs1 = tstStmt.executeQuery(selectPname);
        String name="";
        while (rs1.next()) {
            name+=rs1.getString(1);
        }
        return name;
    }
    public static String getEmployeeName(int eid) throws SQLException {
        String selectPname = "select emp_name from Employees"+" where"+" emp_id="+eid;
        Statement tstStmt = con.createStatement();

        ResultSet rs1 = tstStmt.executeQuery(selectPname);
        String name="";
        while (rs1.next()) {
            name+=rs1.getString(1);
        }
        return name;
    }

    public static String getTestNR(int ssn) throws SQLException {
        String selectPname = "select test_name,result_range from medical_tests"+" where"+" TSN="+ssn;
        Statement tstStmt = con.createStatement();

        ResultSet rs1 = tstStmt.executeQuery(selectPname);
        String name="";
        while (rs1.next()) {
            name+=rs1.getString(1)+" "+rs1.getString(2);
        }
        return name;
    }

    //Start Of Queries __________________________________________________________________________________

    public static int noOfVisitsFromDoc(int doctorID) throws SQLException {
        String c="select count(*) from Visits where doctor_id="+doctorID+";";
        Statement stmt=con.createStatement();
        ResultSet rs1=stmt.executeQuery(c);
        int count=0;
        while (rs1.next()){
            count=rs1.getInt(1);
        }

        return count;
    }

    public static String lastVisitByDoc(int doctorId) throws SQLException {
        String sqlQuery = "select V.date_time from Visits V, Doctor D where D.doctor_id=" + doctorId
                + " and D.doctor_id = V.doctor_id";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);

        rs.next();
        String visit = rs.getString(1);

        stmt.close();
        rs.close();
        return visit;
    }

    public static String lastVisitByPatient(int patientId) throws SQLException {
        String lastVisit = "";
        String sqlQuery = "select V.date_time, V.visit_id from Visits V, Patient P where P.patient_id=" + patientId
                + " and P.patient_id = V.patient_id";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sqlQuery);

        if (rs.next())
            lastVisit = rs.getString(1) + "," + rs.getString(2);
        // date_time,visit_id
        stmt.close();
        rs.close();
        return lastVisit;
    }
    public static double branchTotalCash(int branchID, Date startDate, Date endDate) throws SQLException {
        String totalCash="select sum(v.charge_price) from Visits v";

        if (branchID > 0)
            totalCash += " where v.branch_id=" + branchID;
        if (startDate != null && endDate != null) {
            totalCash += " where v.date_time >= '" + startDate + "' and v.date_time<='" + endDate + " 23:59:59'";
        }
        Statement stmt= con.createStatement();
        ResultSet rs1= stmt.executeQuery(totalCash);
        double sum=0;
        while(rs1.next()){
            sum=rs1.getDouble(1);
        }
        return sum;
    }

    public static double avgTestResult(int testID) throws SQLException {
        String avg="select avg(vt.result) from medical_tests mt,visit_tests_results vt  where vt.TSN=mt.TSN and vt.TSN="+testID+";";
        Statement stmt= con.createStatement();
        ResultSet rs1= stmt.executeQuery(avg);
        double avr=0;
        while(rs1.next()){
            avr=rs1.getDouble(1);
        }
        return avr;
    }

    public static double avgChargePrice() throws SQLException {
        String avg="select avg(v.charge_price) from Visits v;";
        Statement stmt= con.createStatement();
        ResultSet rs1= stmt.executeQuery(avg);
        double avr=0;
        while(rs1.next()){
            avr=rs1.getDouble(1);
        }
        return avr;
    }

    public static void topTests(List<String> sortedlist, Date startDate, Date endDate) throws SQLException {
        String sort="select vt.TSN,mt.test_name,count(*) as Occurences from medical_tests mt,visit_tests_results vt, Visits V\n" +
                "where vt.TSN=mt.TSN and vt.visit_id = V.visit_id and V.date_time>='" + startDate + "' and V.date_time<='" + endDate + " 23:59:59'"
                + " group by mt.TSN order by count(*) desc;";
        System.out.println(sort);
        String plsWork = "SET sql_mode = '';";
        Statement stmt= con.createStatement();
        Statement plsWorkStmt = con.createStatement();
        plsWorkStmt.executeUpdate(plsWork);
        ResultSet rs1=stmt.executeQuery(sort);
        while(rs1.next()){
            String s=rs1.getString(2)+","+rs1.getInt(3);
            sortedlist.add(s);
        }
        stmt.close();
        rs1.close();
        plsWorkStmt.close();
    }


    public static double percOfUnusedTests() throws SQLException {
        String unusedTest="select count(*) from medical_tests mt where mt.TSN NOT IN "+"(select mt.TSN from medical_tests mt,visit_tests_results vt where mt.TSN=vt.TSN group by mt.TSN);";
        String allTests="select count(*) from medical_tests mt;";

        Statement stmt= con.createStatement();
        Statement stmt2=con.createStatement();

        double count=0;
        double total=0;

        ResultSet rs1= stmt.executeQuery(unusedTest);
        ResultSet rs2= stmt2.executeQuery(allTests);

        while(rs1.next()){
            count=rs1.getDouble(1);
        }

        while(rs2.next()){
            total=rs2.getDouble(1);
        }

        return count/total*100;
    }


    public static double branchTotalCash(int branchID) throws SQLException {
        String totalCash="select sum(v.charge_price) from Visits v";

        if (branchID > 0)
            totalCash += " and v.branch_id=" + branchID;
        Statement stmt= con.createStatement();
        ResultSet rs1= stmt.executeQuery(totalCash);
        double sum=0;
        while(rs1.next()){
            sum=rs1.getDouble(1);
        }
        return sum;
    }

    public static int noOfEmployeesInBranch(int branchID) throws SQLException {
        String visitCount="select count(*) from Employees e,Branches b where e.branch_id=b.branch_id and b.branch_id="+branchID+";";
        Statement stmt= con.createStatement();
        ResultSet rs1= stmt.executeQuery(visitCount);
        int count=0;
        while(rs1.next()){
            count=rs1.getInt(1);
        }
        return count;
    }


    public static int noOfVisitsInBranch(int branchID) throws SQLException {
        String visitCount="select count(*) from Visits v,Branches b where v.branch_id=b.branch_id and b.branch_id="+branchID+";";
        Statement stmt= con.createStatement();
        ResultSet rs1= stmt.executeQuery(visitCount);
        int count=0;
        while(rs1.next()){
            count=rs1.getInt(1);
        }
        return count;
    }

    public static double percOfFemaleEmployees() throws SQLException {
        String femaleCount="select count(*) from Employees where gender='Female' group by gender;";
        String allGender="select count(*) from Employees;";

        Statement stmt = con.createStatement();
        Statement stmt2 = con.createStatement();


        ResultSet rs1 = stmt.executeQuery(femaleCount);
        ResultSet rs2 = stmt2.executeQuery(allGender);
        double fc=0;
        double ac=0;

        while(rs1.next()){
            fc=rs1.getDouble(1);
        }

        while(rs2.next()){
            ac=rs2.getDouble(1);
        }

        return fc/ac*100;
    }

    public static double percOfMaleEmployees() throws SQLException {
        String maleCount = "select count(*) from Employees where gender='Male' group by gender;";
        String allGender = "select count(*) from Employees;";

        Statement stmt = con.createStatement();
        Statement stmt2 = con.createStatement();

        ResultSet rs1 = stmt.executeQuery(maleCount);
        ResultSet rs2 = stmt2.executeQuery(allGender);
        double mc = 0;
        double ac = 0;

        while (rs1.next()) {
            mc = rs1.getDouble(1);
        }

        while (rs2.next()) {
            ac = rs2.getDouble(1);
        }

        return mc / ac * 100;
    }



    public static double percOfFemalePatients() throws SQLException {
        String femaleCount="select count(*) from Patient where gender='Female' group by gender;";
        String allGender="select count(*) from Patient;";

        Statement stmt = con.createStatement();
        Statement stmt2 = con.createStatement();


        ResultSet rs1 = stmt.executeQuery(femaleCount);
        ResultSet rs2 = stmt2.executeQuery(allGender);
        double fc=0;
        double ac=0;

        while(rs1.next()){
            fc=rs1.getDouble(1);
        }

        while(rs2.next()){
            ac=rs2.getDouble(1);
        }

        return fc/ac*100;
    }

    public static double percOfMalePatients() throws SQLException {
        String maleCount = "select count(*) from Patient where gender='Male' group by gender;";
        String allGender = "select count(*) from Patient;";

        Statement stmt = con.createStatement();
        Statement stmt2 = con.createStatement();

        ResultSet rs1 = stmt.executeQuery(maleCount);
        ResultSet rs2 = stmt2.executeQuery(allGender);
        double mc = 0;
        double ac = 0;

        while (rs1.next()) {
            mc = rs1.getDouble(1);
        }

        while (rs2.next()) {
            ac = rs2.getDouble(1);
        }

        return mc / ac * 100;
    }

    public static double avgEmpSalary() throws SQLException {
        String avgSal= "select avg(salary) from Employees;";

        Statement stmt = con.createStatement();

        ResultSet rs1 = stmt.executeQuery(avgSal);

        double avgSalary=0;
        while(rs1.next())
        {
            avgSalary=rs1.getDouble(1);
        }
        return avgSalary;
    }

    public static void fillTests(ChoiceBox cbox) throws SQLException {
        String selectTests= "select test_name from medical_tests;";

        Statement stmt = con.createStatement();

        ResultSet rs1 = stmt.executeQuery(selectTests);

        while(rs1.next())
        {
            cbox.getItems().add(rs1.getString(1));
        }
    }

    public static void fillBranches(ChoiceBox cbox) throws SQLException {
        String selectTests= "select branch_name from branches;";
        Statement stmt = con.createStatement();
        ResultSet rs1 = stmt.executeQuery(selectTests);
        while(rs1.next())
        {
            cbox.getItems().add(rs1.getString(1));
        }
    }

    public static int getIdOfTest(String testName) throws SQLException {
        String getID="select TSN from medical_tests where test_name='"+testName+"';";
        Statement stmt= con.createStatement();
        ResultSet rs2 =stmt.executeQuery(getID);
        int id=0;
        while(rs2.next())
        {
            id=rs2.getInt(1);
        }
        return id;
    }

    public static boolean hasDiscount(int patient_id) throws SQLException {
        String hasInsurance="Select hasInsurance from Patient where patient_id="+patient_id+";";
        Statement stmt= con.createStatement();
        ResultSet rs1=stmt.executeQuery(hasInsurance);

        int status=0;

        while(rs1.next())
            status=rs1.getInt(1);

        if(status==0)
            return false;
        else
            return true;
    }

    public static int getIdOfBranch(String branchName) throws SQLException {
        System.out.println(branchName);
        String getID="select branch_id from branches where branch_name='"+branchName+"';";
        Statement stmt= con.createStatement();
        ResultSet rs2 =stmt.executeQuery(getID);
        int id=0;
        while(rs2.next())
        {
            id=rs2.getInt(1);
        }

        return id;
    }

    public static double getDoctorDiscount(int doctor_id) throws SQLException {
        String getDiscount = "select d.discount from Doctor d where d.doctor_id ="+doctor_id+";";
        Statement stmt = con.createStatement();
        ResultSet rs1 = stmt.executeQuery(getDiscount);
        double discount = 0;
        while (rs1.next()){
            System.out.println("Result Set"+rs1.getDouble(1));
            discount = (1 - rs1.getDouble(1));
        }
        return discount;
    }

    //End of Queries  __________________________________________________________________________________
    public static void generatePDF(List<String> strings, List<String> imagePaths, String pdfFileName) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFileName));
            document.open();

            for (String imagePath : imagePaths) {
                Image image = Image.getInstance(imagePath);
                image.setAlignment(Element.ALIGN_CENTER);
                image.setCompressionLevel(2);
                document.add(image);
            }

            for (String str : strings) {
                document.add(new Paragraph(str));
            }

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void setProcessed(int visit_id) throws SQLException {
        String setTrue="update Visits set process_status=True where visit_id="+visit_id+";";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(setTrue);
    }
}

class DBConn {
    private Connection con;
    private String dbURL;
    private String dbUsername;
    private String dbPassword;
    private String URL;
    private String port;
    private String dbName;

    DBConn(String URL, String port, String dbName, String dbUsername, String dbPassword) {
        this.URL = URL;
        this.port = port;
        this.dbName = dbName;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }
    public Connection connectDB() throws ClassNotFoundException, SQLException {
        dbURL = "jdbc:mysql://" + URL + ":" + port + "/" + dbName + "?verifyServerCertificate=false";
        System.out.println(dbURL);

        Properties p = new Properties();
        p.setProperty("user", dbUsername);
        p.setProperty("password", dbPassword);
        p.setProperty("useSSL", "false");
        p.setProperty("autoReconnect", "true");

        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection (dbURL, p);
        //new com.mysql.jdbc.Driver();
        //con = DriverManager.getConnection(dbURL,p);

        return con;
    }
}