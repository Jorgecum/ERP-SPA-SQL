package com.DTO;

public class UsuariosDTO {
    private int idUsuario;
    private String usuario;
    private String paswordHash;
    private int idEntidad;
    private int idRol;
    private int idEstado;
    private String nombreEntidad;
    private String nombreRol;
    private String nombreEstado;



    public UsuariosDTO(){

    }

    public int getIdUsuario(){
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario){
        this.idUsuario = idUsuario;
    }

    public String getUsuario(){
        return usuario;
    }

    public void setUsuario(String usuario){
        this.usuario = usuario;
    }

    public String getPaswordHash(){
        return paswordHash;
    }

    public void setPaswordHash(String paswordHash){
        this.paswordHash = paswordHash;
    }

    public int getIdEntidad(){
        return idEntidad;
    }

    public void setIdEntidad(int idEntidad){
        this.idEntidad = idEntidad;
    }

    public int getIdRol(){
        return idRol;
    }

    public void setIdRol(int idRol){
        this.idRol = idRol;
    }

    public int getIdEstado(){
        return idEstado;
    }

    public void setIdEstado(int idEstado){
        this.idEstado = idEstado;
    }

    public String getNombreEntidad(){
        return nombreEntidad;
    }

    public void setNombreEntidad(String nombreEntidad){
        this.nombreEntidad = nombreEntidad;
    }

    public String getNombreRol(){
        return nombreRol;
    }

    public void setNombreRol(String nombreRol){
        this.nombreRol = nombreRol;
    }

    public String getNombreEstado(){
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado){
        this.nombreEstado = nombreEstado;
    }       


    
}
