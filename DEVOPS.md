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
$ for F in $(ls waterlevel-202301*); do  curl 'http://192.168.1.169:8080/devices/1/entries/files' -X POST -F files=@$F; done
```

## Swagger
### Swagger UI
The API documentation is exposed as a HTML page at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). It is also exposed to make the API document publically accessible, a link to the Swagger Editor can be used along with the swagger yaml `sump_data_rest_api.yaml` file in the repo as below.
[Swagger Editor](https://editor.swagger.io/?url=https://raw.githubusercontent.com/ntamagawa/sumpdata/main/src/api/sump_data_rest_api.yaml)

`sump_data_rest_api.yaml` is generated when Maven `verify` plan is run from IDE, or `mvn verify`.

## Redis server
### Installation
Using Docker, I set up the redis server (no persistent option).
```shell
$ docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack:latest
```
### Configuration
Note that TTL is set to 10 seconds in the below example.
```shell
spring.cache.redis.enabled=true
spring.cache.redis.host=localhost
spring.cache.redis.port=6379
spring.cache.redis.time-to-live=60000
```

### Example
Set a breakpoint in controller, then run a curl command that hit the endpoint, twice. The first time will hit the breakpoint, and the second time it will return without running the controller method.
```shell
curl 'http://localhost:8080/devices/1/entries/2023/01/29'
```
You can see the cache entry in the RedisInsight screen after calling the endpoint by accessing
`http://localhost:8001/redis-stack/browser`.
![RedisInsight.png](assets%2FRedisInsight.png)
