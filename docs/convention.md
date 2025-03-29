# commit convention

- Feat(#이슈 번호): 새로운 기능 추가
- Fix(#이슈 번호): 버그 수정
- Refactor(#이슈 번호): 코드 리팩토링 (기능 변경 없음)
- Docs(#이슈 번호): 문서 수정
- Style(#이슈 번호): 코드 포맷팅, 세미콜론 누락 등 (기능 변경 없음)
- Test(#이슈 번호): 테스트 코드 추가 및 수정
- Chore(#이슈 번호): 빌드 작업, 패키지 매니저 설정

# branch convention

- 배포 환경 브랜치 `main`
- 기본적으로 우리가 사용할 브랜치 `develop`
- 그 산하에 develop 브랜치 기준 `feature/#1 혹은 feature/login` 생성

# URL convention

-  URI는 명사로만 구성한다
-  도메인은 복수형으로 나타낸다 ex) members/, boards/
-  ’/’는 계층 관계를 나타낼 때 사용한다 ex) users/{userIdx}/boards/{boardIdx}
-  ’ _’ 대신에 ‘-’를 사용한다
-  URI만 보고 어떤 기능인지 알 수 있도록 명시적으로 작성한다


  ### **주의 사항**

1. 데이터를 삭제할 때도 Patch를 사용한다
2. 이미 삭제된 데이터여도 내부 혹은 나중에 사용할 일이 생길 수도 있다. 그렇기 때문에 Delete로 데이터를 삭제하는 것이 아닌 status 컬럼을 통해 데이터의 삭제 여부를 관리해준다.
