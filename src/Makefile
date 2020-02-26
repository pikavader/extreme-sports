build_all:
	make build_extras
	javac testServer/MainServerTest.java
	javac testClient/MainAdminTest.java
	javac testClient/MainUserTest.java

build_extras:
	javac extremeServer/*.java
	javac extremeClient/*.java

run_server:
	java testServer/MainServerTest

run_admin:
	java testClient/MainAdminTest

run_user:
	java testClient/MainUserTest

clean:
	rm */*.class
