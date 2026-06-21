package com.DAO;

import java.sql.Date;

import com.DTO.CuotaDTO;
import com.DTO.DetalleVentaDTO;
import com.DTO.MovimientosDTO;
import com.DTO.VentaDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class VentaDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("SDDGPU");

    private ProductosDAO prod = new ProductosDAO();

    public boolean insertarVenta(VentaDTO venta){
        EntityManager em = emf.createEntityManager();
        String sql = """
                INSERT INTO ventas (
                                    id_cliente,
                                    id_usuario,
                                    id_venta_origen,
                                    serie_correlativa,
                                    tipo_comprobante,
                                    fecha_emision,
                                    id_estado_venta,
                                    subtotal,
                                    descuento_global,
                                    total)
                    OUTPUT INSERTED id_venta
                    VALUES (?1,?2,?3,?4,?5,GETDATE(),?6,?7,?8,?9)  
                
                """;
        try {

            em.getTransaction().begin();
            Integer idVentaReferencia = null;
            
            if(venta.getId_venta_referencia() != null){
                idVentaReferencia = venta.getId_venta_referencia();
            }

            Number idGenerado = (Number) em.createNativeQuery(sql)
                .setParameter(1, venta.getCliente().getIdEntidad())
                .setParameter(2, venta.getUsuario().getIdEntidad())
                .setParameter(3, idVentaReferencia)
                .setParameter(4, venta.getSerie_correlativa())
                .setParameter(5, venta.getTipo_comprobante())
                .setParameter(6, venta.getEstado().getIdEstado())
                .setParameter(7, venta.getSubTotal())
                .setParameter(8, venta.getDescuento_global())
                .setParameter(9, venta.getTotal())
                .getSingleResult();
            
            int idVenta = idGenerado.intValue();

            if(venta.getDetalle() != null && !venta.getDetalle().isEmpty()){
                for(DetalleVentaDTO detalle : venta.getDetalle()){
                    if(detalle.getVenta() == null){
                        detalle.setVenta(new VentaDTO());
                    }

                    detalle.getVenta().setIdVenta(idVenta);
                    boolean insertado = insertarDetalle(em, detalle);
                    
                    if(!insertado){
                        throw new Exception("Error al insertar un producto en la venta");
                    }

                    MovimientosDTO movimiento = new MovimientosDTO();
                    movimiento.setIdProducto(detalle.getProducto().getId_producto());
                    movimiento.setIdLote(detalle.getLote().getId_lote());
                    movimiento.setCantidad(detalle.getCantidad());
                    movimiento.setIdTipoMovimiento(2);
                    movimiento.setReferencia(" Venta " + venta.getTipo_comprobante() + " " + venta.getSerie_correlativa());
                    
                    boolean stockActualizado = prod.procesarMovimiento(em, movimiento);
                    if(!stockActualizado){
                        throw new Exception("Error al actualizar un stock del producto");
                    }

                    prod.movimientoInventario(em, movimiento);
                    
                }
            }

            if(venta.getCuotas() != null && !venta.getCuotas().isEmpty()){
                    for(CuotaDTO cuota : venta.getCuotas()){
                        if(cuota.getVenta() == null){
                            cuota.setVenta(new VentaDTO());
                        }

                        cuota.getVenta().setIdVenta(idVenta);
                        boolean generarCuota = insertarCuota(em, cuota);
                        if(!generarCuota){
                            throw new Exception("Error al generar el cronograma de cuotas de la venta.");
                        }
                    }

            }

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        }finally {
            em.close();
        }  
    }


    public boolean insertarDetalle(EntityManager em, DetalleVentaDTO detalle){
         try {
             String sql = """
                             INSERT INTO detalle_ventas(
                                                     id_venta,
                                                         id_producto,
                                                     id_lote,
                                                       cantidad,
                                                     precio_unitario,
                                                     descuento_producto,
                                                     sub_total)
                            VALUES (?1,?2,?3,?4,?5,?6,?7)
                     """;
             em.createNativeQuery(sql)
                 .setParameter(1, detalle.getVenta().getIdVenta())
                 .setParameter(2, detalle.getProducto().getId_producto())
                 .setParameter(3, detalle.getLote().getId_lote())
                 .setParameter(4, detalle.getCantidad())
                 .setParameter(5, detalle.getPrecio_unitario())
                 .setParameter(6, detalle.getDescuento_prod())
                 .setParameter(7, detalle.getSub_total())
                 .executeUpdate();
                 ;
             return true;
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         }
    }

    public boolean insertarCuota(EntityManager em, CuotaDTO cuota){
        try {
            
            String sql = """
                    INSERT INTO cuotas (
                                        id_venta,
                                        numero_cuota,
                                        fecha_vencimiento,
                                        monto,
                                        id_estado_cuota)
                    VALUES (?1,?2,?3,?4,?5)
                    """;
            Date fechaVencimiento = new Date(cuota.getFechaVencimiento().getTime());
            em.createNativeQuery(sql)
                .setParameter(1, cuota.getVenta().getIdVenta())
                .setParameter(2, cuota.getNumeroCuota())
                .setParameter(3, fechaVencimiento)
                .setParameter(4, cuota.getMonto())
                .setParameter(5, 9)
                .executeUpdate();        

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
