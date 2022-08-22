package uz.binart.trackmanagementsystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.binart.trackmanagementsystem.model.Action;

public interface ActionService {

    void captureCreate(Object object, String tableName, Long madeById);

    void captureRead(Object object, String tableName,  Long madeById);

    void captureUpdate(Object initialObject, Object resultObject, String tableName,  Long madeById);

    void captureDelete(Object deletingObject, String tableName, Long madeById);

    Page<Action> findFiltered(Pageable pageable);

}
