# Development and Operations

## Development Environment

### My Instance Setting
The whole local development is set up with a locally running Spring Boot app, Streamlit (Python application), and in Docker container, MySQL, Redis, and Nginx.
![MacNginxDiagram.png](assets%2FMacNginxDiagram.png)

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

## Security
A minimal IP address-based security feature is implemented. By default, it allows loop back and some local network access. To override, use the following property.
```properties
security.allow.ip.list=<list of ip addresses allowed>
```
The intention for this setting is to externalize the allow list. On a production server, you might set the following OS environment variable to allow actual devices' IP addresses.
```shell
export SECURITY_ALLOW_IP_LIST=<list of devices ip addresses>
```

### Enabling SSL
Under development environment, a self-signed certificate may be used.
To generate such a cert, use `keytool`. You can just enter for all questions, then `yes` to accept the default value.
```shell
$ keytool -genkeypair -alias sumpdata -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore sumpdata.p12 -validity 3650 -storepass mypassword
```
```
What is your first and last name?
  [Unknown]:  
What is the name of your organizational unit?
  [Unknown]:  
What is the name of your organization?
  [Unknown]:  
What is the name of your City or Locality?
  [Unknown]:  
What is the name of your State or Province?
  [Unknown]:  
What is the two-letter country code for this unit?
  [Unknown]:  
Is CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown correct?
  [no]:  yes

Generating 2,048 bit RSA key pair and self-signed certificate (SHA384withRSA) with a validity of 3,650 days
	for: CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown

```

Place the generated p12 file either to a path that is accessible by the app, or under the class path.
Configure the properties as follows.
```properties
server.ssl.key-store-type=PKCS12
server.ssl.key-store=classpath:sumpdata.p12 # Classpath
# server.ssl.key-store=/path/to/your/sumpdata.p12 # Or file path
server.ssl.key-store-password=mypassword
server.ssl.key-alias=sumpdata
server.ssl.enabled=true
```
After enabling SSL, most client application such as `curl` or PostMan fail to verify the cert. For `curl`, use `--insecure` option to allow self-signed cert.

This affects also web ui, such as https://localhost:8080/swagger-ui/index.html. Let browser allow self-signed cert, or make it trusted (Mac/Safari) to view the page.

## Logging
Log4j2 wrapped by SLF4J is used for logging. By default, `logs` directory is created one level above the working directory, and `sampdata-server.log` is created in the directory.

### Rolling file
The current setting rolls the log file on startup, daily, and when it gets 10MB, with `.gz` compression.
* To remove compression, take out `.gz` from the `filePattern` in log4j2.xml.
* The log file folder can be set vy `LOG_DIR` environment variable.
* Rolling file is default max 7 files to keep. After that, the oldest file is removed, and shifts log file suffix (so, `*-1.log.gz` is always the oldest).
* TODO: It might be a good idea to make rolling policy overrideable. 