# Sump Data Project
A Java Spring Boot app that manages and visualize sump pump data.

## Dev Environment
```
$ docker volume create sumpdatavolume
$ docker run --name sumpdatamysql -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=myrootpswd -v sumpdatavolume:/var/lib/mysql mysql
```