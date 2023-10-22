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

## Rest API
### add
### all
### upload
Upload multiple files for backfill purpose. The uploaded file name has to have suffix of `-YYYYMMDD` format to specify the date of the data entry. 
Each line should contain `HH:MM:SS,value` format, where the value is a decimal (in cm) for the depth of the water level.
#### Curl sample:
Specify the device ID in the head such as `SumpDeviceId: 1`
Specify each file name as one of `files` parameter to upload. 
```shell
curl http://192.168.1.169:8080/rest/upload -X POST -H "SumpDeviceId: 1" -F files=@/raspi-sump/csv/waterlevel-20230807.csv -F files=@/raspi-sump/csv/waterlevel-20230806.csv
```
Data sample:
```
2023-08-07T00:00:10,10.0
2023-08-07T00:01:06,9.8
2023-08-07T00:02:05,9.7
2023-08-07T00:03:05,9.8
```


## License
This software is released under [MIT License](https://github.com/ntamagawa/sumpdata/blob/main/LICENSE).