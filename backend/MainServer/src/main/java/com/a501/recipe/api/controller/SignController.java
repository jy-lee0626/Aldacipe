package com.a501.recipe.api.controller;


import com.a501.recipe.aop.exception.RefreshTokenExpiredException;
import com.a501.recipe.api.dto.sign.CheckEmailRequestDto;
import com.a501.recipe.api.dto.sign.LoginResponseDto;
import com.a501.recipe.api.dto.token.AccessTokenRequestDto;
import com.a501.recipe.api.dto.token.AccessTokenResponseDto;
import com.a501.recipe.api.dto.token.TokenRequestDto;
import com.a501.recipe.api.dto.token.TokenResponseDto;
import com.a501.recipe.api.dto.user.UserLoginRequestDto;
import com.a501.recipe.api.dto.user.UserSignupRequestDto;
import com.a501.recipe.api.domain.entity.User;
import com.a501.recipe.api.dto.response.CommonResult;
import com.a501.recipe.api.dto.response.OneResult;
import com.a501.recipe.api.service.ResponseService;
import com.a501.recipe.api.service.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(tags = "01. SignUp / LogIn")
@RequiredArgsConstructor
@RestController
public class SignController {
    private final SignService signService;
    private final ResponseService responseService;

    @ApiOperation(value = "로그인", notes = "이메일로 로그인 수행")
    @PostMapping("/login")
    public OneResult<LoginResponseDto> login (HttpServletResponse response,
                                                    @ApiParam(value = "로그인 DTO", required = true) @RequestBody UserLoginRequestDto userLoginRequestDto) {
        TokenResponseDto tokenDto = signService.login(userLoginRequestDto);

        Cookie cookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        cookie.setMaxAge(14 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return responseService.getOneResult(new LoginResponseDto( tokenDto.getUserId() ,new AccessTokenResponseDto(tokenDto.getAccessToken(), tokenDto.getAccessTokenExpireDate())));
    }

    @ApiOperation(value = "회원가입", notes = "회원가입 수행")
    @PostMapping("/signup")
    public CommonResult signup (
            @ApiParam(value = "회원가입 DTO", required = true) @RequestBody UserSignupRequestDto userSignupRequestDto) {
        signService.signup(userSignupRequestDto);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "Access Token, Refresh Token 재발급",
            notes = "Access Token 만료시 회원 검증 후 Refresh Token을 검증해서 두 Token을 재발급")
    @PostMapping("/reissue")
    public OneResult<AccessTokenResponseDto> reissue(@ApiIgnore  @CookieValue(value = "refreshToken", required = false) Cookie refresh, HttpServletRequest request, HttpServletResponse response, @ApiParam(value = "토큰 재발급 DTO", required = true) @RequestBody AccessTokenRequestDto accessTokenRequestDto) {
        System.out.println(refresh);
        if (refresh == null)
            throw new RefreshTokenExpiredException();
        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessTokenRequestDto.getAccessToken(), refresh.getValue());
        TokenResponseDto tokenDto = signService.reissue(tokenRequestDto, response);

        Cookie cookie = new Cookie("refreshToken", tokenDto.getRefreshToken());
        cookie.setMaxAge(14 * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return responseService.getOneResult(new AccessTokenResponseDto(tokenDto.getAccessToken(), tokenDto.getAccessTokenExpireDate()));
    }

    @DeleteMapping("/log-out")
    public CommonResult logout (@ApiIgnore @CookieValue(value = "refreshToken", required = false) Cookie refresh, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        User loginUser = (User) authentication.getPrincipal();
        signService.logout(loginUser.getId());
        return responseService.getSuccessResult();
    }

    @GetMapping("/check-email")
    public CommonResult checkEmailDup(@RequestParam("email") String email){
        return responseService.getOneResult(signService.checkEmailDup(email));
    }
}
