# gradle-annotation-processor

# 개요

평소 사용법만 알았던 어노테이션에 대해서 자세히 알아보고 직접 어노테이션을 만들어보자.

# 어노테이션이란

자바 소스 코드에 추가하여 사용할 수 있는 **메타데이터**의 일종

- @ 기호를 붙여 사용한다.
- JDK 1.5 이상 버전에서 사용가능하다.
- 클래스 파일에 임베디드 되어 컴파일러에 의해 생성된 후 자바 가상머신에 포함되어 작동한다.

## Annotation의 용도

- compiler를 위한 정보 : Annotation은 컴파일러가 에러를 감지하는데 사용
- 컴파일 시간 및 배포 시간 처리 : Annotation 정보를 처리해 코드, XML 파일 등을 생성
- 런타임 처리 : 일부 Annotation은 런타임에 조사됨

## Annotation의 종류

- Built in Annotation : 자바에서 기본 제공하는 어노테이션 ex. @Override, @Deprecated
- Meta Annotation : 커스텀 어노테이션을 만들 수 있게 제공된 어노테이션 ex. @Retention, @Target
- Custom Annotation : 개발자의 의도에 의해 생성된 어노테이션

# 어노테이션 생성 방법

인터페이스 선언과 동일하나 @ 기호를 앞에 붙여준다.

```jsx
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Runtime까지 해당 어노테이션을 삭제하지 않음
@Target(ElementType.TYPE) // 
public @interface MyAnnotation {
	int type() // 어노테이션 선언시에 필요한 데이터를 여기다 선언하기
	// 클래스, 메서드 또는 필드에 선언한다면, @MyAnnotation(type = 1) 이런 형식으로 사용됨
}

```

## @Retention

어노테이션이 언제까지 살아있을 것인지를 정하는 어노테이션

- RetentionPolicy.SOURCE : 소스코드(.java) 까지 남아있는다.
- RetentionPolicy.CLASS : 바이트코드(.class) 까지 남아있는다.
- RetentionPolicy.RUNTIME : 런타임까지 남아있는다.

## 어노테이션 활용하기

Annotation Processor를 통해 어노테이션을 처리할 수 있다.

컴파일 시점에 끼어들어 특정한 어노테이션이 붙어있는 소스코드를 참조해서 새로운 소스코드를 만들어 낼 수 있다

이를 확인해보기 위해 커스텀 어노테이션을 만들어 보자.
