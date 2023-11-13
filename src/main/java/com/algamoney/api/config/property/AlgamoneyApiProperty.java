package com.algamoney.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("algamoney")
public class AlgamoneyApiProperty {
	
	private final Seguranca seguranca = new Seguranca();
	
	private final S3 s3 = new S3();
	
	private String originPermitida;
	
	public Seguranca getSeguranca() {
		return seguranca;
	}
	
	public S3 getS3() {
		return s3;
	}
	
	public String getOriginPermitida() {
		return originPermitida;
	}
	
	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}

	public static class Seguranca {
	
		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}
	
	}
	
	public static class S3 {
		
		private String accessKeyId;
		
		private String secretAccessKey;

		public String getAccessKeyId() {
			return accessKeyId;
		}

		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}

		public String getSecretAccessKey() {
			return secretAccessKey;
		}

		public void setSecretAccessKey(String secretAcessKey) {
			this.secretAccessKey = secretAcessKey;
		}
	}
}
