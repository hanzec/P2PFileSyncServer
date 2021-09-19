package com.hanzec.P2PFileSyncServer.model.exception;

import com.google.gson.Gson;
import com.hanzec.P2PFileSyncServer.model.exception.auth.*;

import com.hanzec.P2PFileSyncServer.model.api.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlers {
    private static final Gson gson = new Gson();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler({
            TokenVerifyFaildException.class,
            EmailAlreadyExistException.class,
            TokenAlreadyExpireException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void GenericBadRequestException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendMessage(ex,response);
        logger.debug("Bad Request because " + ex.getClass().getName() + " for endpoint [ " + request.getRequestURI() + "]" );
    }

    @ExceptionHandler({
            TokenNotFoundException.class,
            UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void GenericNotFoundException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendMessage(ex,response);
        logger.debug("Page not found because " + ex.getClass().getName() + " for endpoint [ " + request.getRequestURI() + "]" );
    }

    @ExceptionHandler({
            PasswordNotMatchException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void GenericForbiddenException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendMessage(ex,response);
        logger.debug("Request forbidden because " + ex.getClass().getName() + " for endpoint [ " + request.getRequestURI() + "]" );
    }

    @ExceptionHandler({})
    @ResponseStatus(HttpStatus.CONFLICT)
    public void GenericConflictException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendMessage(ex,response);
        logger.debug("Request forbidden because " + ex.getClass().getName() + " for endpoint [ " + request.getRequestURI() + "]" );
    }

    private static void sendMessage(Exception ex, HttpServletResponse response) throws IOException {
        MethodArgumentNotValidException c = (MethodArgumentNotValidException) ex;
        List<ObjectError> errors =c.getBindingResult().getAllErrors();

        List<String> errorList = new ArrayList<>();

        errors.forEach(V -> { errorList.add(V.getDefaultMessage());});

        response.getWriter().write(
                gson.toJson(new Response().addResponse("Error Item",errorList)));
    }
}
