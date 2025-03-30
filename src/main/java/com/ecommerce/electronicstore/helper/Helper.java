package com.ecommerce.electronicstore.helper;

import com.ecommerce.electronicstore.dto.PageableResponse;
import com.ecommerce.electronicstore.dto.UserDto;
import com.ecommerce.electronicstore.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class Helper {
    public static <U,V>PageableResponse<V>getPageableResponse(Page<U> page,Class<V>type){
        List<U> entity=page.getContent();
        List<V> userDto = entity.stream()
                .map(object -> new ModelMapper().map(object,type)) // Correct mapping
                .collect(Collectors.toList());
        PageableResponse<V> response=new PageableResponse<>();
        response.setContent(userDto);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLastPage(page.isLast());
        return response;
    }
}
