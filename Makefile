APP_NAME=pi_2025_s1-0.0.1-SNAPSHOT.jar
JAR_PATH=target/$(APP_NAME)
PROFILE=local

.PHONY: build run clean

build:
	mvnw.cmd clean package -DskipTests

run: build
	java -jar $(JAR_PATH) --spring.profiles.active=$(PROFILE)

clean:
	mvnw.cmd clean
