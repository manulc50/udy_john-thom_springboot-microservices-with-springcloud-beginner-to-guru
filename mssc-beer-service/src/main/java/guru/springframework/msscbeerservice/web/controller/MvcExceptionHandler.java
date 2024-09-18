package guru.springframework.msscbeerservice.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MvcExceptionHandler {
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<List<String>> validationErrorHandler(ConstraintViolationException ex){
		List<String> errorsList = new ArrayList<String>(ex.getConstraintViolations().size());
		ex.getConstraintViolations().forEach(error -> errorsList.add(error.toString()));
		return ResponseEntity.badRequest().body(errorsList);
		
	}

}
