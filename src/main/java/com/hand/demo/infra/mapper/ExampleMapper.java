package com.hand.demo.infra.mapper;

import com.hand.demo.api.dto.PrefixDTO;
import io.choerodon.mybatis.common.BaseMapper;
import com.hand.demo.domain.entity.Example;

import java.util.List;

/**
 * Mapper
 */
public interface ExampleMapper extends BaseMapper<Example> {
    List<PrefixDTO> selectList(PrefixDTO prefixDTO);

}
