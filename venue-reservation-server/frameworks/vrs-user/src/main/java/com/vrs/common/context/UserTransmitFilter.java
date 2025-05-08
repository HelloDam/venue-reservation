package com.vrs.common.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;


/**
 * @Author dam
 * @create 2024/11/16 16:10
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    /**
     * 处理每个请求，都会执行这个方法
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     */
    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String username = httpServletRequest.getHeader("userName");
        if (!StringUtils.isEmpty(username)) {
            // --if-- username不为空，说明是经过了过滤器的
            Long userId = Long.parseLong(httpServletRequest.getHeader("userId"));
            String userTypeStr = httpServletRequest.getHeader("userType");
            // 如果用户类型为空，直接设置为普通用户
            Integer userType = null == userTypeStr ? 100 : Integer.parseInt(userTypeStr);
            String organizationIdStr = httpServletRequest.getHeader("organizationId");
            Long organizationId = null == organizationIdStr ? null : Long.parseLong(organizationIdStr);
            UserContext.setUser(new UserInfoDTO(userId, username, userType, organizationId));
        }
        try {
            // 放行，继续进行业务处理
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 业务处理完成之后，移除上下文信息。不移除，会发生内存泄漏
            UserContext.removeUser();
        }
    }
}