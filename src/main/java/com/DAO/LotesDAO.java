package com.DAO;

import java.util.*;

import com.DTO.CertificadosDTO;
import com.DTO.LotesDTO;
import com.DTO.ProductosDTO;

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
            Integer idCertificado = null;
            if(lote.getCerti() != null){
                idCertificado = lote.getCerti().getId_certifiado();
            }

            em.createNativeQuery(sql)
                .setParameter(1, lote.getProducto().getId_producto())
                .setParameter(2, idCertificado)
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

                CertificadosDTO certi = new CertificadosDTO();
                certi.setId_certificado(((Number)fila[1]).intValue());
                lote.setCerti(certi);

                ProductosDTO prod = new ProductosDTO();
                prod.setId_producto(((Number)fila[2]).intValue());
                lote.setProducto(prod);

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
