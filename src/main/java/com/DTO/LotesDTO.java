package com.DTO;
import java.util.Date;

public class LotesDTO {
    private int id_lote;
    private int id_productos;
    private int id_certificado;
    private String numero_lote;
    private Date fecha_entrada;
    private int stock_lote;
    
    public LotesDTO() {
    }

    public int getId_lote() {
        return id_lote;
    }

    public void setId_lote(int id_lote) {
        this.id_lote = id_lote;
    }

    public int getId_productos() {
        return id_productos;
    }

    public void setId_productos(int id_productos) {
        this.id_productos = id_productos;
    }

    public int getId_certificado() {
        return id_certificado;
    }

    public void setId_certificado(int id_certificado) {
        this.id_certificado = id_certificado;
    }

    public String getNumero_lote() {
        return numero_lote;
    }

    public void setNumero_lote(String numero_lote) {
        this.numero_lote = numero_lote;
    }

    public Date getFecha_entrada() {
        return fecha_entrada;
    }

    public void setFecha_entrada(Date fecha_entrada) {
        this.fecha_entrada = fecha_entrada;
    }

    public int getStock_lote() {
        return stock_lote;
    }

    public void setStock_lote(int stock_lote) {
        this.stock_lote = stock_lote;
    }

    
    
}
