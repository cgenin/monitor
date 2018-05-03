#
# Anti-Monitor
#

server/target: web/dist
	@echo "package Fat Jar"
	cd server && mvn clean install

clean:
		@echo "clean All distributions"
		@rm -Rf web/dist
		@rm -Rf server/target

web/dist: clean
	@echo "package web"
	cd web && yarn install && yarn build


docker: server/target
	@echo "package Docker Image"
	cd server && mvn dockerfile:build

