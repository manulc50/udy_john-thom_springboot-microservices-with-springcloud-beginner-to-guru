package guru.springframework.msscbrewery.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MvcExceptionHandler {
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<List<String>> validationErrorHandler(ConstraintViolationException ex){
		List<String> errors = new ArrayList<String>(ex.getConstraintViolations().size());
		ex.getConstraintViolations().forEach(constraintViolation -> errors.add(constraintViolation.getPropertyPath() + " : " + constraintViolation.getMessage()));
		return ResponseEntity.badRequest().body(errors);
	}
	
	@ExceptionHandler(BindException.class)
	public ResponseEntity<List<ObjectError>> handlerBindException(BindException ex){
		return new ResponseEntity<List<ObjectError>>(ex.getAllErrors(),HttpStatus.BAD_REQUEST);
	}

}
