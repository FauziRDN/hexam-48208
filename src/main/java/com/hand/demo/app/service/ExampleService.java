package com.hand.demo.app.service;

import com.hand.demo.api.dto.PrefixDTO;

import java.util.List;

/**
 * ExampleService
 */
public interface ExampleService {

    List<PrefixDTO> selectList(PrefixDTO prefixDTO);

}
