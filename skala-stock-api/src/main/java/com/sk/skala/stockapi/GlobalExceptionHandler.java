package com.sk.skala.stockapi;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/** ===========================
	 *  Spring MVC 기본 예외들
	 *  =========================== */

	// 지원하지 않는 HTTP 메서드 (405)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	public Response handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
		log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage(), e);
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(),
				"지원하지 않는 요청 방식입니다: " + e.getMethod());
		return response;
	}

	// 잘못된 JSON, 요청 본문 파싱 불가 (400)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public Response handleNotReadable(HttpMessageNotReadableException e) {
		log.error("HttpMessageNotReadableException: {}", e.getMessage(), e);
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(),
				"잘못된 요청 형식(JSON 파싱 오류)");
		return response;
	}

	// Validation 실패 (400)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public Response handleValidation(MethodArgumentNotValidException e) {
		log.error("Validation failed: {}", e.getMessage(), e);
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(),
				"요청 파라미터 유효성 검증 실패");
		return response;
	}

	/** ===========================
	 *  커스텀 예외
	 *  =========================== */

	@ExceptionHandler(ParameterException.class)
	@ResponseBody
	public Response handleParameterException(ParameterException e) {
		log.error("ParameterException: {}", e.getMessage(), e);
		Response response = new Response();
		response.setError(e.getCode(), e.getMessage());
		return response;
	}

	@ExceptionHandler(ResponseException.class)
	@ResponseBody
	public Response handleResponseException(ResponseException e) {
		log.error("ResponseException: {}", e.getMessage(), e);
		Response response = new Response();
		response.setError(e.getCode(), e.getMessage());
		return response;
	}

	/** ===========================
	 *  일반 예외
	 *  =========================== */

	@ExceptionHandler(NullPointerException.class)
	@ResponseBody
	public Response handleNullPointer(NullPointerException e) {
		log.error("NullPointerException: {}", e.getMessage(), e);
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(), "NullPointerException 발생");
		return response;
	}

	@ExceptionHandler(SecurityException.class)
	@ResponseBody
	public Response handleSecurity(SecurityException e) {
		log.error("SecurityException: {}", e.getMessage(), e);
		Response response = new Response();
		response.setError(Error.NOT_AUTHENTICATED.getCode(), e.getMessage());
		return response;
	}

	// 모든 예외를 최종 캐치 (fallback)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Response handleGeneral(Exception e) {
		log.error("Unhandled Exception: {}", e.getMessage(), e);
		Response response = new Response();
		response.setError(Error.SYSTEM_ERROR.getCode(),
				e.getMessage() != null ? e.getMessage() : "알 수 없는 오류 발생");
		return response;
	}
}
