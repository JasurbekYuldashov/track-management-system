package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.model.Action;
import uz.binart.trackmanagementsystem.repository.type.ActionTypeRepository;
import uz.binart.trackmanagementsystem.repository.ActionRepository;
import uz.binart.trackmanagementsystem.service.ActionService;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {

    private final ActionTypeRepository actionTypeRepository;
    private final ActionRepository actionRepository;

    public void captureCreate(Object object, String tableName, Long madeById){
        Action action = initAction(madeById, tableName);
        action.setActionTypeId(actionTypeRepository.getByName("create").getId());
        action.setInitialObject(object);
        action.setResultObject(null);
        actionRepository.save(action);
    }

    public void captureRead(Object object, String tableName, Long madeById){
        Action action = initAction(madeById, tableName);
        action.setActionTypeId(actionTypeRepository.getByName("read").getId());
        action.setInitialObject(object);
        action.setResultObject(null);
        actionRepository.save(action);
    }

    public void captureUpdate(Object initialObject, Object resultObject, String tableName, Long madeById){
        Action action = initAction(madeById, tableName);
        action.setActionTypeId(actionTypeRepository.getByName("update").getId());
        action.setInitialObject(initialObject);
        action.setResultObject(resultObject);
        actionRepository.save(action);
    }

    public void captureDelete(Object deletingObject, String tableName, Long madeById){
        Action action = initAction(madeById, tableName);
        action.setActionTypeId(actionTypeRepository.getByName("delete").getId());
        action.setInitialObject(deletingObject);
        actionRepository.save(action);
    }

    public Page<Action> findFiltered(Pageable pageable){
        return actionRepository.findAll(pageable);
    }

    private Action initAction(Long madeById, String tableName){
        Action action = new Action();
        action.setActionTime(new Date());
        action.setMadeById(madeById);
        action.setTableName(tableName);
        return action;
    }

}
