package com.wjc.codetest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice(value = {"com.wjc.codetest.product.controller"})
public class GlobalExceptionHandler {

    /*
        [문제] 모든 RuntimeException이 500 서버 오류로 처리됩니다.

        [원인] 비즈니스 에러와 서버 에러를 구분하지 않고 INTERNAL_SERVER_ERROR로 일괄 처리하고 있습니다.

        [개선안]
            대안:
                RuntimeException을 상속 받은 CustomException을 구성하고, 상태 코드(HttpStatus) 정보를 기반으로 응답합니다.

            선택 근거:
                RuntimeException을 상속받아 CustomException을 httpStatus와 message 필드로 구성하고 관리하면,
                상황에 맞는 에러 메시지와 상태 코드를 통해 유연한 예외 처리가 가능해집니다.

                비즈니스 에러와 서버 에러를 구분해 클라이언트에게 올바른 응답을 주는 것은 매우 중요합니다.
                예를 들어, 상품 조회 중 throw new RuntimeException("product not found");가 발생했다면,
                이것은 500 서버 에러가 아닌 404 Not Found로 응답하는 것이 맞습니다.
                500을 반환하면 클라이언트는 서버 장애로 오해할 수 있습니다.

                또한, 비즈니스 에러와 서버 에러 로그를 분리하는 것도 중요합니다.
                장애를 추적하거나 원인을 분석할 때 두 에러가 섞여 있으면 추적이 어려워집니다.
                모든 예외를 동일한 error 로그로 남기면 추적시 혼란이 생깁니다.

                CustomException을 구성하면 서버 에러만 로깅하도록 구성할 수 있습니다.
                ex)
                    if (e.getStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                        log.error(e.getMessage());
                    }
    */
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> runTimeException(Exception e) {
        log.error("status :: {}, errorType :: {}, errorCause :: {}",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "runtimeException",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
