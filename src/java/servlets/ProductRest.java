/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entities.Product;
import entities.ProductList;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author c0641048
 */
@Path("/product")
@RequestScoped
public class ProductRest {

    @Inject
    ProductList prodList;

    @GET
    @Produces("application/json")
    public Response getAll() {
        return Response.ok(prodList.toJson()).build();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getById(@PathParam("id") int id) {
        return Response.ok(prodList.get(id).toJSON()).build();
    }

    /**
     *
     * @param json
     * @return
     */
    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) {
        Response response;
        try {
            prodList.add(new Product(json));
            return Response.ok(prodList.get(json.getInt("productID")).toJSON()).build();
        } catch (Exception ex) {
            return Response.status(500).build();

        }
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response set(@PathParam("id") int id, JsonObject json) {

        try {
            Product p = new Product(json);
            prodList.set(id, p);
            return Response.ok("ProductID "+id+" has been updated").build();
        } catch (Exception ex) {
            return Response.status(500).build();

        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        try {
            prodList.remove(id);
            return Response.ok("ProductID "+id+" has been deleted").build();
        } catch (Exception ex) {
            return Response.status(500).build();
        }

    }

}

