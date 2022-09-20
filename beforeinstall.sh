#!/usr/bin/env bash  
#첫번째 줄 #!/usr/bin/env bash는 주석이 아니다 배쉬 경로 설정에 필요

#해당 경로의 레포지토리들을 삭제해줌
REPOSITORY=/home/ubuntu/

if [ -d $REPOSITORY/myapp ]; then
    rm -rf $REPOSITORY/myapp
fi
mkdir -vp $REPOSITORY/myapp
