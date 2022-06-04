package org.springcms.core.jwt.utils;

import io.jsonwebtoken.Claims;
import org.springcms.core.jwt.constant.TokenConstant;
import org.springcms.core.jwt.vo.CmsUser;

import javax.servlet.http.HttpServletRequest;

public class AuthUtils {

    public static CmsUser getUser() {
        HttpServletRequest request = WebUtils.getRequest();
        if (request == null) {
            return null;
        }

        return getUser(request);
    }

    public static CmsUser getUser(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return null;
        }

        return JwtUtils.getUser(String.valueOf(claims.get(TokenConstant.USER_ID)));
    }

    public static Claims getClaims(HttpServletRequest request) {
        String token = request.getHeader(TokenConstant.HEADER);
        if (token == null || token.isEmpty()) {
            token = request.getParameter(TokenConstant.HEADER);
        }

        if (token == null || token.isEmpty()) {
            return null;
        }

        Claims claims = JwtUtils.parseJWT(token);
        return claims;
    }
}
