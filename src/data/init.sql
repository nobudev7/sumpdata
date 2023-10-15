create database sumpdata;
create user 'sumpadmin'@'%' identified by 'pumpisinthebasement';
grant all on sumpdata.* to 'sumpadmin'@'%';
