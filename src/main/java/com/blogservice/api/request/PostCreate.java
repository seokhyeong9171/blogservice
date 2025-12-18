package com.blogservice.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class PostCreate {

    @NotBlank(message = "타이틀을 입력하세요.")
    public String title;

    @NotBlank(message = "컨텐츠을 입력해주세요.")
    public String content;

}
