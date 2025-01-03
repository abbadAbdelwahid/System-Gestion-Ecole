package gestion.ecole.dao;

import java.util.List;

public interface CRUD<T> {
    boolean insert(T obj);
    T get(int id);
    List<T> getAll();
    boolean update(T obj);
    boolean delete(int id);
}
