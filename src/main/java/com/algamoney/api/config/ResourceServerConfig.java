package com.algamoney.api.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

import com.algamoney.api.config.property.AlgamoneyApiProperty;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Profile("oauth-security")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig  {
	
	@Autowired
	private AlgamoneyApiProperty apiProperty;
	
	@Bean
	public SecurityFilterChain resourcerServerFilterChain(HttpSecurity http) throws Exception {
		http.formLogin(Customizer.withDefaults())
		       .csrf((csrf) -> csrf.disable())
		       .oauth2ResourceServer((oauth2ResourceServer) ->
				oauth2ResourceServer
					.jwt((jwt) ->
						jwt.jwtAuthenticationConverter(jwtAthenticationConverter())
				))
		        .logout(logoutConfig -> {
		        	logoutConfig.logoutSuccessHandler(
		        		(request, response, authentication) -> {
		        			String returnTo = request.getParameter("returnTo");
		        			
		        			if(!StringUtils.hasText(returnTo))
		        				returnTo = apiProperty.getSeguranca().getAuthServerUrl();
		        			
		        			response.setStatus(302);
		        			response.sendRedirect(returnTo);
		        		}
		        	);
		        });
	    return http.build();
	}
	
	private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAthenticationConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
			List<String> authorities = jwt.getClaimAsStringList("authorities");
			
			if(authorities == null)
				authorities = Collections.emptyList();
			
			JwtGrantedAuthoritiesConverter scopesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
			
			Collection<GrantedAuthority> grantedAuthorities = scopesAuthoritiesConverter.convert(jwt);
			
			grantedAuthorities.addAll(authorities.stream()
					.map(SimpleGrantedAuthority::new)
					.collect(Collectors.toList()));
			
			return grantedAuthorities;
		});
		
		
		return jwtAuthenticationConverter;
	}
	
	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
