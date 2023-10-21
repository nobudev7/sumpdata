# Sump Data Project
A Java Spring Boot app that manages and visualize sump pump data.

## Dev Environment
```
$ docker volume create sumpdatavolume
$ docker run --name sumpdatamysql -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=myrootpswd -v sumpdatavolume:/var/lib/mysql mysql
```


## Raspi Sump

The following command will add the latest sump data entry to the server.
The intention is to run this very minute to keep updating the record.
```
curl http://192.168.1.169:8080/rest/add -d deviceId=1 -d measuredOn=`date "+%Y-%m-%d"`T`cat waterlevel-\`date "+%Y%m%d"\`.csv  |tail -1|cut -d ',' -f 1` -d value=`cat waterlevel-\`date "+%Y%m%d"\`.csv |tail -1 |cut -d ',' -f 2`
```
