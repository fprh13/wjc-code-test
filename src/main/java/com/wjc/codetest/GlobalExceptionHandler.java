package com.wjc.codetest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
    [문제] Global 기능의 유지보수 문제

    [원인] Global 클래스가 별도의 디렉토리 없이 관리되고 있습니다.

    [개선안]
        대안: GlobalExceptionHandler를 global(common) 패키지에 위치시킵니다.

        트레이드오프:
            A가 B를 의존한다는 것은, "B의 변경에 A가 영향을 받는다" 입니다.

            다른 도메인이 global 패키지를 의존한다면, global 패키지의 변경이 다른 도메인에 영향을 미치게 됩니다.
            그래서 global 패키지를 수정할 때는 다른 도메인에 끼치는 영향을 고려하여 설계하게 됩니다.

            하지만 다른 도메인은 global 패키지를 신경쓰면서 자신을 수정할 필요는 없습니다.
            이것이 단방향 의존의 장점입니다.

            여기까지가 단방향 의존을 유지했을 때의 장단점입니다.
            만약 양방향 의존이 발생한다면, 서로의 변경이 전파되어 유지보수성이 급격히 떨어지므로
            이러한 상황이 생기지 않도록 조심하여 설계해야만 합니다.

        선택 근거:
            global 관련 클래스를 루트 패키지에 계속 추가하면 가독성과 유지보수성이 저하되고, "global"이라는 하나의 책임이 분산됩니다.
            프로젝트 전반에서 빈번히 추가되고 관리될 공통 기능을 한곳에 모아서 일관되게 관리할 필요가 있습니다.

            Product 패키지는 Product라는 하나의 책임을 가지고 계층화된 의존으로 관리되듯,
            global도 하나의 책임과 명확한 의존 설계를 가져야합니다.

            "만드는 사람이 수고로우면 쓰는 사람이 편하고, 만드는 사람이 편하면 쓰는 사람이 수고롭다."
            global이라는 하나의 책임을 묶어 잘 만들어두고 유지보수한다면, 다른 도메인에서 사용하는 개발자는 편해집니다.
 */
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
