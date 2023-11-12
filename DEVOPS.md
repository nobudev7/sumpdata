# Development and Operations

## Development Environment

### Set up data source 
For a data source for this application, I used MySQL on Docker. To persists the data, set up a volume.
```
$ docker volume create sumpdatavolume
$ docker run --name sumpdatamysql -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=myrootpswd -v sumpdatavolume:/var/lib/mysql mysql
# To Restart the same mysql container
$ docker start -a sumpdatamysql  
```
To configure connection, set up the properties like below.
```properties
spring.datasource.url=jdbc:mysql://${MYSQL_HOST:localhost}:3306/sumpdata
spring.datasource.username=sumpadmin
spring.datasource.password=pumpisinthebasement
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## Posting data from Raspi-Sump

### Live data upload
By default, Raspi-Sump measures the water level per minute. To upload data as it is generated without chaning any Raspi-Sump code, one way is to keep monitoring the CSV file it generates, and run it via a cron entry like below.
```shell
 curl -X POST -H 'Content-Type: text/plain' "http://192.168.1.169:8080/devices/1/entries?measuredOn=`date "+%Y-%m-%d"`T`cat waterlevel-\`date "+%Y%m%d"\`.csv  |tail -1|cut -d ',' -f 1`&value=`cat waterlevel-\`date "+%Y%m%d"\`.csv |tail -1 |cut -d ',' -f 2`"
```

### Bulk Upload of CSV files

To backfill the past data, use POST with multipart file upload. For example, to upload a month worth of CSV files, run the following command.
```shell
$ for F in $(ls waterlevel-202301*); do  curl 'http://192.168.1.169:8080/devices/1/entries' -X POST -F files=@$F; done
```



