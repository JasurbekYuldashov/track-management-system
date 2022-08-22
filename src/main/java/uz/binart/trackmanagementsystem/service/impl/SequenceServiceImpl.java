package uz.binart.trackmanagementsystem.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uz.binart.trackmanagementsystem.service.SequenceService;

@Service
@RequiredArgsConstructor
public class SequenceServiceImpl implements SequenceService {

    private final JdbcTemplate jdbcTemplate;

    public void updateSequence(String entityName){
        jdbcTemplate.execute(String.format("select setval('%s', (select max(id) from %s));", entityName + "_id_seq", entityName));
    }

}
