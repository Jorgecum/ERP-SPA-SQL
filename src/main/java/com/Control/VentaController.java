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
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/VentaController")
public class VentaController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    VentaDAO ventaDAO = new VentaDAO();
    Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if(action == null || action.trim().isEmpty()){
            action = "listarMetodos";
        }  
        PrintWriter out = response.getWriter();

        try {
            if(action.equals("listarMetodos")){
                List<MetodosPagoDTO> listaMetodosPagoDTOs = ventaDAO.listarMetodosDPago();
                out.print(gson.toJson(listaMetodosPagoDTOs));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\":\"Acción GET no válida en ventas\"}");
            }
        } catch (Exception e) {
           e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if (out == null) { 
                out = response.getWriter(); 
            }
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage() != null ? e.getMessage() : "Error interno en el doGet de ventas");
            out.print(gson.toJson(errorResponse));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if(action == null || action.trim().isEmpty()){
            action = "insertar";
        }   
        PrintWriter out = response.getWriter();
        try {
            

            if(action.equals("insertar")){

                BufferedReader reader = request.getReader();
                JsonObject jsonInput = gson.fromJson(reader, JsonObject.class); 

                VentaDTO venta = gson.fromJson(jsonInput.get("venta"), VentaDTO.class);

                double totalCalculado = 0.0;

                if(venta.getDetalle() != null){
                    for(DetalleVentaDTO detalle : venta.getDetalle()){
                        
                        int cantidad = (detalle.getCantidad() != null) ? detalle.getCantidad() : 0;
                        double precioUnitario = (detalle.getPrecio_unitario() != null) ? detalle.getPrecio_unitario() : 0.0;
                        double descuentoProd = (detalle.getDescuento_prod() != null) ? detalle.getDescuento_prod() : 0.0;

                        double subTotalPd = (cantidad * precioUnitario);
                        
                        subTotalPd -= (subTotalPd * descuentoProd);

                        totalCalculado += subTotalPd;
                    }
                }

                double descuentoGlobal = (venta.getDescuento_global() != null) ? venta.getDescuento_global() : 0.0;
                totalCalculado -= totalCalculado * descuentoGlobal;

                double igvCalculado = totalCalculado * 0.18;
                totalCalculado = totalCalculado + igvCalculado;

                double totalVenta = (venta.getTotal() != null) ? venta.getTotal() : 0.0;

                if(Math.abs(totalVenta - totalCalculado) > 0.05){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"error\": \"Validación fallida: El total de la venta no coincide con la suma de sus detalles.\"}");
                    return;
                }

                double totalPagadoAcumulado = 0.0;
                if (venta.getPagos() != null) {
                    for (PagoDTO pago : venta.getPagos()) {
                        totalPagadoAcumulado += (pago.getMonto_total() != null) ? pago.getMonto_total() : 0.0;
                    }
                }

                if (venta.getCuotas() != null && !venta.getCuotas().isEmpty()) {
                    double sumaCuotas = 0.0;
                    for (CuotaDTO cuota : venta.getCuotas()) {
                        sumaCuotas += (cuota.getMonto() != null) ? cuota.getMonto() : 0.0;
                    }

                    
                    double saldoAFinanciar = totalCalculado - totalPagadoAcumulado;

                    
                    if (Math.abs(sumaCuotas - saldoAFinanciar) > 0.05) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"success\": false, \"error\": \"Validación de Crédito fallida: La suma de las cuotas (" + sumaCuotas + ") no coincide con el saldo financiado restante (" + saldoAFinanciar + ").\"}");
                        return;
                    }
                }

                boolean exito = ventaDAO.insertarVenta(venta);
                
                if(exito){
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print("{\"success\": true, \"message\": \"Venta y flujo financiero procesados con éxito en Solda-Master.\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"error\": \"Error interno en el servidor al guardar la transacción en SQL Server.\"}");
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"error\": \"Error crítico en el controlador de ventas: " + e.getMessage() + "\"}");
        
        } finally {

            out.flush();
            out.close();
        }
    }
}