package com.fqq.futurecommunity.web.test;

import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.exception.BaseException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fqq
 * @Date: 2026/3/6 - 03 - 06 - 17:11
 * @Description: com.fqq.community.web.test
 * @version: 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/queryUserById")
    public BaseResponse<User> queryUserById(Integer userId){
        if(userId != null){
            return BaseResponse.success(new User(userId,"zhangsan"));
        }else{
            return BaseResponse.fail("查询用户信息失败！");
        }
    }
    public BaseResponse addUser(@Validated User user, BindingResult bindingResult){
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        //如果参数校验失败，会将错误信息封装成对象组装在 BindingResult
        if(!fieldErrors.isEmpty()){
            return BaseResponse.fail(fieldErrors.get(0).getDefaultMessage());//getDefaultMessage()的信息是@NotNull(message = "用户id不能为空")
        }
        return BaseResponse.success("OK!");
    }
    @RequestMapping("/queryUser")
    public BaseException queryUser(User user){
        throw new BaseException("500","异常");
    }
}
