package com.algamoney.api.cors;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.algamoney.api.config.property.AlgamoneyApiProperty;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
	
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		var req = (HttpServletRequest) request;
		var resp = (HttpServletResponse) response;
		
		resp.setHeader("Access-Control-Allow-Origin", algamoneyApiProperty.getOriginPermitida());
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		
		if("OPTIONS".equals(req.getMethod()) && algamoneyApiProperty.getOriginPermitida().equals(req.getHeader("Origin"))) {
			resp.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			resp.setHeader("Access-Control-Allow-HEADERS", "Authorization, Content-Type, Accept");
			resp.setHeader("Access-Control-Max-Age", "3600");
			
			
			resp.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(request, response);
		}
	}

}
