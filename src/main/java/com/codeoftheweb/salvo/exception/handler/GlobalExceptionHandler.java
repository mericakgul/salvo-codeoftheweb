//package com.codeoftheweb.salvo.exception.handler;

//import com.codeoftheweb.salvo.model.dto.ExceptionMessage;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.util.Date;

//@ControllerAdvice
//public class GlobalExceptionHandler {

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ExceptionMessage> handleIllegalArgumentException(IllegalArgumentException exception) {
//        exception.printStackTrace();
//        return ResponseEntity
//                .internalServerError()
//                .body(ExceptionMessage.builder()
//                        .httpStatus(HttpStatus.NOT_ACCEPTABLE)
//                        .message("The type of the id is not correct. It should be UUID type.")
//                        .timestamp(new Date())
//                        .build());
//    }

//    @ExceptionHandler(RollbackException.class)
//    public ResponseEntity<ExceptionMessage> handleRollbackException(RollbackException exception){
//        exception.printStackTrace();;
//        return ResponseEntity
//                .internalServerError()
//                .body(ExceptionMessage.builder()
//                        .httpStatus(HttpStatus.BAD_REQUEST)
//                        .message("lalalal")
//                        .timestamp(new Date())
//                        .build());
//    }
//
//    @ExceptionHandler(TransactionSystemException.class)
//    public ResponseEntity<ExceptionMessage> handleConstraintViolationException(TransactionSystemException exception){
//        exception.printStackTrace();;
//        return ResponseEntity
//                .internalServerError()
//                .body(ExceptionMessage.builder()
//                        .httpStatus(HttpStatus.BAD_REQUEST)
//                        .message("yayayyayaya")
//                        .timestamp(new Date())
//                        .build());
//    }
//
//}
