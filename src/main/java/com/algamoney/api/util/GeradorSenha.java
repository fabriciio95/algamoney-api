package com.algamoney.api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class GeradorSenha {

	public static void main(String[] args) {
		var encoder = new BCryptPasswordEncoder();
		
		System.out.println(encoder.encode("admin"));
	}
}
