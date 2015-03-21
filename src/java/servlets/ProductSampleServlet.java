/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import credentials.Credentials;
import java.io.StringReader;
import java.sql.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 *
 * @author c0641048
 */
@Path("/Product")
public class ProductSampleServlet {

    
    @GET
    @Produces("application/json")
    public String doGet() {
        String str = getResults("SELECT * FROM product");
        return str;
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public String doGet(@PathParam("id") String id) {
        String str = getResults("SELECT * FROM product where productID = ?", id);
        return str;
    }
    
    
    //private String strg;



    private String getResults(String query, String... params) {
        StringBuilder sb = new StringBuilder();
        String theString = "";
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            List list = new LinkedList();
            while (rs.next()) {
                Map map = new LinkedHashMap();
                map.put("productID", rs.getInt("productID"));
                map.put("name", rs.getString("name"));
                map.put("description", rs.getString("description"));
                map.put("quantity", rs.getInt("quantity"));

                //list.add(map);
                theString = jsonob.build().toString();
                Object build = productArray.build();

            }
            //theString = JSONValue.toJSONString(list);
        } catch (SQLException ex) {
            Logger.getLogger(ProductSampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (params.length == 0) {
            theString = productArray.build().toString();
        }
            
        return theString;
    }


//    private int doUpdate(String query, String... params) {
//        int numChanges = 0;
//        try (Connection conn = Credentials.getConnection()) {
//            PreparedStatement pstmt = conn.prepareStatement(query);
//            for (int i = 1; i <= params.length; i++) {
//                pstmt.setString(i, params[i - 1]);
//            }
//            numChanges = pstmt.executeUpdate();
//        } catch (SQLException ex) {
//            Logger.getLogger(ProductSampleServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return numChanges;
  //  }
  //  private final ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

    @POST
    @Consumes("application/json")
    
    private void doPost(String strg) {
        JsonParser parser = Json.createParser(new StringReader(strg));
        Map<String, String> map = new HashMap<>();
        String name = "", value;
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    name = parser.getString();
                    break;
                case VALUE_STRING:

                    value = parser.getString();
                    map.put(name, value);
                    break;
                case VALUE_NUMBER:
                    value = Integer.toString(parser.getInt());
                    map.put(name, value);
                    break;
            }
        }
        System.out.println(map);
        String str1 = map.get("name");
        String description = map.get("description");
        String quantity = map.get("quantity");
        doUpdate("insert into product ( name, description, quantity) values ( ?, ?, ?)", str1, description, quantity);
    }
    
     private int doUpdate(String query, String... params) {
        int numChanges = 0;
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            numChanges = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProductSampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numChanges;
    }

     @DELETE
    @Path("{id}")
    public void doDelete(@PathParam("id") String id, String strg) {
        doUpdate("delete from product where productId = ?", id);
    }




    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public void doPut(@PathParam("id") String id, String strg) {
        JsonParser parser = Json.createParser(new StringReader(strg));
        Map<String, String> map = new HashMap<>();
        String name = "", value;
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case KEY_NAME:
                    name = parser.getString();
                    break;
                case VALUE_STRING:
                    value = parser.getString();
                    map.put(name, value);
                    break;
                case VALUE_NUMBER:
                    value = Integer.toString(parser.getInt());
                    map.put(name, value);
                    break;
            }
        }
        System.out.println(map);

        String str1 = map.get("name");
        String description = map.get("description");
        String quantity = map.get("quantity");
        doUpdate("update product set productId = ?, name = ?, description = ?, quantity = ? where productID = ?", id, str1, description, quantity, id);

    }


    private static class productArray {

        private static Object build() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public productArray() {
        }
    }

    private static class executorService {

        public executorService() {
        }
    }
}
