package com.Control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.DAO.CertificadosDAO;
import com.DAO.LotesDAO;
import com.DAO.ProductosDAO;
import com.DTO.CertificadosDTO;
import com.google.gson.Gson;;

@WebServlet("/InventarioController")
public class InventarioController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductosDAO prodDAO = new ProductosDAO();
    private LotesDAO loteDAO = new LotesDAO();
    private CertificadosDAO certiDAO = new CertificadosDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if(action == null){
            action = "insertar";
        }



        try (PrintWriter out = response.getWriter()) {

            BufferedReader reader = request.getReader();

            CertificadosDTO certi = gson.fromJson(reader, CertificadosDTO.class);

            switch (action) {
                case "insertar":

                    break;
            
                default:
                    break;
            }
            
        } catch (Exception e) {
             e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Estado 500
            try {
                response.getWriter().print("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }

    }


}

