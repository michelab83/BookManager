package com.michelab.common.dto.response;

import java.util.Map;

public class UserBookResponseDto extends BaseResponse {


    public UserBookResponseDto(Map<String, Object> map) {
        super();
    }

    public UserBookResponseDto(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
    Map<Long, BookResponseDto> bookResponseDtoMap;

    public Map<Long, BookResponseDto> getBookResponseDtoMap() {
        return bookResponseDtoMap;
    }

    public void setBookResponseDtoMap(Map<Long, BookResponseDto> bookResponseDtoMap) {
        this.bookResponseDtoMap = bookResponseDtoMap;
    }
}
