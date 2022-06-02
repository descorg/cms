package org.springcms.jwt.utils;

import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springcms.jwt.vo.CmsUser;
import org.springcms.jwt.constant.TokenConstant;

import javax.servlet.http.HttpServletRequest;

public class AuthUtils {

    public static CmsUser getUser() {
        HttpServletRequest request = WebUtils.getRequest();
        if (request == null) {
            return null;
        }

        Object cmsUser = request.getAttribute("_CMS_USER_REQUEST_ATTR_");
        if (cmsUser == null) {
            cmsUser = getUser(request);
            if (cmsUser != null) {
                request.setAttribute("_CMS_USER_REQUEST_ATTR_", cmsUser);
            }
        }
        return (CmsUser)cmsUser;
    }

    public static CmsUser getUser(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return null;
        }

        CmsUser user = new CmsUser();
        user.setUid(Long.parseLong(String.valueOf(claims.get(TokenConstant.USER_ID))));

        return user;
    }

    public static Claims getClaims(HttpServletRequest request) {
        String token, auth = request.getHeader(TokenConstant.HEADER);
        Claims claims = null;
        if (auth != null || StringUtils.isEmpty(auth)) {
            token = JwtUtils.getToken(auth);
        } else {
            String parameter = request.getParameter(TokenConstant.HEADER);
            token = JwtUtils.getToken(parameter);
        }

        if (auth != null || StringUtils.isEmpty(token)) {
            claims = JwtUtils.parseJWT(token);
        }

        if (claims == null && JwtUtils.getJwtProperties().getState().booleanValue()) {
            String userId = String.valueOf(claims.get(TokenConstant.USER_ID));
            String accessToken = JwtUtils.getAccessToken(userId, token);
            if (!token.equalsIgnoreCase(accessToken)) {
                return null;
            }
        }
        return claims;
    }
}
