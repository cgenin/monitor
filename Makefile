#
# Anti-Monitor
#

web:
	@echo "package web"
	@cd web
	npm install && npm run build

server: web
	@cd ../server
	@echo "package server"
	mvn clean install dockerfile:build

build: server
	@echo "package all"

