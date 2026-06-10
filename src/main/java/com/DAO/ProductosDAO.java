package com.DAO;

import java.util.ArrayList;
import java.util.List;

import com.DTO.ProductosDTO;
import com.DTO.CategoriasDTO;
import com.DTO.UnidadesDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

public class ProductosDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("SDDGPU");

    public int insertarProducto(ProductosDTO prod){
        EntityManager em = emf.createEntityManager();

        String sql = """
                INSERT INTO productos(id_categoria,
                                    id_unidad_medida,
                                    id_estado,
                                    codigo_barras,
                                    nombre_descripcion,
                                    precio_venta,
                                    precio_mayorista,
                                    precio_distribuidor,
                                    stock,
                                    stock_minimo,
                                    maneja_lote,
                                    imagen_url)
                OUTPUT INSERTED.id_producto
                VALUES (?1,?2,?3,?4,?5,?6,?7,?8,?9,?10,?11,?12)
                """;

        try {

            em.getTransaction().begin();

            Number idGenerado = (Number) em.createNativeQuery(sql)
                .setParameter(1, prod.getId_categoria())
                .setParameter(2, prod.getId_unidad_medida())
                .setParameter(3, 19)
                .setParameter(4, prod.getCodigo_barras())
                .setParameter(5, prod.getNombre_descripcion())
                .setParameter(6, prod.getPrecio_venta())
                .setParameter(7, prod.getPrecio_mayorista())
                .setParameter(8, prod.getPrecio_distribuidor())
                .setParameter(9, prod.getStock())
                .setParameter(10, prod.getStock_minimo())
                .setParameter(11, prod.isManeja_lote() )
                .setParameter(12, prod.getImagen_url())
                .getSingleResult();
            
            int id = idGenerado.intValue();

            String codigoFinal = (prod.getCodigo_unico() == null || prod.getCodigo_unico().trim().isEmpty()) 
                             ? "P00" + id 
                             : prod.getCodigo_unico();
            
            String sqlUpdate = "UPDATE productos SET codigo_unico = ?1 WHERE id_producto = ?2";
            em.createNativeQuery(sqlUpdate)
                    .setParameter(1, codigoFinal)
                    .setParameter(2, id)
                    .executeUpdate();


            em.getTransaction().commit();
            return idGenerado.intValue();

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return -1;
        }finally {
            em.close();
        }  
    }

    @SuppressWarnings("unchecked")

    public List<ProductosDTO> mostrarProductos(){
        EntityManager em = emf.createEntityManager();
        List<ProductosDTO> prodList = new ArrayList<>();

        String sql = """
                SELECT P.id_producto,
                    P.codigo_barras,
                    P.codigo_unico,
                    P.nombre_descripcion,
                    C.nombre,
                    P.precio_venta,
                    P.stock,
                    M.nombre,
                    P.maneja_lote
                FROM productos AS P
                INNER JOIN medidas AS M ON P.id_unidad_medida = M.id_medida
                INNER JOIN categorias AS C ON P.id_categoria = C.id_categoria
                """;
        try {
            Query query = em.createNativeQuery(sql);
            List<Object[]> result = query.getResultList();

            for(Object[] fila: result){
                ProductosDTO prod = new ProductosDTO();

                prod.setId_producto(((Number) fila[0]).intValue());
                prod.setCodigo_barras((String) fila[1]);
                prod.setCodigo_unico((String) fila[2]);
                prod.setNombre_descripcion((String) fila[3]);
                prod.setCategoriaNombre((String) fila[4]);
                prod.setPrecio_venta(((Number) fila[5]).doubleValue());
                prod.setMedidaNombre((String) fila[6]);
                prod.setManeja_lote((Boolean) fila[7]);

                prodList.add(prod);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return prodList;
    }

    public boolean insertarCategoria(CategoriasDTO categoria){
        EntityManager em = emf.createEntityManager();
        String sql = """
                INSERT INTO categorias (nombre,
                                        descripcion,
                                        id_estado)
                VALUES (?1,?2,?3)
                """;
    
        try {
            em.getTransaction().begin();
            em.createNativeQuery(sql)
                .setParameter(1, categoria.getNombreCategoria())
                .setParameter(2, categoria.getDescripcion())
                .setParameter(3, 1)
                .executeUpdate();
            
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

    public boolean insertarMedidad(UnidadesDTO medida){
        EntityManager em = emf.createEntityManager();
        String sql = """
                INSERT INTO medidas (nombre,
                                    id_estado)
                VALUES(?1,?2)
                """;
        try {

            em.getTransaction().begin();
            em.createNativeQuery(sql)
                .setParameter(1, medida.getNombre())
                .setParameter(2, 1)
                .executeUpdate();

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

    @SuppressWarnings("unchecked")
    public List<UnidadesDTO> mostrarMedidas(){
        EntityManager em = emf.createEntityManager();
        List<UnidadesDTO> listaUnidades = new ArrayList<>();

        String sql = """
                SELECT id_medida,
                        nombre
                FROM medidas
                WHERE id_estado = 1
                """;

        try {
            
            Query query = em.createNativeQuery(sql);
            List<Object[]> resultado = query.getResultList();
            for(Object[] fila : resultado){

                UnidadesDTO medidas = new UnidadesDTO();
                medidas.setId_medida(((Number)fila[0]).intValue());
                medidas.setNombre((String)fila[1]);

                listaUnidades.add(medidas);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return listaUnidades;
    }

    @SuppressWarnings("unchecked")
    public List<CategoriasDTO> mostrarCategorias(){
        EntityManager em = emf.createEntityManager();
        List<CategoriasDTO> listaCategorias = new ArrayList<>();

        String sql = """
                SELECT id_categoria,
                        nombre
                FROM categorias
                WHERE id_estado = 1
                """;

        try {
            
            Query query = em.createNamedQuery(sql);
            List<Object[]> resultado = query.getResultList();
            for(Object[]fila : resultado){
                CategoriasDTO cat = new CategoriasDTO();
                cat.setId_categoria(((Number)fila[0]).intValue());
                cat.setNombreCategoria((String)fila[1]);
                listaCategorias.add(cat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return listaCategorias;
    }



}
