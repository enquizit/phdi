package com.cdc.linkage.repository;

import com.cdc.linkage.entities.Algorithm;
import com.cdc.linkage.model.AlgorithmResultDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface AlgorithmRepository extends ReactiveCrudRepository<Algorithm, Long> {

  String query = """
      SELECT
                a.id AS algorithm_id,
                a.name AS algorithm_name,
                a.type AS algorithm_type,
              
                ap.name AS param_name,
                apm.parameter_value AS param_value,
              
                f1.name AS block_field_name,
                bf.transformation_type AS block_name,
                tt.name AS transformation_type,
              
                f2.name AS criteria_field_name,
                c.function_name AS function_name,
                c.threshold AS criteria_field_threshold
              
            FROM algorithm a
                -- Join with algorithm_parameter_mapping and algorithm_parameter for parameters
                LEFT JOIN algorithm_parameter_mapping apm ON a.id = apm.algorithm_id
                LEFT JOIN algorithm_parameter ap ON apm.parameter_id = ap.id
              
                -- Join with blocking_field, transformation_type, and field for blocking information
                LEFT JOIN blocking_field bf ON a.id = bf.algorithm_id
                LEFT JOIN transformation_type tt ON bf.transformation_type = tt.id
                LEFT JOIN field f1 ON f1.id = bf.field_id
              
                -- Join with criteria and field for criteria information
                LEFT JOIN criteria c ON a.id = c.algorithm_id
                LEFT JOIN field f2 ON f2.id = c.field_id
         
      WHERE a.id = :id
      """;

  @Query(query)
  Flux<AlgorithmResultDto> getAlgorithmDataArrayById(Long id);

}
