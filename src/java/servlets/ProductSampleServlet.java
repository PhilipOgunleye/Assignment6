/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import credentials.Credentials;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import static java.nio.file.Files.delete;
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
@WebServlet("/Product")
public class ProductSampleServlet extends HttpServlet {

    private String strg;



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

                list.add(map);

            }
            theString = JSONValue.toJSONString(list);
        } catch (SQLException ex) {
            Logger.getLogger(ProductSampleServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return theString.replace("},", "},\n");
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
    private final ExecutorService executorService = java.util.concurrent.Executors.newCachedThreadPool();

    @POST
    @Consumes(value = "application/json")
    public void doPost(@Suspended final AsyncResponse asyncResponse, final String str) {
        executorService.submit(() -> {
            doDoPost(str);
            asyncResponse.resume(javax.ws.rs.core.Response.ok().build());
        });
    }

    private void doDoPost(String strg) {
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

    @DELETE
    @Path("{id}")
    public void doDelete(@Suspended
            final AsyncResponse asyncResponse, @PathParam(value = "id")
            final String id, final String strg) {
        executorService.submit(() -> {
            doDoDelete(id, strg);
            asyncResponse.resume(javax.ws.rs.core.Response.ok().build());
        });
    }

    private void doDoDelete(@PathParam("id") String id, String strg) {
        doUpdate("delete from product where productId = ?", id);
    }

    /**
     *
     * @param asyncResponse
     * @param id
     */
    @GET
    @Path(value = "{id}")
    @Produces(value = "application/json")
    public void doGeta(@Suspended final AsyncResponse asyncResponse, @PathParam(value = "id") final String id) {
        executorService.submit(() -> {
            asyncResponse.resume(doDoGet(id));
        });
    }

    private String doDoGet(@PathParam("id") String id) {
        String strg = getResults("SELECT * FROM product where productID = ?", id);
        return strg;
    }

    @GET
    @Produces(value = "application/json")
    public void doGet(@Suspended final AsyncResponse asyncResponse) {
        executorService.submit(() -> {
            asyncResponse.resume(doDoGeta());
        });
    }

    private String doDoGeta() {
        String strg = getResults("SELECT * FROM product");
        return strg;
    }

    @PUT
    @Path(value = "{id}")
    @Consumes(value = "application/json")
    public void doPut(@Suspended final AsyncResponse asyncResponse, @PathParam(value = "id") final String id, final String strg) {
        executorService.submit(() -> {
            doDoPut(id, strg);
            asyncResponse.resume(javax.ws.rs.core.Response.ok().build());
        });
    }

    private void doDoPut(@PathParam("id") String id, String strg) {
        JsonParser parser;
        parser = Json.createParser(new StringReader(strg));
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
            }
        }
        System.out.println(map);
        String str1 = map.get("name");
        String description = map.get("description");
        String quantity = map.get("quantity");
        doUpdate("update product set productId = ?, name = ?, description = ?, quantity = ? where productID = ?", id, str1, description, quantity, id);
    }
}
