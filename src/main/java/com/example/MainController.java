package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.bytecode.opencsv.CSVWriter;

@Controller
@EnableAutoConfiguration
public class MainController {

  @RequestMapping("/home")
  String home() {
      return "home";
  }
  @RequestMapping(value="/datepicker/", method = RequestMethod.POST)
  @ResponseBody
  String datesubmit(@RequestBody String body) {
    System.out.println("body => " + body);
    Map<String,String> records = parseParams(body);

    
    String apptDate = records.get("date");
    System.out.println("appDate"+apptDate);
  
    try {
      Connection connection = getConnection();
      dateRecord(connection, apptDate);
     
    } catch (Exception e) {
      e.printStackTrace();
      return "There was an error: " + e.getMessage();
    }

    return "This is date";
  }

  private Map<String,String> parseParams(String body) {
    String[] args = body.split("&");

    Map<String,String> records = new HashMap<String,String>();

    for (String arg : args) {
      String[] parts = arg.split("=");
      String key = parts[0];
      String val = parts.length > 1 ? URLDecoder.decode(parts[1]) : null;
      records.put(key, val);
    }

    return records;
  }
 
  private void dateRecord(Connection connection, String date) throws SQLException, IOException {
	String pathCurrentTime =Long.toString(new Date().getTime());
	String path = "D:\\record\\"+pathCurrentTime+".csv";
	CSVWriter writer = new CSVWriter(new FileWriter(path));
    PreparedStatement pstmt = connection.prepareStatement(
        "SELECT * FROM appointments WHERE date=?");
    pstmt.setString(1, date);
    ResultSet rs = pstmt.executeQuery();

    while (rs.next()) {
    	String id =rs.getString("invitee_id");
    	String dateRecord =rs.getString("date");
    	System.out.println(id+":"+dateRecord);
    	String value=id+","+dateRecord;
    	String [] country = value.split(",");
   	  	writer.writeNext(country);
    }
    writer.close();
    
  }

 

  private Connection getConnection() throws URISyntaxException, SQLException {
	  String dbUrl = "jdbc:mysql://localhost:3306/springbootdb";
      String username = "root";
      String password = "admin";
      return DriverManager.getConnection(dbUrl, username, password);
   
  }
  
  public void csvWrite(String value,String path) throws IOException{
	  
  }

}
