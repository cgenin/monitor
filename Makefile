#
# Anti-Monitor
#

.PHONY: server/target/*.jar

clean:
		@echo "clean All distributions"
		@rm -Rf web/dist
		@rm -Rf server/target

web/dist: clean
	@echo "package web"
	cd web && yarn install && yarn build

server/target/*.jar: web/dist
	@echo "package Fat Jar"
	cd server && mvn clean install

docker: server/target/*.jar
	@echo "package Docker Image"
	cd server && mvn dockerfile:build

