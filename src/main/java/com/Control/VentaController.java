package com.Control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.DAO.*;
import com.DTO.*;

import com.google.gson.Gson;

@WebServlet("/VentaController")
public class VentaController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    VentaDAO ventaDAO = new VentaDAO();
    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if(action == null || action.trim().isEmpty()){
            action = "insertar";
        }   

        try {
            PrintWriter out = null;

            if(action.equals("insertar")){
                out = response.getWriter();

                BufferedReader reader = request.getReader();

                VentaDTO venta = gson.fromJson(reader, VentaDTO.class);

                double totalCalculado = 0.0;
                double descuento_producto = 0.0;
                double descuento_global = 0.0;

                if(venta.getDetalle() != null){
                    for(DetalleVentaDTO detalle : venta.getDetalle()){
                        descuento_producto = detalle.getDescuento_prod();

                        double subTotalPd = (detalle.getCantidad() * detalle.getPrecio_unitario());
                        subTotalPd -= (subTotalPd * descuento_producto);

                        totalCalculado += subTotalPd;
                    }
                }

                totalCalculado -= totalCalculado*descuento_global;

                if(Math.abs(venta.getTotal() - totalCalculado) > 0.01){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"error\": \"Validación fallida: El total de la venta no coincide con la suma de sus detalles.\"}");
                    return;
                }

                if(venta.getCuotas() != null){
                    
                }

                boolean insertarVenta = ventaDAO.insertarVenta(venta);
                if(insertarVenta){
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print("{\"success\": true, \"message\": \"Venta registrada con éxito en Solda-Master\"}");
                }else{
                         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("{\"success\": false, \"error\": \"Error interno al insertar venta en SQL Server\"}");
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
}