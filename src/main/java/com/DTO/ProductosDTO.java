package com.DTO;

public class ProductosDTO {
    private int id_producto;
    private int id_categoria;
    private int id_unidad_medida;
    private int id_estado;
    private String codigo_barras;
    private String nombre_descripcion;
    private Double precio_venta;
    private Double precio_mayorista;
    private Double precio_distribuidor;
    private int stock;
    private int stock_minimo;
    private boolean maneja_lote;
    private String  imagen_url;
    private String codigo_unico;
    private String categoriaNombre;
    private String medidaNombre;
    

    public ProductosDTO() {
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public int getId_unidad_medida() {
        return id_unidad_medida;
    }

    public void setId_unidad_medida(int id_unidad_medida) {
        this.id_unidad_medida = id_unidad_medida;
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public String getCodigo_barras() {
        return codigo_barras;
    }

    public void setCodigo_barras(String codigo_barras) {
        this.codigo_barras = codigo_barras;
    }

    public String getNombre_descripcion() {
        return nombre_descripcion;
    }

    public void setNombre_descripcion(String nombre_descripcion) {
        this.nombre_descripcion = nombre_descripcion;
    }

    public Double getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(Double precio_venta) {
        this.precio_venta = precio_venta;
    }

    public Double getPrecio_mayorista() {
        return precio_mayorista;
    }

    public void setPrecio_mayorista(Double precio_mayorista) {
        this.precio_mayorista = precio_mayorista;
    }

    public Double getPrecio_distribuidor() {
        return precio_distribuidor;
    }

    public void setPrecio_distribuidor(Double precio_distribuidor) {
        this.precio_distribuidor = precio_distribuidor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStock_minimo() {
        return stock_minimo;
    }

    public void setStock_minimo(int stock_minimo) {
        this.stock_minimo = stock_minimo;
    }

    public boolean isManeja_lote() {
        return maneja_lote;
    }

    public void setManeja_lote(boolean maneja_lote) {
        this.maneja_lote = maneja_lote;
    }

    public String getImagen_url() {
        return imagen_url;
    }
    
    public void setImagen_url(String imagen_url) {
        this.imagen_url = imagen_url;
    }

    public String getCodigo_unico() {
        return codigo_unico;
    }

    public void setCodigo_unico(String codigo_unico) {
        this.codigo_unico = codigo_unico;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getMedidaNombre() {
        return medidaNombre;
    }

    public void setMedidaNombre(String medidaNombre) {
        this.medidaNombre = medidaNombre;
    }

    
    
}
