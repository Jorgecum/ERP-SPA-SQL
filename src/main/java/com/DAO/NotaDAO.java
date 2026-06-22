package com.DAO;

import com.DTO.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class NotaDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("SDDGPU");

    private ProductosDAO prod = new ProductosDAO();

    public boolean insertarNota(NotaDTO nota){
        EntityManager em = emf.createEntityManager();
        String sql = """
                INSERT INTO notas_credito (
                                            id_venta,
                                            serie_correlativa,
                                            fecha_emision,
                                            motivo,
                                            monto_total)
                OUTPUT INSERTED.id_orden
                VALUES(?1,?2,GETDATE(),?3,?4)
                """;

            try {
                
                em.getTransaction().begin();
                
                Number idGenerado = (Number) em.createNativeQuery(sql)
                    .setParameter(1, nota.getVenta().getIdVenta())
                    .setParameter(2, nota.getSerie_correlativa())
                    .setParameter(3, nota.getMotivo())
                    .setParameter(4, nota.getMonto_total())
                    .getSingleResult();
                
                int idNota = idGenerado.intValue();

                if(nota.getDetalle() != null && !nota.getDetalle().isEmpty()){
                    for(DetalleNotaDTO detalle : nota.getDetalle()){
                        if(detalle.getNota() == null){
                            detalle.setNota(new NotaDTO());
                        }

                        detalle.getNota().setId_nota(idNota);
                        boolean insertado = insertarDetalle(em, detalle);

                        if(!insertado){
                            throw new Exception("Error al insertar un detalle de nota");
                        }

                        MovimientosDTO movimiento = new MovimientosDTO();
                        movimiento.setIdProducto(detalle.getProducto().getId_producto());

                        Integer idLote = (detalle.getLote() != null) ? detalle.getLote().getId_lote() : null;
                        movimiento.setIdLote(idLote); 

                        movimiento.setCantidad(detalle.getCantidad());
                        movimiento.setIdMovimiento(1);
                        movimiento.setReferencia("Nota Credito " + nota.getSerie_correlativa() + " (Referencia " + nota.getVenta().getSerie_correlativa() + " )");

                        boolean stockActualizado = prod.procesarMovimiento(em, movimiento);

                        if(!stockActualizado){
                            throw new Exception("Error al actualizar un stock del producto");
                        }

                        prod.movimientoInventario(em, movimiento);
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

    public boolean insertarDetalle(EntityManager em, DetalleNotaDTO detalle){
        try {

            String sql = """
                    INSERT INTO detalle_nota_credito (
                                                        id_nota,
                                                        id_producto,
                                                        cantidad,
                                                        precio_unitario,
                                                        subtotal,
                                                        id_lote)
                    VALUES (?1,?2,?3,?4,?5,?6)
                    """;
            Integer idLote = (detalle.getLote() != null) ? detalle.getLote().getId_lote() : null;

            em.createNativeQuery(sql)
                .setParameter(1, detalle.getNota().getId_nota())
                .setParameter(2, detalle.getProducto().getId_producto())
                .setParameter(3, detalle.getCantidad())
                .setParameter(4, detalle.getPrecio_unitario())
                .setParameter(5, detalle.getSubtotal())
                .setParameter(6, idLote)
                .executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
