package com.DAO;

import java.util.*;

import com.DTO.LotesDTO;

import jakarta.persistence.*;


public class LotesDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("SDDGPU");

    public boolean insertarLotes(LotesDTO lote){
        EntityManager em = emf.createEntityManager();
        String sql = """
                INSERT INTO lotes (id_producto,
                                    id_certificado,
                                    numero_lote,
                                    stock_lote)
                VALUES  (?1,?2,?3,?4)
                """;

        try {

            em.getTransaction().begin();
            em.createNativeQuery(sql)
                .setParameter(1, lote.getId_productos())
                .setParameter(2, lote.getId_certificado())
                .setParameter(3, lote.getNumero_lote())
                .setParameter(4, lote.getStock_lote())
                .executeUpdate();

            em.getTransaction().commit();
            return true;
            
        } catch (Exception e) {
             e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
            
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<LotesDTO> mostrarLotes(){
        EntityManager em = emf.createEntityManager();
        List<LotesDTO> listaLotes = new ArrayList<>();

        String sql = """
                SELECT id_lote, 
                    id_certificado,
                    id_producto,
                    numero_lote,
                    fecha_entrada,
                    stock_lote
                FROM lotes
                """;

        try {
            
            Query query = em.createNativeQuery(sql);
            List<Object[]> resultado = query.getResultList();

            for(Object[] fila : resultado){
                LotesDTO lote = new LotesDTO();
                lote.setId_lote(((Number)fila[0]).intValue());
                lote.setId_certificado(((Number)fila[1]).intValue());
                lote.setId_productos(((Number)fila[2]).intValue());
                lote.setNumero_lote((String)fila[3]);
                lote.setFecha_entrada((Date)fila[4]);
                lote.setStock_lote(((Number)fila[5]).intValue());

                listaLotes.add(lote);
            }


        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            em.close();
        }
        
        return listaLotes;
    }

}
